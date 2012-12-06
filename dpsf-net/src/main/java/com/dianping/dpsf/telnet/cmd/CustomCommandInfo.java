/**
 * 
 */
package com.dianping.dpsf.telnet.cmd;

import java.lang.reflect.Method;

import com.dianping.dpsf.telnet.TelnetCommandExecutor;
import com.dianping.dpsf.telnet.TelnetCommandInfo;

/**    
 * <p>    
 * Title: CustomCommandInfo.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-1 下午03:05:57   
 */
public class CustomCommandInfo extends TelnetCommandInfo{
	
	private String beanName;
	
	private Object bean;
	
	private String methodName;
	
	private Method method;
	
	public CustomCommandInfo(String cmd,String beanName,String methodName,
			String description,TelnetCommandExecutor executor){
		super(cmd,description,executor);
		this.beanName = beanName;
		this.methodName = methodName;
	}

	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * @param bean the bean to set
	 */
	public void setBean(Object bean) {
		this.bean = bean;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

}
