package com.dianping.dpsf.invoke.filter.cluster;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.*;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.exception.NoConnectionException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用出错，则进行该Service剩余Provider的重试
 * Note：该策略仅适用于只读业务，有写操作的业务不建议使用，可能产生重复数据
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
        ClusterMeta.ClusterMetaItem clusterMetaItem = (ClusterMeta.ClusterMetaItem) invocationContext.getTransientContextValue(CONTEXT_CLUSTER_ITEM);
        List<Client> selectedClients = new ArrayList<Client>();
        Throwable lastError = null;
        Object retry = clusterMetaItem.getAttribute(Constants.CONFIG_CLUSTER_RETRY);
        int maxInvokeTimes = (retry != null ? (Integer) retry : Constants.DEFAULT_FAILOVER_RETRY) + 1;
        Object timeoutRetryObj = clusterMetaItem.getAttribute(Constants.CONFIG_CLUSTER_TIMEOUT_RETRY);
        boolean timeoutRetry = timeoutRetryObj != null ? (Boolean) timeoutRetryObj : Constants.DEFAULT_FAILOVER_TIMEOUT_RETRY;
        boolean nextInvokeErrorExit = false;
        int invokeTimes = 0;
        for (int index = 0; index < maxInvokeTimes; index++) {
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
                invokeTimes++;
                invocationContext.setRemoteClient(clientSelected);
                DPSFResponse response = handler.handle(invocationContext);
                if (lastError != null) {
                    logger.warn("Retry method[" + invocationContext.getMethod().getName() + "] on service[" + metaData.getServiceName()
                            + "] succeed after " + invokeTimes + " times, last failed invoke's error: " + lastError.getMessage(), lastError);
                }
                return response;
            } catch (Throwable e) {
                //若指定强制调用某机器，则不再重试
                if (ClientContext.getUseClientAddress() != null) {
                    throw e;
                }

                lastError = e;
                if (nextInvokeErrorExit) {
                    break;
                }
                if (e instanceof NetTimeoutException) {
                    if (!timeoutRetry) {
                        throw e;
                    } else {
                        nextInvokeErrorExit = true;     //超时最多重试一次
                    }
                }
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
