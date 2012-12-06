/**
 * 
 */
package com.dianping.dpsf.async;

import com.dianping.dpsf.exception.DPSFException;

/**    
 * <p>    
 * Title: DPSFCallback.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-22 上午12:12:51   
 */
public interface ServiceCallback {
	
	/**
	 * 正常结果返回
	 * @param result
	 */
	public void callback(Object result);
	
	/**
	 * 后端应用Service抛出的异常
	 * @param e
	 */
	public void serviceException(Exception e);

	/**
	 * 通信框架发生异常，没有必要可以不处理
	 * @param e
	 */
	public void frameworkException(DPSFException e);
}
