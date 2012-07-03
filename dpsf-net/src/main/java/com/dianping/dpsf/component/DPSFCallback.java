/**
 * 
 */
package com.dianping.dpsf.component;

import org.jboss.netty.channel.ChannelFuture;

import com.dianping.dpsf.RequestError;
import com.dianping.dpsf.stat.CentralStatService.CentralStatContext;

/**    
 * <p>    
 * Title: DPSFCallback.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:17:03   
 */
public interface DPSFCallback extends Runnable,DPSFCall{
	
	public void setCentralStatContext(CentralStatContext centralStatContext);
	
	public void callback(DPSFResponse response);
	
	public DPSFFuture getFuture(ChannelFuture future);
	
	public void fail(RequestError error);
	
	public void setRequest(DPSFRequest request);
	
	public DPSFRequest getRequest();

}
