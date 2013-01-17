package com.dianping.dpsf.invoke.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;

/**
 * 对Service调用执行一些Finalize处理
 * User: jian.liu
 * Date: 13-1-7
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class GatewayInvokeFilter extends InvocationInvokeFilter {

    public GatewayInvokeFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        DPSFMetaData metaData = invocationContext.getMetaData();
        try {
            return handler.handle(invocationContext);
        } catch (Throwable e) {
            if (Constants.CALL_FUTURE.equalsIgnoreCase(metaData.getCallMethod())) {
                ServiceFutureFactory.remove();
            }
            throw e;
        }
    }

}
