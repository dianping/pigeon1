/**
 * 
 */
package com.dianping.dpsf.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import com.dianping.dpsf.control.PigeonConfig;

/**    
  * <p>    
  * Title: DPSFThreadPool.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-20 上午11:35:54   
  */ 
public class DPSFThreadPool {
	
	private String name;
	
	private ThreadPoolExecutor executor ;
	private DefaultThreadFactory factory;
	
	protected DPSFThreadPool(String poolName){
		this.name = poolName;
		this.executor = (ThreadPoolExecutor)Executors.newCachedThreadPool(new DefaultThreadFactory(poolName));
	}
	
	protected DPSFThreadPool(String poolName,int corePoolSize,int maximumPoolSize){
		
		this(poolName,corePoolSize,maximumPoolSize,new SynchronousQueue<Runnable>());
	}
	
	protected DPSFThreadPool(String poolName,int corePoolSize,int maximumPoolSize,
			BlockingQueue<Runnable> workQueue){
		this(poolName,corePoolSize,maximumPoolSize,workQueue,new AbortPolicy());
	}
	
	public DPSFThreadPool(String poolName, int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue,
			RejectedExecutionHandler handler) {
		this.name = poolName;
		this.factory = new DefaultThreadFactory(this.name);
		if(PigeonConfig.isUseNewInvokeLogic()) {
			this.executor = new DPSFThreadPoolExecutor(corePoolSize, maximumPoolSize,
					60, TimeUnit.SECONDS, this.factory,handler);
		}else{
			this.executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
					 20L, TimeUnit.SECONDS,
					 workQueue,
	                 this.factory,
	                 handler);
		}
	}

	public void execute(Runnable run){
		this.executor.execute(run);
	}
	
	public <T> Future<T> submit(Callable<T> call){
		return this.executor.submit(call);
	}
	
	public Future<?> submit(Runnable run){
		return this.executor.submit(run);
	}
	
	public ThreadPoolExecutor getExecutor(){
		return this.executor;
	}

	/**
	 * @return the factory
	 */
	public DefaultThreadFactory getFactory() {
		return factory;
	}

}
