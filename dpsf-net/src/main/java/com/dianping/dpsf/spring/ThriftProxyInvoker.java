/**
 * 
 */
package com.dianping.dpsf.spring;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.channel.thrift.ChannelBufferTTransport;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.protocol.thrift.ThriftRequest;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffers;

/**    
 * <p>    
 * Title: ProxyInvoker.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-7 下午09:58:35   
 */
public class ThriftProxyInvoker implements InvocationHandler{
	
	private static Logger log = DPSFLog.getLogger();
	
	private DPSFMetaData metaData;
	
	private String serviceClassName;
	
	private Map<String,Field[]> fields 
		= new ConcurrentHashMap<String,Field[]>();
		
	private Map<String,Class> argsClazz = new ConcurrentHashMap<String,Class>();
	
	private Map<String,TBase> returnObj = new ConcurrentHashMap<String,TBase>();
	
	public ThriftProxyInvoker(String iface,DPSFMetaData metaData) throws ClassNotFoundException{
		this.serviceClassName = iface.substring(0, iface.lastIndexOf("$Iface"));
		Class serviceClass = Class.forName(serviceClassName);
		
		this.metaData = metaData;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String argsName = this.serviceClassName+"$"+method.getName()+"_args";
		Class argsClass = this.argsClazz.get(argsName);
		if(argsClass == null){
			argsClass = Class.forName(argsName);
			this.argsClazz.put(argsName, argsClass);
		}
		TBase argsObj = (TBase)(argsClass.newInstance());
		if(args != null){
			Field[] thisFields = getArgsFields(argsName);
			int k = 0;
			for(Object arg : args){
				thisFields[k++].set(argsObj, arg);
			}
		}
		
		DPSFRequest request = new ThriftRequest(this.metaData.getServiceName(),
				method.getName(),Constants.MESSAGE_TYPE_SERVICE,
				this.metaData.getTimeout(),argsObj,argsName);
		if(Constants.CALL_SYNC.equalsIgnoreCase(this.metaData.getCallMethod())){
			DPSFResponse res = DefaultInvoker.getInstance()
			.invokeSync(request, metaData, null);
			if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE){
				if(method.getReturnType() == void.class || method.getReturnType() == Void.class){
					return null;
				}else{
					if(res.getReturn() != null){
						TBase returnValue = this.returnObj.get(argsName);
						if(returnValue == null){
							returnValue = (TBase)method.getReturnType().newInstance();
							this.returnObj.put(argsName,returnValue);
						}
						ChannelBufferTTransport transport = new ChannelBufferTTransport(ChannelBuffers.wrappedBuffer((ByteBuffer)res.getReturn()));
						TProtocol protocol = new TBinaryProtocol(transport);
						returnValue = returnValue.deepCopy();
						returnValue.read(protocol);
						return returnValue;
					}else{
						return null;
					}
				}
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION){
				log.error(res.getCause());
				throw new DPSFException(res.getCause());
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION){
				throw new TException(res.getCause());
			}
		}
		return null;
	}
	
	private Field[] getArgsFields(String argsName) throws ClassNotFoundException{
		
		
		Field[] thisFields = this.fields.get(argsName);
		if(thisFields == null){
			Class argsClass = this.argsClazz.get(argsName);
			Field[] thisFields_ = argsClass.getFields();
			thisFields = new Field[thisFields_.length-1];				
			for(int i=0;i<thisFields_.length-1;i++){
				thisFields_[i].setAccessible(true);
				thisFields[i] = thisFields_[i];
			}		
			this.fields.put(argsName, thisFields);
		}
		
		return thisFields;
	}

}
