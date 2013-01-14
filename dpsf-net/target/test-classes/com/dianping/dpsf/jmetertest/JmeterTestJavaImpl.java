package com.dianping.dpsf.jmetertest;

public class JmeterTestJavaImpl implements JmeterTestJavaIFace{

	@Override
	public int jmeterTest(int param) {
		return ++param;
	}

}
