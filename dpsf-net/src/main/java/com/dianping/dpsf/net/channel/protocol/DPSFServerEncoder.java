/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * <p>
 * Title: DPSFProtobufEncoder.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-13 上午12:21:14
 */
public class DPSFServerEncoder extends DPSFEncoder {

	public DPSFServerEncoder() {
		super();
	}

	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		return super.encode(ctx, channel, msg);
	}

}
