/**
 * 
 */
package com.dianping.pigeon.engine.jetty;

import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.dianping.pigeon.engine.servlet.ServiceServlet;

/**
 * @author sean.wang
 * @since Jul 16, 2012
 */
public class JettyInit {

	public void init(Map<String, Object> services, int pigeonPort, int enginePort) throws Exception {
		Server server = new Server(enginePort);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		context.addServlet(new ServletHolder(new ServiceServlet(services, pigeonPort)), "/services");

		ServletHolder holder = new ServletHolder(new DefaultServlet());
		String staticsDir = DefaultServlet.class.getClassLoader().getResource("com/dianping/pigeon/engine/").toExternalForm();
		holder.setInitParameter("resourceBase", staticsDir);
		holder.setInitParameter("gzip", "false");
		context.addServlet(holder, "/statics/*");

		server.start();
		server.join();
	}

}
