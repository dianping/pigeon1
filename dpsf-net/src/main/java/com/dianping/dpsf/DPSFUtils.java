/**
 * 
 */
package com.dianping.dpsf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.ChannelHandlerContext;

/**    
 * <p>    
 * Title: DP.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-9 下午09:24:39   
 */
public class DPSFUtils {
	
	public static void setAttachment(ChannelHandlerContext ctx,int seq,Object value){
		Map<Integer,Object> attachments = (Map<Integer,Object>)ctx.getAttachment();
		if(attachments == null){
			attachments = createAttachment(ctx);
		}
		attachments.put(seq, value);
	}
	
	public static Object getAttachmentNotRemove(ChannelHandlerContext ctx,int seq){
		Map<Integer,Object> attachments = (Map<Integer,Object>)ctx.getAttachment();
		if(attachments == null){
			attachments = createAttachment(ctx);
		}
		return attachments.get(seq);
	}
	
	public static Object getAttachment(ChannelHandlerContext ctx,int seq){
		Map<Integer,Object> attachments = (Map<Integer,Object>)ctx.getAttachment();
		if(attachments == null){
			attachments = createAttachment(ctx);
		}
		return attachments.remove(seq);
	}
	
	private static Map<Integer,Object> createAttachment(ChannelHandlerContext ctx){
		synchronized(ctx){
			Map<Integer,Object> attachments = (Map<Integer,Object>)ctx.getAttachment();
			if(attachments == null){
				attachments = new ConcurrentHashMap<Integer,Object>();
				ctx.setAttachment(attachments);
			}
			return attachments;
		}
	}

}
