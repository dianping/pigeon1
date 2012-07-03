/**
 * 
 */
package com.dianping.dpsf.component.impl;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;

import com.dianping.cat.Cat;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.RequestError;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.exception.NoSupportedException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.stat.CentralStatService;
import com.dianping.dpsf.stat.CentralStatService.CentralStatContext;
import com.dianping.dpsf.stat.CentralStatService.ReturnCode;
import com.dianping.dpsf.stat.RpcStatsPool;

/**    
 * <p>    
 * Title: CallbackFuture.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-20 上午11:53:40   
 */
public class CallbackFuture implements DPSFCallback,DPSFFuture{
	
	protected static Logger logger = DPSFLog.getLogger();
	
	private DPSFResponse response;
	
	private ChannelFuture future;
	
	private boolean done = false;
	private boolean concelled =false;
	private boolean success = false;
	
	private RequestError error;
	
	private DPSFRequest request;
	
	private Client client;
	
	private CentralStatContext centralStatContext;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		synchronized(this){
			this.done = true;
			try {
				if(this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE){
					this.success = true;
				}
				if (centralStatContext != null && centralStatContext.getDuration() == null) {
					centralStatContext.setDuration(System.currentTimeMillis() - request.getCreateMillisTime());
				}
				
			} catch (ServiceException e) {
				logger.error(e.getMessage(),e);
			}
			
			this.notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFCallback#callback(com.dianping.dpsf.net.component.DPSFResponse)
	 */
	public void callback(DPSFResponse response) {
			this.response = response;
		
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
		synchronized(this){
			long start = request.getCreateMillisTime();
			while(!this.done){
				long timeoutMillis_ = timeoutMillis - (System.currentTimeMillis() - start);
				if(timeoutMillis_ <= 0){
					this.error = RequestError.TIMEOUT;
					StringBuffer sb = new StringBuffer();
					sb.append(this.error.getMsg()).append("\r\n seq:").append(request.getSequence())
					.append(" callType:").append(request.getCallType()).append("\r\n serviceName:")
					.append(request.getServiceName()).append(" methodName:").append(request.getMethodName())
					.append("\r\n host:").append(client.getHost()).append(":").append(client.getPort());
					
					RpcStatsPool.flowOut(request, client.getAddress());
					
					NetTimeoutException netTimeoutException = new NetTimeoutException(sb.toString());

					Cat.getProducer().logError(netTimeoutException);
					throw netTimeoutException;
				}else{
					this.wait(timeoutMillis_);
				}
			}
			
			Object context = ContextUtil.getContext();
			if(context != null){
				Integer order = ContextUtil.getOrder(this.response.getContext());
				if(order != null && order > 0){
					ContextUtil.setOrder(context, order);
				}
				if(this.success){
					//传递业务上下文
					ContextUtil.addSuccessContext(this.response.getContext());
				}else{
					//传递业务上下文
					ContextUtil.addFailedContext(this.response.getContext());
				}
			}
			try {
				if(response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION){
					StringBuffer sb = new StringBuffer();
					sb.append("Service Exception Info *************\r\n")
					.append(" token:").append(ContextUtil.getTooken(this.response.getContext())).append("\r\n")
					.append(" seq:").append(request.getSequence())
					.append(" callType:").append(request.getCallType()).append("\r\n serviceName:")
					.append(request.getServiceName()).append(" methodName:").append(request.getMethodName())
					.append("\r\n host:").append(client.getHost()).append(":").append(client.getPort());
					logger.error(sb.toString());
					response.setReturn((Throwable)response.getReturn());
				}
				
				if (response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
					if (centralStatContext != null) {
						if (centralStatContext.getDuration() == null) {
							centralStatContext.setDuration(System.currentTimeMillis() - start);
						}
						centralStatContext.setReturnCode(ReturnCode.EXCEPTION);
						CentralStatService.notifyMethodInvoke(centralStatContext);
					}
				}
			} catch (ServiceException e) {
				throw new NetException(e);
			}
			return this.response;
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
		synchronized(this){
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
		synchronized(this){
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

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFCallback#setCentralStatContext(com.dianping.dpsf.stat.CentralStatService.CentralStatContext)
	 */
	@Override
	public void setCentralStatContext(CentralStatContext centralStatContext) {
		this.centralStatContext = centralStatContext;
	}

}
