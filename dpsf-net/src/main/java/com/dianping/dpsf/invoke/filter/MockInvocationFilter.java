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

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationContext;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationFilter;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class MockInvocationFilter extends RemoteInvocationFilter<InvocationInvokeContext> {

    public MockInvocationFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        //TODO implement me!
        DPSFResponse response = handler.handle(invocationContext);
        return response;
    }

}
