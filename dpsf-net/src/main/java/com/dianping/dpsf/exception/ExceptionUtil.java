/**
 * 
 */
package com.dianping.dpsf.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**    
 * <p>    
 * Title: ExceptionUtil.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-11 下午11:52:15   
 */
public class ExceptionUtil {
	
	@SuppressWarnings("rawtypes")
	static ThreadLocal threadLocal = new ThreadLocal();
	
	@SuppressWarnings("unchecked")
	public static void addException(NetException ne){
		threadLocal.set(ne);
	}
	
	public static NetException get(){
		return (NetException)threadLocal.get();
	}
	
	public static String extractStackTrace(Throwable t) {
		StringWriter me = new StringWriter();
		PrintWriter pw = new PrintWriter(me);
		t.printStackTrace(pw);
		pw.flush();
		return me.toString();
    }

}
