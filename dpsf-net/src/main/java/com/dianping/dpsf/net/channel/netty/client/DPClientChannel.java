/**
 * 
 */
package com.dianping.dpsf.net.channel.netty.client;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;

/**    
 * <p>    
 * Title: DPClientChannel.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-14 下午11:50:46   
 */
public class DPClientChannel implements Channel{
	
	private final AtomicInteger lock = new AtomicInteger(0);
	
	private final Channel channel;	
	public DPClientChannel(Channel channel){
		this.channel = channel;
	}
	
	public boolean tryLock(){
		if(this.lock.get() < 0){
			return false;
		}
		if(this.lock.getAndIncrement()>=0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean tryLock_(){
		if(this.lock.getAndDecrement() == 0){
			return true;
		}else{
			this.lock.incrementAndGet();
			return false;
		}
	}
	
	public void unLock(){
		this.lock.decrementAndGet();
	}
	
	public int getLock(){
		return this.lock.get();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Channel o) {
		return this.channel.compareTo(o);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getId()
	 */
	public Integer getId() {
		return this.channel.getId();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getFactory()
	 */
	public ChannelFactory getFactory() {
		return this.channel.getFactory();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getParent()
	 */
	public Channel getParent() {
		return this.channel.getParent();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getConfig()
	 */
	public ChannelConfig getConfig() {
		return this.channel.getConfig();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getPipeline()
	 */
	public ChannelPipeline getPipeline() {
		return this.channel.getPipeline();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#isOpen()
	 */
	public boolean isOpen() {
		return this.channel.isOpen();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#isBound()
	 */
	public boolean isBound() {
		return this.channel.isBound();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#isConnected()
	 */
	public boolean isConnected() {
		return this.channel.isConnected();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getLocalAddress()
	 */
	public SocketAddress getLocalAddress() {
		return this.channel.getLocalAddress();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getRemoteAddress()
	 */
	public SocketAddress getRemoteAddress() {
		return this.channel.getRemoteAddress();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#write(java.lang.Object)
	 */
	public ChannelFuture write(Object message) {
		return this.channel.write(message);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#write(java.lang.Object, java.net.SocketAddress)
	 */
	public ChannelFuture write(Object message, SocketAddress remoteAddress) {
		return this.channel.write(message, remoteAddress);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#bind(java.net.SocketAddress)
	 */
	public ChannelFuture bind(SocketAddress localAddress) {
		return this.channel.bind(localAddress);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#connect(java.net.SocketAddress)
	 */
	public ChannelFuture connect(SocketAddress remoteAddress) {
		return this.channel.connect(remoteAddress);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#disconnect()
	 */
	public ChannelFuture disconnect() {
		return this.channel.disconnect();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#unbind()
	 */
	public ChannelFuture unbind() {
		return this.channel.unbind();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#close()
	 */
	public ChannelFuture close() {
		return this.channel.close();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getCloseFuture()
	 */
	public ChannelFuture getCloseFuture() {
		return this.channel.getCloseFuture();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#getInterestOps()
	 */
	public int getInterestOps() {
		return this.channel.getInterestOps();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#isReadable()
	 */
	public boolean isReadable() {
		return this.channel.isReadable();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#isWritable()
	 */
	public boolean isWritable() {
		return this.channel.isWritable();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#setInterestOps(int)
	 */
	public ChannelFuture setInterestOps(int interestOps) {
		return this.channel.setInterestOps(interestOps);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#setReadable(boolean)
	 */
	public ChannelFuture setReadable(boolean readable) {
		return this.channel.setReadable(readable);
	}

}
