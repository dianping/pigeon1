package com.dianping.pigeon.engine.model;

public class ServiceMethod {
	private String name;
	
	private Class<?>[] parameterTypes;

	public ServiceMethod(String name, Class<?>[] parameterTypes) {
		this.name = name;
		this.parameterTypes = parameterTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

}
