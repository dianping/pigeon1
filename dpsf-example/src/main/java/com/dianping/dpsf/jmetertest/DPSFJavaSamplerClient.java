/**
 * 
 */
package com.dianping.dpsf.jmetertest;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//import com.dianping.cat.Cat;
import com.dianping.dpsf.example.BeansConfParser;

/**    
 * <p>    
 * Title: DPSFJavaSamplerClient.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-12 下午01:32:47   
 */
public class DPSFJavaSamplerClient extends AbstractJavaSamplerClient{
	
	private final static String BEAN_SERVICES_NAME = "jmeter/jmeter-beans.xml";
	
	private static JmeterTestJavaIFace hessianBean; 
	
	private  int serializable = 0;
	
	private static String catClientXml;
	
	private ThreadLocal<Boolean> tl = new ThreadLocal<Boolean>();
	
	private static Logger log = Logger.getLogger(DPSFJavaSamplerClient.class);
	static{
		ApplicationContext beanFactory = null;
		try {
			beanFactory = new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(BEAN_SERVICES_NAME));
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hessianBean = (JmeterTestJavaIFace)beanFactory.getBean("jmeterClient");
		
//		if (catClientXml == null) {
//			catClientXml = "/data/appdatas/cat/client.xml";
//		}
//		
//		Cat.initialize(new File(catClientXml));
		//for background thread
//		Cat.setup(null);
	}
	
	public void setupTest(JavaSamplerContext arg0) {
 
        	String seriStr = arg0.getParameter("serializable", "java").toLowerCase();
            if(seriStr.equals("java")){
            	serializable = 1;
            }else if(seriStr.equals("hessian")){
            	serializable = 2;
            }else if(seriStr.equals("pb")){
            	serializable = 3;
            }else if(seriStr.equals("thrift")){
            	serializable = 4;
            }
        
    }
	
	public void init(){
		//Cat.
		//
		//
		//
	}
	
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument("serializable", "java");  
		return params;
	} 
static AtomicInteger count = new AtomicInteger(0);
	/* (non-Javadoc)
	 * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
	 */
	public SampleResult runTest(JavaSamplerContext arg0) {
		
//			Cat.setup("fdfsdfsdfdsfsdfljmlfmc.v,mvlsjkflsml.");
			
			SampleResult result = new SampleResult();
			result.sampleStart();
			result.setSuccessful(true);
			for(int i=0;i<100;i++){
				try{
					hessianBean.jmeterTest(1);
					
				}catch(Exception e){
					int c = count.incrementAndGet();
					if(c%100==0){
						result.setSuccessful(false);
					}
				}
			}
			result.sampleEnd();
//					Cat.reset();
			return result;
		
	}
	
	

}
