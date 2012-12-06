package com.dianping.dpsf.fail;

public interface FailTimeout {
	
	public int testCallNormal1(int param);
	public int testCallNormal2(int param);
	public int testCallNormal3(int param);
	
	public int testCallTimeout1(int param);
	public int testCallTimeout2(int param);
	public int testCallTimeout3(int param);
}
