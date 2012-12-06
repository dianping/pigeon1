package com.dianping.dpsf.fail;

public class TestParameterNoSerializable {

	private int parameter = 0;
	public TestParameterNoSerializable(int parameter){
		this.parameter = parameter;
	}
	public int getParameter() {
		return parameter;
	}
	public void setParameter(int parameter) {
		this.parameter = parameter;
	}

}
