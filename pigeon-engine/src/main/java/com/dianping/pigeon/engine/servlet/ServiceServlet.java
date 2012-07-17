/**
 * 
 */
package com.dianping.pigeon.engine.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dianping.pigeon.engine.model.Service;
import com.dianping.pigeon.engine.model.ServiceMethod;
import com.dianping.pigeon.engine.model.ServicePage;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author sean.wang
 * @since Jul 16, 2012
 */
public class ServiceServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(ServiceServlet.class);

	private static final long serialVersionUID = -2703014417332812558L;

	private static Set<String> ingoreMethods = new HashSet<String>();

	static {
		Method[] objectMethodArray = Object.class.getMethods();
		for (Method method : objectMethodArray) {
			ingoreMethods.add(method.getName() + ":" + Arrays.toString(method.getParameterTypes()));
		}

	}

	private static final Configuration cfg = new Configuration();

	static {
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		ClassTemplateLoader templateLoader = new ClassTemplateLoader(ServiceServlet.class, "/com/dianping/pigeon/engine/view");
		cfg.setTemplateLoader(templateLoader);
	}

	private ServicePage model;

	public ServiceServlet(Map<String, Object> services, int pigeonPort) {
		ServicePage page = new ServicePage();
		page.setPort(pigeonPort);
		for (Entry<String, Object> entry : services.entrySet()) {
			String serviceName = entry.getKey();
			Object service = entry.getValue();
			Service s = new Service();
			s.setName(serviceName);
			s.setClassName(service.getClass().getCanonicalName());
			Method[] methods = service.getClass().getMethods();
			for (Method method : methods) {
				String key = method.getName() + ":" + Arrays.toString(method.getParameterTypes());
				if (!ingoreMethods.contains(key)) {
					s.addMethod(new ServiceMethod(method.getName(), method.getParameterTypes()));
				}
			}
			page.addService(s);
		}

		this.model = page;
	}

	public String getView() {
		return "Service.ftl";
	}

	public String getContentType() {
		return "text/html; charset=UTF-8";
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(getContentType());
		response.setStatus(HttpServletResponse.SC_OK);

		Template temp = cfg.getTemplate(getView());
		try {
			temp.process(this.model, response.getWriter());
		} catch (TemplateException e) {
			log.error("template:", e);
		}
	}

}
