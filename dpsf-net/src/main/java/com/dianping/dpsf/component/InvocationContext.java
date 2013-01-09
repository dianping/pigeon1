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
package com.dianping.dpsf.component;


/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class InvocationContext {

    protected DPSFRequest request;

    public DPSFRequest getRequest() {
        return request;
    }

    public void setRequest(DPSFRequest request) {
        this.request = request;
    }
}
