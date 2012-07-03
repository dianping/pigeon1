/**
 * 
 */
package com.dianping.dpsf.protocol.hessian;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.dianping.dpsf.net.channel.protocol.AbstractEncoder;

/**    
 * <p>    
 * Title: HessianEncoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-13 上午09:54:01   
 */
public class HessianEncoder extends AbstractEncoder{
	
	private static final Logger log = Logger.getLogger(SerializerFactory.class.getName());
	
	 private final int estimatedLength = 512;
	 
	 static {
		 //Eliminate Hessian SerializerFactory class's warning log, in order to cooperate with pigeon.net framework
		 try {log.setLevel(Level.SEVERE);} catch (Throwable e) {}
	 }

	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {
		ChannelBufferOutputStream bout =
            new ChannelBufferOutputStream(dynamicBuffer(
                    estimatedLength, ctx.getChannel().getConfig().getBufferFactory()));
        bout.write(LENGTH_PLACEHOLDER);
		Hessian2Output h2out = new Hessian2Output(bout);
		try{
		    h2out.writeObject(msg);
		    h2out.flush();
		}finally{
			h2out.close();
		}
		ChannelBuffer encoded = bout.buffer();
        afterDo(encoded);
        return encoded;
	}

}
