package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.spi.InvocationProcessFilter;

public class PerformanceProcessFilter extends InvocationProcessFilter {

	@Override
	public DPSFResponse invoke(RemoteInvocationHandler handler,
			InvocationProcessContext invocationContext) throws Throwable {
		long start = System.currentTimeMillis();
    	try{
	    	return handler.handle(invocationContext);
        }finally{
        	invocationContext.putContextValue(Constants.CONTEXT_SERVER_COST, System.currentTimeMillis() - start);
        }
	}

}
