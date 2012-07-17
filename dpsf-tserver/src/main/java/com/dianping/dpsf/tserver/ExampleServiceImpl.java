/**
 * 
 */
package com.dianping.dpsf.tserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**    
 * <p>    
 * Title: JavaExampleServiceImpl.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 下午05:50:18   
 */
public class ExampleServiceImpl implements ExampleService{

	private static Logger logger = Logger.getLogger(ExampleServiceImpl.class);
	
	private String dd;
	
	int k=0;
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.java.JavaExampleService#testService(int, java.lang.String, com.dianping.dpsf.example.java.Parameter)
	 */
	public List<ReturnVal> testService(int id, String name, ParameterVal parameter)  throws ExampleException{
//		System.out.printl("id: "+id+"  name: "+name+" parameter value: "+parameter.getValue()+"###############");
		
//		throw new ExampleException("%%%%%%%%%%%%%%%%%%%%%%%%%%");
		k++;
		if(k%1000==0)logger.info("Server:"+k);
		List<ReturnVal> rs = new ArrayList<ReturnVal>();
		for(int i=0;i<id;i++){
			rs.add(new ReturnVal("returnerewgfxcgdfgdfgdfgdfgg::::::"+id));
		}
		System.out.println(id+"&&&&&&");
		return rs;
	}
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.java.JavaExampleService#testService(java.lang.String, com.dianping.dpsf.example.java.ParameterVal)
	 */
	public ReturnVal testService(String name, ParameterVal parameter) {
//		throw new RuntimeException();
		k++;
		if(k%1000==0)logger.info("Server:"+k);
		System.out.println(name+"&&&&&&");
//		try {
//			Thread.sleep(5000000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return new ReturnVal("return");
	}
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.example.java.JavaExampleService#testService()
	 */
	public void testService() {
//		for(int i=0;i<90000;i++){
//			for(int j=0;j<90000;j++)
//			"fsfsdfsdfdsfsdfdf".hashCode();
//		}
//		logger.error("Server test Exception",new Exception("Server Exception"));
		k++;
//		System.out.println(ex);
//		if(k%1000==0)logger.info("Server:"+k);
		System.out.println(k+"$$$$$"+dd);
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @return the dd
	 */
	public String getDd() {
		return dd;
	}
	/**
	 * @param dd the dd to set
	 */
	public void setDd(String dd) {
		this.dd = dd;
	}

}
