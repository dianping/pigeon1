/**
 * 
 */
package com.dianping.dpsf.channel.thrift;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

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
public class ByteBufferTTransport extends TTransport{
	
	private ByteBuffer byteBuffer;
	
	private static int capacity = 512;
	
	public ByteBufferTTransport(){
		this.byteBuffer = ByteBuffer.allocate(capacity);
	}

	/**
	 * @return the byteBuffer
	 */
	public ByteBuffer getByteBuffer() {
		if(this.byteBuffer.capacity() > capacity){
			capacity = this.byteBuffer.capacity();
		}
		return this.byteBuffer;
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
		int position = this.byteBuffer.position();
		this.byteBuffer.get(buf, off, len);
		return this.byteBuffer.position() - position;
	}

	/* (non-Javadoc)
	 * @see org.apache.thrift.transport.TTransport#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] buf, int off, int len) throws TTransportException {
		try{
			this.byteBuffer.put(buf, off, len);
		}catch(BufferOverflowException e){
			ByteBuffer bb = this.byteBuffer;
			this.byteBuffer = ByteBuffer.allocate((int)(this.byteBuffer.capacity()*1.5));
			this.byteBuffer.put(bb);
			write(buf, off, len);
		} 
		
	}

}
