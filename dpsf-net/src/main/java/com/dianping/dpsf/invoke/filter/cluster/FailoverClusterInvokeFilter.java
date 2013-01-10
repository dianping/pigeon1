package com.dianping.dpsf.invoke.filter.cluster;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.manager.ClientManager;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-10
 * Time: 上午10:48
 * To change this template use File | Settings | File Templates.
 */
public class FailoverClusterInvokeFilter extends ClusterInvokeFilter {

    public static final String NAME = "fail-over";

    public FailoverClusterInvokeFilter(ClientManager clientManager) {
        super(clientManager);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String name() {
        return NAME;
    }

}
