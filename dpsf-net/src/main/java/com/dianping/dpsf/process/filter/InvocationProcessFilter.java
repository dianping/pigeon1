package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-7
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvocationProcessFilter extends RemoteInvocationFilter<InvocationProcessContext> {

    public InvocationProcessFilter(int order) {
        super(order);
    }

}
