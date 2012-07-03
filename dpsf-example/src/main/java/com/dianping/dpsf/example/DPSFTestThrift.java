/**
 * 
 */
package com.dianping.dpsf.example;

import org.apache.thrift.TException;
import org.dom4j.DocumentException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.example.thrift.ExampleService;
import com.dianping.dpsf.example.thrift.Parameter;
import com.dianping.dpsf.example.thrift.ReturnValue;

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
public class DPSFTestThrift {
	
	private final static String BEAN_SERVICES_NAME = "project-beans.xml";
	
	public static void main(String[] args) throws BeansException, DocumentException{
		ApplicationContext beanFactory 
		= new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		ExampleService.Iface bean = (ExampleService.Iface)beanFactory.getBean("exampleSyncThrift");
		Parameter param = new Parameter();
		param.setName("bb");
		int k=0;
		while(true){
			ReturnValue rv = null;
			try{
				rv = bean.getMoney(param, "cc");
			}catch (Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage()+"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
			
			if(rv == null){
//				System.out.println(null+"********************************************************");
			}else{
//				System.out.println(rv.getMoney()+"********************************************************");
			}
			k++;
			try {
				bean.test();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			k++;
			if(k%1000==0)System.out.println("Client:"+k);
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

}
