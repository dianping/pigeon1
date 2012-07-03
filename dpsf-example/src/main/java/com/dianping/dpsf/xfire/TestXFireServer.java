/**
 * 
 */
package com.dianping.dpsf.xfire;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.example.BeansConfParser;

/**    
 * <p>    
 * Title: TestXFireServer.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-4-20 上午01:52:09   
 */
public class TestXFireServer {
	
private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws Exception{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
	} 
}
