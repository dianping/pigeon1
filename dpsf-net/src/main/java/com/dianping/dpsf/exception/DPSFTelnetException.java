/**
 * 
 */
package com.dianping.dpsf.exception;

/**    
 * <p>    
 * Title: DPSFRuntimeException.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-30 上午10:26:08   
 */
public class DPSFTelnetException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8759281075117453873L;

	public DPSFTelnetException(String msg){
		super(msg);
	}
	
	public DPSFTelnetException(Throwable cause){
		super(cause);
	}

}
