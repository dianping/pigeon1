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
import com.dianping.dpsf.component.RemoteInvocation;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.protocol.DefaultRequest;

import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public abstract class ClusterInvocationFilter extends RemoteInvocationFilter {

    protected ClientManager clientManager;

    private static AtomicLong requestSequenceMaker = new AtomicLong();

    protected ClusterInvocationFilter(ClientManager clientManager) {
        super(0);
        this.clientManager = clientManager;
    }

    public abstract String name();

    protected DefaultRequest createRemoteCallRequest(RemoteInvocation invocation, DPSFMetaData metaData) {
        DefaultRequest request = new DefaultRequest(metaData.getServiceName(), invocation.getMethod().getName(),
                invocation.getArguments(), metaData.getSerialize(), Constants.MESSAGE_TYPE_SERVICE, metaData.getTimeout(),
                invocation.getMethod().getParameterTypes());
        request.setSequence(requestSequenceMaker.incrementAndGet() * -1);   //(* -1): in order to distinguish from old logic
        request.setAttachment(Constants.REQ_ATTACH_WRITE_BUFF_LIMIT, metaData.isWriteBufferLimit());
        if (Constants.CALL_ONEWAY.equalsIgnoreCase(metaData.getCallMethod())) {
            request.setCallType(Constants.CALLTYPE_NOREPLY);
        } else {
            request.setCallType(Constants.CALLTYPE_REPLY);
        }
        invocation.setRequest(request);
        return request;
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
}
