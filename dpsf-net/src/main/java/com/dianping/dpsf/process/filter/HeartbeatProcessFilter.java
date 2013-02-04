package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.process.ResponseFactory;
import com.dianping.dpsf.spi.InvocationProcessFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class HeartbeatProcessFilter extends InvocationProcessFilter {

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        if (invocationContext.getRequest().getMessageType() == Constants.MESSAGE_TYPE_HEART) {
            return ResponseFactory.createHeartResponse(invocationContext.getRequest());
        }
        return handler.handle(invocationContext);
    }

}
