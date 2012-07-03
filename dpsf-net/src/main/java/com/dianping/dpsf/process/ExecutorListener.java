/**
 * 
 */
package com.dianping.dpsf.process;

import com.dianping.dpsf.component.DPSFRequest;

/**    
 * <p>    
 * Title: ExecutorListener.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-26 下午07:52:23   
 */
public interface ExecutorListener {
	
	public void executorCompleted(DPSFRequest request);

}
