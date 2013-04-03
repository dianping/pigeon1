/**
 *
 */
package com.dianping.dpsf.component.impl;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.RequestError;
import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.Client;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;

/**
 * <p>
 * Title: ServiceCallback.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2011-3-22 上午12:48:19
 */
public class ServiceWarpCallback implements DPSFCallback {

    private static Logger logger = DPSFLog.getLogger();

    private DPSFResponse response;

    private ChannelFuture future;

    private DPSFRequest request;

    private Client client;

    private ServiceCallback callback;

    public ServiceWarpCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */
    @Override
    public void run() {
        try {
            if (ContextUtil.getContext() != null) {
                if (this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
                    //传递业务上下文
                    ContextUtil.addSuccessContext(this.response.getContext());
                } else {
                    //传递业务上下文
                    ContextUtil.addFailedContext(this.response.getContext());
                }
            }
            if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
                StringBuffer sb = new StringBuffer();
                sb.append("Service Exception Info *************\r\n")
//                        .append(" token:").append(ContextUtil.getToken(this.response.getContext())).append("\r\n")
                        .append(" seq:").append(request.getSequence())
                        .append(" callType:").append(request.getCallType()).append("\r\n serviceName:")
                        .append(request.getServiceName()).append(" methodName:").append(request.getMethodName())
                        .append("\r\n host:").append(client.getHost()).append(":").append(client.getPort());
                response.setReturn(new DPSFException(request.getServiceName(), client.getHost() + ":" + client.getPort(),
                        sb.toString(), (Throwable) response.getReturn()));
            }
            try {
                if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
                    this.callback.callback(response.getReturn());
                } else if (response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
                    logger.error(response.getCause());
                    this.callback.frameworkException(new DPSFException(response.getCause()));
                } else if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
                    this.callback.serviceException((Exception) response.getReturn());
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("ServiceCallback error", e);
            }
        } catch (ServiceException e) {
            this.callback.frameworkException(new NetException(e));
        } catch (NetException e) {
            this.callback.frameworkException(e);
        }
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCall#setClient(com.dianping.dpsf.net.channel.Client)
      */
    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#callback(com.dianping.dpsf.component.DPSFResponse)
      */
    @Override
    public void callback(DPSFResponse response) {
        this.response = response;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#getFuture(org.jboss.netty.channel.ChannelFuture)
      */
    @Override
    public DPSFFuture getFuture(ChannelFuture future) {
        this.future = future;
        return null;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#fail(com.dianping.dpsf.RequestError)
      */
    @Override
    public void fail(RequestError error) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#setRequest(com.dianping.dpsf.component.DPSFRequest)
      */
    @Override
    public void setRequest(DPSFRequest request) {
        this.request = request;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#getRequest()
      */
    @Override
    public DPSFRequest getRequest() {
        return this.request;
    }

}
