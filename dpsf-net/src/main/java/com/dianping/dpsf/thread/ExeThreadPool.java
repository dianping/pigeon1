/**
 * 
 */
package com.dianping.dpsf.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

/**    
  * <p>    
  * Title: ExeThreadPool.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-20 上午11:35:42   
  */ 
public class ExeThreadPool extends DPSFThreadPool{
	
	
	public ExeThreadPool(String poolName){
		super(poolName);
	}
	public ExeThreadPool(String poolName,int corePoolSize,int maximumPoolSize){
		super(poolName,corePoolSize,maximumPoolSize);
	}

	public ExeThreadPool(String poolName,int corePoolSize,int maximumPoolSize,
			BlockingQueue<Runnable> workQueue){
		super(poolName,corePoolSize,maximumPoolSize,workQueue);
	}
	
	public ExeThreadPool(String poolName,int corePoolSize,int maximumPoolSize, BlockingQueue<Runnable> workQueue,
			RejectedExecutionHandler handler){
		super(poolName,corePoolSize,maximumPoolSize,workQueue, handler);
	}
	
}
