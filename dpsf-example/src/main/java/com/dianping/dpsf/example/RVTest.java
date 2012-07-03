/**
 * 
 */
package com.dianping.dpsf.example;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**    
 * <p>    
 * Title: RVTest.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-11-15 下午02:54:47   
 */
public class RVTest {
	public static void main(String[] args) throws Exception{
		ReturnVal rv = new ReturnVal("java", 8);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream javaOutput = new ObjectOutputStream(bout);
	        javaOutput.writeObject(rv);
	        javaOutput.flush();
	        byte[] arrayOfByte = bout.toByteArray();
	        System.out.println(arrayOfByte.length);
	}
}
