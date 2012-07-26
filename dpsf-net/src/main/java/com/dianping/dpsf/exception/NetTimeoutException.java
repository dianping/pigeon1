package com.dianping.dpsf.exception;

/**    
  * <p>    
  * Title: NetException.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:46   
  */ 
public class NetTimeoutException extends NetException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1961488305051802648L;

	public NetTimeoutException(){
		super();
	}
	
	public NetTimeoutException(String message){
		super(message);
	}
	
	public NetTimeoutException(Throwable cause){
		super(cause);
	}
	
	public NetTimeoutException(String message, Throwable cause){
		super(message,cause);
	}

}
