package com.dianping.dpsf.component;

import org.jboss.netty.channel.Channel;

import com.dianping.dpsf.repository.ServiceRepository;

/**
 * Created with IntelliJ IDEA. User: jian.liu Date: 13-1-4 Time: 下午4:07 To
 * change this template use File | Settings | File Templates.
 */
/**
 * @author xiangbin.miao
 *
 */
public interface InvocationProcessContext extends InvocationContext {

	public ServiceRepository getServiceRepository();

	public Throwable getServiceError();
	
	public void setServiceError(Throwable serviceError);
	
	public Channel getChannel();
	
}
