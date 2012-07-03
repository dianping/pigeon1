/**
 * 
 */
package com.dianping.dpsf.spring;

import java.util.Map;
import java.util.Map.Entry;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.thread.BoundedThreadPool;
import org.springframework.context.ConfigurableApplicationContext;

import com.dianping.dpsf.xfire.ServiceBean;
import com.dianping.dpsf.xfire.XFireSpringServlet;

/**    
 * <p>    
 * Title: WSInit.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-5-31 下午03:44:36   
 */
public class WSInit {
	
	private final static String SERVICE_DOMAIN = "http://service.dianping.com/";
	
	public static void init(String itemName,ConfigurableApplicationContext applicationContext,
			Map<String,Object> services,int port) throws Exception{
		for(Entry<String,Object> srv : services.entrySet()){
			String key = srv.getKey().toLowerCase();
			if(key.indexOf(SERVICE_DOMAIN) == -1){
				throw new RuntimeException("serviceName is error:"+key);
			}
			key = key.substring(SERVICE_DOMAIN.length());
			String itemName_ = key.substring(0,key.indexOf("/"));
			if(itemName == null){
				itemName = itemName_;
			}else if(!itemName.equals(itemName_)){
				throw new RuntimeException("item name must be same:"+srv.getKey());
			}
			ServiceBean sb = new ServiceBean();
			sb.setApplicationContext(applicationContext);
			sb.setName(key.substring(itemName.length()+1));
			Class[] its = srv.getValue().getClass().getInterfaces();
			if(its.length == 0){
				throw new RuntimeException("bean class:"+srv.getValue().getClass().getName()+" must implement interface");
			}
			sb.setServiceClass(its[0]);
			sb.setInvoker(new org.codehaus.xfire.service.invoker.BeanInvoker(srv.getValue()));
			sb.init();
		}
		
		System.setProperty("org.mortbay.util.URI.charset", "utf-8");
		org.mortbay.jetty.Server server = new org.mortbay.jetty.Server() ;
		BoundedThreadPool btp=new BoundedThreadPool();
		btp.setName("Server-Request-WS");
		btp.setMinThreads(1);
		btp.setMaxThreads(10);
		server.setThreadPool(btp);
        final Connector connector = new SelectChannelConnector();
        
        connector.setPort(Integer.getInteger("jetty.port",port).intValue());

        server.setConnectors(new Connector[]{connector});

        final Context root = new Context(server,"/", Context.SESSIONS);

        final ServletHandler servlet_handler = new ServletHandler();
        XFireSpringServlet.setXFire((org.codehaus.xfire.XFire)applicationContext.getBean("xfire"));
        servlet_handler.addServletWithMapping(XFireSpringServlet.class,"/"+itemName+"/*");
        
        final HandlerList handlers = new HandlerList();

        handlers.addHandler(servlet_handler);

        root.setHandler(handlers);
        server.start();
	}

}
