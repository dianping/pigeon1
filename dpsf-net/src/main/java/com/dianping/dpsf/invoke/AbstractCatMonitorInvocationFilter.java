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

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public abstract class AbstractCatMonitorInvocationFilter extends RemoteInvocationFilter {

    private volatile long errorCounter = 0L;

    public AbstractCatMonitorInvocationFilter(int order) {
        super(order);
    }

    protected void logCatError(Throwable e) {
        String errorMsg = "[Cat]Monitor pigeon call failed.";
        if (errorCounter <= 50) {
            logger.error(errorMsg, e);
        } else if (errorCounter < 1000 && errorCounter % 40 == 0) {
            logger.error(errorMsg, e);
        } else if (errorCounter % 200 == 0) {
            logger.error(errorMsg, e);
        }
        errorCounter++;
    }

}
