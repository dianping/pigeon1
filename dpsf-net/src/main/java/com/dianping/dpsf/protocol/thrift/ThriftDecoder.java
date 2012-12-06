/**
 * 
 */
package com.dianping.dpsf.protocol.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.channel.thrift.ChannelBufferTTransport;
import com.dianping.dpsf.exception.DPSFRuntimeException;
import com.dianping.dpsf.net.channel.protocol.AbstractDecoder;

/**    
 * <p>    
 * Title: ThriftDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-15 上午10:10:18   
 */
public abstract class ThriftDecoder extends AbstractDecoder{

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.Decoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	public Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
			throws Exception {

		ChannelBuffer buffer = (ChannelBuffer)msg;
		
		ChannelBuffer frame = beforeDo(ctx,buffer);
		if(frame == null){
			return null;
		}
		
		ChannelBufferTTransport transport = new ChannelBufferTTransport(frame);
		TProtocol protocol = new TBinaryProtocol(transport);
		TBase tb = null;
		try{
			tb = readTBase(protocol);
		}catch(DPSFRuntimeException e){
			return null;
		}
		return wrappedObject(tb);
	}
	
	protected abstract TBase readTBase(TProtocol protocol) throws TException;
	
	protected abstract Object wrappedObject(TBase tb) throws Exception;

}
