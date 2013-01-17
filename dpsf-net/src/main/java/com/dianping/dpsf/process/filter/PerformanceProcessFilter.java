package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;

public class PerformanceProcessFilter extends InvocationProcessFilter{

	public PerformanceProcessFilter(int order) {
		super(order);
	}

	@Override
	public DPSFResponse invoke(RemoteInvocationHandler handler,
			InvocationProcessContext invocationContext) throws Throwable {
		
		
		return null;
	}

}
