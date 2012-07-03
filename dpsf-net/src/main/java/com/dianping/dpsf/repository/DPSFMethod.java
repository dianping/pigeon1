/**
 * 
 */
package com.dianping.dpsf.repository;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: DPSFMethod.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-30 下午03:05:57   
 */
public class DPSFMethod {
	
	private Method method;
	
	private Object service;
	
	private Class[] parameterClasses;
	
	private int parameterLength;
	
//	private Map<Integer,Integer> matchingValue = new ConcurrentHashMap<Integer,Integer>();
	
	public DPSFMethod(Object service,Method method){
		this.service = service;
		this.method = method;
		this.parameterClasses = regulateTypes(this.method.getParameterTypes());
		this.parameterLength = this.parameterClasses.length;
	}
	
	private Class[] regulateTypes(Class[] types){
		for(int i=0;i<types.length;i++){
			if(types[i] == byte.class){
				types[i] = Byte.class;
			}else if(types[i] == short.class){
				types[i] = Short.class;
			}else if(types[i] == int.class){
				types[i] = Integer.class;
			}else if(types[i] == boolean.class){
				types[i] = Boolean.class;
			}else if(types[i] == long.class){
				types[i] = Long.class;
			}else if(types[i] == float.class){
				types[i] = Float.class;
			}else if(types[i] == double.class){
				types[i] = Double.class;
			}else {
			}
		}
		return types;
	}
	
	public int getParameterSize(){
		return this.parameterLength;
	}
	
	
	/**
	 * 
	 * 返回匹配度
	 * 如果返回值等于参数个数，表示完全匹配
	 * 如果返回值为0---参数个数，表示部分匹配
	 * 如果返回-1，表示有不匹配项
	 * @param paramClassNames
	 * @return
	 * @throws ServiceException
	 */
	public int matching(String[] paramClassNames) throws ServiceException{
		int k = 0;
		for(int i=0;i<paramClassNames.length;i++){
			if(paramClassNames[i].equals(Constants.TRANSFER_NULL)){
				continue;
			}
			Class paramClass = null;
			try {
				paramClass = Class.forName(paramClassNames[i]);
				
			} catch (ClassNotFoundException e) {
				throw new ServiceException("no class:"+paramClassNames[i]+" for parameter");
			}
			if(paramClass == this.parameterClasses[i]){
				k++;
			}
			if(!this.parameterClasses[i].isAssignableFrom(paramClass)){
				return -1;
			}
		}
		return k;
	}
	
	public Method getMethod(){
		return this.method;
	}

	/**
	 * @return the service
	 */
	public Object getService() {
		return service;
	}

}
