/**
 * 
 */
package com.dianping.dpsf.async;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.exception.DPSFException;

/**    
 * <p>    
 * Title: ServiceFutureFactory.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-22 上午09:45:27   
 */
public class ServiceFutureFactory {
	
	private static Logger log = DPSFLog.getLogger();
	private static ThreadLocal<ServiceFuture> threadFuture = new ThreadLocal<ServiceFuture>();
	
	public static ServiceFuture getFuture(){
		ServiceFuture future = threadFuture.get();
		threadFuture.remove();
		return future;
	}
	
	public static void setFuture(ServiceFuture future) throws DPSFException{
		if(threadFuture.get() != null) {
			threadFuture.remove();
			String msg = "you must call \"ServiceFutureFactory.getFuture()\" before second call service if you use future call";
			log.error(msg);
            throw new DPSFException(msg);
		}
		threadFuture.set(future);
	}
	
	public static void remove(){
		threadFuture.remove();
	}
	
	/**
	 * 直接返回调用结果，用于异步调用配置情况下的同步调用
	 * @param <T> 返回值类型
	 * @param res 返回值类
	 * @return    调用结果
	 * @throws InterruptedException
	 * @throws DPSFException
	 */
	public static <T> T getResult(Class<T> res) throws InterruptedException, DPSFException{
		return (T)getFuture()._get();
	}
	
	/**
	 * 直接返回调用结果，用于异步调用配置情况下的同步调用
	 * @return 调用结果
	 * @throws InterruptedException
	 * @throws DPSFException
	 */
	public static Object getResult() throws InterruptedException, DPSFException{
		return getFuture()._get();
	}

}
