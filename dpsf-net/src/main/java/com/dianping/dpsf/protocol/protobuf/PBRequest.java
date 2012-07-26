/**
 * 
 */
package com.dianping.dpsf.protocol.protobuf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.Version;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.DPSFRuntimeException;
import com.dianping.dpsf.exception.NoSupportedException;
import com.dianping.dpsf.exception.ServiceException;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

/**    
 * <p>    
 * Title: PBRequest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午01:58:08   
 */
public class PBRequest implements DPSFRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8840220134866666929L;
	
	private DPSFProtos.Request request;
	
	private MethodDescriptor method;
	
	private Message parameterReq;
	
	private String serviceName;
	
	private int callType;
	
	private int messageType;
	
	private int timeout = -1;
	
	private long transferId; 
	
	private long createTime;
	
	private String paramClassName;
	
	private transient Map<String, Object> attachments = new HashMap<String, Object>();
	
	private final static RpcController controller = new RpcController(){

		public void reset() {
			throw new NoSupportedException();
		}

		public boolean failed() {
			throw new NoSupportedException();
		}

		public String errorText() {
			throw new NoSupportedException();
		}

		public void startCancel() {
			throw new NoSupportedException();
		}

		public void setFailed(String reason) {
			throw new NoSupportedException();
		}

		public boolean isCanceled() {
			throw new NoSupportedException();
		}

		public void notifyOnCancel(RpcCallback<Object> callback) {
			throw new NoSupportedException();
		}
		
	};
	
	private static Map<String,GeneratedMessage> initParameters 
		= new ConcurrentHashMap<String,GeneratedMessage>();
	
	//Server
	public PBRequest(MessageLite request){
		this.request = (DPSFProtos.Request)request;
	}
	
	//Client
	public PBRequest(MethodDescriptor method,Message parameterReq,String serviceName, int messageType, int timeout) {
		this.method = method;
		this.parameterReq = parameterReq;
		this.paramClassName = parameterReq.getClass().getName();
		this.serviceName = serviceName;
		this.messageType = messageType;
		this.timeout = timeout;
	}
	
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getSerializ()
	 */
	public byte getSerializ() {
		return Constants.SERILIZABLE_PB;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getObject()
	 */
	public Object getObject() {
		if(this.request == null){
			this.request = createRequest();
		}
		return this.request;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#setSequence(long)
	 */
	public void setSequence(long seq) {
		this.transferId = seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSequence()
	 */
	public long getSequence() {
		if(this.transferId > 0){
			return this.transferId;
		}
		if(this.request != null){
			return this.request.getTransferId();
		}
		throw new DPSFRuntimeException("transferId is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#setCallType(int)
	 */
	public void setCallType(int callType) {
		this.callType = callType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getCallType()
	 */
	public int getCallType() {
		if(this.callType > 0){
			return this.callType;
		}
		if(this.request != null){
			return this.request.getCallType();
		}
		return this.callType;
	}
	
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getTimeout()
	 */
	public int getTimeout() {
		return this.timeout > 0 ? this.timeout : this.request.getTimeout();
	}
	

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getCreateMillisTime()
	 */
	public long getCreateMillisTime() {
		return this.createTime;
	}
	
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	private DPSFProtos.Request createRequest(){
		DPSFProtos.Request sysRequest = DPSFProtos.Request.newBuilder().setMessageType(Constants.MESSAGE_TYPE_SERVICE)
		.setCallType(this.callType).setTransferId(this.transferId).setRpcVersion(Version.getCurrentVersion())
		.setServiceName(this.serviceName).setParamClassName(this.paramClassName)
		.setMethodName(this.method.getName()).setParameters(parameterReq.toByteString()).build();
		
		return sysRequest;
	}
	
	public int hashCode(){
		return new Long(this.transferId).hashCode();
	}
	
	public String getServiceName(){
		if(this.serviceName != null){
			return this.serviceName;
		}
		if(this.request != null){
			return this.request.getServiceName();
		}
		throw new DPSFRuntimeException("serviceName is null");
	}
	
	public String getMethodName(){
		if(this.method != null){
			return this.method.getName();
		}
		if(this.request != null){
			return this.request.getMethodName();
		}
		throw new DPSFRuntimeException("methodName is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getParameterType()
	 */
	public String[] getParamClassName() {
		if(this.paramClassName != null){
			return new String[]{RpcController.class.getName(),this.paramClassName};
		}
		if(this.request == null){
			throw new DPSFRuntimeException("subRequest is null");
		}
		return new String[]{RpcController.class.getName(),this.request.getParamClassName()};
	}
	
	private GeneratedMessage getParamMessage() throws ServiceException{
		if(this.paramClassName == null){
			if(this.request == null){
				throw new DPSFRuntimeException("subRequest is null");
			}
			this.paramClassName = this.request.getParamClassName();
		}
		GeneratedMessage gml = PBRequest.initParameters.get(this.paramClassName);
		if(gml == null){
			Class<?> paramClass = null;
			try {
				paramClass = Class.forName(this.paramClassName);
			} catch (ClassNotFoundException e) {
				throw new ServiceException(e.getMessage(),e);
			}
			Field f = null;
			try {
				f = paramClass.getDeclaredField("defaultInstance");
			} catch (SecurityException e) {
				throw new ServiceException(e.getMessage(),e);
			} catch (NoSuchFieldException e) {
				throw new ServiceException(e.getMessage(),e);
			}
			f.setAccessible(true);
			try {
				gml = (GeneratedMessage)f.get(null);
			} catch (IllegalArgumentException e) {
				throw new ServiceException(e.getMessage(),e);
			} catch (IllegalAccessException e) {
				throw new ServiceException(e.getMessage(),e);
			}
			PBRequest.initParameters.put(this.paramClassName, gml);
		}
		return gml;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getParameters()
	 */
	public Object[] getParameters() throws ServiceException {
		if(this.parameterReq != null){
			return new Object[]{this.parameterReq};
		}
		if(this.request == null){
			throw new DPSFRuntimeException("subRequest is null");
		}
		GeneratedMessage gml = getParamMessage();
		MessageLite ml = null;
		try {
			ml = gml.newBuilderForType().mergeFrom(this.request.getParameters()).build();
		} catch (InvalidProtocolBufferException e) {
			throw new ServiceException(e.getMessage(),e);
		}
		return new Object[]{controller,ml};
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getMessageType()
	 */
	public int getMessageType() {
		if(this.messageType != 0){
			return this.messageType;
		}
		if(this.request == null){
			throw new DPSFRuntimeException("subRequest is null");
		}
		return this.request.getMessageType();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFRequest#createMillisTime()
	 */
	public void createMillisTime() {
		this.createTime = System.currentTimeMillis();
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
