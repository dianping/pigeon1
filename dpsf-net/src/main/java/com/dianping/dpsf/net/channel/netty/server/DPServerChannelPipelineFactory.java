package com.dianping.dpsf.net.channel.netty.server;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import com.dianping.dpsf.net.channel.Server;
import com.dianping.dpsf.net.channel.protocol.DPSFEncoder;
import com.dianping.dpsf.net.channel.protocol.DPSFServerDecoder;
import com.dianping.dpsf.net.channel.protocol.DPSFServerEncoder;
import com.dianping.dpsf.process.RequestProcessor;

/**    
  * <p>    
  * Title: DPServerChannelPipelineFactory.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:20   
  */ 
public class DPServerChannelPipelineFactory implements ChannelPipelineFactory{
	
	private RequestProcessor processor;
	
	public DPServerChannelPipelineFactory(RequestProcessor processor){
		this.processor = processor;
	}

	public ChannelPipeline getPipeline() throws Exception {

		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("decoder", new DPSFServerDecoder());
		pipeline.addLast("encoder", new DPSFServerEncoder());
		pipeline.addLast("handler", new DPServerHandler(processor));
		return pipeline;
	}

}
