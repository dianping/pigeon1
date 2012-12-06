package com.dianping.dpsf.tool.converter;

public class MethodSignature {
	private String methodName = null;
	private Class<?>[] paraTypes = null;
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParaTypes() {
		return paraTypes;
	}
	public void setParaTypes(Class<?>[] paraTypes) {
		this.paraTypes = paraTypes;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for(Class<?> pt : paraTypes) {
			code += pt.hashCode();
		}
		code += methodName.hashCode();
		return code; 
	}
	
	@Override
	public boolean equals(Object obj) {
		MethodSignature ms = (MethodSignature)obj;
		boolean isEqual = true;
		if(!methodName.equals(ms.getMethodName())) {
			isEqual = false;
		}
		for(int i = 0; i < paraTypes.length; i++) {
			if(paraTypes[i].getName() != ms.getParaTypes()[i].getName()) {
				isEqual = false;
				break;
			}
		}
		return isEqual;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(methodName+"[");
		for(int i = 0; i < paraTypes.length; i++) {
			sb.append(paraTypes[i].getName());
			if(i != paraTypes.length - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
}
