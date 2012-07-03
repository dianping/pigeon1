/**
 * 
 */
package com.dianping.dpsf.component;

import java.util.concurrent.TimeUnit;

import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: DPSFFuture.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:58:16   
 */
public interface DPSFFuture extends DPSFCall{
	
	public DPSFResponse get() throws InterruptedException, NetException;

	public DPSFResponse get(long timeoutMillis) throws InterruptedException, NetException;
	
	public DPSFResponse get(long timeout, TimeUnit unit) throws InterruptedException, NetException;
	
	public boolean cancel();

	public boolean isCancelled();

	public boolean isDone();


}
