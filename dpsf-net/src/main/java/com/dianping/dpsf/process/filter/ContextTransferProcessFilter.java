package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.spi.InvocationProcessFilter;
import com.dianping.dpsf.stat.ServiceStat;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
public class ContextTransferProcessFilter extends InvocationProcessFilter {

    private ServiceStat serverServiceStat = ServiceStat.getServerServiceStat();

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        DPSFRequest request = invocationContext.getRequest();
        serverServiceStat.countService(invocationContext.getRequest().getServiceName());
        ContextUtil.setContext(request.getContext());
        DPSFResponse response = null;
        try {
            response = handler.handle(invocationContext);
            return response;
        } finally {
            if (response != null) {
                try {
                    response.setContext(ContextUtil.getContext());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void setServerServiceStat(ServiceStat serverServiceStat) {
        this.serverServiceStat = serverServiceStat;
    }

}
