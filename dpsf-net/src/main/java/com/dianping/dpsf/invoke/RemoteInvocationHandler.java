package com.dianping.dpsf.invoke;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationContext;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public interface RemoteInvocationHandler {

    DPSFResponse handle(InvocationContext invocationContext) throws Throwable;

}
