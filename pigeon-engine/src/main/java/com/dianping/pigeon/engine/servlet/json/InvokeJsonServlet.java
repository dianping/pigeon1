/**
 * 
 */
package com.dianping.pigeon.engine.servlet.json;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianping.pigeon.engine.servlet.ServiceServlet;
import com.google.gson.Gson;

/**
 * @author sean.wang
 * @since Jul 22, 2012
 */
public class InvokeJsonServlet extends ServiceServlet {

	private Map<String, Object> services;

	public InvokeJsonServlet(Map<String, Object> services, int pigeonPort) {
		this.services = services;
	}

	private Gson gson = new Gson();

	private static final long serialVersionUID = -4886018160888366456L;

	protected void generateView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String serviceName = request.getParameter("serviceName");
		String methodName = request.getParameter("methodName");
		String[] types = request.getParameterValues("methodParameterTypes");
		String[] values = request.getParameterValues("methodParameters");
		Object service = this.services.get(serviceName);
		if (service == null) {
			return;
		}
		Class<?> serviceClz = service.getClass();
		Class<?>[] typesClz = null;
		if (types != null) {
			typesClz = new Class<?>[types.length];
			for (int i = 0; i < types.length; i++) {
				try {
					String className = types[i];
					typesClz[i] = Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new ServletException(e);
				}
			}
		}
		Method method = null;
		try {
			method = serviceClz.getMethod(methodName, typesClz);
			method.setAccessible(true);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		Object[] valuesObj = null;
		if (values != null) {
			valuesObj = new Object[values.length];
			for (int i = 0; i < values.length; i++) {
				valuesObj[i] = gson.fromJson(values[i], typesClz[i]);
			}
		}
		Object result = null;
		try {
			result = method.invoke(service, valuesObj);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		String json = gson.toJson(result);
		response.getWriter().write(json);
	}
}
