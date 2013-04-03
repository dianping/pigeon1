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
package com.dianping.dpsf.invoke.filter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.spi.InvocationInvokeFilter;

/**
 * 关于Service调用上下文的设置
 * 
 * @author danson.liu
 */
public class ContextPrepareInvokeFilter extends InvocationInvokeFilter {

	@Override
	public DPSFResponse invoke(RemoteInvocationHandler handler,
			InvocationInvokeContext invocationContext) throws Throwable {
		initRequest(invocationContext.getRequest());
		Client client = invocationContext.getClient();
		ClientContext.setUsedClientAddress(client.getAddress());
		
		transferContextValueToRequest(invocationContext, invocationContext.getRequest());
		return handler.handle(invocationContext);
	}

	  //初始化Request的createTime和timeout，以便统一这两个值
    private void initRequest(DPSFRequest request) {
        Object createTime = ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME);
        Object timeout = ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT);

        if (createTime != null) {

            long createTime_ = Long.parseLong(String.valueOf(createTime));
            int timeout_ = Integer.parseInt(String.valueOf(timeout));

            Object firstFlag = ContextUtil.getLocalContext(Constants.REQUEST_FIRST_FLAG);
            if (firstFlag == null) {
                ContextUtil.putLocalContext(Constants.REQUEST_FIRST_FLAG, true);
                request.setCreateMillisTime(createTime_);
            } else {
                long now = System.currentTimeMillis();
                timeout_ = timeout_ - (int) (now - createTime_);
                if (timeout_ <= 0) {
                    throw new NetTimeoutException("method has been timeout for first call (startTime:" + new Date(createTime_) + " timeout:" + timeout_ + ")");
                }
                request.setCreateMillisTime(now);
            }
            if (timeout_ < request.getTimeout()) {
                request.setTimeout(timeout_);
            }
        } else {
            request.setCreateMillisTime(System.currentTimeMillis());
        }
    }
	
	 private void transferContextValueToRequest(final InvocationInvokeContext invocationContext, final DPSFRequest request) {
	        DPSFMetaData metaData = invocationContext.getMetaData();
	        Client client = invocationContext.getClient();
	        Object contextHolder = ContextUtil.createContext(metaData.getServiceName(), invocationContext.getMethod().getName(),
	        		client.getHost(), client.getPort());
	        
	        Map<String,Serializable> contextValues = invocationContext.getContextValues();
	        if(contextHolder != null){
	        	
		        if (contextValues != null) {
		            for (Map.Entry<String, Serializable> entry : contextValues.entrySet()) {
		                ContextUtil.putContextValue(contextHolder, entry.getKey(), entry.getValue());
		            }
		        }
	        }else{
//	        	contextHolder = contextValues;
	        }
	        
	        request.setContext(contextHolder);
	    }

}
