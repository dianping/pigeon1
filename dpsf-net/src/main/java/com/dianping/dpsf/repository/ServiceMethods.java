/**
 * 
 */
package com.dianping.dpsf.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: NameMethods.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-30 上午10:59:37   
 */
public class ServiceMethods {
	
	/**
	 * 根据方法名和参数个数Map方法集合
	 */
	private Map<String,Map<Integer,List<DPSFMethod>>> methods 
		= new ConcurrentHashMap<String,Map<Integer,List<DPSFMethod>>>();
	
	private Map<String,Map<DPSFParam,DPSFMethod>> bestMacthMethod 
		= new ConcurrentHashMap<String,Map<DPSFParam,DPSFMethod>>();
	
	private DPSFMethod currentMethod;
	
	private int methodSize = 0;
	
	private Object service;
	
	private String serviceName;
	
	public ServiceMethods(String serviceName,Object service){
		this.serviceName = serviceName;
		this.service = service;
	}
	
	void addMethod(String methodName,DPSFMethod method){
		if(this.currentMethod == null){
			this.currentMethod = method;
		}
		Map<Integer,List<DPSFMethod>> methodMap = this.methods.get(methodName);
		if(methodMap == null){
			methodMap = new HashMap<Integer,List<DPSFMethod>>();
			this.methods.put(methodName, methodMap);
		}
		List<DPSFMethod> methodList = methodMap.get(method.getParameterSize());
		if(methodList == null){
			methodList = new ArrayList<DPSFMethod>();
			methodMap.put(method.getParameterSize(), methodList);
		}
		methodList.add(method);
		methodSize++;
	}
	public DPSFMethod getMethod(String methodName,DPSFParam paramNames) throws ServiceException{
		if(methodSize == 1){
			return this.currentMethod;
		}else{
			DPSFMethod method = getBestMatchMethodForCache(methodName,paramNames);
			if(method == null){
				synchronized(this){
					method = getBestMatchMethodForCache(methodName,paramNames);
					if(method == null){
						method = getBestMatchMethod(methodName,paramNames);
						this.bestMacthMethod.get(methodName).put(paramNames, method);
					}
				}
			}
			return method;
		}
	}
	
	private DPSFMethod getBestMatchMethodForCache(String methodName,DPSFParam paramNames){
		Map<DPSFParam,DPSFMethod> paramMethodMap = this.bestMacthMethod.get(methodName);
		if(paramMethodMap == null){
			paramMethodMap = new HashMap<DPSFParam,DPSFMethod>();
			this.bestMacthMethod.put(methodName, paramMethodMap);
		}
		return paramMethodMap.get(paramNames);
	}
	
	private DPSFMethod getBestMatchMethod(String methodName,DPSFParam paramNames) throws ServiceException{
		
		Map<Integer,List<DPSFMethod>> methodMap = this.methods.get(methodName);
		if(methodMap == null){
			throw new ServiceException("Service  serviceName:"+this.service+" is not this method for name:"+methodName);
		}
		List<DPSFMethod> methodList = methodMap.get(paramNames.getLength());
		if(methodList == null || methodList.size() == 0){
			throw new ServiceException("Service  serviceName:"+this.service+
					" is not this method:"+methodName+" for "+paramNames.getLength()+" parameters");
		}
		if(paramNames.getLength() == 0){
			return methodList.get(0);
		}
		int matchingValue = -1;
		DPSFMethod bestMethod = null;
		
		for(DPSFMethod dpsfm : methodList){
			int mv = dpsfm.matching(paramNames.getParamNames());
			if(mv > matchingValue){
				matchingValue = mv;
				bestMethod = dpsfm;
			}
		}
		if(matchingValue < 0){
			throw new ServiceException("Service  serviceName:"+this.service
					+" is not this method:"+methodName+" for parameter class types");
		}
		return bestMethod;
	}
}
