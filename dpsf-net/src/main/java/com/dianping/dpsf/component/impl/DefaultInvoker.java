/**
 * 
 */
package com.dianping.dpsf.component.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.dianping.dpsf.control.PigeonConfig;
import org.apache.log4j.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFController;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.protocol.DefaultRequest;
import com.dianping.dpsf.stat.CentralStatService;
import com.dianping.dpsf.stat.CentralStatService.CentralStatContext;
import com.dianping.dpsf.stat.CentralStatService.ReturnCode;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.stat.ServiceStat;
import com.dianping.dpsf.telnet.cmd.TelnetCommandServiceStat;
import com.dianping.dpsf.thread.CycThreadPool;
import com.dianping.dpsf.thread.ExeThreadPool;
import com.site.helper.Splitters;
import com.site.helper.Stringizers;

/**
 * <p>
 * Title: DefaultInvoker.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 上午12:05:09
 */
public class DefaultInvoker implements Invoker {

	private static Logger log = DPSFLog.getLogger();

	private Map<Long, Object[]> requestMap = new ConcurrentHashMap<Long, Object[]>();

	private AtomicLong sequenceMaker = new AtomicLong(0);

	private ExeThreadPool threadPool = new ExeThreadPool("DPSF-DEF-Invoker-Exe");

	private static Invoker invoker;
	private ServiceStat requestStat = new ServiceStat();

	private DefaultInvoker() {
		CycThreadPool.getPool().execute(new TimeoutCheck());
	}

	private synchronized static void createInvoker() {
		if (invoker != null) {
			return;
		}
		invoker = new DefaultInvoker();
		ClientManagerFactory.getClientManager().setInvoker(invoker);
		TelnetCommandServiceStat.getInstance().setInvoker((DefaultInvoker) invoker);
	}

	public static Invoker getInstance() {
		if (invoker == null) {
			createInvoker();
		}
		return invoker;
	}

	public DPSFResponse invokeSync(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException, InterruptedException {
		DPSFFuture future = invokeFuture(request, metaData, controller);
		DPSFResponse res = null;
		try {
			res = future.get(metaData.getTimeout());
		} catch (NetTimeoutException e) {
			requestStat.failCountService(request.getServiceName());
			throw e;
		}

		return res;
	}

	public void invokeCallback(DPSFRequest request, DPSFMetaData metaData, DPSFController controller, DPSFCallback callback) throws NetException {
		initRequest(request);
		if (request.getCallType() == 0) {
			request.setCallType(Constants.CALLTYPE_REPLY);
		}
		long seq = sequenceMaker.incrementAndGet();
		request.setSequence(seq);
		request.setAttachment(Constants.REQ_ATTACH_WRITE_BUFF_LIMIT, metaData.isWriteBufferLimit());
		Client client = ClientManagerFactory.getClientManager().getClient(metaData.getServiceName(), metaData.getGroup(), request);

		MessageProducer cat = Cat.getProducer();
		Event event = cat.newEvent("PigeonCall.server", client.getHost() +":" + client.getPort());
		try {
			event.addData(Stringizers.forJson().from(((DefaultRequest) request).getParameters(), CatConstants.MAX_LENGTH,CatConstants.MAX_ITEM_LENGTH));
			event.setStatus(Event.SUCCESS);
		} catch (Exception e) {
			event.setStatus(e);
		}
		

		if (request.getCallType() == Constants.CALLTYPE_REPLY) {
			Object[] callData = new Object[5];
			int index = 0;
			callData[index++] = request;
			callData[index++] = controller;
			callData[index++] = callback;
			callData[index++] = metaData.getGroup();

			try {
				callData[index] = new CentralStatContext(request.getServiceName(), request.getMethodName(), ((DefaultRequest) request).getParameterClasses(), client.getHost() + ":" + client.getPort(), request.getCallType());
			} catch (Exception e) {
				callData[index] = new CentralStatContext(request.getServiceName(), request.getMethodName(), client.getHost() + ":" + client.getPort(), request.getCallType());
			}
			this.requestMap.put(seq, callData);
			callback.setCentralStatContext((CentralStatContext) callData[4]);
			callback.setRequest(request);
			callback.setClient(client);
		}

		// 传递业务上下文
		Object newContext = ContextUtil.createContext(request.getServiceName(), request.getMethodName(), client.getHost(), client.getPort());
		request.setContext(newContext);

		// Add Cat Info
		String serverMessageId = Cat.getProducer().createMessageId();
		MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
		if (tree == null) {
			Cat.setup(null);
			tree = Cat.getManager().getThreadLocalMessageTree();
		}
		String rootMessageId = tree.getRootMessageId() == null ? tree.getMessageId() : tree.getRootMessageId();
		String currentMessageId = tree.getMessageId();

		ContextUtil.addCatInfo(newContext, CatConstants.PIGEON_ROOT_MESSAGE_ID, rootMessageId);
		ContextUtil.addCatInfo(newContext, CatConstants.PIGEON_CURRENT_MESSAGE_ID, currentMessageId);
		ContextUtil.addCatInfo(newContext, CatConstants.PIGEON_SERVER_MESSAGE_ID, serverMessageId);

		cat.logEvent(CatConstants.TYPE_REMOTE_CALL, CatConstants.NAME_REQUEST, Transaction.SUCCESS, serverMessageId);

		RpcStatsPool.flowIn(request, client.getAddress());
		try {
			client.write(request, callback);
		} catch (RuntimeException e) {
			RpcStatsPool.flowOut(request, client.getAddress());
            throw e;
		}

		ClientContext.setUsedClientAddress(client.getAddress());
		requestStat.countService(request.getServiceName());
		// notify one way call
		if (Constants.CALLTYPE_NOREPLY == request.getCallType()) {
			try {
				CentralStatService.notifyMethodInvoke(new CentralStatContext(request.getServiceName(), request.getMethodName(), ((DefaultRequest) request).getParameterClasses(), client.getHost() + ":" + client.getPort(), request.getCallType()));
			} catch (Exception e) {
				CentralStatService.notifyMethodInvoke(new CentralStatContext(request.getServiceName(), request.getMethodName(), client.getHost() + ":" + client.getPort(), request.getCallType()));
			}
		}
	}

	public DPSFFuture invokeFuture(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException {
		CallbackFuture future = new CallbackFuture();
		invokeCallback(request, metaData, controller, future);
		return future;
	}

	public void invokeOneway(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException {
		request.setCallType(Constants.CALLTYPE_NOREPLY);
		invokeCallback(request, metaData, controller, null);
	}

	public void invokeReponse(DPSFResponse response) {
		Object[] callData = requestMap.get(response.getSequence());
		if (callData != null) {

			// {{{ cat
			DPSFRequest request = (DPSFRequest) callData[0];
			List<String> serviceMeta = Splitters.by("/").noEmptyItem().split(request.getServiceName());
			int length = serviceMeta.size();
			if (length > 2) {
				StringBuilder sb = new StringBuilder();
				sb.append(serviceMeta.get(length - 2)).append(":").append(serviceMeta.get(length - 1)).append(":").append(request.getMethodName());
			}

			DPSFCallback callback = (DPSFCallback) callData[2];
			if (callback != null) {
				Client client = callback.getClient();
				if (client != null) {
					RpcStatsPool.flowOut(request, client.getAddress());
				}

				// Log call back invocation for noraml return code.
				long duration = System.currentTimeMillis() - request.getCreateMillisTime();
				CentralStatContext centralStatContext = (CentralStatContext) callData[4];
				if (centralStatContext != null) {
					try {
						if (duration < request.getTimeout()) {
							if (response.getMessageType() != Constants.MESSAGE_TYPE_EXCEPTION || (response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION && callback instanceof ServiceWarpCallback)) {
								if (centralStatContext.getDuration() == null) {
									centralStatContext.setDuration(duration);
								}
								centralStatContext.setReturnCode(response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION ? ReturnCode.EXCEPTION : ReturnCode.SUCCESS);
								CentralStatService.notifyMethodInvoke(centralStatContext);
							}
						}
					} catch (ServiceException e) {
						log.warn("Get MessageType for callback invoke failed. Miss Center Stat for this invocation.");
					}
				}
				callback.callback(response);
				this.threadPool.execute(callback);
			}
			requestMap.remove(response.getSequence());
			requestStat.timeService(callback.getRequest().getServiceName(), callback.getRequest().getCreateMillisTime());
		} else {
            if (!PigeonConfig.isUseNewInvokeLogic()) {
			    log.warn("no request for response:" + response.getSequence());
            }
		}
	}

	//初始化Request的createTime和timeout，以便统一这两个值
	private void initRequest(DPSFRequest request){
		Object createTime = ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME);
		if(createTime != null){
			request.setCreateMillisTime(Long.parseLong(String.valueOf(createTime)));
		}else{
			request.setCreateMillisTime(System.currentTimeMillis());
		}
		Object timeout = ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT);
		if(timeout != null){
			int timeout_ = Integer.parseInt(String.valueOf(timeout));
			if(timeout_ < request.getTimeout()){
				request.setTimeout(timeout_);
			}
		}
	}

	private class TimeoutCheck implements Runnable {

		public void run() {
			while (true) {
				try {
					long now = System.currentTimeMillis();
					for (Long key : requestMap.keySet()) {
						Object[] requestData = requestMap.get(key);
						if (requestData != null) {
							DPSFRequest request = (DPSFRequest) requestData[0];
							if (request.getCreateMillisTime() + request.getTimeout() < now) {
								DPSFCallback callback = (DPSFCallback) requestData[2];
								if (callback != null && callback.getClient() != null) {
									RpcStatsPool.flowOut(request, callback.getClient().getAddress());
								}
								// Log call back invocation for timeout
								CentralStatContext centralStatContext = (CentralStatContext) requestData[4];
								if (centralStatContext != null) {
									centralStatContext.setDuration(now - request.getCreateMillisTime());
									centralStatContext.setReturnCode(ReturnCode.TIMEOUT);
									CentralStatService.notifyMethodInvoke(centralStatContext);
								}

								requestMap.remove(key);
								log.warn("remove timeout key:" + key);
							}
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

		}

	}

	/**
	 * @return the requestStat
	 */
	public ServiceStat getRequestStat() {
		return requestStat;
	}

	public static void setInvoker(Invoker invoker) {
		DefaultInvoker.invoker = invoker;
	}

}
