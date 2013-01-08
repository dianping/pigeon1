/**
 * 
 */
package com.dianping.dpsf.net.channel.netty.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.ConnectMetaData;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.process.ResponseFactory;
import com.dianping.dpsf.stat.RpcStatsPool;

/**    
 * <p>    
 * Title: NettyClient.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-3 上午11:38:55   
 */
public class NettyClient implements Client{
	
	private Logger logger = DPSFLog.getLogger();
	
	private ClientBootstrap bootstrap;
	
	private ClientManager clientManager;
	
	private Channel channel;
	
	private String host;
	
	private int port = 20000;
	
	private String address;
	
	private List<String> serviceNames;
	
	private int connectTimeout = 3000;
	
	private long lastMessageTime = 0;
	
	private volatile boolean connected = false;
	
	private volatile boolean closed = false;
	
	private volatile boolean active = true;
	private volatile boolean activeSetable = false;
	
	public static final int CLIENT_CONNECTIONS = Runtime.getRuntime().availableProcessors();
	
	public NettyClient(String host,int port,ClientManager clientManager){
		this.host = host;
		this.port = port;
		this.address = host + ConnectMetaData.PLACEHOLDER + port;
		this.clientManager = clientManager;
		this.bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                		clientManager.getBossExecutor(),
                		clientManager.getWorkerExecutor()));
		this.bootstrap.setOption("writeBufferHighWaterMark", PigeonConfig.getWriteBufferHighWater());
		this.bootstrap.setOption("writeBufferLowWaterMark", PigeonConfig.getWriteBufferLowWater());
		this.bootstrap.setPipelineFactory(new DPClientChannelPipelineFactory(this,clientManager.getClientResponseThreadPool()));
	}
	
	public synchronized void connect() throws NetException{
		if(this.connected || this.closed){
			return;
		}
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		if(future.awaitUninterruptibly(this.connectTimeout,TimeUnit.MILLISECONDS)){
			if(future.isSuccess()){
				logger.warn("Client is conneted to "+this.host+":"+this.port);
				this.connected = true;
			}else{
				logger.warn("Client is not conneted to "+this.host+":"+this.port);
			}
		}
		this.channel = future.getChannel();
//		this.channel.getCloseFuture().addListener(new CloseListener());
		
	}
	public DPSFFuture write(DPSFRequest message,DPSFCallback callback) {
		Object[] msg = new Object[]{message,callback};
		ChannelFuture future = null;
		
		if(channel == null){
			logger.error("channel:"+null+" ^^^^^^^^^^^^^^");
		}else{
			
			future = channel.write(msg);
			
			if(message.getMessageType() == Constants.MESSAGE_TYPE_SERVICE
					|| message.getMessageType() == Constants.MESSAGE_TYPE_HEART){
				future.addListener(new MsgWriteListener(message));
			}
			this.lastMessageTime = message.getCreateMillisTime();
		}
		if(callback != null){
			return callback.getFuture(future);
		}else{
			return null;
		}
	}
	
	public void write(DPSFRequest message){
		write(message, null);
	}
	
	public void connectionException(Object attachment,ExceptionEvent e){
		this.connected = false;
		this.clientManager.connectionException(this,attachment,e);
	}

	
	public List<String> getServiceNames() {
		return serviceNames;
	}

	public void addServiceName(String serviceName){
		this.serviceNames.add(serviceName);
	}
	
	public void doResponse(DPSFResponse response) {
		try {
			this.clientManager.processResponse(response,this);
		} catch (ServiceException e) {
			logger.warn(e);
		}
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.activeSetable) {
			this.active = active;
		}
	}

	public boolean isActiveSetable() {
		return activeSetable;
	}

	public void setActiveSetable(boolean activeSetable) {
		this.activeSetable = activeSetable;
	}

	@Override
	public boolean isWritable() {
		return this.channel.isWritable();
	}

	/**
	 * @return the lastMessageTime
	 */
	public long getLastMessageTime() {
		return lastMessageTime;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	public class MsgWriteListener implements ChannelFutureListener{
		
		private DPSFRequest request;
		
		public MsgWriteListener(DPSFRequest request){
			this.request = request;
		}
		

		/* (non-Javadoc)
		 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
		 */
		public void operationComplete(ChannelFuture future) throws Exception {
			if(future.isSuccess()){
				return;
			}
			if (request.getMessageType() != Constants.MESSAGE_TYPE_HEART) {
				//这里心跳发送失败不关闭client, 交由实际调用去关闭
				connected = false;
			}
			logger.error("channel:"+future.getChannel()==null?null:future.getChannel().getId()+" *****************"+future.getCause());
			RpcStatsPool.flowOut(request, NettyClient.this.address);
			DPSFResponse response = ResponseFactory.createFailResponse(request, future.getCause());
			doResponse(response);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.Client#getPort()
	 */
	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}
	
//	private class CloseListener implements ChannelFutureListener {
//
//		/* (non-Javadoc)
//		 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
//		 */
//		@Override
//		public void operationComplete(ChannelFuture future) throws Exception {
//			ClusterConfigure.getInstance().removeConnect(NettyClient.this.host+ConnectMetaData.PLACEHOLDER+NettyClient.this.port);
//		}
//		
//	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof NettyClient){
			NettyClient nc = (NettyClient)obj;
			return this.address.equals(nc.getAddress());
		}else{
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public void close() {
		closed = true;	//防止HeartTask重新连接
		channel.close();
	}

}
