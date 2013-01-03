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
package com.dianping.dpsf.invoke;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.RemoteInvocation;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.stat.ServiceStat;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class StatTrackInvocationFilter extends RemoteInvocationFilter {

    private ServiceStat clientServiceStat = ServiceStat.getClientServiceStat();

    public StatTrackInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, RemoteInvocation invocation) throws Throwable {
        DPSFRequest request = invocation.getRequest();
        Client remoteClient = invocation.getRemoteClient();
        DPSFMetaData metaData = invocation.getMetaData();

        RpcStatsPool.flowIn(request, remoteClient.getAddress());
        try {
            DPSFResponse result = handler.handle(invocation);
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
