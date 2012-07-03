/**
 * 
 */
package com.dianping.dpsf.net.channel;

import org.jboss.netty.channel.Channel;

/**    
 * <p>    
 * Title: Channel.java   
 * </p>    
 * <p>    
 * Description: 连接通道  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-3 上午11:02:49   
 */
public interface DPSFChannel {
	
	public <C> C getChannel(C c);

}
