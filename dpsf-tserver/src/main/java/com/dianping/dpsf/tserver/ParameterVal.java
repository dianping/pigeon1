/**
 * 
 */
package com.dianping.dpsf.tserver;

import java.io.Serializable;

/**    
 * <p>    
 * Title: Parameter.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 下午05:47:26   
 */
public class ParameterVal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3874680771383308304L;
	
	private String value;
	public ParameterVal(){}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	public ParameterVal(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
