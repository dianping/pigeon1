/**
 * 
 */
package com.dianping.dpsf.protocol.protobuf;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.net.channel.protocol.AbstractEncoder;
import com.google.protobuf.MessageLite;

/**
 * <p>
 * Title: PBEncoder.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 下午04:48:44
 */
public class PBEncoder extends AbstractEncoder {

	private static final Logger logger = DPSFLog.getLogger();

	private static final int estimatedLength = 512;
	
	private static AtomicLong num = new AtomicLong(0);

	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if (!(msg instanceof MessageLite)) {
			return msg;
		}
		long now = 0;
		if (num.incrementAndGet() % 10000 == 0) {
			now = System.nanoTime();
		}
		byte[] bytes = ((MessageLite) msg).toByteArray();

		ChannelBufferOutputStream bout = new ChannelBufferOutputStream(dynamicBuffer(estimatedLength, ctx.getChannel().getConfig().getBufferFactory()));
		bout.write(LENGTH_PLACEHOLDER);
		bout.write(bytes);

		ChannelBuffer cb = bout.buffer();
		afterDo(cb);
		if (now > 0) {
			logger.warn("encoder time:" + (System.nanoTime() - now) / 1000);
		}
		return cb;

	}

}
