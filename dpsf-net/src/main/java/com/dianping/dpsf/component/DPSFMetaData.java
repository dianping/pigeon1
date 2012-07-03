/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.async.ServiceCallback;

/**    
 * <p>    
 * Title: DPSFMetaData.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-18 下午11:53:41   
 */
public class DPSFMetaData {

	private String serviceName;
	
	private String callMethod;
	
	private byte serialize;
	
	private int timeout;
	
	private ServiceCallback callback;
	
	private String group;

	private boolean writeBufferLimit;
	
	public DPSFMetaData(String serviceName,int timeout,String group, boolean writeBufferLimit){
		this.serviceName = serviceName;
		this.timeout = timeout;
		this.group = group;
		this.writeBufferLimit = writeBufferLimit;
	}
	
	public DPSFMetaData(String serviceName,int timeout,String callMethod,String serialize,ServiceCallback callback,String group, 
		boolean writeBufferLimit){
		
		this.serviceName = serviceName;
		this.timeout = timeout;
		this.callMethod = callMethod;
		this.callback = callback;
		this.group = group;
		this.writeBufferLimit = writeBufferLimit;
		if(Constants.SERIALIZE_PB.equalsIgnoreCase(serialize)){
			this.serialize = Constants.SERILIZABLE_PB;
		}else if(Constants.SERIALIZE_JAVA.equalsIgnoreCase(serialize)){
			this.serialize = Constants.SERILIZABLE_JAVA;
		}else if(Constants.SERIALIZE_HESSIAN.equalsIgnoreCase(serialize)){
			this.serialize = Constants.SERILIZABLE_HESSIAN;
		}else if(Constants.SERIALIZE_THRIFT.equalsIgnoreCase(serialize)){
			this.serialize = Constants.SERILIZABLE_THRIFT;
		}
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the callMethod
	 */
	public String getCallMethod() {
		return callMethod;
	}

	/**
	 * @param callMethod the callMethod to set
	 */
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}

	/**
	 * @return the serialize
	 */
	public byte getSerialize() {
		return serialize;
	}

	/**
	 * @param serialize the serialize to set
	 */
	public void setSerialize(byte serialize) {
		this.serialize = serialize;
	}

	/**
	 * @return the callback
	 */
	public ServiceCallback getCallback() {
		return callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(ServiceCallback callback) {
		this.callback = callback;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isWriteBufferLimit() {
		return writeBufferLimit;
	}
	
}
