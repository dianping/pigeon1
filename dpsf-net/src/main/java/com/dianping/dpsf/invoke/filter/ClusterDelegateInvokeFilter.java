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

import com.dianping.dpsf.component.ClusterMeta;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.filter.cluster.ClusterInvokeFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Service Cluster实现的Delegate
 *
 * @author danson.liu
 */
public class ClusterDelegateInvokeFilter extends InvocationInvokeFilter {

    private static final Map<String, ClusterInvokeFilter> clusterFilters = new HashMap<String, ClusterInvokeFilter>();

    public ClusterDelegateInvokeFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        ClusterMeta clusterMeta = invocationContext.getMetaData().getClusterMeta();
        String cluster = clusterMeta.getName();
        ClusterInvokeFilter filter = clusterFilters.get(cluster);
        if (filter == null) {
            throw new DPSFException("Cluster[" + cluster + "] is not supported.");
        }
        try {
            return filter.invoke(handler, invocationContext);
        } catch (Exception e) {
            logger.error("Invoke remote call failed.", e);
            throw e;
        }
    }

    public static void registerCluster(ClusterInvokeFilter cluster) {
        clusterFilters.put(cluster.name(), cluster);
    }

}
