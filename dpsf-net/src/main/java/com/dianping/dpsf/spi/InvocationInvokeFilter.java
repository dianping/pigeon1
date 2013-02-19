package com.dianping.dpsf.spi;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-7
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvocationInvokeFilter implements RemoteInvocationFilter<InvocationInvokeContext> {
    
    protected final Logger logger = DPSFLog.getLogger();

    public static enum InvokePhase {
        Call, Before_Call, Cluster, Before_Cluster, Error_Handle, Finalize;
    }

}
