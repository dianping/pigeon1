/**
 * 
 */
package com.dianping.dpsf.tserver;

import java.util.List;

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
public interface ExampleService {
	
	public List<ReturnVal> testService(int id,String name,ParameterVal parameter) throws ExampleException;
	public ReturnVal testService(String name,ParameterVal parameter);
	public void testService();

}
