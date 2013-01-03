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
package com.dianping.dpsf.invoke.cluster;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.RemoteInvocation;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.protocol.DefaultRequest;

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
    public DPSFResponse invoke(RemoteInvocationHandler handler, RemoteInvocation invocation) throws Throwable {
        DPSFMetaData metaData = invocation.getMetaData();
        DPSFRequest request = createRemoteCallRequest(invocation, metaData);
        Client remoteClient = clientManager.getClient(metaData.getServiceName(), metaData.getGroup(), request, null);
        invocation.setRemoteClient(remoteClient);
        return handler.handle(invocation);
    }

    @Override
    public String name() {
        return NAME;
    }

}
