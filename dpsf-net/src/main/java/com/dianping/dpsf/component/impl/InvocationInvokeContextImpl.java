package com.dianping.dpsf.component.impl;

import java.lang.reflect.Method;

import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.net.channel.Client;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-4
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class InvocationInvokeContextImpl extends AbstractInvocationContext implements InvocationInvokeContext{

    private DPSFMetaData                    metaData;
    private Method                          method;
    private Object[]                        arguments;
    private Client                          client;

    public InvocationInvokeContextImpl(DPSFMetaData metaData,Method method,Object[] arguments) {
    	super(null);
        this.metaData = metaData;
        this.method = method;
        this.arguments = arguments;
    }

    public DPSFMetaData getMetaData() {
        return metaData;
    }

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public Client getClient() {
		return client;
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

}
