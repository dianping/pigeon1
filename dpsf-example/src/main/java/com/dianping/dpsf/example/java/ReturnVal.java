/**
 * 
 */
package com.dianping.dpsf.example.java;

import java.io.Serializable;

/**    
 * <p>    
 * Title: ReturnValue.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 下午05:46:26   
 */
public class ReturnVal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2962627112008263271L;
	private String value;
	
	public ReturnVal(){}
	
	public ReturnVal(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
