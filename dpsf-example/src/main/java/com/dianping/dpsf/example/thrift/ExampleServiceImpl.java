/**
 * 
 */
package com.dianping.dpsf.example.thrift;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.dianping.dpsf.example.java.JavaExampleServiceImpl;

/**    
 * <p>    
 * Title: ExampleServiceImpl.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-16 上午09:42:21   
 */
public class ExampleServiceImpl implements ExampleService.Iface{
	
	private static Logger logger = Logger.getLogger(ExampleServiceImpl.class);

	int k=0;
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.thrift.ExampleService.Iface#getMoney(com.dianping.dpsf.example.thrift.Parameter, java.lang.String)
	 */
	public ReturnValue getMoney(Parameter parameter, String name)
			throws InvalidOperation,TException {
//		throw new RuntimeException("$$$$$$$$$$$$$$$$$");
		k++;
		if(k%1000==0)logger.info("Server:"+k);
		return new ReturnValue(55);
	}
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.thrift.ExampleService.Iface#test()
	 */
	public void test() throws TException {
		k++;
		if(k%1000==0)logger.info("Server:"+k);
	}

}
