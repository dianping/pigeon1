/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.exception.ServiceException;

/**
 * <p>
 * Title: Request.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 上午12:51:15
 */
public interface DPSFRequest extends DPSFSerializable {

	void setCallType(int callType);

	int getCallType();

	int getTimeout();

	long getCreateMillisTime();

	void createMillisTime();

	String getServiceName();

	String getMethodName();

	String[] getParamClassName() throws ServiceException;

	Object[] getParameters() throws ServiceException;

	int getMessageType();

	/**
	 * support for request logic, not for transport
	 * 
	 * @param name
	 * @param attachment
	 */
	void setAttachment(String name, Object attachment);

	Object getAttachment(String name);

}
