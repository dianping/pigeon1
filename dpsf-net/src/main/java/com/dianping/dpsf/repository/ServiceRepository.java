/**
 * 
 */
package com.dianping.dpsf.repository;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.zookeeper.ZooKeeperManager;


/**    
 * <p>    
 * Title: ServiceRepository.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-26 下午04:12:18   
 */
public class ServiceRepository {
	
	private Map<String,ServiceMethods> methods = new ConcurrentHashMap<String,ServiceMethods>();
	
	private Set<String> ingoreMethods = new HashSet<String>();
	
	private Map<String,Object> services = new ConcurrentHashMap<String,Object>();
	
	private final static String CONNECTOR = "_";
	
	public ServiceRepository(){
		Method[] objectMethodArray = Object.class.getMethods();
		for(Method method : objectMethodArray){
			this.ingoreMethods.add(method.getName());
		}
		
		Method[] classMethodArray = Class.class.getMethods();
		for(Method method : classMethodArray){
			this.ingoreMethods.add(method.getName());
		}
	}
	
	
	public void registerService(String serviceName,Object service) throws ClassNotFoundException{
		this.services.put(serviceName, service);
		ZooKeeperManager.getInstance().register(serviceName);
	}
	
	public Object getService(String serviceName){
		return this.services.get(serviceName);
	}
	
	public Collection<String> getServiceNames() {
		return this.services.keySet();
	}
	
	public DPSFMethod getMethod(String serviceName,String methodName,String[] paramClassNames) throws ServiceException{
		ServiceMethods serviceMethods = this.methods.get(serviceName);
		if(serviceMethods == null){
			synchronized(this){
				serviceMethods = this.methods.get(serviceName);
				if(serviceMethods == null){
					Object service = this.services.get(serviceName);
					if(service == null){
						throw new ServiceException("cant not find serivce for serviceName:"+serviceName);
					}
					Method[] methodArray = service.getClass().getMethods();
					serviceMethods = new ServiceMethods(serviceName,service);
					for(Method method : methodArray){
						if(!this.ingoreMethods.contains(method.getName())){
							method.setAccessible(true);
							serviceMethods.addMethod(method.getName(),new DPSFMethod(service,method));
						}
					}
					this.methods.put(serviceName, serviceMethods);
				}
			}
		}
		return serviceMethods.getMethod(methodName,new DPSFParam(paramClassNames));
	}

}
