/**
 * 
 */
package com.dianping.dpsf.context;

/**    
 * <p>    
 * Title: ClientContext.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-7-4 下午10:50:24   
 */
public class ClientContext {
	
	private static ThreadLocal<String> used_tl = new ThreadLocal<String>();
	private static ThreadLocal<String> use_tl = new ThreadLocal<String>();
	
	public static String getUsedClientAddress(){
		String address = used_tl.get();
		used_tl.remove();
		return address;
	}
	
	public static void setUsedClientAddress(String address){
		used_tl.set(address);
	}
	
	public static void setUseClientAddress(String address){
		use_tl.set(address);
	}
	
	public static String getUseClientAddress(){
		String address = use_tl.get();
		use_tl.remove();
		return address;
	}

}
