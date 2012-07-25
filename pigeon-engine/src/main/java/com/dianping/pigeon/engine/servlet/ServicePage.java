package com.dianping.pigeon.engine.servlet;

import java.util.ArrayList;
import java.util.List;

import com.dianping.pigeon.engine.model.Service;

public class ServicePage {
	private int port;

	private List<Service> services;

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public void addService(Service s) {
		if (services == null) {
			services = new ArrayList<Service>();
		}
		services.add(s);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
