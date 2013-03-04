package com.dianping.dpsf.invoke.lifecycle;

/**
 * @author xiangbin.miao
 *
 */
public interface DPSFRequestLifeCycle {
	
	
	public void lifeCycle(DPSFEvent event,Object param);
	
	
	public static enum DPSFEvent{
		ResponseReturn,
		GetResult,
		GetTimeout
	}
	
}
