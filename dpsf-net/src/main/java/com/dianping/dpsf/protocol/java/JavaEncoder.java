/**
 * 
 */
package com.dianping.dpsf.protocol.java;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.io.ObjectOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.component.DPSFSerializable;
import com.dianping.dpsf.net.channel.protocol.AbstractEncoder;

/**    
 * <p>    
 * Title: JavaEncoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-8 上午11:02:22   
 */
public class JavaEncoder extends AbstractEncoder{

    private final int estimatedLength = 512;
	
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.Encoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {

		ChannelBufferOutputStream bout =
            new ChannelBufferOutputStream(dynamicBuffer(
                    estimatedLength, ctx.getChannel().getConfig().getBufferFactory()));
		beforeDo(bout);
        ObjectOutputStream oout = new CompactObjectOutputStream(bout);
        try{
        	oout.writeObject(msg);
            oout.flush();
        }finally{
        	oout.close();
        }

        ChannelBuffer encoded = bout.buffer();
        afterDo(encoded,msg);
        return encoded;
	}

}
