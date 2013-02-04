/**
 * File Created at 12-12-29
 *
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.component;

import java.io.Serializable;
import java.util.Map;


/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public interface InvocationContext {

    public DPSFRequest                 getRequest();
    
    public void                        setRequest(DPSFRequest request);

	public DPSFResponse                getResponse();
	
	public void                        setResponse(DPSFResponse response);
	
	public void                        putContextValue(String key, Serializable value);
	
	public Serializable                getContextValue(String key);
	
	public Map<String, Serializable>   getContextValues();

    public void                        putTransientContextValue(String key, Object value);

    public Object                      getTransientContextValue(String key);
}
