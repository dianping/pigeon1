package com.dianping.dpsf.component.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationContext;

/**
 * @author xiangbin.miao
 *
 */
public abstract class AbstractInvocationContext implements InvocationContext{
	
	protected DPSFRequest                      request;
	protected DPSFResponse                     response;
	private Map<String, Serializable>          contextValues;
    //不会通过request传递到服务端，可用于filter之间传递参数
    private Map<String, Object>                transientContextValues;
    
    public AbstractInvocationContext(DPSFRequest request){
    	this.request = request;
    }

	@Override
	public DPSFRequest getRequest() {
		return request;
	}
	
	public void setRequest(DPSFRequest request) {
		this.request = request;
	}

	@Override
	public DPSFResponse getResponse() {
		return response;
	}
	
	public void setResponse(DPSFResponse response){
		this.response = response;
	}

	@Override
	public void putContextValue(String key, Serializable value) {
		if (contextValues == null) {
			contextValues = new HashMap<String, Serializable>();
		}
		contextValues.put(key, value);
	}

	@Override
	public Serializable getContextValue(String key) {
		if (contextValues == null) {
            return null;
        }
        return contextValues.get(key);
	}

	@Override
	public Map<String, Serializable> getContextValues() {
		return contextValues;
	}

	@Override
	public void putTransientContextValue(String key, Object value) {
		if (transientContextValues == null) {
            transientContextValues = new HashMap<String, Object>();
        }
        transientContextValues.put(key, value);
	}

	@Override
	public Object getTransientContextValue(String key) {
		if (transientContextValues == null) {
            return null;
        }
        return transientContextValues.get(key);
	}
	
	public void removeTransientContextValue(String key) {
        if (transientContextValues != null) {
            transientContextValues.remove(key);
        }
    }

}
