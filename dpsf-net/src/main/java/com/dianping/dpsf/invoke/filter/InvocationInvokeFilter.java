package com.dianping.dpsf.invoke.filter;

import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-7
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvocationInvokeFilter extends RemoteInvocationFilter<InvocationInvokeContext> {

    public InvocationInvokeFilter(int order) {
        super(order);
    }

    public static enum InvokePhase {
        Call, Before_Call, Cluster, Before_Cluster, Error_Handle, Finalize;
    }

}
