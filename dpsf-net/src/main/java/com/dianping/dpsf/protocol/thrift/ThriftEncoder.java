/**
 * 
 */
package com.dianping.dpsf.protocol.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.channel.thrift.ChannelBufferTTransport;
import com.dianping.dpsf.net.channel.protocol.AbstractEncoder;
import com.dianping.dpsf.net.channel.protocol.Encoder;

/**    
 * <p>    
 * Title: ThriftEncoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-15 上午10:11:40   
 */
public class ThriftEncoder extends AbstractEncoder{
	
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.Encoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {

		TBase message = (TBase)msg;
		ChannelBufferTTransport transport = new ChannelBufferTTransport();
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		transport.getChannelBuffer().writeBytes(LENGTH_PLACEHOLDER);
		message.write(protocol);
		ChannelBuffer cb = transport.getChannelBuffer();
		afterDo(cb);
		return cb;
	}
	

}
