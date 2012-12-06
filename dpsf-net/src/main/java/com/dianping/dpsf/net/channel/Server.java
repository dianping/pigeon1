package com.dianping.dpsf.net.channel;

import com.dianping.dpsf.exception.NetException;

/**    
  * <p>    
  * Title: Server.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:46:23   
  */ 
public interface Server {
	
	public void start() throws NetException;
	
	public void stop();

}
