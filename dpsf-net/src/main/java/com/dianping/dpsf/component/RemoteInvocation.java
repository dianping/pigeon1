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

import com.dianping.dpsf.net.channel.Client;

import java.lang.reflect.Method;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class RemoteInvocation {

    private DPSFMetaData    metaData;
    private Method          method;
    private Object[]        arguments;

    private Client          remoteClient;
    private DPSFRequest     request;
    private Object          trackerContext;

    public RemoteInvocation(DPSFMetaData metaData, Method method, Object[] arguments) {
        this.metaData = metaData;
        this.method = method;
        this.arguments = arguments;
    }

    public DPSFMetaData getMetaData() {
        return metaData;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Client getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(Client remoteClient) {
        this.remoteClient = remoteClient;
    }

    public Object getTrackerContext() {
        return trackerContext;
    }

    public void setTrackerContext(Object trackerContext) {
        this.trackerContext = trackerContext;
        if (request != null) {
            request.setContext(trackerContext);
        }
    }

    public DPSFRequest getRequest() {
        return request;
    }

    public void setRequest(DPSFRequest request) {
        this.request = request;
    }
}
