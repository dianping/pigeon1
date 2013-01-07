/**
 * File Created at 12-12-29
 *
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.invoke.filter;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.site.helper.Stringizers;

/**
 * TODO can remove to dpsf-monitor-cat.jar
 *
 * @author danson.liu
 */
public class RemoteCallMonitorInvocationFilter extends AbstractCatMonitorInvocationFilter<InvocationInvokeContext> {

    public RemoteCallMonitorInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        MessageProducer cat = null;

        try {
            cat = Cat.getProducer();
        } catch (Exception e) {
            logCatError(e);
        }

        if (cat != null) {
            try {
                Client remoteClient = invocationContext.getRemoteClient();
                Event event = cat.newEvent("PigeonCall.server", remoteClient.getHost() + ":" + remoteClient.getPort());
                try {
                    event.addData(Stringizers.forJson().from(invocationContext.getArguments(), CatConstants.MAX_LENGTH, CatConstants.MAX_ITEM_LENGTH));
                    event.setStatus(Event.SUCCESS);
                } catch (Exception e) {
                    event.setStatus(e);
                }

                String serverMessageId = cat.createMessageId();
                MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
                if (tree == null) {
                    Cat.setup(null);
                    tree = Cat.getManager().getThreadLocalMessageTree();
                }
                String rootMessageId = tree.getRootMessageId() == null ? tree.getMessageId() : tree.getRootMessageId();
                String currentMessageId = tree.getMessageId();

                Object trackerContext = invocationContext.getTrackerContext();
                ContextUtil.addCatInfo(trackerContext, CatConstants.PIGEON_ROOT_MESSAGE_ID, rootMessageId);
                ContextUtil.addCatInfo(trackerContext, CatConstants.PIGEON_CURRENT_MESSAGE_ID, currentMessageId);
                ContextUtil.addCatInfo(trackerContext, CatConstants.PIGEON_SERVER_MESSAGE_ID, serverMessageId);

                cat.logEvent(CatConstants.TYPE_REMOTE_CALL, CatConstants.NAME_REQUEST, Transaction.SUCCESS, serverMessageId);
            } catch (Exception e) {
                logCatError(e);
            }
        }
        return handler.handle(invocationContext);
    }

}
