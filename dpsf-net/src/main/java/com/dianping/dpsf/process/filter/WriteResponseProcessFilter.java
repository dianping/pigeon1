package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import org.jboss.netty.channel.Channel;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class WriteResponseProcessFilter extends InvocationProcessFilter {

    public WriteResponseProcessFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        try {
            Channel channel = invocationContext.getChannel();
            DPSFRequest request = invocationContext.getRequest();
            DPSFResponse response = handler.handle(invocationContext);
            if (request.getCallType() != Constants.CALLTYPE_NOREPLY && (request.getMessageType() != Constants.MESSAGE_TYPE_HEART || PigeonConfig.isHeartBeatResponse())) {
                channel.write(response);
            }
            invocationContext.processComplete();
            return response;
        } finally {
            ContextUtil.clearContext();
            ContextUtil.clearLocalContext();
        }
    }

}
