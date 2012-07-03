/**
 * 
 */
package com.dianping.dpsf.protocol.thrift;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffers;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.Version;
import com.dianping.dpsf.channel.thrift.ChannelBufferTTransport;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.DPSFRuntimeException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: ThriftRequest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-14 下午02:46:07   
 */
public class ThriftRequest implements DPSFRequest{
	
	private static Logger logger = DPSFLog.getLogger();
	
	private Request request;
	
	private long seq;
	
	private int callType;
	
	private int timeout;
	
	private long createMillisTime;
	
	private String serviceName;
	
	private String methodName;
	
	private String[] paramCalssNames;
	
	private int messageType;
	
	private TBase parameter;
	
	private String argsClassName;
	
	private Object[] parameterObj;
	
	private ChannelBufferTTransport transport;
	private TProtocol protocol;
	
	private static Map<String,Short[]> initParamFieldNum = new ConcurrentHashMap<String,Short[]>();
	
	private static Map<String,TBase> initArgsObj = new ConcurrentHashMap<String,TBase>();
	
	private transient Map<String, Object> attachments = new HashMap<String, Object>();
	
	public ThriftRequest(Request request) throws Exception{
		this.request = request;
		this.transport = new ChannelBufferTTransport(ChannelBuffers.wrappedBuffer(this.request.getParameters()));
		this.protocol = new TBinaryProtocol(transport);
		String argsClassName = this.request.getArgsClassName();
		TBase argsObj = initArgsObj.get(argsClassName);
		if(argsObj == null){
			argsObj = (TBase)(Class.forName(argsClassName).newInstance());
			initArgsObj.put(argsClassName, argsObj);
		}
		this.parameter = argsObj.deepCopy();
		this.parameter.read(this.protocol);
	}
	
	public ThriftRequest(String serviceName,String methodName,int messageType,
			int timeout,TBase parameter,String argsClassName){
		
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.messageType = messageType;
		this.timeout = timeout;
		this.argsClassName = argsClassName;
		this.parameter = parameter;
		this.transport = new ChannelBufferTTransport();
		this.protocol = new TBinaryProtocol(transport);
	}
	

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getSerializ()
	 */
	public byte getSerializ() {
		return Constants.SERILIZABLE_THRIFT;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setSequence(long)
	 */
	public void setSequence(long seq) {
		this.seq = seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getSequence()
	 */
	public long getSequence() {

		if(this.seq > 0){
			return this.seq;
		}
		if(this.request != null){
			return this.request.getTransferId();
		}
		throw new DPSFRuntimeException("seq is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getObject()
	 */
	public Object getObject() throws NetException {
		if(this.request == null){
			try {
				this.request = this.createRequest();
			} catch (TException e) {
				throw new NetException(e);
			}
		}
		return this.request;
	}
	
	private Request createRequest() throws TException{
		this.parameter.write(this.protocol);
		this.request = new Request(this.messageType,this.callType,this.seq,
				Version.getCurrentVersion(),this.timeout,this.serviceName,
				this.methodName,this.transport.getChannelBuffer().toByteBuffer(),this.argsClassName);
		
		return this.request;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#setCallType(int)
	 */
	public void setCallType(int callType) {
		this.callType = callType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getCallType()
	 */
	public int getCallType() {
		if(this.callType > 0){
			return this.callType;
		}
		if(this.request != null){
			return this.request.getCallType();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getTimeout()
	 */
	public int getTimeout() {

		if(this.timeout > 0){
			return this.timeout;
		}
		if(this.request != null){
			return this.request.getTimeout();
		}
		throw new DPSFRuntimeException("timeout is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getCreateMillisTime()
	 */
	public long getCreateMillisTime() {
		return this.createMillisTime;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#createMillisTime()
	 */
	public void createMillisTime() {
		this.createMillisTime = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getServiceName()
	 */
	public String getServiceName() {

		if(this.serviceName != null){
			return this.serviceName;
		}
		if(this.request != null){
			return this.request.getServiceName();
		}
		throw new DPSFRuntimeException("serviceName is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getMethodName()
	 */
	public String getMethodName() {

		if(methodName != null){
			return this.methodName;
		}
		if(this.request != null){
			return this.request.getMethodName();
		}
		throw new DPSFRuntimeException("methodName is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getParamClassName()
	 */
	public String[] getParamClassName() throws ServiceException {
		if(this.paramCalssNames != null){
			return this.paramCalssNames;
		}
		if(this.parameterObj == null){
			getParameters();
		}
		this.paramCalssNames = new String[this.parameterObj.length];
		int k = 0;
		for(Object paramObj : this.parameterObj){
			this.paramCalssNames[k] = this.parameterObj[k].getClass().getName();
			k++;
		}
		return this.paramCalssNames;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getParameters()
	 */
	public Object[] getParameters() throws ServiceException {
		if(this.parameterObj == null){
			
			Short[] paramFieldNum = initParamFieldNum.get(this.parameter.getClass().getName());
			
			if(paramFieldNum == null){
				Field byNameField = null;
				try {
					byNameField = Class.forName(this.parameter.getClass().getName()+"$_Fields").getDeclaredField("byName");
				} catch (Exception e) {
					throw new ServiceException(e.getMessage(),e);
				}
				byNameField.setAccessible(true);
				Map<String, TFieldIdEnum> fields = null;
				try {
					fields = (Map<String, TFieldIdEnum>)(byNameField.get(null));
				} catch (Exception e) {
					throw new ServiceException(e.getMessage(),e);
				}
				paramFieldNum = new Short[fields.size()];
				int k = 0;
				for(String fieldName : fields.keySet()){
					paramFieldNum[k++] = fields.get(fieldName).getThriftFieldId();
				}
				initParamFieldNum.put(this.parameter.getClass().getName(),paramFieldNum);
			}
			
			this.parameterObj = new Object[paramFieldNum.length];
			int k = 0;
			for(Short num : paramFieldNum){
				this.parameterObj[k++] = this.parameter.getFieldValue(num);
			}
		}
		return this.parameterObj;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#getMessageType()
	 */
	public int getMessageType() {
		if(this.messageType > 0){
			return this.messageType;
		}
		if(this.request != null){
			return this.request.getMessageType();
		}
		throw new DPSFRuntimeException("messageType is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getContext()
	 */
	@Override
	public Object getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setContext(java.lang.Object)
	 */
	@Override
	public void setContext(Object context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAttachment(String name, Object attachment) {
		this.attachments.put(name, attachment);
	}

	@Override
	public Object getAttachment(String name) {
		return this.attachments.get(name);
	}

}
