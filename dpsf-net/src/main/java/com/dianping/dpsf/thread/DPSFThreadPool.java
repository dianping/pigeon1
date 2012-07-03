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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		this.name = poolName;
		this.factory = new DefaultThreadFactory(poolName);
		this.executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
												 60L, TimeUnit.SECONDS,
								                 new SynchronousQueue<Runnable>(),
								                 this.factory
								                 );
	}
	
	protected DPSFThreadPool(String poolName,int corePoolSize,int maximumPoolSize,
			BlockingQueue<Runnable> workQueue){
		this.name = poolName;
		this.factory = new DefaultThreadFactory(poolName);
		this.executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
												 60L, TimeUnit.SECONDS,
												 workQueue,
								                 this.factory
								                 );
	}
	
	public DPSFThreadPool(String poolName, int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue,
			RejectedExecutionHandler handler) {
		this.name = poolName;
		this.factory = new DefaultThreadFactory(poolName);
		this.executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
												 60L, TimeUnit.SECONDS,
												 workQueue,
								                 this.factory,
								                 handler);
	}

	public void execute(Runnable run){
		this.executor.execute(run);
	}
	
	public <T> Future<T> submit(Callable<T> call){
		return this.executor.submit(call);
	}
	
	public Future submit(Runnable run){
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
