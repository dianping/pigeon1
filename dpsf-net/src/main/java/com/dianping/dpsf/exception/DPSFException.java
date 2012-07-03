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
public class DPSFException extends RuntimeException{
	
	private String serviceName;
	private String address;
	public DPSFException(String msg){
		super(msg);
	}
	public DPSFException(String serviceName,String address,String msg,Throwable cause){
		super(msg,cause);
		this.serviceName = serviceName;
		this.address = address;
	}
	public DPSFException(){
		super();
	}
	
	public DPSFException(Throwable cause){
		super(cause);
	}
	
	public DPSFException(String message, Throwable cause){
		super(message,cause);
	}
}
