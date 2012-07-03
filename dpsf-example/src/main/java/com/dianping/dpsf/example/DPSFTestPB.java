/**
 * 
 */
package com.dianping.dpsf.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.example.protobuf.DPSFExample.Parameter;
import com.dianping.dpsf.example.protobuf.DPSFExample.ReturnValue;
import com.dianping.dpsf.example.protobuf.DPSFExample.ExampleService.BlockingInterface;

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
public class DPSFTestPB {
	
	private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws Exception{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		BlockingInterface bean = (BlockingInterface)beanFactory.getBean("exampleSyncPB");
		Parameter param = Parameter.newBuilder().setName("test").setAge(2).addBook("book").build();
		
		int k = 0;
		while(true){
//			Thread.sleep(20000000);
			ReturnValue rv = null;
			try{
				rv = bean.getMoney(null, param);
			}catch (Exception e){
//				e.printStackTrace();
				System.out.println(e.getMessage()+"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
			
			if(rv == null){
//				System.out.println(null+"********************************************************");
			}else{
//				System.out.println(rv.getMoney()+"********************************************************");
			}
			k++;
			rv = bean.test(null, param);
			k++;
			if(k%1000==0)System.out.println("Client:"+k);
//			Thread.sleep(2000);
		}
	}

}
