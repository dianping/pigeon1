package com.dianping.dpsf.invoke.filter.cluster;

import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NoConnectionException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用出错，则进行该Service剩余Provider的重试
 *
 * Date: 13-1-10
 * Time: 上午10:48
 */
public class FailoverClusterInvokeFilter extends ClusterInvokeFilter {

    public static final String NAME = "fail-over";

    public FailoverClusterInvokeFilter(ClientManager clientManager) {
        super(clientManager);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        DPSFMetaData metaData = invocationContext.getMetaData();
        List<Client> selectedClients = new ArrayList<Client>();
        Throwable lastError = null;
        int invokeTimes = 2;
        for (int index = 0; index < invokeTimes; index++) {
            DPSFRequest request = createRemoteCallRequest(invocationContext, metaData);
            Client clientSelected = null;
            try {
                clientSelected = clientManager.getClient(metaData.getServiceName(), metaData.getGroup(), request, selectedClients);
            } catch (NoConnectionException e) {
                if (index > 0) {
                    throw new NoConnectionException("After " + (index + 1) + " times invocation: " + e.getMessage());
                }
            }
            selectedClients.add(clientSelected);
            try {
                invocationContext.setRemoteClient(clientSelected);
                DPSFResponse response = handler.handle(invocationContext);
                if (lastError != null) {
                    logger.warn("Retry method[" + invocationContext.getMethod().getName() + "] on service[" + metaData.getServiceName()
                            + "] succeed after " + (index + 1) + " times, last failed invoke's error: " + lastError.getMessage(), lastError);
                }
                return response;
            } catch (Throwable e) {
                lastError = e;
            }
        }
        throw new DPSFException("Invoke method[" + invocationContext.getMethod().getName() + "] on service[" + metaData.getServiceName()
                + "] failed with " + invokeTimes + " times, last error: " + (lastError != null ? lastError.getMessage() : ""),
                lastError != null && lastError.getCause() != null ? lastError.getCause() : lastError);
    }

    @Override
    public String name() {
        return NAME;
    }

}
