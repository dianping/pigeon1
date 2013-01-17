package com.dianping.dpsf.component;

import java.lang.reflect.Method;

import com.dianping.dpsf.net.channel.Client;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-4
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public interface InvocationInvokeContext extends InvocationContext {
	
	public DPSFMetaData getMetaData();
	
	public Method getMethod();

	public Object[] getArguments();
	
	public Client getClient();
	
	public void setClient(Client client);
	
}
