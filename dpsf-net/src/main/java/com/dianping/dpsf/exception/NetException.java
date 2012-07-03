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
public class NetException extends DPSFException{
	
	public NetException(){
		super();
	}
	
	public NetException(String message){
		super(message);
	}
	
	public NetException(Throwable cause){
		super(cause);
	}
	
	public NetException(String message, Throwable cause){
		super(message,cause);
	}

}
