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
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.filter.cluster.ClusterInvocationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ClusterDelegateInvocationFilter extends RemoteInvocationFilter<InvocationInvokeContext> {

    private static final Map<String, ClusterInvocationFilter> clusterFilters = new HashMap<String, ClusterInvocationFilter>();

    public ClusterDelegateInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        ClusterMeta clusterMeta = invocationContext.getMetaData().getClusterMeta();
        String cluster = clusterMeta.getName();
        ClusterInvocationFilter filter = clusterFilters.get(cluster);
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

    public static void registerCluster(ClusterInvocationFilter cluster) {
        clusterFilters.put(cluster.name(), cluster);
    }

}
