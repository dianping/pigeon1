package com.dianping.pigeon.engine.model;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;

	private Class<?> type;

	private List<ServiceMethod> methods;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ServiceMethod> getMethods() {
		return this.methods;
	}

	public void setMethods(List<ServiceMethod> methods) {
		this.methods = methods;
	}

	public void addMethod(ServiceMethod serviceMethod) {
		if (this.methods == null) {
			this.methods = new ArrayList<ServiceMethod>();
		}
		this.methods.add(serviceMethod);
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

}
