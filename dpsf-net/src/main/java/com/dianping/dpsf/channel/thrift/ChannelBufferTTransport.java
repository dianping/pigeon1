/**
 * 
 */
package com.dianping.dpsf.channel.thrift;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.dianping.dpsf.exception.DPSFRuntimeException;

/**    
 * <p>    
 * Title: ByteBufferTTransport.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-14 下午02:01:51   
 */
public class ChannelBufferTTransport extends TTransport{
	
	private ChannelBuffer channelBuffer;
	
	private static int capacity = 512;
	
	private int initReadIndex = 0;
	
	public ChannelBufferTTransport(){
		this(ChannelBuffers.dynamicBuffer(capacity));
	}
	
	public ChannelBufferTTransport(ChannelBuffer channelBuffer){
		this.channelBuffer = channelBuffer;
		this.initReadIndex = this.channelBuffer.readerIndex();
	}

	/**
	 * @return the byteBuffer
	 */
	public ChannelBuffer getChannelBuffer() {
		if(this.channelBuffer.capacity() > capacity){
			capacity = this.channelBuffer.capacity();
		}
		return this.channelBuffer;
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#isOpen()
	 */
	@Override
	public boolean isOpen() {
		throw new DPSFRuntimeException("method isOpen is not supported");
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#open()
	 */
	@Override
	public void open() throws TTransportException {
		throw new DPSFRuntimeException("method open is not supported");
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#close()
	 */
	@Override
	public void close() {
		throw new DPSFRuntimeException("method close is not supported");
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] buf, int off, int len) throws TTransportException {
		if(this.channelBuffer.readableBytes() < len){
			this.channelBuffer.readerIndex(this.initReadIndex);
			throw new DPSFRuntimeException("channelBuffer is not readAble");
		}
		int position = this.channelBuffer.readerIndex();
		this.channelBuffer.readBytes(buf, off, len);
		return this.channelBuffer.readerIndex() - position;
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] buf, int off, int len) throws TTransportException {
		
		this.channelBuffer.writeBytes(buf, off, len);
		
	}

}
