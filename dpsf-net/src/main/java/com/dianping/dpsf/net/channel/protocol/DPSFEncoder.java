/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;


import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFSerializable;
import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: DPSFProtobufEncoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-13 上午12:21:14   
 */
public abstract class DPSFEncoder extends OneToOneEncoder implements Encoder{
	private static Logger logger = DPSFLog.getLogger();
	
	public DPSFEncoder() {
        super();
    }
    public Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    	
    	if(msg instanceof String){
    		byte[] msgBytes = ((String) msg).getBytes(Constants.TELNET_CHARSET);
    		ChannelBuffer cb = ChannelBuffers.buffer(msgBytes.length);
    		cb.writeBytes(msgBytes);
    		return cb;
    	}
    	
        if(msg instanceof DPSFSerializable){
        	
        	DPSFSerializable message = (DPSFSerializable)msg;
        	
        	ChannelBuffer buffer =  (ChannelBuffer)EncoderAndDecoderFactory.getEncoder(message.getSerializ())
        	.encode(ctx, channel, message.getObject());
        	buffer.setBytes(0, Constants.MESSAGE_HEAD);
        	buffer.setByte(2, message.getSerializ());
        	buffer.readerIndex(0);
            return buffer;
        	
        }else{
        	throw new NetException("message to encode must be instanceof DPSFSerializable");
        }
    }
}
