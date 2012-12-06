/**
 * 
 */
package com.dianping.dpsf.net.channel.netty;

import org.jboss.netty.channel.Channel;

import com.dianping.dpsf.net.channel.DPSFChannel;

/**    
 * <p>    
 * Title: NettyChannel.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-3 上午11:04:10   
 */
public class NettyChannel implements DPSFChannel{

	private Channel channel = null;
	
	public NettyChannel(Channel channel){
		this.channel = channel;
	}

	/**
	 * @return the channel
	 */
	public <C> C getChannel(C c){
		return (C)channel;
	}
	
}
