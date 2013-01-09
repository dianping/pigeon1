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
package com.dianping.dpsf.invoke;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationContext;
import com.dianping.dpsf.invoke.filter.InvocationInvokeFilter;
import com.dianping.dpsf.process.filter.InvocationProcessFilter;

import java.util.*;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class RemoteInvocationHandlerFactory {

    private static List<InvocationInvokeFilter>     internalInvokeFilters       =   new ArrayList<InvocationInvokeFilter>();
    private static List<InvocationProcessFilter>    internalProcessFilters      =   new ArrayList<InvocationProcessFilter>();

    public static RemoteInvocationHandler createInvokeHandler(List<? extends RemoteInvocationFilter> filters) {
        return createHandler(internalInvokeFilters, filters);
    }

    public static RemoteInvocationHandler createProcessHandler(List<? extends RemoteInvocationFilter> filters) {
        return createHandler(internalProcessFilters, filters);
    }

    private static RemoteInvocationHandler createHandler(List<? extends RemoteInvocationFilter> internalFilters, List<? extends RemoteInvocationFilter> filters) {
        Map<Integer, RemoteInvocationFilter> filterMap = new HashMap<Integer, RemoteInvocationFilter>();
        for (RemoteInvocationFilter filter : internalFilters) {
            filterMap.put(filter.order(), filter);
        }
        if (filters != null && !filters.isEmpty()) {
            for (RemoteInvocationFilter filter : filters) {
                filterMap.put(filter.order(), filter);
            }
        }
        List<RemoteInvocationFilter> filterList = new ArrayList<RemoteInvocationFilter>(filterMap.values());
        Collections.sort(filterList);
        RemoteInvocationHandler last = null;
        for (int i = filterList.size() - 1; i >= 0; i--) {
            final RemoteInvocationFilter filter = filterList.get(i);
            final RemoteInvocationHandler next = last;
            last = new RemoteInvocationHandler() {
                @Override
                public DPSFResponse handle(InvocationContext invocationContext) throws Throwable {
                    return filter.invoke(next, invocationContext);
                }
            };
        }
        return last;
    }

    public static void registerInternalInvokeFilter(InvocationInvokeFilter filter) {
        internalInvokeFilters.add(filter);
    }

    public static void registerInternalProcessFilter(InvocationProcessFilter filter) {
        internalProcessFilters.add(filter);
    }
}
