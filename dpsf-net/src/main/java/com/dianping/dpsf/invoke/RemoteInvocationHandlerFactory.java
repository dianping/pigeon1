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

	private static Map<InvocationInvokeFilter.InvokePhase, List<InvocationInvokeFilter>> internalInvokeFilters = new LinkedHashMap<InvocationInvokeFilter.InvokePhase, List<InvocationInvokeFilter>>();
	private static Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> internalProcessFilters = new LinkedHashMap<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>>();

	public static RemoteInvocationHandler createInvokeHandler(
			Map<InvocationInvokeFilter.InvokePhase, List<InvocationInvokeFilter>> filters) {
		return createHandler(internalInvokeFilters, filters);
	}

	public static RemoteInvocationHandler createProcessHandler(
			Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> filters) {
		return createHandler(internalProcessFilters, filters);
	}

	private static <K, V extends RemoteInvocationFilter<? extends InvocationContext>> RemoteInvocationHandler createHandler(
			Map<K, List<V>> internalFilters, Map<K, List<V>> filters) {
		Map<K, List<V>> mergedFilters = new LinkedHashMap<K, List<V>>(
				internalFilters);
		if (filters != null) {
			for (Map.Entry<K, List<V>> entry : filters.entrySet()) {
				mergedFilters.get(entry.getKey()).addAll(0, entry.getValue());
			}
		}
		RemoteInvocationHandler last = null;
		List<V> filterList = new ArrayList<V>();
		for (Map.Entry<K, List<V>> entry : mergedFilters.entrySet()) {
			filterList.addAll(entry.getValue());
		}
		for (int i = filterList.size() - 1; i >= 0; i--) {
			final V filter = filterList.get(i);
			final RemoteInvocationHandler next = last;
			last = new RemoteInvocationHandler() {
				@Override
				public DPSFResponse handle(
						InvocationContext invocationContext)
						throws Throwable {
					return filter.innerInvoke(next,invocationContext);
				}
			};
		}
		return last;
	}

	public static void registerInternalInvokeFilter(
			InvocationInvokeFilter.InvokePhase phase,
			InvocationInvokeFilter filter) {
		List<InvocationInvokeFilter> filters = internalInvokeFilters.get(phase);
		if (filters == null) {
			filters = new ArrayList<InvocationInvokeFilter>();
			internalInvokeFilters.put(phase, filters);
		}
		filters.add(filter);
	}

	public static void registerInternalProcessFilter(
			InvocationProcessFilter.ProcessPhase phase,
			InvocationProcessFilter filter) {
		List<InvocationProcessFilter> filters = internalProcessFilters
				.get(phase);
		if (filters == null) {
			filters = new ArrayList<InvocationProcessFilter>();
			internalProcessFilters.put(phase, filters);
		}
		filters.add(filter);
	}
}
