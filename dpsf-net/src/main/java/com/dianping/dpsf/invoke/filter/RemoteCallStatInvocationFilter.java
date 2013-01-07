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
import com.dianping.dpsf.component.*;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.stat.ServiceStat;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class RemoteCallStatInvocationFilter extends RemoteInvocationFilter<InvocationInvokeContext> {

    private ServiceStat clientServiceStat = ServiceStat.getClientServiceStat();

    public RemoteCallStatInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        DPSFRequest request = invocationContext.getRequest();
        Client remoteClient = invocationContext.getRemoteClient();
        DPSFMetaData metaData = invocationContext.getMetaData();

        RpcStatsPool.flowIn(request, remoteClient.getAddress());
        try {
            DPSFResponse result = handler.handle(invocationContext);
            clientServiceStat.countService(request.getServiceName());
            return result;
        } catch (Exception e) {
            if (Constants.CALL_SYNC.equalsIgnoreCase(metaData.getCallMethod())
                    && e instanceof NetTimeoutException) {
                clientServiceStat.failCountService(request.getServiceName());
            }
            RpcStatsPool.flowOut(request, remoteClient.getAddress());
            throw e;
        }
    }

    public void setClientServiceStat(ServiceStat clientServiceStat) {
        this.clientServiceStat = clientServiceStat;
    }

}
