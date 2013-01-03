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
package com.dianping.dpsf.invoke;

import com.dianping.cat.Cat;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.RemoteInvocation;
import com.site.helper.Splitters;

import java.util.List;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ServiceCallMonitorInvocationFilter extends AbstractCatMonitorInvocationFilter {

    public ServiceCallMonitorInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, RemoteInvocation invocation) throws Throwable {
        DPSFMetaData metaData = invocation.getMetaData();
        MessageProducer cat = null;
        try {cat = Cat.getProducer();} catch (Exception e) {logCatError(e);}
        Transaction t = null;
        if (cat != null) {
            try {
                t = cat.newTransaction("PigeonCall", getRemoteCallName(invocation));
                t.setStatus(Transaction.SUCCESS);
                t.addData("CallType", metaData.getCallMethod());
            } catch (Exception e) {
                logCatError(e);
            }
        }
        try {
            return handler.handle(invocation);
        } catch (Throwable e) {
            try {
                if (t != null) t.setStatus(e);
                Cat.getProducer().logError(e);
            } catch (Exception e1) {
                logCatError(e1);
            }
            throw e;
        } finally {
            try {
                if (t != null) t.complete();
            } catch (Exception e) {
                logCatError(e);
            }
        }
    }

    private String getRemoteCallName(RemoteInvocation invocation) {
        List<String> serviceFrags = Splitters.by("/").noEmptyItem().split(invocation.getMetaData().getServiceName());
        int fragLenght = serviceFrags.size();
        String name = "Unknown";
        if (fragLenght > 2) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(serviceFrags.get(fragLenght - 2)).append(':').append(serviceFrags.get(fragLenght - 1))
                    .append(':').append(invocation.getMethod().getName());
            Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
            sb.append('(');
            int pLen = parameterTypes.length;
            for (int i = 0; i < pLen; i++) {
                Class<?> parameterType = parameterTypes[i];
                sb.append(parameterType.getSimpleName());
                if (i < pLen - 1) {
                    sb.append(',');
                }
            }
            sb.append(')');
            name = sb.toString();
        }
        return name;
    }

}
