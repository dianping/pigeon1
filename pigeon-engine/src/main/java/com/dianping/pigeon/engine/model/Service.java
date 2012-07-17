package com.dianping.pigeon.engine.model;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;

	private String className;

	private List<ServiceMethod> methods;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String clzName) {
		this.className = clzName;
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

}
