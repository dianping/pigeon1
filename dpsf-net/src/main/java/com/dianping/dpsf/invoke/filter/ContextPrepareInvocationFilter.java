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

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.*;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ContextPrepareInvocationFilter extends RemoteInvocationFilter<InvocationInvokeContext> {

    public ContextPrepareInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        initRequest(invocationContext.getRequest());
        DPSFMetaData metaData = invocationContext.getMetaData();
        Client remoteClient = invocationContext.getRemoteClient();
        Object trackerContext = ContextUtil.createContext(metaData.getServiceName(), invocationContext.getMethod().getName(),
                remoteClient.getHost(), remoteClient.getPort());
        invocationContext.setTrackerContext(trackerContext);
        ClientContext.setUsedClientAddress(remoteClient.getAddress());
        return handler.handle(invocationContext);
    }

    private void initRequest(DPSFRequest request) {
        Object createTime = ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME);
        if (createTime != null) {
            request.setCreateMillisTime(Long.parseLong(String.valueOf(createTime)));
        } else {
            request.setCreateMillisTime(System.currentTimeMillis());
        }
        Object timeout = ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT);
        if (timeout != null) {
            int timeout_ = Integer.parseInt(String.valueOf(timeout));
            if (timeout_ < request.getTimeout()) {
                request.setTimeout(timeout_);
            }
        }
    }

}
