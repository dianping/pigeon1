package com.dianping.dpsf.process.filter;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.invoke.filter.CatMonitorSupport;
import com.dianping.dpsf.spi.InvocationProcessFilter;
import com.site.helper.Stringizers;
import org.jboss.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class MonitorProcessFilter extends InvocationProcessFilter {

    private CatMonitorSupport monitorSupport = new CatMonitorSupport();

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        DPSFRequest request = invocationContext.getRequest();
        Channel channel = invocationContext.getChannel();
        MessageProducer cat = null;
        Transaction transaction = null;
        try {
            cat = Cat.getProducer();
            transaction = cat.newTransaction("PigeonService", monitorSupport.getRemoteCallFullName(request.getServiceName(), request.getMethodName(),
                    request.getParamClassName()));
            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
            String parameters = Stringizers.forJson().from(request.getParameters(), CatConstants.MAX_LENGTH, CatConstants.MAX_ITEM_LENGTH);
            cat.logEvent("PigeonService.client", address.getAddress().getHostAddress() + ":" + address.getPort(), Message.SUCCESS, parameters);

            Object context = request.getContext();
            String rootMessageId = ContextUtil.getContextValue(context, CatConstants.PIGEON_ROOT_MESSAGE_ID);
            String serverMessageId = ContextUtil.getContextValue(context, CatConstants.PIGEON_CURRENT_MESSAGE_ID);
            String currentMessageId = ContextUtil.getContextValue(context, CatConstants.PIGEON_SERVER_MESSAGE_ID);
            MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
            if (tree == null) {
                Cat.setup(null);
                tree = Cat.getManager().getThreadLocalMessageTree();
            }
            tree.setRootMessageId(rootMessageId);
            tree.setParentMessageId(serverMessageId);
            tree.setMessageId(currentMessageId);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            monitorSupport.catLogError(cat, e);
        }
        try {
            handler.handle(invocationContext);
        } catch (RuntimeException e) {
            monitorSupport.catLogError(cat, e);
            if (transaction != null) transaction.setStatus(e);
        } finally {
            try {
                if (transaction != null) transaction.complete();
            } catch (Exception e) {
                monitorSupport.logCatError(e);
            }
            //service异常与后续的异常的打印先后关系不是很重要
            monitorSupport.catLogError(cat, invocationContext.getServiceError());
        }
        return null;
    }

}
