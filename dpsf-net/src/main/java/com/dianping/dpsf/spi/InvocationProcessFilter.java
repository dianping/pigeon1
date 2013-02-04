package com.dianping.dpsf.spi;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-7
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvocationProcessFilter implements RemoteInvocationFilter<InvocationProcessContext> {
    
    protected final Logger logger = DPSFLog.getLogger();

    public static enum ProcessPhase {
        Execute, Before_Execute, Write, Before_Write;
    }

}
