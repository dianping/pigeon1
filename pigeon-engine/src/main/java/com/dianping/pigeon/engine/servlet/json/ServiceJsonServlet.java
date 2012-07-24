/**
 * 
 */
package com.dianping.pigeon.engine.servlet.json;

import java.util.Map;

import com.dianping.pigeon.engine.servlet.ServiceServlet;

/**
 * @author sean.wang
 * @since Jul 17, 2012
 */
public class ServiceJsonServlet extends ServiceServlet {

	private static final long serialVersionUID = -3000545547453006628L;

	public ServiceJsonServlet(Map<String, Object> services, int pigeonPort) {
		super(services, pigeonPort);
	}

	@Override
	public String getView() {
		return "ServiceJson.ftl";
	}

	public String getContentType() {
		return "application/json; charset=UTF-8";
	}
}
