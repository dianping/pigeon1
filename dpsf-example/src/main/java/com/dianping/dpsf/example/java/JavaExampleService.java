/**
 * 
 */
package com.dianping.dpsf.example.java;

import java.util.List;

import com.dianping.dpsf.example.java.ExampleException;
import com.dianping.dpsf.example.java.ParameterVal;
import com.dianping.dpsf.example.java.ReturnVal;

/**    
 * <p>    
 * Title: JavaExampleService.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 下午05:44:40   
 */
public interface JavaExampleService {
	
	public List<ReturnVal> testService(int id,String name,ParameterVal parameter) throws ExampleException;
	public ReturnVal testService(String name,ParameterVal parameter);
	public void testService();

}
