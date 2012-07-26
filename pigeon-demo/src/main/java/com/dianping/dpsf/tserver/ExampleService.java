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
	
	public List<ReturnVal> service(int id,String name,ParameterVal parameter) throws ExampleException;
	
	public ReturnVal service(String name,ParameterVal parameter);
	
	public void longTimeService();

}
