package com.dianping.test.server.remote;

import java.io.Serializable;

public class TestParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6010653175010455379L;

	public TestParam(String f1, int f2) {
		this(f1,f2,0);
	}
	
	public TestParam(String f1, int f2, int f3) {
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
	}

	public String f1 = "a";
	
	public int f2 = 1;
	
	private int f3 = 0;


	public String getF1() {
		return f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}

	public int getF2() {
		return f2;
	}

	public void setF2(int f2) {
		this.f2 = f2;
	}
	
	public int getF3(){
		return f3;
	}

}
