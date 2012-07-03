/**
 * 
 */
package com.dianping.dpsf.protocol.hessian;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.caucho.hessian.io.Hessian2Input;
import com.dianping.dpsf.net.channel.protocol.AbstractDecoder;

/**    
 * <p>    
 * Title: HessianDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-13 上午09:53:36   
 */
public class HessianDecoder extends AbstractDecoder{
	
	public Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {
		
		ChannelBuffer buffer = (ChannelBuffer)msg;
		
		ChannelBuffer frame = beforeDo(buffer);
		if(frame == null){
			return null;
		}

		Hessian2Input h2in = new Hessian2Input(new ChannelBufferInputStream(frame));
		try{
			return h2in.readObject();
		}finally{
			h2in.close();
		}
	    
	}

}
