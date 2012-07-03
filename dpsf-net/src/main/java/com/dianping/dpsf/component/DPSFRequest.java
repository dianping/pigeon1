/**
 * 
 */
package com.dianping.dpsf.component;

import java.util.Map;

import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: Request.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:51:15   
 */
public interface DPSFRequest extends DPSFSerializable{
	
	public void setCallType(int callType);
	
	public int getCallType();
	
	public int getTimeout();
	
	public long getCreateMillisTime();
	
	public void createMillisTime();
	
	public String getServiceName();
	
	public String getMethodName();
	
	public String[] getParamClassName() throws ServiceException ;
	
	public Object[] getParameters()throws ServiceException;
	
	public int getMessageType();
	
	/**
	 * support for request logic, not for transport
	 * @param name
	 * @param attachment
	 */
	public void setAttachment(String name, Object attachment);
	
	public Object getAttachment(String name);

}
