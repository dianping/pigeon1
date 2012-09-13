/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-6-3
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.protocol.hessian;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.caucho.hessian.io.HessianOutput;
import com.dianping.dpsf.component.DPSFSerializable;
import com.dianping.dpsf.net.channel.protocol.AbstractEncoder;

/**
 * TODO Comment of Hessian1Encoder
 * @author danson.liu
 *
 */

/**    
 * <p>    
 * Title: Hessian1Encoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author danson.liu 
 * @version 1.6.0
 * @created 2012-6-3 下午17:41:46   
 */
public class Hessian1Encoder  extends AbstractEncoder {
	
	private final int estimatedLength = 512;

	@Override
	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		ChannelBufferOutputStream bout = new ChannelBufferOutputStream(
				dynamicBuffer(estimatedLength, ctx.getChannel().getConfig().getBufferFactory())
		);
		beforeDo(bout);
        HessianOutput h1out = new HessianOutput(bout);
		try{
		    h1out.writeObject(msg);
		    h1out.flush();
		}finally{
			h1out.close();
		}
		ChannelBuffer encoded = bout.buffer();
        afterDo(encoded,msg);
        return encoded;
	}

}
