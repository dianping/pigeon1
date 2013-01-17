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
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.site.helper.Stringizers;

/**
 * 对每次Remote Call调用的Cat监控
 *
 * @author danson.liu
 */
public class RemoteCallMonitorInvokeFilter extends InvocationInvokeFilter {

    private CatMonitorSupport monitorSupport = new CatMonitorSupport();

    public RemoteCallMonitorInvokeFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        MessageProducer cat = null;

        try {
            cat = Cat.getProducer();
        } catch (Exception e) {
            monitorSupport.logCatError(e);
        }

        if (cat != null) {
            try {
                Client client = invocationContext.getClient();
                Event event = cat.newEvent("PigeonCall.server", client.getAddress());
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

                invocationContext.putContextValue(CatConstants.PIGEON_ROOT_MESSAGE_ID, rootMessageId);
                invocationContext.putContextValue(CatConstants.PIGEON_CURRENT_MESSAGE_ID, currentMessageId);
                invocationContext.putContextValue(CatConstants.PIGEON_SERVER_MESSAGE_ID, serverMessageId);

                cat.logEvent(CatConstants.TYPE_REMOTE_CALL, CatConstants.NAME_REQUEST, Transaction.SUCCESS, serverMessageId);
            } catch (Exception e) {
                monitorSupport.logCatError(e);
            }
        }
        return handler.handle(invocationContext);
    }

}
