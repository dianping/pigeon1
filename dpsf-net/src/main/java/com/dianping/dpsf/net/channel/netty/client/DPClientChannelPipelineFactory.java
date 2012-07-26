package com.dianping.dpsf.net.channel.netty.client;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.protocol.DPSFClientDecoder;
import com.dianping.dpsf.net.channel.protocol.DPSFClientEncoder;
import com.dianping.dpsf.thread.DPSFThreadPool;

/**    
  * <p>    
  * Title: DPClientChannelPipelineFactory.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:05   
  */ 
public class DPClientChannelPipelineFactory implements ChannelPipelineFactory{
	
	private Client client;
	
	private ChannelHandler decoder;
	private ChannelHandler encoder;
	private ChannelHandler handler;
	
	public DPClientChannelPipelineFactory(Client client,DPSFThreadPool threadPool){
		this.client = client;
		this.decoder = new DPSFClientDecoder();
		this.encoder = new DPSFClientEncoder();
		this.handler = new DPClientHandler(this.client,threadPool);
	}

	public ChannelPipeline getPipeline() throws Exception {

		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("decoder", decoder);
		pipeline.addLast("encoder", encoder);
		pipeline.addLast("handler", handler);
		return pipeline;
	}

}
