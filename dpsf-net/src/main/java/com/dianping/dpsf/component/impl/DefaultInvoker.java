/**
 *
 */
package com.dianping.dpsf.component.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
import com.dianping.dpsf.Disposable;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFController;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.protocol.DefaultRequest;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.stat.ServiceStat;
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
@Deprecated
public class DefaultInvoker implements Invoker, Disposable {

    private static Logger log = DPSFLog.getLogger();

    private Map<Long, Object[]> requestMap = new ConcurrentHashMap<Long, Object[]>();

    private AtomicLong sequenceMaker = new AtomicLong(0);

    private ExeThreadPool threadPool = new ExeThreadPool("DPSF-DEF-Invoker-Exe");

    private ServiceStat requestStat = ServiceStat.getClientServiceStat();

    private ClientManager clientManager;
    
    private volatile boolean disposed;

    @Deprecated
    public DefaultInvoker(ClientManager clientManager) {
        this.clientManager = clientManager;
        clientManager.setInvoker(this);
        CycThreadPool.getPool().execute(new TimeoutCheck());
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
        Client client = clientManager.getClient(metaData.getServiceName(), metaData.getGroup(), request);

        MessageProducer cat = Cat.getProducer();
        Event event = cat.newEvent("PigeonCall.server", client.getHost() + ":" + client.getPort());
        try {
            event.addData(Stringizers.forJson().from(((DefaultRequest) request).getParameters(), CatConstants.MAX_LENGTH, CatConstants.MAX_ITEM_LENGTH));
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

            this.requestMap.put(seq, callData);
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

        ContextUtil.putContextValue(newContext, CatConstants.PIGEON_ROOT_MESSAGE_ID, rootMessageId);
        ContextUtil.putContextValue(newContext, CatConstants.PIGEON_CURRENT_MESSAGE_ID, currentMessageId);
        ContextUtil.putContextValue(newContext, CatConstants.PIGEON_SERVER_MESSAGE_ID, serverMessageId);

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
    private void initRequest(DPSFRequest request) {
        Object createTime = ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME);
        Object timeout = ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT);

        if (createTime != null) {

            long createTime_ = Long.parseLong(String.valueOf(createTime));
            int timeout_ = Integer.parseInt(String.valueOf(timeout));

            Object firstFlag = ContextUtil.getLocalContext(Constants.REQUEST_FIRST_FLAG);
            if (firstFlag == null) {
                ContextUtil.putLocalContext(Constants.REQUEST_FIRST_FLAG, true);
                request.setCreateMillisTime(createTime_);
            } else {
                long now = System.currentTimeMillis();
                timeout_ = timeout_ - (int) (now - createTime_);
                if (timeout_ <= 0) {
                    throw new NetTimeoutException("method has been timeout for first call (startTime:" + new Date(createTime_) + " timeout:" + timeout_ + ")");
                }
                request.setCreateMillisTime(now);
            }
            if (timeout_ < request.getTimeout()) {
                request.setTimeout(timeout_);
            }
        } else {
            request.setCreateMillisTime(System.currentTimeMillis());
        }
    }

    @Override
    public void destroy() throws Exception {
        disposed = true;
    }

    private class TimeoutCheck implements Runnable {

        public void run() {
            while (!disposed) {
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

}
