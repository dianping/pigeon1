package com.dianping.dpsf.fail;

import java.io.Serializable;

public class TestParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3070302725233370957L;
	
	private int parameter = 0;
	public TestParameter(int parameter){
		this.parameter = parameter;
	}
	public int getParameter() {
		return parameter;
	}
	public void setParameter(int parameter) {
		this.parameter = parameter;
	}

}
