/**
 * 
 */
package com.dianping.dpsf.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelException;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.PigeonBootStrap;
import com.dianping.dpsf.PigeonBootStrap.Container;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.invoke.RemoteInvocationHandlerFactory;
import com.dianping.dpsf.net.channel.Server;
import com.dianping.dpsf.net.channel.netty.server.NettyServer;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.dpsf.spi.InvocationProcessFilter;

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
public class ServiceRegistry {

	private static Logger logger = DPSFLog.getLogger();

	private Map<String, Object> services;

	private int port = 21111;
	
	private int enginePort = 21115;

	private ServiceRepository sr;

	private boolean publish = true;

	private String serviceType = "dp";

	public static boolean isInit = false;

	private int corePoolSize = 200;
	private int maxPoolSize = 2000;
	private int workQueueSize = 300;
    private Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> customizedInvocationFilters;
	private boolean enableEngine = true;
    private Container       container;
    private Server          server;

	public ServiceRegistry() {

	}

	public void init() throws Exception {
		if ("dp".equals(this.serviceType.trim().toLowerCase())) {
			initDPService();
			if (enableEngine) {
				try {
					initEngine();
				} catch (ClassNotFoundException e) {
					logger.warn("can't init pigeon-engine:" + e.toString());
				}
			}
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
        initializeServerComponents();
		this.sr = new ServiceRepository();
		this.server = new NettyServer(port, corePoolSize, maxPoolSize, workQueueSize,
                this.sr, RemoteInvocationHandlerFactory.createProcessHandler(customizedInvocationFilters));
		try {
			server.start();
			container.registerComponent(server);
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

    private void initializeServerComponents() {
        PigeonBootStrap.setupServer();
        container = PigeonBootStrap.getContainer();
    }

	private void initEngine() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> wsClazz = Class.forName("com.dianping.pigeon.engine.jetty.JettyInit");
		Method[] ms = wsClazz.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getName().equals("init")) {
				m.invoke(null, new Object[] { this.services, this.port, this.getEnginePort() });
				logger.info("PigeonEngine starting......");
				break;
			}
		}
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

	public boolean isEnableEngine() {
		return enableEngine;
	}

	public void setEnableEngine(boolean enableEngine) {
		this.enableEngine = enableEngine;
	}

	public int getEnginePort() {
		return enginePort;
	}

	public void setEnginePort(int enginePort) {
		this.enginePort = enginePort;
	}

    public void setCustomizedInvocationFilters(Map<InvocationProcessFilter.ProcessPhase, List<InvocationProcessFilter>> customizedInvocationFilters) {
        this.customizedInvocationFilters = customizedInvocationFilters;
    }
    
    public void destroy() {
        if (this.server != null) {
            this.server.stop();
        }
    }
}
