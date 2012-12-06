/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.DPSFUtils;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.process.ResponseFactory;
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
	private static Logger log = Logger.getLogger(DPSFDecoder.class);
	
	private static boolean isClientTest = false;
	private static boolean isServerTest = false;
	
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
		
		List<Object> messages = null;
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
				
				if(isClientTest && this instanceof DPSFClientDecoder){
					message = null;
		    		DPSFDecoder.class.getClassLoader().loadClass("com.dianping.test.client.xxx");
		    	}
		    	if(isServerTest && this instanceof DPSFServerDecoder){
		    		message = null;
		    		DPSFDecoder.class.getClassLoader().loadClass("com.dianping.test.server.xxx");
		    	}
			}catch (Exception e){
				isException = true;
				try {
					//解析对端encoder扩展的seq字段
					Object seqObj = DPSFUtils.getAttachment(ctx, Constants.ATTACHMENT_REQUEST_SEQ);
					if(seqObj != null){
						long seq = Long.parseLong(String.valueOf(seqObj));
						String errorMsg = "Deserialize Exception>>>>host:"
							+((InetSocketAddress)channel.getRemoteAddress()).getHostName()
							+" seq:"+seq+ "\n" +e.getMessage();
						logger.error(errorMsg,e);
						log.error(errorMsg,e);
						doFailResponse(channel, 
								ResponseFactory.createThrowableResponse(seq,serializable,e));
					}
					
				} catch (Exception e1) {
					logger.error("", e1);
				}
			}
			
			if(message != null){
				//telnet
				if(isTelnet == null){
					isTelnet = false;
					DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_IS_TELNET,isTelnet);
				}
				
				if(messages == null){
					messages = new ArrayList<Object>();
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
    
    public abstract void doFailResponse(Channel channel,DPSFResponse response);

	public static boolean isClientTest() {
		return isClientTest;
	}

	/**
	 * 单元测试方法，开发用户勿用
	 * @param isClientTest
	 */
	public static void setClientTest(boolean isClientTest) {
		DPSFDecoder.isClientTest = isClientTest;
	}

	public static boolean isServerTest() {
		return isServerTest;
	}

	/**
	 * 单元测试方法，开发用户勿用
	 * @param isServerTest
	 */
	public static void setServerTest(boolean isServerTest) {
		DPSFDecoder.isServerTest = isServerTest;
	}

}
