/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;


import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFUtils;

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
public class DPSFClientEncoder extends DPSFEncoder{
	
	public DPSFClientEncoder() {
        super();
    }

    public Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        DPSFUtils.setAttachment(ctx,Constants.ATTACHMENT_RETRY,msg);
        Object[] message = (Object[])msg;
        return super.encode(ctx, channel, message[0]);
    }

}
