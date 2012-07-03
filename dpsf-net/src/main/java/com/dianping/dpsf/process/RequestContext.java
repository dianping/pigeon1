/**
 * 
 */
package com.dianping.dpsf.process;

import java.util.concurrent.Future;

/**    
 * <p>    
 * Title: RequestContext.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-6-16 下午03:17:28   
 */
public class RequestContext {
	
	private Future future;
	private Thread thread;
	private String host;
	
	public RequestContext(String host){
		this.host = host;
	}
	
	public RequestContext(Future future,String host){
		this.future = future;
		this.host = host;
	}

	/**
	 * @return the future
	 */
	public Future getFuture() {
		return future;
	}

	/**
	 * @param future the future to set
	 */
	public void setFuture(Future future) {
		this.future = future;
	}

	/**
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * @param thread the thread to set
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
