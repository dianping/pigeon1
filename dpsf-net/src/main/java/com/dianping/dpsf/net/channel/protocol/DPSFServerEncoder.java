/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;


import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFRequest;
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
public class DPSFServerEncoder extends DPSFEncoder{
	
	public DPSFServerEncoder() {
        super();
    }

    public Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        return super.encode(ctx, channel, msg);
    }

}
