/**
 * 
 */
package com.dianping.dpsf.thread;



/**    
 * <p>    
 * Title: CycThreadPool.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-16 下午09:22:35   
 */
public final class CycThreadPool extends DPSFThreadPool{
	
	private static DPSFThreadPool threadPool;
	
	
	private CycThreadPool(String poolName){
		super(poolName);
	}
	
	public static DPSFThreadPool getPool(){
		if(threadPool == null){
			threadPool = new CycThreadPool("DPSF-ThreadPool-Cyc");
		}
		return threadPool;
	}

}
