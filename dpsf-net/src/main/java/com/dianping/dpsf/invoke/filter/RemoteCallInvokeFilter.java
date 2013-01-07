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
import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.async.ServiceFutureImpl;
import com.dianping.dpsf.component.*;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.component.impl.ServiceWarpCallback;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.RemoteInvocationRepository;
import com.dianping.dpsf.net.channel.Client;

/**
 * 执行实际的Remote Call，包括Sync, Future，Callback，Oneway
 *
 * @author danson.liu
 */
public class RemoteCallInvokeFilter extends InvocationInvokeFilter {

    private RemoteInvocationRepository remoteInvocationRepository = RemoteInvocationRepository.INSTANCE;
    private static final DPSFResponse NO_RETURN_RESPONSE = new NoReturnResponse();

    public RemoteCallInvokeFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        Client remoteClient = invocationContext.getRemoteClient();
        DPSFRequest request = invocationContext.getRequest();
        DPSFMetaData metaData = invocationContext.getMetaData();
        String callMethod = metaData.getCallMethod();
        if (Constants.CALL_SYNC.equalsIgnoreCase(callMethod)) {
            CallbackFuture future = new CallbackFuture();
            sendRequest(remoteClient, request, future);
            return future.get(metaData.getTimeout());
        } else if (Constants.CALL_CALLBACK.equalsIgnoreCase(callMethod)) {
            sendRequest(remoteClient, request, new ServiceWarpCallback(metaData.getCallback()));
            return NO_RETURN_RESPONSE;
        } else if (Constants.CALL_FUTURE.equalsIgnoreCase(callMethod)) {
            CallbackFuture future = new ServiceFutureImpl(metaData.getTimeout());
            sendRequest(remoteClient, request, future);
            ServiceFutureFactory.setFuture((ServiceFuture) future);
            return NO_RETURN_RESPONSE;
        } else if (Constants.CALL_ONEWAY.equalsIgnoreCase(callMethod)) {
            sendRequest(remoteClient, request, null);
            return NO_RETURN_RESPONSE;
        }
        throw new DPSFException("Call method[" + callMethod + "] is not supported!");
    }

    private void sendRequest(Client remoteClient, DPSFRequest request, DPSFCallback callback) {
        if (request.getCallType() == Constants.CALLTYPE_REPLY) {
            RemoteInvocationRepository.RemoteInvocationBean invocationBean = new RemoteInvocationRepository.RemoteInvocationBean();
            invocationBean.request = request;
            invocationBean.callback = callback;
            callback.setRequest(request);
            callback.setClient(remoteClient);
            remoteInvocationRepository.put(request.getSequence(), invocationBean);
        }
        try {
            remoteClient.write(request, callback);
        } catch (RuntimeException e) {
            remoteInvocationRepository.remove(request.getSequence());
            throw new NetException("Send request to service provider failed.", e);
        }
    }

    public void setRemoteInvocationRepository(RemoteInvocationRepository remoteInvocationRepository) {
        this.remoteInvocationRepository = remoteInvocationRepository;
    }

    static class NoReturnResponse implements DPSFResponse {
        @Override
        public void setMessageType(int messageType) {
        }

        @Override
        public int getMessageType() throws ServiceException {
            return 0;
        }

        @Override
        public String getCause() {
            return null;
        }

        @Override
        public Object getReturn() {
            return null;
        }

        @Override
        public void setReturn(Object obj) {
        }

        @Override
        public byte getSerializ() {
            return 0;
        }

        @Override
        public void setSequence(long seq) {
        }

        @Override
        public long getSequence() {
            return 0;
        }

        @Override
        public Object getObject() throws NetException {
            return null;
        }

        @Override
        public Object getContext() {
            return null;
        }

        @Override
        public void setContext(Object context) {
        }
    }

}
