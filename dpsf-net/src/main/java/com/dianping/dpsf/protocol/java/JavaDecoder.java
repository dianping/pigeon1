/**
 * 
 */
package com.dianping.dpsf.protocol.java;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.net.channel.protocol.AbstractDecoder;

/**    
 * <p>    
 * Title: JavaDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 上午11:21:23   
 */
public class JavaDecoder extends AbstractDecoder{
	
	
	private static ClassLoader classLoader = JavaDecoder.class.getClassLoader();
	
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.Decoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	public Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {
		ChannelBuffer buffer = (ChannelBuffer)msg;
		
		ChannelBuffer frame = beforeDo(buffer);
		if(frame == null){
			return null;
		}
		CompactObjectInputStream coin = new CompactObjectInputStream(
	            new ChannelBufferInputStream(frame), classLoader);
		try{
			return coin.readObject();
		}finally{
		   coin.close();
		}
	}

}
