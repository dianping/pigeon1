/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-27
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
package com.dianping.dpsf.csharp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Ignore;
import org.junit.Test;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.HessianOutput;
import com.dianping.dpsf.protocol.DefaultRequest;


/**
 * TODO Comment of HessianSerializeTest
 * 
 * @author danson.liu
 * 
 */
public class HessianSerializeTest {

	@Test
	@Ignore
	public void testDeserialize() {
		try {
			InputStream bais = new FileInputStream(
					"/Users/liujian/personal/workspace-csharp/ExamplesTests/dpsf-client-demo-test/bin/Debug/hessian_out_csharp.bin");
			Hessian2Input hessian2Input = new Hessian2Input(bais);
//			HessianInput hessian2Input = new HessianInput(bais);
			DpsfRequest rv = (DpsfRequest) hessian2Input.readObject();
			System.out.println("request.Name: " + rv.getName());
			System.out.println("request.Age: " + rv.getAge());
//			System.out.println("request.Price: " + rv.getPrice());
			System.out.println("request.Birthday: " + rv.getBirthday());
			System.out.println("request.F1: " + rv.getF1());
			System.out.println("request.F2: " + rv.getF2());
			// place here not finally block, because byte array stream is not
			// required to be closed
			hessian2Input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testDesirializeDefaultRequest() {
		try {
			InputStream bais = new FileInputStream(
				"/Users/liujian/personal/workspace-csharp/ExamplesTests/dpsf-client-demo-test/bin/Debug/default_request_csharp.bin");
			Hessian2Input hessian2Input = new Hessian2Input(bais);
			DefaultRequest request = (DefaultRequest) hessian2Input.readObject();
			System.out.println(request);
			System.out.println(request.getSerializ());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testSerialize() {
		try {
			OutputStream os = new FileOutputStream(
					"/Users/liujian/personal/workspace-csharp/ExamplesTests/dpsf-client-demo-test/bin/Debug/hessian_out_java.bin");
//			Hessian2Output hessian2Output = new Hessian2Output(os);
			HessianOutput hessian2Output = new HessianOutput(os);
			DpsfRequest request = new DpsfRequest();
			request.setName("nice dpsf");
			request.setAge(30);
			request.setPrice(new BigDecimal(13.5));
			request.setBirthday(new Date());
			request.setF1(3.5f);
			request.setF2(5.7);
			request.setAdditional(50.55);
			InnerRequest[] innerReqs = new InnerRequest[2];
			InnerRequest innerReq = new InnerRequest();
			innerReq.setMethod("Check");
			innerReq.setFavors(new Object[] {"haha", new Integer(3)});
			Map<String, Double> counters = new HashMap<String, Double>();
			counters.put("a1", 200.9);
			counters.put("a2", 300.3);
			innerReq.setCounters(counters);
			
			List<Product> products = new ArrayList<Product>();
			Product product = new Product();
			product.setName("IPhone");
			product.setCount(2);
			products.add(product);
			product = new Product();
			product.setName("IPad");
			product.setCount(1);
			products.add(product);
			innerReq.setProducts(products);
			
			innerReqs[0] = innerReq;
			
			innerReq = new InnerRequest();
			innerReq.setMethod("Nice");
			innerReq.setFavors(new Object[] {"gooooo", 14});
			innerReqs[1] = innerReq;
			
			request.setInnerReqs(innerReqs);
			
			hessian2Output.writeObject(request);
			
//			hessian2Output.writeObject(new ServiceException("java.lang.RuntimeException", "error happend", "sssssss-stack-trace"));
			
//			Product product = new Product();
//			product.setName("Iphone");
//			product.setCount(3);
//			hessian2Output.writeObject(product);
			hessian2Output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testDeserializePure() {
		try {
			InputStream bais = new FileInputStream(
				"/Users/liujian/personal/workspace-csharp/ExamplesTests/dpsf-client-demo-test/bin/Debug/serialize_request_csharp.bin");
			byte[] bytes = new byte[8];
			int readed = bais.read(bytes);
			System.out.println(readed);
//			System.out.println(bytes[0]);
			System.out.println(getLong(bytes, false));
//			System.out.println(bais.read());
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final static long getLong(byte[] buf, boolean asc) {
	    if (buf == null) {
	      throw new IllegalArgumentException("byte array is null!");
	    }
	    if (buf.length > 8) {
	      throw new IllegalArgumentException("byte array size > 8 !");
	    }
	    long r = 0;
	    if (asc)
	      for (int i = buf.length - 1; i >= 0; i--) {
	        r <<= 8;
	        r |= (buf[i] & 0x00000000000000ff);
	      }
	    else
	      for (int i = 0; i < buf.length; i++) {
	        r <<= 8;
	        r |= (buf[i] & 0x00000000000000ff);
	      }
	    return r;
	  }
	
	public static long toLong(byte[] b) 

	{ 
	long l = 0; 

	l = b[0]; 

	l |= ((long) b[1] << 8); 

	l |= ((long) b[2] << 16); 

	l |= ((long) b[3] << 24); 

	l |= ((long) b[4] << 32); 

	l |= ((long) b[5] << 40); 

	l |= ((long) b[6] << 48); 

	l |= ((long) b[7] << 56); 

	return l; 
	} 
	
	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
		ChannelBuffer buffer = ChannelBuffers.buffer(20);
		System.out.println(buffer.readerIndex());
		System.out.println(buffer.writerIndex());
		System.out.println(buffer.readable());
	}

}
