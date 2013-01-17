package com.dianping.dpsf.component.impl;

import org.jboss.netty.channel.Channel;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.repository.ServiceRepository;

/**
 * Created with IntelliJ IDEA. User: jian.liu Date: 13-1-4 Time: 下午4:07 To
 * change this template use File | Settings | File Templates.
 */
/**
 * @author xiangbin.miao
 *
 */
public class InvocationProcessContextImpl extends AbstractInvocationContext implements InvocationProcessContext{

	private ServiceRepository serviceRepository;
	private Throwable serviceError;
	private Channel channel;

	public InvocationProcessContextImpl(DPSFRequest request, Channel channel,
			ServiceRepository serviceRepository) {
		super(request);
		this.channel = channel;
		this.serviceRepository = serviceRepository;
	}

	public ServiceRepository getServiceRepository() {
		return serviceRepository;
	}

	public Throwable getServiceError() {
		return serviceError;
	}

	public void setServiceError(Throwable serviceError) {
		this.serviceError = serviceError;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

}
