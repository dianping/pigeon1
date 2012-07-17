/**
 * 
 */
package com.dianping.pigeon.engine.jetty;

import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.dianping.pigeon.engine.servlet.ServiceServlet;
import com.dianping.pigeon.engine.servlet.StaticsServlet;

/**
 * @author sean.wang
 * @since Jul 16, 2012
 */
public class JettyInit {
	private static final Log log = LogFactory.getLog(JettyInit.class);

	public static void init(Map<String, Object> services, int pigeonPort, int enginePort) throws Exception {
		Server server = new Server(enginePort);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		context.addServlet(new ServletHolder(new ServiceServlet(services, pigeonPort)), "/services");

		ServletHolder holder = new ServletHolder(new StaticsServlet());
		URL url = JettyInit.class.getClassLoader().getResource("com/dianping/pigeon/engine/statics");
		if (url == null) {
			log.error("can't find static files!");
			return;
		}
		String staticsDir = url.toExternalForm();
		holder.setInitParameter("resourceBase", staticsDir);
		log.info("set resourceBase:" + staticsDir);
		holder.setInitParameter("gzip", "false");
		context.addServlet(holder, "/jquery/*");
		context.addServlet(holder, "/ztree/*");

		server.start();
		server.join();
	}

}
