/**
 * 
 */
package com.dianping.dpsf.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.dianping.dpsf.invoke.RemoteInvocationHandlerFactory;
import com.dianping.dpsf.process.filter.InvocationProcessFilter;
import com.dianping.dpsf.spring.PigeonBootStrap;
import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.netty.server.NettyServer;
import com.dianping.dpsf.repository.ServiceRepository;


/**    
 * <p>    
 * Title: ServiceBeanFactory.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-26 上午10:43:19   
 */
public class ServiceRegistry {
	
	private static Logger logger = DPSFLog.getLogger();
	
	private Map<String,Object> services;
	
	private int port = 20000;
	
	private ServiceRepository sr;
	
	private String serviceType = "dp";
	
	public static boolean isInit = false;
	
	private int corePoolSize = 100;
	private int maxPoolSize = 2000;
	private int workQueueSize = 100;
    private Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> customizedInvocationFilters;
	
	public ServiceRegistry(){
		
	}
	
	public ServiceRegistry(int port){
		this.port = port;
	}
	
	public void init() throws ClassNotFoundException {
		if("dp".equals(this.serviceType.trim().toLowerCase())){
			initDPService();
		}else if("ws".equals(this.serviceType.trim().toLowerCase())){
			initWSService();
		}else{
			throw new RuntimeException("serviceType is error:"+this.serviceType);
		}
		
	}
	
	public static void defaultInit(){
		try {
			new ServiceRegistry().initDPService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDPService() throws ClassNotFoundException {
		isInit = true;
        PigeonBootStrap.setupServer();
		this.sr = new ServiceRepository();
		com.dianping.dpsf.net.channel.Server server = new NettyServer(port, corePoolSize, maxPoolSize, workQueueSize,
                this.sr, RemoteInvocationHandlerFactory.createProcessHandler(customizedInvocationFilters));
		server.start();
		logger.info("DPSF Server start ************");
		
		if(this.services != null){
			for(String serviceName : this.services.keySet()){
				this.sr.registerService(serviceName, this.services.get(serviceName));
			}
		}
	}
	
	private void initWSService() {
		
//		Class wsClazz = Class.forName("com.dianping.dpsf.spring.WSInit");
//		Method[] ms = wsClazz.getDeclaredMethods();
//		Method initMethod = null;
//		for(Method m : ms){
//			if(m.getName().equals("init")){
//				m.invoke(null, new Object[]{this.itemName,this.applicationContext,this.services,this.port});
//			}
//		}
//        logger.info("Jetty Server start......");
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(Map<String, Object> services) {
		this.services = services;
	}

	public void register(String serviceName,Object service) throws ServiceException{
		if(this.services == null){
			this.services = new HashMap<String,Object>();
		}
		if(this.services.containsKey(serviceName)){
			throw new ServiceException("service:"+serviceName+" has been existent");
		}
		this.services.put(serviceName, service);
	}
	

	/**
	 * @return the corePoolSize
	 */
	public int getCorePoolSize() {
		return corePoolSize;
	}

	/**
	 * @param corePoolSize the corePoolSize to set
	 */
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	/**
	 * @return the maxPoolSize
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * @param maxPoolSize the maxPoolSize to set
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * @return the workQueueSize
	 */
	public int getWorkQueueSize() {
		return workQueueSize;
	}

	/**
	 * @param workQueueSize the workQueueSize to set
	 */
	public void setWorkQueueSize(int workQueueSize) {
		this.workQueueSize = workQueueSize;
	}

    public void setCustomizedInvocationFilters(Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> customizedInvocationFilters) {
        this.customizedInvocationFilters = customizedInvocationFilters;
    }
}
