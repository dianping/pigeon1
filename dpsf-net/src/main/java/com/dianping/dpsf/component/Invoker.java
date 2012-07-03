/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: Invoker.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-18 下午03:14:21   
 */
public interface Invoker {
	
	public DPSFResponse invokeSync(DPSFRequest request,
			DPSFMetaData metaData,DPSFController controller)throws NetException, InterruptedException;
	
	public void invokeCallback(DPSFRequest request,
			DPSFMetaData metaData,DPSFController controller,DPSFCallback callback)throws NetException;
	
	public DPSFFuture invokeFuture(DPSFRequest request,
			DPSFMetaData metaData,DPSFController controller)throws NetException;
	
	public void invokeOneway(DPSFRequest request,
			DPSFMetaData metaData,DPSFController controller)throws NetException;
	
	public void invokeReponse(DPSFResponse response);

}
