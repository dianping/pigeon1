/**
 * 
 */
package com.dianping.pigeon.engine.jetty;

import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;

import com.dianping.pigeon.engine.servlet.ServiceServlet;
import com.dianping.pigeon.engine.servlet.json.InvokeJsonServlet;
import com.dianping.pigeon.engine.servlet.json.ServiceJsonServlet;

/**
 * @author sean.wang
 * @since Jul 16, 2012
 */
public class JettyInit {
	private JettyInit(){}
	private static final Log log = LogFactory.getLog(JettyInit.class);

	public static void init(Map<String, Object> services, int pigeonPort, int enginePort) {
		Server server = new Server(enginePort);

		Context context = new Context(Context.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		context.addServlet(new ServletHolder(new ServiceServlet(services, pigeonPort)), "/services");
		context.addServlet(new ServletHolder(new ServiceJsonServlet(services, pigeonPort)), "/services.json");
		context.addServlet(new ServletHolder(new InvokeJsonServlet(services, pigeonPort)), "/invoke.json");

		ServletHolder holder = new ServletHolder(new DefaultServlet());
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

		try {
			server.start();
		} catch (Exception e) {
			log.error("init", e);
		}
		try {
			server.join();
		} catch (InterruptedException e) {
			log.error("init", e);
		}
	}

}
