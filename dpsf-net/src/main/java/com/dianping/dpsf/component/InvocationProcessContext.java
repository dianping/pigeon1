package com.dianping.dpsf.component;

import com.dianping.dpsf.process.ExecutorListener;
import com.dianping.dpsf.repository.ServiceRepository;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: jian.liu Date: 13-1-4 Time: 下午4:07 To
 * change this template use File | Settings | File Templates.
 */
public class InvocationProcessContext extends InvocationContext {

	private Channel channel;
	private ServiceRepository serviceRepository;
	private Throwable serviceError;

	public InvocationProcessContext(DPSFRequest request, Channel channel,
			ServiceRepository serviceRepository) {
		this.request = request;
		this.channel = channel;
		this.serviceRepository = serviceRepository;
	}

	public Channel getChannel() {
		return channel;
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
}
