/**
 * 
 */
package com.dianping.dpsf.net.channel.netty.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: DPClientChannelGroup.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-14 下午05:23:27   
 */
public class DPClientChannelGroup {
	private Logger logger = DPSFLog.getLogger();

	private final String name;
	private final InetSocketAddress address;
	private ClientBootstrap bootstrap;
	private int connectTimeout = 2000;
	
	private final int minSize = NettyClient.CLIENT_CONNECTIONS;
	
	private final AtomicInteger currentUsedNum = new AtomicInteger(0);
	
	private final List<DPClientChannel> channelList = new ArrayList<DPClientChannel>();
	private final Map<Integer,DPClientChannel> channelMap = new ConcurrentHashMap<Integer,DPClientChannel>();
	
	
	private final ChannelFutureListener remover = new ChannelFutureListener() {
	    public void operationComplete(ChannelFuture future) throws Exception {
	    	
	        remove(channelMap.get(future.getChannel().getId()));
	        
	    }
	};
	
	
	public DPClientChannelGroup(String name,InetSocketAddress address,
			ClientBootstrap bootstrap,int connectTimeout) {
	    if (name == null) {
	        throw new NullPointerException("name");
	    }
	    if(address == null){
	    	throw new NullPointerException("address");
	    }
	    if(bootstrap == null){
	    	throw new NullPointerException("bootstrap");
	    }
	    this.name = name;
	    this.address = address;
	    this.bootstrap = bootstrap;
	    this.connectTimeout = connectTimeout;
//	    CycThreadPool.getPool().submit(new GroupTask());
	}
	
	public String getName() {
	    return name;
	}
	
	public boolean isEmpty() {
	    return channelList.isEmpty();
	}
	
	public int size() {
	    return channelList.size();
	}
	
	public void init() throws NetException {
		int newSize = this.minSize-this.channelList.size();
		
		createChannel(newSize);
	}
	
	private void createChannel(int newSize) throws NetException{
		NetException e = null;
		int timeout = this.connectTimeout;
		for(int i=0;i<newSize;i++){
			ChannelFuture future = bootstrap.connect(this.address);
			if(future.awaitUninterruptibly(timeout,TimeUnit.MILLISECONDS)){
				if(future.isSuccess()){
					future.getChannel().getCloseFuture().addListener(remover);
					DPClientChannel channel = new DPClientChannel(future.getChannel());
					channelList.add(channel);
					channelMap.put(channel.getId(), channel);
					logger.warn("Client is conneted to "+this.address.getHostName()+":"+this.address.getPort());
					continue;
				}else{
					timeout = 100;
				}
			}
			future.getChannel().getCloseFuture().awaitUninterruptibly();
			
			if(e == null){
				e = new NetException(future.getCause());
			}
		}
		if(e != null){
			throw e;
		}
	}
	
	public void destoryChannel(int desSize){
		int k = 0;
		for(int i=0;i<this.channelList.size();i++){
			if(this.channelList.get(i).tryLock_()){
				this.channelList.get(i).close();
				if(++k == desSize){
					return;
				}
			}
		}
	}
	
	public DPClientChannel getChannel(){
		if(this.channelList.size() == 0){
			return null;
		}
		int index = (int)(this.channelList.size()*Math.random());
		DPClientChannel channel = this.channelList.get(index);
		if(channel != null && channel.tryLock()){
			this.currentUsedNum.incrementAndGet();
			return channel;
		}else{
			return getChannel();
		}
	}
	
	public List<DPClientChannel> getChannels(){
		return this.channelList;
	} 
	
	public void release(DPClientChannel channel){
		channel.unLock();
		this.currentUsedNum.decrementAndGet();
	}
	
	public boolean remove(DPClientChannel channel) {
	    if(channelList.remove(channel)){
	    	channel.getCloseFuture().removeListener(remover);
	    	channelMap.remove(channel.getId());
	    	logger.warn(address.getHostName()+" remove channel:"+channel.getId());
	    	if(channel.getLock() > 0){
	    		this.currentUsedNum.addAndGet(-channel.getLock());
	    	}
		    return true;
	    }
	    return false;
	}

	
}
