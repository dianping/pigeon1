package com.dianping.pigeon.engine.model;

public class ServiceMethod {
	private String name;
	
	private Class<?>[] parameterTypes;
	
	private Class<?> returnType;

	public ServiceMethod(String name, Class<?>[] parameterTypes, Class<?> returnType) {
		this.name = name;
		this.parameterTypes = parameterTypes;
		this.returnType = returnType;
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

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

}
