/**
 * 
 */
package com.dianping.dpsf.exception;

/**    
 * <p>    
 * Title: NoConnectionException.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-7-6 下午09:28:21   
 */
public class NoConnectionException extends NetException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7540546627782984539L;

	public NoConnectionException(String msg){
		super(msg);
	}

}
