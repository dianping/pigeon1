/**
 * 
 */
package com.dianping.dpsf.jmetertest;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**    
 * <p>    
 * Title: MainTest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-12 下午03:58:15   
 */
public class MainTest {
	
	private final static String BEAN_SERVICES_NAME = "jmeter-client.xml";
	
	private static JmeterTestJavaIFace hessianBean;
	
	private static ExecutorService es;
	
	private static String catClientXml = "D://data/client.xml";
	
	static{
		ApplicationContext beanFactory = null;
		try {
			String path = new File(BEAN_SERVICES_NAME).getAbsolutePath();
			System.out.println(path);
			beanFactory = new ClassPathXmlApplicationContext(BEAN_SERVICES_NAME);
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		hessianBean = (JmeterTestJavaIFace)beanFactory.getBean("jmeterClient");
		
		es = Executors.newFixedThreadPool(100);
		
//		if (catClientXml == null) {
//			catClientXml = "/data/appdatas/cat/client.xml";
//		}
//		
//		Cat.initialize(new File(catClientXml));
		//for background thre
//		Cat.setup(null);
	}
	
	public static void main(String[] args){
		
		
//		Cat.setup("fdfsdfsdfdsfsdfljmlfmc.v,mvlsjkflsml.");
			hessianBean.jmeterTest(1);
//		Cat.reset();
//		DPSFJavaSamplerClient client = new DPSFJavaSamplerClient();
//		client.runTest(null);
//		for(int i = 0;i<100;i++){
//		}
		
	}
	
}
