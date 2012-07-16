/**
 * 
 */
package com.dianping.dpsf.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

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
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-26 上午10:43:19
 */
public class ServiceRegistry implements ApplicationContextAware {

	private static Logger logger = DPSFLog.getLogger();

	private Map<String, Object> services;

	private int port = 21111;

	private ServiceRepository sr;

	private boolean publish = true;

	private String serviceType = "dp";

	public static boolean isInit = false;

	private String itemName;

	private ConfigurableApplicationContext applicationContext;

	private int corePoolSize = 200;
	private int maxPoolSize = 2000;
	private int workQueueSize = 300;

	public ServiceRegistry() {

	}

	public void init() throws Exception {
		if ("dp".equals(this.serviceType.trim().toLowerCase())) {
			initDPService();
		} else if ("ws".equals(this.serviceType.trim().toLowerCase())) {
			initWSService();
		} else if ("engine".equals(this.serviceType.trim().toLowerCase())) {
			initEngine();
		} else {
			throw new RuntimeException("serviceType is error:" + this.serviceType);
		}

	}

	public static void defaultInit() {
		try {
			new ServiceRegistry().initDPService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDPService() throws Exception {
		isInit = true;
		this.sr = new ServiceRepository();
		com.dianping.dpsf.net.channel.Server server = new NettyServer(port, corePoolSize, maxPoolSize, workQueueSize, this.sr);
		try {
			server.start();
		} catch (ChannelException e) {
			if (this.port != 21111) {
				throw e;
			}
			logger.warn("Address already in use:21111  ************");
			return;
		}

		logger.info("DPSF Server start ************");

		if (this.services != null) {
			for (String serviceName : this.services.keySet()) {
				this.sr.registerService(serviceName, this.services.get(serviceName));
			}
		}
	}

	private void initEngine() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> wsClazz = Class.forName("com.dianping.pigeon.engine.jetty.JettyInit");
		Method[] ms = wsClazz.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getName().equals("init")) {
				m.invoke(null, new Object[] { this.services, this.port, 8080 });
				logger.info("PigeonEngine starting......");
				break;
			}
		}
	}

	private void initWSService() throws Exception {

		Class<?> wsClazz = Class.forName("com.dianping.dpsf.spring.WSInit");
		Method[] ms = wsClazz.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getName().equals("init")) {
				m.invoke(null, new Object[] { this.itemName, this.applicationContext, this.services, this.port });
			}
		}
		logger.info("Jetty Server starting......");
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	public void setServices(Map<String, Object> services) {
		this.services = services;
	}

	public void register(String serviceName, Object service) throws ServiceException {
		if (this.services == null) {
			this.services = new HashMap<String, Object>();
		}
		if (this.services.containsKey(serviceName)) {
			throw new ServiceException("service:" + serviceName + " has been existent");
		}
		this.services.put(serviceName, service);
	}

	/**
	 * @return the publish
	 */
	public boolean isPublish() {
		return publish;
	}

	/**
	 * @param publish
	 *            the publish to set
	 */
	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if ("ws".equals(this.serviceType.trim().toLowerCase())) {
			this.applicationContext = (ConfigurableApplicationContext) applicationContext;
		}
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the corePoolSize
	 */
	public int getCorePoolSize() {
		return corePoolSize;
	}

	/**
	 * @param corePoolSize
	 *            the corePoolSize to set
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
	 * @param maxPoolSize
	 *            the maxPoolSize to set
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
	 * @param workQueueSize
	 *            the workQueueSize to set
	 */
	public void setWorkQueueSize(int workQueueSize) {
		this.workQueueSize = workQueueSize;
	}

}
