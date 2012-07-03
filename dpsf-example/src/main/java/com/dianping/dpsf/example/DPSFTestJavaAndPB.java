/**
 * 
 */
package com.dianping.dpsf.example;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.example.java.JavaExampleService;
import com.dianping.dpsf.example.java.ParameterVal;
import com.dianping.dpsf.example.java.ReturnVal;
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
public class DPSFTestJavaAndPB {
	
	private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws Exception{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		JavaExampleService javaBean = (JavaExampleService)beanFactory.getBean("exampleSyncJava");
		ParameterVal param = new ParameterVal("parameter");
		
		BlockingInterface pbBean = (BlockingInterface)beanFactory.getBean("exampleSyncPB");
		Parameter paramPB = Parameter.newBuilder().setName("test").setAge(2).addBook("book").build();
		int k=0;
		Thread.sleep(2000000);
		while(true){
			long now = System.nanoTime();
			List<ReturnVal> rva = null;
			try{
				rva = javaBean.testService(1, "test", param);
			}catch(Exception e){
				e.printStackTrace();
			}
			
//			System.out.println("Java time:"+(System.nanoTime()-now));
			if(rva == null){
				System.out.println(null+"###############");
			}else{
//				System.out.println(rva.getValue()+"###############");
			}
			
			now = System.nanoTime();
			ReturnValue rv = pbBean.getMoney(null, paramPB);
//			System.out.println("PB time:"+(System.nanoTime()-now));
			if(rv == null){
				System.out.println(null+"%%%%%%%%%%%%%%%%%%%%%");
			}else{
//				System.out.println(rv.getMoney()+"%%%%%%%%%%%%%%%%%%%%%");
			}
			k++;
			if(k%1000==0)System.out.println("Client:"+k);
//			Thread.sleep(2000);
		}
	}

}
