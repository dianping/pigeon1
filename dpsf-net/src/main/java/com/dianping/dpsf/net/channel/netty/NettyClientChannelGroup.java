/**
 * 
 */
package com.dianping.dpsf.net.channel.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

/**    
 * <p>    
 * Title: NettyClientChannelGroup.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-4 上午12:45:44   
 */
public class NettyClientChannelGroup {
	
	private static final AtomicInteger nextId = new AtomicInteger(1);
	
	
	private ChannelGroup channelGroup;
	
	public Map<Integer,Channel> channelMap = new ConcurrentHashMap<Integer,Channel>();
	
	public NettyClientChannelGroup(){
		this.channelGroup = new DefaultChannelGroup("Client-Channel-Group-0X"+nextId.getAndIncrement());
	}
	
	public void add(Channel channel){
		this.channelGroup.add(channel);
		this.channelMap.put(channel.getId(),channel);
	}

}
