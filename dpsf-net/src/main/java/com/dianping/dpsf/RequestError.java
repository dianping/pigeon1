/**
 * 
 */
package com.dianping.dpsf;

/**    
 * <p>    
 * Title: Error.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-24 下午02:57:06   
 */
public enum RequestError {
	
	NOCONNECTION("no connect for use"),
	
	TIMEOUT("request timeout"),
	
	CHANNELFAIL("request error");
	
	private String errorMsg;
	
	private RequestError(String errorMsg){
		this.errorMsg = errorMsg;
	}
	
	public String getMsg(){
		return this.errorMsg;
	}

}
