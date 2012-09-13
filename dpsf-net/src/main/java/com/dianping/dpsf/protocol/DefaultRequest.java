/**
 * 
 */
package com.dianping.dpsf.protocol;

import java.util.HashMap;
import java.util.Map;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: DefaultRequest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-7 下午10:13:26   
 */
public class DefaultRequest implements DPSFRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 652592942114047764L;
	
	private byte serialize;
	
	private long seq;
	
	private int callType;
	
	private int timeout;
	
	private long createMillisTime;
	
	private String serviceName;
	
	private String methodName;
	
	private Object[] parameters;
	
	private int messageType;
	
	private Object context;
	
	private transient Class<?>[] parameterClasses;
	
	private transient Map<String, Object> attachments = new HashMap<String, Object>();
	
	public DefaultRequest(String serviceName,String methodName,
			Object[] parameters,byte serialize,int messageType,int timeout,Class<?>[] parameterClasses){
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.parameters = parameters;
		this.serialize = serialize;
		this.messageType = messageType;
		this.timeout = timeout;
		this.parameterClasses = parameterClasses;
	}
	
	/**
	 * @return the parameterClasses
	 */
	public Class<?>[] getParameterClasses() {
		return parameterClasses;
	}

	/**
	 * @param parameterClasses the parameterClasses to set
	 */
	public void setParameterClasses(Class<?>[] parameterClasses) {
		this.parameterClasses = parameterClasses;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSerializ()
	 */
	public byte getSerializ() {
		return this.serialize;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#setSequence(long)
	 */
	public void setSequence(long seq) {
		this.seq = seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSequence()
	 */
	public long getSequence() {
		return this.seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getObject()
	 */
	public Object getObject() {
		return this;
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
		return this.callType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getTimeout()
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getCreateMillisTime()
	 */
	public long getCreateMillisTime() {
		return this.createMillisTime;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getServiceName()
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getMethodName()
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getParamClassName()
	 */
	public String[] getParamClassName() {
		if(this.parameters == null){
			return new String[0];
		}
		String[] paramClassNames = new String[this.parameters.length];
		
		int k = 0;
		for(Object parameter : this.parameters){
			if(parameter == null){
				paramClassNames[k] = Constants.TRANSFER_NULL;
			}else{
				paramClassNames[k] = this.parameters[k].getClass().getName();
			}
			k++;
		}
		return paramClassNames;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getParameters()
	 */
	public Object[] getParameters() throws ServiceException {
		return this.parameters;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFRequest#getMessageType()
	 */
	public int getMessageType() {
		return this.messageType;
	}


	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getContext()
	 */
	@Override
	public Object getContext() {
		return this.context;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setContext(java.lang.Object)
	 */
	@Override
	public void setContext(Object context) {
		this.context = context;
	}

	@Override
	public void setAttachment(String name, Object attachment) {
		attachments.put(name, attachment);
	}

	@Override
	public Object getAttachment(String name) {
		return attachments.get(name);
	}

	@Override
	public void setCreateMillisTime(long createTime) {
		this.createMillisTime = createTime;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
