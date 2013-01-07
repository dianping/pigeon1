package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.process.ResponseFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:40
 * To change this template use File | Settings | File Templates.
 */
public class EchoProcessFilter extends RemoteInvocationFilter<InvocationProcessContext> {

    public EchoProcessFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        String methodName = invocationContext.getRequest().getMethodName();
        Object[] parameters = invocationContext.getRequest().getParameters();
        if (Constants.ECHO_METHOD.equals(methodName) && parameters != null && parameters.length == 1) {
            return ResponseFactory.createSuccessResponse(invocationContext.getRequest(), parameters[0]);
        }
        return handler.handle(invocationContext);
    }

}
