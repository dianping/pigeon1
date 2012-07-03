/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;

/**    
 * <p>    
 * Title: AbstractDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-13 上午10:44:03   
 */
public abstract class AbstractDecoder implements Decoder{
	
	private final static int fieldLenth = 4;
	
	protected ChannelBuffer beforeDo(ChannelBuffer buffer){
		
		if (buffer.readableBytes() < fieldLenth) {
            return null;
        }

        long frameLength =buffer.getUnsignedInt(buffer.readerIndex());
        
        // never overflows because it's less than maxFrameLength
        int frameLengthInt = (int) frameLength;
        if (buffer.readableBytes()-fieldLenth < frameLengthInt) {
            return null;
        }

        buffer.skipBytes(fieldLenth);

        // extract frame
        int readerIndex = buffer.readerIndex();
        ChannelBuffer frame = buffer.slice(readerIndex, frameLengthInt);
        buffer.readerIndex(readerIndex + frameLengthInt);
		
		return frame;
	}
	

}
