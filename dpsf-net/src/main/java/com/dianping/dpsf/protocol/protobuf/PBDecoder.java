/**
 * 
 */
package com.dianping.dpsf.protocol.protobuf;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.net.channel.protocol.AbstractDecoder;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageLite;

/**    
 * <p>    
 * Title: PBDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午05:15:14   
 */
public abstract class PBDecoder extends AbstractDecoder{

	private static Logger logger = DPSFLog.getLogger();
	
	private final MessageLite prototype;
    private final ExtensionRegistry extensionRegistry;
    
    /**
     * Creates a new instance.
     */
    public PBDecoder(MessageLite prototype) {
        this(prototype, null);
    }

    public PBDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
        if (prototype == null) {
            throw new NullPointerException("prototype");
        }
        this.prototype = prototype.getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }
    static AtomicLong num = new AtomicLong(0);
    public Object decode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    	long now = 0;
		if(num.incrementAndGet()%10000==0){
			now = System.nanoTime();
		}
    	ChannelBuffer buffer = (ChannelBuffer)msg;
		
		ChannelBuffer frame = beforeDo(buffer);
		if(frame == null){
			return null;
		}
    	
    	MessageLite response = null;
        if (extensionRegistry == null) {
        	response = prototype.newBuilderForType().mergeFrom(
                    new ChannelBufferInputStream(frame)).build();
        } else {
        	response = prototype.newBuilderForType().mergeFrom(
                    new ChannelBufferInputStream(frame),
                    extensionRegistry).build();
        }
        Object obj = wrappedObject(response);
        if(now > 0){
			logger.warn("decoder time:"+(System.nanoTime()-now)/1000);
		}
        
        return obj;
    }
    
    abstract Object wrappedObject(MessageLite message);
}
