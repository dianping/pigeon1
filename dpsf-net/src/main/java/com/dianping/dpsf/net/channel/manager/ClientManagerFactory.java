/**
 * 
 */
package com.dianping.dpsf.net.channel.manager;

import com.dianping.dpsf.net.channel.netty.NettyClientManager;


/**    
 * <p>    
 * Title: ChannelManagerFactory.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:28:08   
 */
public class ClientManagerFactory {
	
	private static ClientManager manager;
	
	public static ClientManager getClientManager() {
		if (manager == null) {
			synchronized (ClientManagerFactory.class) {
				if (manager == null) {
					manager = new NettyClientManager();
				}
			}
		}
		return manager;
	}

	public static void setManager(ClientManager manager) {
		ClientManagerFactory.manager = manager;
	}
	
}
