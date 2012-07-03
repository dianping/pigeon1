/**
 * 
 */
package com.dianping.dpsf.example.protobuf;

import org.apache.log4j.Logger;

import com.dianping.dpsf.example.java.JavaExampleServiceImpl;
import com.dianping.dpsf.example.protobuf.DPSFExample.Parameter;
import com.dianping.dpsf.example.protobuf.DPSFExample.ReturnValue;
import com.dianping.dpsf.example.protobuf.DPSFExample.ExampleService.BlockingInterface;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/**    
 * <p>    
 * Title: ExampleServiceImpl.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-2 下午03:26:35   
 */
public class ExampleServiceImpl implements BlockingInterface{

	private static Logger logger = Logger.getLogger(ExampleServiceImpl.class);
	
	int k=0;
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.protobuf.DPSFExample.ExampleService.BlockingInterface#getMoney(com.google.protobuf.RpcController, com.dianping.dpsf.example.protobuf.DPSFExample.Parameter)
	 */
	public ReturnValue getMoney(RpcController controller, Parameter request)
			throws ServiceException {
//		throw new RuntimeException("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//		System.out.println(request.getName()+"%%%%%%%%%%%%%%%%%%%%%");
		k++;
		if(k%1000==0)logger.info("Server:"+k);
		return ReturnValue.newBuilder().setMoney(3).build();
	}
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.protobuf.DPSFExample.ExampleService.BlockingInterface#test(com.google.protobuf.RpcController, com.dianping.dpsf.example.protobuf.DPSFExample.Parameter)
	 */
	public ReturnValue test(RpcController controller, Parameter request)
			throws ServiceException {
		k++;
		if(k%1000==0)logger.info("Server:"+k);
		return ReturnValue.newBuilder().setMoney(3).build();
	}

}
