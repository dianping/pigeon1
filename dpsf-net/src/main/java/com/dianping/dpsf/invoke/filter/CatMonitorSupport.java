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

import com.dianping.cat.message.MessageProducer;
import com.dianping.dpsf.DPSFLog;
import com.site.helper.Splitters;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class CatMonitorSupport {

    private final Logger logger = DPSFLog.getLogger();

    private volatile long errorCounter = 0L;

    public void logCatError(Throwable e) {
        try {
            String errorMsg = "[Cat]Monitor pigeon call failed.";
            if (errorCounter <= 50) {
                logger.error(errorMsg, e);
            } else if (errorCounter < 1000 && errorCounter % 40 == 0) {
                logger.error(errorMsg, e);
            } else if (errorCounter % 200 == 0) {
                logger.error(errorMsg, e);
            }
        } catch (Exception e2) {/*do nothing*/}
        errorCounter++;
    }

    public void catLogError(MessageProducer producer, Throwable e) {
        try {
            if (producer != null && e != null) {
                producer.logError(e);
            }
        } catch (Exception e2) {
            logCatError(e2);
        }
    }

    public String getRemoteCallFullName(String serviceName, String methodName, Class<?>[] parameterTypes) {
        if (parameterTypes != null) {
            String[] parameterTypes_ = new String[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes_[i] = parameterTypes[i].getSimpleName();
            }
            return getRemoteCallFullName(serviceName, methodName, parameterTypes_);
        } else {
            return getRemoteCallFullName(serviceName, methodName, new String[0]);
        }
    }

    public String getRemoteCallFullName(String serviceName, String methodName, String[] parameterTypes) {
        List<String> serviceFrags = Splitters.by("/").noEmptyItem().split(serviceName);
        int fragLenght = serviceFrags.size();
        String name = "Unknown";
        if (fragLenght > 2) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(serviceFrags.get(fragLenght - 2)).append(':').append(serviceFrags.get(fragLenght - 1))
                    .append(':').append(methodName);
            sb.append('(');
            int pLen = parameterTypes.length;
            for (int i = 0; i < pLen; i++) {
                String parameter = parameterTypes[i];
                int idx = parameter.lastIndexOf(".");
                if (idx > -1) {
                    parameter = parameter.substring(idx + 1);
                }
                sb.append(parameter);
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
