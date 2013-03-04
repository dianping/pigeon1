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

import java.util.Map;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.lifecycle.DPSFRequestLifeCycle;
import com.dianping.dpsf.spi.InvocationInvokeFilter;

/**
 * @author xiangbin.miao
 *
 */
public class PerformanceInvokeFilter extends InvocationInvokeFilter {


    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {

    	long start = System.currentTimeMillis();
    	
        DPSFResponse result = handler.handle(invocationContext);
        
        CallbackFuture future = (CallbackFuture)invocationContext.getTransientContextValue(Constants.CONTEXT_FUTURE);
        if(future != null){
        	CallStatLifeCycle lifeCycle = new CallStatLifeCycle(start,invocationContext);
        	future.addRequestLifeCycle(lifeCycle);
        }
        if(Constants.CALL_SYNC.equalsIgnoreCase(invocationContext.getMetaData().getCallMethod())){
        	
        	Object ctx = result.getContext();
        	Integer cost = null;
        	if(ctx instanceof Map){
        		Map ctx_ = (Map)ctx;
        		cost = Integer.parseInt(String.valueOf(((Map)ctx).get(Constants.CONTEXT_SERVER_COST)));
        	}else{
        		cost = ContextUtil.getContextValue(ctx, Constants.CONTEXT_SERVER_COST);
        	}
        	
        	System.out.println(System.currentTimeMillis() - start + ">>>>>>>>>>>>>>>>>>1");
			System.out.println(cost + ">>>>>>>>>>>>>>>>>>1");
        }
        return result;
    }
    
    private class CallStatLifeCycle implements DPSFRequestLifeCycle{
    	
    	private long startTimestamp;
    	private InvocationInvokeContext invocationContext;
    	
    	public CallStatLifeCycle(long startTimestamp,InvocationInvokeContext invocationContext){
    		this.startTimestamp = startTimestamp;
    		this.invocationContext = invocationContext;
    	}

		@Override
		public void lifeCycle(DPSFEvent event,Object param) {
			if(event == DPSFEvent.ResponseReturn || event == DPSFEvent.GetTimeout){
				
				DPSFResponse response = (DPSFResponse)param;
				
				Integer cost = null;
				if(response != null){
					Object ctx = response.getContext();
					if(ctx instanceof Map){
		        		Map ctx_ = (Map)ctx;
		        		cost = Integer.parseInt(String.valueOf(((Map)ctx).get(Constants.CONTEXT_SERVER_COST)));
		        	}else{
		        		cost = ContextUtil.getContextValue(ctx, Constants.CONTEXT_SERVER_COST);
		        	}
				}
				
				
				System.out.println(System.currentTimeMillis() - startTimestamp + ">>>>>>>>>>>>>>>>>>1");
				System.out.println(cost + ">>>>>>>>>>>>>>>>>>1");
			}
		}
    	
    }

}
