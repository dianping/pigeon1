/**
 * 
 */
package com.dianping.dpsf.example;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.example.java.JavaExampleService;
import com.dianping.dpsf.example.java.ParameterVal;
import com.dianping.dpsf.example.java.ReturnVal;

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
public class DPSFTestHessian {
	
	private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws Throwable{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		JavaExampleService bean = (JavaExampleService)beanFactory.getBean("exampleFutureJava");
		ParameterVal param = new ParameterVal("parameter");
//		Thread.sleep(2000000);
		int k = 0;
		while(true){
			try{
			ReturnVal rv = null;
			System.out.println(bean.toString());
			List<ReturnVal> rs = bean.testService(5, "test", param);
			ServiceFuture future = ServiceFutureFactory.getFuture();
			rs = (List<ReturnVal>)future._get();
			if(rs == null){
				System.out.println(null+"**************");
			}else{
				System.out.println(rs.size()+"^^^^^^");
			}
			k++;
			rv = bean.testService("test", param);
			future = ServiceFutureFactory.getFuture();
			rv = (ReturnVal)future._get();
			System.out.println(rv.getValue()+"&&&&&&&&&&");
			k++;
			bean.testService();
			future = ServiceFutureFactory.getFuture();
			Object ro = future._get();
			System.out.println(ro+"%%%%%%");
			k++;
			if(k%1000==0)System.out.println("Client:"+k);
//			Thread.sleep(2000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
