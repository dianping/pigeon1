/**
 * 
 */
package com.dianping.dpsf.component.impl;

import java.util.concurrent.TimeUnit;

import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.NoSupportedException;
import com.dianping.dpsf.net.channel.Client;

/**    
 * <p>    
 * Title: DefaultDPSFFuture.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-24 上午11:27:13   
 */
public class DefaultDPSFFuture implements DPSFFuture{

	private Client client;

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#get()
	 */
	public DPSFResponse get() {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#get(long)
	 */
	public DPSFResponse get(long timeoutMills) {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#get(long, java.util.concurrent.TimeUnit)
	 */
	public DPSFResponse get(long timeout, TimeUnit unit) {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#cancel()
	 */
	public boolean cancel() {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#isCancelled()
	 */
	public boolean isCancelled() {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFFuture#isDone()
	 */
	public boolean isDone() {
		throw new NoSupportedException();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFFuture#setClient(com.dianping.dpsf.net.channel.Client)
	 */
	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Client getClient() {
		return this.client;
	}
	
}
