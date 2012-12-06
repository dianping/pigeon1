/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**    
 * <p>    
 * Title: Decoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午05:50:41   
 */
public interface Decoder {

	Object decode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception;
	
}
