package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.process.ResponseFactory;
import com.dianping.dpsf.stat.ServiceStat;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-6
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public class ErrorResponseProcessFilter extends RemoteInvocationFilter<InvocationProcessContext> {

    public ErrorResponseProcessFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        DPSFRequest request = invocationContext.getRequest();
        DPSFResponse response = null;
        try {
            response = handler.handle(invocationContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (request.getCallType() == Constants.CALLTYPE_REPLY) {
                response = ResponseFactory.createFailResponse(request, e);
            }
        }
        return response;
    }

}
