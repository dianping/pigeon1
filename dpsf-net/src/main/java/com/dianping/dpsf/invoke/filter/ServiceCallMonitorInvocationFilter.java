/**
 * File Created at 12-12-31
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
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;

import java.lang.reflect.Method;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ServiceCallMonitorInvocationFilter extends AbstractCatMonitorInvocationFilter<InvocationInvokeContext> {

    public ServiceCallMonitorInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        DPSFMetaData metaData = invocationContext.getMetaData();
        MessageProducer cat = null;
        try {cat = Cat.getProducer();} catch (Exception e) {logCatError(e);}
        Transaction transaction = null;
        if (cat != null) {
            try {
                Method method = invocationContext.getMethod();
                transaction = cat.newTransaction("PigeonCall", getRemoteCallFullName(metaData.getServiceName(),
                        method.getName(), method.getParameterTypes()));
                transaction.setStatus(Transaction.SUCCESS);
                transaction.addData("CallType", metaData.getCallMethod());
            } catch (Exception e) {
                logCatError(e);
            }
        }
        try {
            return handler.handle(invocationContext);
        } catch (Throwable e) {
            try {
                if (transaction != null) transaction.setStatus(e);
                if (cat != null) {
                    cat.logError(e);
                }
            } catch (Exception e1) {
                logCatError(e1);
            }
            throw e;
        } finally {
            try {
                if (transaction != null) transaction.complete();
            } catch (Exception e) {
                logCatError(e);
            }
        }
    }

}
