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
	
	/**
	 * 在整个调用流程中公用，会随着调用被传播，如被修改，会随着调用流被同步
	 * @param key
	 * @param value
	 */
	public void                        putContextValue(String key, Serializable value);
	
	/**
	 * 在整个调用流程中公用，会随着调用被传播，如被修改，会随着调用流被同步
	 * @param key
	 * @return
	 */
	public Serializable                getContextValue(String key);
	
	/**
	 * 在整个调用流程中公用，会随着调用被传播，如被修改，会随着调用流被同步
	 * @return
	 */
	public Map<String, Serializable>   getContextValues();

    /**
     * 仅在当前进程生效，不垮进程共享
     * @param key
     * @param value
     */
    public void                        putTransientContextValue(String key, Object value);

    /**
     * 仅在当前进程生效，不垮进程共享
     * @param key
     * @return
     */
    public Object                      getTransientContextValue(String key);
}
