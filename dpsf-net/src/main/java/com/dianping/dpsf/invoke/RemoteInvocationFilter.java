package com.dianping.dpsf.invoke;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationContext;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public interface RemoteInvocationFilter<I extends InvocationContext> {

    DPSFResponse invoke(RemoteInvocationHandler handler, I invocationContext) throws Throwable;

}
