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
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;

/**
 * 用于对一些弱依赖的Service接口方法进行MOCK，一旦调用该接口方法出错时，返回mock对象，不影响
 * 主业务的流程
 *
 * @author danson.liu
 */
public class MockInvokeFilter extends InvocationInvokeFilter {

    public MockInvokeFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationInvokeContext invocationContext) throws Throwable {
        //TODO implement me!
        return handler.handle(invocationContext);
    }

}
