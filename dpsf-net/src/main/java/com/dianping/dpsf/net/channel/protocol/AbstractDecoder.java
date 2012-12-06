/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFUtils;

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
	
	protected ChannelBuffer beforeDo(ChannelHandlerContext ctx,ChannelBuffer buffer){
		
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
        int msgLen = parseExpand(ctx,buffer,frameLengthInt);
        ChannelBuffer frame = buffer.slice(readerIndex, msgLen);
        buffer.readerIndex(readerIndex + frameLengthInt);
		
		return frame;
	}
	
	private int parseExpand(ChannelHandlerContext ctx,ChannelBuffer buffer,int frameLengthInt){
		int msgLen = frameLengthInt;
		byte[] expandFlag = new byte[3];
        buffer.getBytes(buffer.readerIndex()+frameLengthInt-3, expandFlag);
        if(expandFlag[0] == Constants.EXPAND_FLAG_FIRST 
        		&& expandFlag[1] == Constants.EXPAND_FLAG_SECOND
        		&& expandFlag[2] == Constants.EXPAND_FLAG_THIRD){
        	msgLen = frameLengthInt - AbstractEncoder.EXPAND_LANGTH;
        	long seq = buffer.getLong(buffer.readerIndex()+msgLen);
        	DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_REQUEST_SEQ, seq);
        }
        
        return msgLen;
	}
	

}
