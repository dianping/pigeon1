/**
 * 
 */
package com.dianping.dpsf.exception;

/**    
 * <p>    
 * Title: NoSupportedException.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-23 下午05:48:49   
 */
public class NoSupportedException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8598189303332401202L;

	public NoSupportedException(){
		super("not supported this operation");
	}

}
