/**
 * File Created at 12-12-31
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
package com.dianping.dpsf.invoke.filter.cluster;

import com.dianping.dpsf.component.*;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManager;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class FailfastClusterInvocationFilter extends ClusterInvocationFilter {

    public static final String NAME = "fail-fast";

    public FailfastClusterInvocationFilter(ClientManager clientManager) {
        super(clientManager);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        DPSFMetaData metaData = invocationContext.getMetaData();
        DPSFRequest request = createRemoteCallRequest(invocationContext, metaData);
        Client remoteClient = clientManager.getClient(metaData.getServiceName(), metaData.getGroup(), request, null);
        invocationContext.setRemoteClient(remoteClient);
        return handler.handle(invocationContext);
    }

    @Override
    public String name() {
        return NAME;
    }

}
