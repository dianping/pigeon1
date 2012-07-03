/**
 * 
 */
package com.dianping.dpsf.exception;

/**    
 * <p>    
 * Title: ServiceException.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-30 下午02:02:50   
 */
public class ServiceException extends Exception{
	
	public ServiceException(){
		super();
	}
	
	public ServiceException(String msg){
		super(msg);
	}
	
	public ServiceException(String msg,Throwable cause){
		super(msg,cause);
	}

}
