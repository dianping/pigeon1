/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: DPSFResponse.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:54:49   
 */
public interface DPSFResponse extends DPSFSerializable{
	
	public void setMessageType(int messageType);
	
	public int getMessageType() throws ServiceException;
	
	public String getCause();
	
	public Object getReturn();

	public void setReturn(Object obj);
}
