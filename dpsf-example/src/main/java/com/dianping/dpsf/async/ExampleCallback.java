/**
 * 
 */
package com.dianping.dpsf.async;

import com.dianping.dpsf.exception.DPSFException;

/**    
 * <p>    
 * Title: ExampleCallback.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-22 上午12:30:19   
 */
public class ExampleCallback implements ServiceCallback{

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#callback(java.lang.Object)
	 */
	@Override
	public void callback(Object result) {
//		System.out.println(result+"****");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#serviceException(java.lang.Exception)
	 */
	@Override
	public void serviceException(Exception e) {
		e.printStackTrace();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#frameworkException(com.dianping.dpsf.exception.DPSFException)
	 */
	@Override
	public void frameworkException(DPSFException e) {
		e.printStackTrace();
	}


}
