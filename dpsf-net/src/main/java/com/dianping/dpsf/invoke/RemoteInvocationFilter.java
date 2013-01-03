package com.dianping.dpsf.invoke;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.RemoteInvocation;
import org.apache.log4j.Logger;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public abstract class RemoteInvocationFilter implements Comparable<RemoteInvocationFilter> {

    protected final Logger logger = DPSFLog.getLogger();

    private int order;

    public RemoteInvocationFilter(int order) {
        this.order = order;
    }

    public abstract DPSFResponse invoke(RemoteInvocationHandler handler, RemoteInvocation invocation) throws Throwable;

    public int order() {
        return order;
    }

    @Override
    public int compareTo(RemoteInvocationFilter o) {
        if (o == null) {
            return -1;
        }
        return order - o.order;
    }

}
