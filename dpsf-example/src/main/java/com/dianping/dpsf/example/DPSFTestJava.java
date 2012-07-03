/**
 * 
 */
package com.dianping.dpsf.example;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.example.java.JavaExampleService;
import com.dianping.dpsf.example.java.JavaExampleServiceImpl;
import com.dianping.dpsf.example.java.ParameterVal;

/**    
 * <p>    
 * Title: NettyTest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-9 上午10:40:07   
 */
public class DPSFTestJava {
	
	private static Logger logger = Logger.getLogger(DPSFTestJava.class);
	
	private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws Exception{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		JavaExampleService bean = (JavaExampleService)beanFactory.getBean("exampleSyncJava");
		ParameterVal param = new ParameterVal("parameter");
//		Thread.sleep(5000);
		System.out.println("Client:start");
		int k=0;
		DOMConfigurator.configure(Thread.currentThread()
                .getContextClassLoader().getResource("log4j.xml"));
		while(true){
			logger.error("Client1 test Exception",new Exception("Client1 Exception"));
			try{
//				ReturnVal rv = null;
//				List<ReturnVal> rs = bean.testService(1, "test", param);
//				k++;
//				rv = bean.testService("test", param);
//				k++;
				bean.testService();
				k++;
				System.out.println(">>>"+k);
//				if(k%1000==0)System.out.println("Client:"+k);
			}catch(Exception e){
				e.printStackTrace();
			}
//			logger.error("Client2 test Exception",new Exception("Client2 Exception"));
			Thread.sleep(1000);
		}
	}

}
