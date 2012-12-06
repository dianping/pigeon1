/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;


import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFUtils;
import com.dianping.dpsf.component.DPSFResponse;

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

	@Override
	public void doFailResponse(Channel channel, DPSFResponse response) {
		List<DPSFResponse> respList = new ArrayList<DPSFResponse>();
		respList.add(response);
		Channels.fireMessageReceived(channel,respList);
	}

}
