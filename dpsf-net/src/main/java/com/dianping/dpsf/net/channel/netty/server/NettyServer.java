package com.dianping.dpsf.net.channel.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.net.channel.Server;
import com.dianping.dpsf.process.RequestProcessor;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.dpsf.thread.DefaultThreadFactory;

/**    
  * <p>    
  * Title: NettyServer.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:33   
  */ 
public class NettyServer implements Server{
	
	private static Logger log = DPSFLog.getLogger();
	
	private String ip = null;
	
	private int port = 20000;
	
	private ServerBootstrap bootstrap;
	
	private boolean started = false;
	
	public NettyServer(int port,int corePoolSize,int maxPoolSize,int workQueueSize,ServiceRepository sr){
		this(null,port,corePoolSize,maxPoolSize,workQueueSize,sr);
	}
	
	public NettyServer(String ip,int port,int corePoolSize,int maxPoolSize,int workQueueSize,ServiceRepository sr){
		this.ip = ip;
		this.port = port;
		this.bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                		Executors.newCachedThreadPool(new DefaultThreadFactory("Netty-Server-BossExecutor")),
                		Executors.newCachedThreadPool(new DefaultThreadFactory("Netty-Server-WorkerExecutor"))));
		this.bootstrap.setPipelineFactory(new DPServerChannelPipelineFactory(new RequestProcessor(sr,this.port,corePoolSize,maxPoolSize,workQueueSize)));
	}
	

	public void start() {
		if(!started){
			InetSocketAddress address = null;
			if(this.ip == null){
				address = new InetSocketAddress(this.port);
			}else{
				address = new InetSocketAddress(this.ip,this.port);
			}
			this.bootstrap.bind(address);
			this.started = true;
			//Runtime.getRuntime().addShutdownHook(new Thread(new HookRunnable(this)));
		}
		log.info("Server start at port:"+this.port+"***********");
	}

	public void stop() {
		this.bootstrap.releaseExternalResources();
		this.started = false;
	}


}
