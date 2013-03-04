/**
 *
 */
package com.dianping.dpsf.component.impl;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.RequestError;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFLifeCycleListener;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.invoke.lifecycle.DPSFRequestLifeCycle;
import com.dianping.dpsf.invoke.lifecycle.DPSFRequestLifeCycle.DPSFEvent;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.protocol.DefaultResponse;
import com.dianping.dpsf.stat.RpcStatsPool;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Title: CallbackFuture.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-8-20 上午11:53:40
 */
public class CallbackFuture implements DPSFCallback, DPSFFuture, DPSFLifeCycleListener{

    protected static Logger logger = DPSFLog.getLogger();

    private DPSFResponse response;

    private ChannelFuture future;

    private boolean done = false;
    private boolean concelled = false;
    private boolean success = false;

    private RequestError error;

    private DPSFRequest request;

    private Client client;
    
    private List<DPSFRequestLifeCycle> lifeCycles = new ArrayList<DPSFRequestLifeCycle>();

    /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */
    public void run() {
        synchronized (this) {
            this.done = true;
            try {
                if (this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
                    this.success = true;
                }
            } catch (ServiceException e) {
                logger.error(e.getMessage(), e);
            }

            this.notifyAll();
        }
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFCallback#callback(com.dianping.dpsf.net.component.DPSFResponse)
      */
    public void callback(DPSFResponse response) {
        this.response = response;
        lifeCycle(DPSFEvent.ResponseReturn,response);
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#get()
      */
    public DPSFResponse get() throws InterruptedException, NetException {
        return get(Long.MAX_VALUE);
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#get(long)
      */
    public DPSFResponse get(long timeoutMillis) throws InterruptedException, NetException {
        synchronized (this) {
            long start = request.getCreateMillisTime();
            while (!this.done) {
                long timeoutMillis_ = timeoutMillis - (System.currentTimeMillis() - start);
                if (timeoutMillis_ <= 0) {
                    this.error = RequestError.TIMEOUT;
                    StringBuffer sb = new StringBuffer();
                    sb.append(this.error.getMsg()).append("\r\n seq:").append(request.getSequence())
                            .append(" callType:").append(request.getCallType()).append("\r\n serviceName:")
                            .append(request.getServiceName()).append(" methodName:").append(request.getMethodName())
                            .append("\r\n host:").append(client.getHost()).append(":").append(client.getPort())
                            .append("\r\n timeout:" + request.getTimeout());

                    RpcStatsPool.flowOut(request, client.getAddress());

                    NetTimeoutException netTimeoutException = new NetTimeoutException(sb.toString());

//					Cat.getProducer().logError(netTimeoutException);
                    
                    lifeCycle(DPSFEvent.GetTimeout,response);
                    throw netTimeoutException;
                } else {
                    this.wait(timeoutMillis_);
                }
            }

            Object context = ContextUtil.getContext();
            if (context != null) {
                Integer order = ContextUtil.getOrder(this.response.getContext());
                if (order != null && order > 0) {
                    ContextUtil.setOrder(context, order);
                }
                if (this.success) {
                    //传递业务上下文
                    ContextUtil.addSuccessContext(this.response.getContext());
                } else {
                    //传递业务上下文
                    ContextUtil.addFailedContext(this.response.getContext());
                }
            }
            try {
                if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION
                        || response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
                    Throwable cause = null;
                    if (response instanceof DefaultResponse) {
                        cause = (Throwable) response.getReturn();
                    } else {
                        cause = new DPSFException(response.getCause());
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append(cause.getMessage()).append("\r\n");
                    sb.append("Remote Service Exception Info *************\r\n")
//                            .append(" token:").append(ContextUtil.getToken(this.response.getContext())).append("\r\n")
                            .append(" seq:").append(request.getSequence())
                            .append(" callType:").append(request.getCallType()).append("\r\n serviceName:")
                            .append(request.getServiceName()).append(" methodName:").append(request.getMethodName())
                            .append("\r\n host:").append(client.getHost()).append(":").append(client.getPort())
                            .append("\r\n timeout:" + request.getTimeout());
                    Field field;
                    try {
                        field = Throwable.class.getDeclaredField("detailMessage");
                        field.setAccessible(true);
                        field.set(cause, sb.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    logger.error(cause.getMessage(), cause);
                }
            } catch (ServiceException e) {
                throw new NetException(e);
            }
//			if(response!=null){
//			Cat.getProducer().logEvent("PigeonCall", "Response", Message.SUCCESS, Stringizers.forJson().from(this.response.getReturn(), CatConstants.MAX_LENGTH,CatConstants.MAX_ITEM_LENGTH));
//			}
            
            lifeCycle(DPSFEvent.GetResult,response);
            return this.response;
        }
    }

    private void lifeCycle(DPSFEvent event,DPSFResponse response){
    	for(DPSFRequestLifeCycle lifeCycle : lifeCycles){
    		lifeCycle.lifeCycle(event,response);
    	}
    }
    
    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#get(long, java.util.concurrent.TimeUnit)
      */
    public DPSFResponse get(long timeout, TimeUnit unit) throws InterruptedException, NetException {
        return get(unit.toMillis(timeout));
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#cancel()
      */
    public boolean cancel() {
        synchronized (this) {
            this.concelled = this.future.cancel();
            this.notifyAll();
        }

        return this.concelled;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#isCancelled()
      */
    public boolean isCancelled() {
        return this.concelled;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFFuture#isDone()
      */
    public boolean isDone() {
        return this.done;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFCallback#getFuture(org.jboss.netty.channel.ChannelFuture)
      */
    public DPSFFuture getFuture(ChannelFuture future) {

        this.future = future;
        return this;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.net.component.DPSFCallback#fail(java.lang.Error)
      */
    public void fail(RequestError error) {
        synchronized (this) {
            this.error = error;
            this.done = true;
            this.concelled = false;
            this.success = false;
            this.future = null;
            this.notifyAll();
        }

    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFCallback#setRequest(com.dianping.dpsf.component.DPSFRequest)
      */
    public void setRequest(DPSFRequest request) {
        this.request = request;
    }

    /* (non-Javadoc)
      * @see com.dianping.dpsf.component.DPSFFuture#setClient(com.dianping.dpsf.net.channel.Client)
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
      * @see com.dianping.dpsf.component.DPSFCallback#getRequest()
      */
    @Override
    public DPSFRequest getRequest() {
        return this.request;
    }

	@Override
	public void addRequestLifeCycle(DPSFRequestLifeCycle lifeCycle) {
		this.lifeCycles.add(lifeCycle);
	}

}
