/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.DPSFUtils;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.telnet.TelnetServer;

/**    
 * <p>    
 * Title: DPSFDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午04:14:24   
 */
public abstract class DPSFDecoder extends OneToOneDecoder implements Decoder{

	private static Logger logger = DPSFLog.getLogger();
	
    public Object decode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    	if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
    	
    	ChannelBuffer cb = (ChannelBuffer)DPSFUtils.getAttachment(ctx, Constants.ATTACHMENT_BYTEBUFFER);
    	
		ChannelBuffer cb_ = (ChannelBuffer)msg;
		if(cb == null){
			cb = cb_;
		}else{
			cb.writeBytes(cb_);
		}
		
		//处理telnet
    	Boolean isTelnet = (Boolean)DPSFUtils.getAttachmentNotRemove(ctx, Constants.ATTACHMENT_IS_TELNET);
    	if(isTelnet != null && isTelnet){
    		int lastReadIndex = cb.readerIndex();
    		if(!TelnetServer.getInstance().executeCMD(ctx,channel,cb)){
    			setAttachment(ctx,channel,cb,lastReadIndex);
    		}
    		return null;
    	}
		
		List messages = null;
		int lastReadIndex = cb.readerIndex();
		while(cb.readable()){
			if(cb.readableBytes() <= 3){
				setAttachment(ctx,channel,cb,lastReadIndex);
				break;
			}
			byte head = cb.readByte();
			if(!(head == Constants.MESSAGE_HEAD_FIRST)){
				
				logger.error("error message head: "+head);
				continue;
			}
			head = cb.readByte();
			if(!(head == Constants.MESSAGE_HEAD_SECOND)){
				logger.error("error message head: "+head);
				continue;
			}
			
			byte serializable = cb.readByte();
			boolean isException = false;
			Object message = null;
			try{
				message = getDecoder(serializable).decode(ctx, channel, cb);
			}catch (Exception e){
				isException = true;
				logger.error(e.getMessage(),e);
			}
			
			if(message != null){
				//telnet
				if(isTelnet == null){
					isTelnet = false;
					DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_IS_TELNET,isTelnet);
				}
				
				if(messages == null){
					messages = new ArrayList();
				}
				messages.add(doInitMsg(message));
				lastReadIndex = cb.readerIndex();
				
			}else if(isException){
				lastReadIndex = cb.readerIndex();
			}else{
				
				setAttachment(ctx,channel,cb,lastReadIndex);
				break;
			}
		}
		//telnet
		if(isTelnet == null){
			if(TelnetServer.getInstance().isTelnet(ctx, channel, cb)){
				DPSFUtils.getAttachment(ctx, Constants.ATTACHMENT_BYTEBUFFER);
			}
		}
		return messages;
	}
    
    private void setAttachment(ChannelHandlerContext ctx,Channel channel,ChannelBuffer cb,int lastReadIndex){
    	cb.readerIndex(lastReadIndex);
		if(!(cb instanceof DynamicChannelBuffer)||cb.writerIndex() > 102400){
			ChannelBuffer db = dynamicBuffer(
                    cb.readableBytes()*2, channel.getConfig().getBufferFactory());
			db.writeBytes(cb);
			cb = db;
		}
		
		DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_BYTEBUFFER,cb);
    }
    
    public abstract Decoder getDecoder(int serializable)throws NetException;
    
    public abstract Object doInitMsg(Object message);
    
}
