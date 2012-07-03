/**
 * 
 */
package com.dianping.dpsf.api;

import com.dianping.dpsf.example.java.JavaExampleService;
import com.dianping.dpsf.example.java.JavaExampleServiceImpl;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: APITest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-7-7 下午06:38:58   
 */
public class APITest {
	
	public static void main(String[] args) throws Exception{
		ServiceRegistry sr = new ServiceRegistry(2000);
		sr.register("test", new JavaExampleServiceImpl());
		sr.init();
		
		ProxyFactory<JavaExampleService> pf = new ProxyFactory<JavaExampleService>();
		
		pf.setIface(JavaExampleService.class);
		pf.setHosts("127.0.0.1:2000");
		pf.setServiceName("test");
		pf.init();
		JavaExampleService proxy = pf.getProxy();
		proxy.testService();
	}

}
