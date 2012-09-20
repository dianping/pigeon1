/**
 * 
 */
package com.dianping.dpsf.process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.repository.DPSFMethod;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.dpsf.stat.ServiceStat;
import com.site.helper.Splitters;
import com.site.helper.Stringizers;

/**
 * <p>
 * Title: RequestExecutor.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-26 下午07:18:57
 */
public class RequestExecutor implements Runnable {

	private final static Logger logger = DPSFLog.getLogger();

	public static ServiceStat requestStat = new ServiceStat();

	private DPSFRequest request;
	private Channel channel;
	private ServiceRepository sr;
	private RequestProcessor processor;

	private List<ExecutorListener> listeners = new ArrayList<ExecutorListener>();

	public RequestExecutor(DPSFRequest request, Channel channel, ServiceRepository sr, RequestProcessor processor) {
		this.request = request;
		this.channel = channel;
		this.sr = sr;
		this.processor = processor;
	}

	public void addListener(ExecutorListener listener) {
		this.listeners.add(listener);
	}

	static ResponseWriteListener listener = new ResponseWriteListener();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		MessageProducer cat = null;
		Transaction t = null;
		try {
			this.processor.putThread(this.request, Thread.currentThread());

			cat = Cat.getProducer();

			String name = "Unknown";

			List<String> serviceMeta = Splitters.by("/").noEmptyItem().split(this.request.getServiceName());
			int length = serviceMeta.size();

			if (length > 2) {
				StringBuilder sb = new StringBuilder(128);

				sb.append(serviceMeta.get(length - 2)).append(':').append(serviceMeta.get(length - 1)).append(':').append(this.request.getMethodName());
				Object[] parameters = request.getParameters();
				sb.append('(');
				if(parameters != null){
					int pLen = parameters.length;
					for (int i = 0; i < pLen; i++) {
						Object parameter = parameters[i];
						sb.append(parameter.getClass().getSimpleName());
						if (i < pLen - 1) {
							sb.append(',');
						}
					}
				}
				sb.append(')');
				name = sb.toString();
				
				
			}

			t = cat.newTransaction("PigeonService", name);
			InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
			String parameters = Stringizers.forJson().from(request.getParameters(), CatConstants.MAX_LENGTH,CatConstants.MAX_ITEM_LENGTH);
			cat.logEvent("PigeonService.client", address.getAddress().getHostAddress() + ":" + address.getPort(), Message.SUCCESS,  parameters);

			Object context = request.getContext();
			String rootMessageId = ContextUtil.getCatInfo(context, CatConstants.PIGEON_ROOT_MESSAGE_ID);
			String serverMessageId = ContextUtil.getCatInfo(context, CatConstants.PIGEON_CURRENT_MESSAGE_ID);
			String currentMessageId = ContextUtil.getCatInfo(context, CatConstants.PIGEON_SERVER_MESSAGE_ID);
			MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
			if (tree == null) {
				Cat.setup(null);
				tree = Cat.getManager().getThreadLocalMessageTree();
			}

			tree.setRootMessageId(rootMessageId);
			tree.setParentMessageId(serverMessageId);
			tree.setMessageId(currentMessageId);

			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e2) {
			cat.logError(e2);
		}

		try {
			requestStat.countService(this.request.getServiceName());
			DPSFResponse response = null;
			int messageType = this.request.getMessageType();
			try {
				if (messageType == Constants.MESSAGE_TYPE_SERVICE) {
					// 传递上下文
					ContextUtil.setContext(this.request.getContext());
					ContextUtil.putLocalContext(Constants.REQUEST_CREATE_TIME, this.request.getCreateMillisTime());
					ContextUtil.putLocalContext(Constants.REQUEST_TIMEOUT, this.request.getTimeout());
					response = doBusiness();
					requestStat.timeService(this.request.getServiceName(), this.request.getCreateMillisTime());
					if (response == null) {
						runCompleted();
						return;
					}
					// 传递上下文
					response.setContext(ContextUtil.getContext());
					
				} else if (messageType == Constants.MESSAGE_TYPE_HEART) {
					response = doHeart();
				}
			} catch (Exception e) {
				response = doFailResponse(e);
				// 传递上下文
				try {
					response.setContext(ContextUtil.getContext());
				} catch (NetException e1) {
					logger.error(e.getMessage(), e);
				}
			}
			if (messageType != Constants.MESSAGE_TYPE_HEART || PigeonConfig.isHeartBeatResponse()) {
				this.channel.write(response);
			}
			runCompleted();
		} catch (RuntimeException e) {
			cat.logError(e);
			t.setStatus(e);
		} finally {
			ContextUtil.clearContext();
			ContextUtil.clearLocalContext();
			t.complete();
		}
	}

	private DPSFResponse doHeart() {
		return ResponseFactory.createHeartResponse(this.request);
	}

	private DPSFResponse doBusiness() {
		DPSFResponse response = null;
		DPSFMethod method = null;
		try {
			method = this.sr.getMethod(request.getServiceName(), request.getMethodName(), request.getParamClassName());
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			if (this.request.getCallType() == Constants.CALLTYPE_REPLY) {
				response = doFailResponse(e);
			}
		}

		if (method != null) {
			Method method_ = method.getMethod();
			Object returnObj = null;
			try {
				long now = 0;
				if (logger.isDebugEnabled()) {
					now = System.nanoTime();
				}
				returnObj = method_.invoke(method.getService(), this.request.getParameters());
				if (now > 0) {
					logger.debug("service:" + request.getServiceName() + "_" + request.getMethodName());
					logger.debug("execute time:" + (System.nanoTime() - now) / 1000);
					logger.debug("RequestId:" + request.getSequence());
				}
			} catch (InvocationTargetException e1) {
				Throwable e2 = e1.getTargetException();
				if (e2 != null) {
					logger.error(e2.getMessage(), e2);
				}
				Cat.getProducer().logError(e2);
				if (this.request.getCallType() == Constants.CALLTYPE_REPLY) {
					return ResponseFactory.createServiceExceptionResponse(this.request, e2);
				}
			} catch (Exception e1) {
				logger.error(e1.getMessage(), e1);
				Cat.getProducer().logError(e1);
				if (this.request.getCallType() == Constants.CALLTYPE_REPLY) {
					response = doFailResponse(e1);
				}
			}

			if (this.request.getCallType() == Constants.CALLTYPE_REPLY) {
				response = ResponseFactory.createSuccessResponse(this.request, returnObj);
			}
		}
		return response;
	}

	private DPSFResponse doFailResponse(Exception e) {
		logger.error(e.getMessage(), e);
		if (this.request.getCallType() == Constants.CALLTYPE_REPLY) {
			return ResponseFactory.createFailResponse(this.request, e);
		}
		return null;
	}

	private void runCompleted() {
		for (ExecutorListener listener : listeners) {
			listener.executorCompleted(this.request);
		}
	}

	public static class ResponseWriteListener implements ChannelFutureListener {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
		 */
		public void operationComplete(ChannelFuture future) throws Exception {

		}

	}

}
