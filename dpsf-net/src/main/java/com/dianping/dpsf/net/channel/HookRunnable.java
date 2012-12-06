package com.dianping.dpsf.net.channel;

/**    
  * <p>    
  * Title: HookRunnable.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:46:11   
  */ 
public class HookRunnable implements Runnable{
	
	private Server server;
	
	public HookRunnable(Server server){
		this.server = server;
	}

	public void run() {
		this.server.stop();
	}
	
}