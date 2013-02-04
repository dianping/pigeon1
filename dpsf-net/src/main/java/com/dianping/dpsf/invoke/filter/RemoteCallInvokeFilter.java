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
import java.util.Map;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.async.ServiceFutureImpl;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.component.impl.ServiceWarpCallback;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.RemoteInvocationRepository;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.spi.InvocationInvokeFilter;

/**
 * 执行实际的Remote Call，包括Sync, Future，Callback，Oneway
 *
 * @author danson.liu
 */
public class RemoteCallInvokeFilter extends InvocationInvokeFilter {

    private RemoteInvocationRepository  remoteInvocationRepository;
    private static final DPSFResponse   NO_RETURN_RESPONSE          = new NoReturnResponse();
    
    public RemoteCallInvokeFilter(RemoteInvocationRepository remoteInvocationRepository) {
        this.remoteInvocationRepository = remoteInvocationRepository;
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        Client client = invocationContext.getClient();
        DPSFRequest request = invocationContext.getRequest();
        DPSFMetaData metaData = invocationContext.getMetaData();
        String callMethod = metaData.getCallMethod();
        transferContextValueToRequest(invocationContext, request);
        if (Constants.CALL_SYNC.equalsIgnoreCase(callMethod)) {
            CallbackFuture future = new CallbackFuture();
            sendRequest(client, request, future);
            return future.get(metaData.getTimeout());
        } else if (Constants.CALL_CALLBACK.equalsIgnoreCase(callMethod)) {
            sendRequest(client, request, new ServiceWarpCallback(metaData.getCallback()));
            return NO_RETURN_RESPONSE;
        } else if (Constants.CALL_FUTURE.equalsIgnoreCase(callMethod)) {
            CallbackFuture future = new ServiceFutureImpl(metaData.getTimeout());
            sendRequest(client, request, future);
            ServiceFutureFactory.setFuture((ServiceFuture) future);
            return NO_RETURN_RESPONSE;
        } else if (Constants.CALL_ONEWAY.equalsIgnoreCase(callMethod)) {
            sendRequest(client, request, null);
            return NO_RETURN_RESPONSE;
        }
        throw new DPSFException("Call method[" + callMethod + "] is not supported!");
    }

    private void sendRequest(Client client, DPSFRequest request, DPSFCallback callback) {
        if (request.getCallType() == Constants.CALLTYPE_REPLY) {
            RemoteInvocationRepository.RemoteInvocationBean invocationBean = new RemoteInvocationRepository.RemoteInvocationBean();
            invocationBean.request = request;
            invocationBean.callback = callback;
            callback.setRequest(request);
            callback.setClient(client);
            remoteInvocationRepository.put(request.getSequence(), invocationBean);
        }
        try {
        	client.write(request, callback);
        } catch (RuntimeException e) {
            remoteInvocationRepository.remove(request.getSequence());
            throw new NetException("Send request to service provider failed.", e);
        }
    }

    private void transferContextValueToRequest(final InvocationInvokeContext invocationContext, final DPSFRequest request) {
        DPSFMetaData metaData = invocationContext.getMetaData();
        Client client = invocationContext.getClient();
        Object contextHolder = ContextUtil.createContext(metaData.getServiceName(), invocationContext.getMethod().getName(),
        		client.getHost(), client.getPort());
        Map<String,Serializable> contextValues = invocationContext.getContextValues();
        if (contextValues != null) {
            for (Map.Entry<String, Serializable> entry : contextValues.entrySet()) {
                ContextUtil.putContextValue(contextHolder, entry.getKey(), entry.getValue());
            }
        }
        request.setContext(contextHolder);
    }

    static class NoReturnResponse implements DPSFResponse {
        /**
		 * 
		 */
		private static final long serialVersionUID = 4348389641787057819L;

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
