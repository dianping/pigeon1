/**
 * 
 */
package com.dianping.dpsf;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: ContextUtil.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-12-9 下午05:21:27   
 */
public class ContextUtil {
	
	private static Logger logger = DPSFLog.getLogger();
	
	public static final String TRAC_ORDER = "tracker_order";
	
	static Method createContextMethod = null;
	static Method setContextMethod = null;
	static Method getContextMethod = null;
	static Method addSuccessContextMethod = null;
	static Method addFailedContextMethod = null;
	static Method getTokenMethod = null;
	
	static Method getExtensionMethod = null;
	static Method addExtensionMethod = null;
	
	static boolean flag = false;
	static Object[] defObjs = new Object[]{};
	static{
		
		try {
			Class contextHolderClass = Class.forName("com.dianping.avatar.tracker.ExecutionContextHolder");
			Class contextClass = Class.forName("com.dianping.avatar.tracker.TrackerContext");
			
			createContextMethod = contextHolderClass.getDeclaredMethod("createRemoteTrackerContext", new Class[]{String.class});
			createContextMethod.setAccessible(true);
			
			setContextMethod = contextHolderClass.getDeclaredMethod("setTrackerContext", new Class[]{contextClass});
			setContextMethod.setAccessible(true);
			
			getContextMethod = contextHolderClass.getDeclaredMethod("getTrackerContext", new Class[]{});
			getContextMethod.setAccessible(true);
			
			addSuccessContextMethod = contextHolderClass.getDeclaredMethod("addSucceedRemoteTrackerContext", new Class[]{contextClass});
			addSuccessContextMethod.setAccessible(true);
			
			addFailedContextMethod = contextHolderClass.getDeclaredMethod("addFailedRemoteTrackerContext", new Class[]{contextClass});
			addFailedContextMethod.setAccessible(true);
			
			getTokenMethod = contextClass.getDeclaredMethod("getToken", new Class[]{});
			getTokenMethod.setAccessible(true);
			
			getExtensionMethod = contextClass.getDeclaredMethod("getExtension", new Class[]{String.class});
			getExtensionMethod.setAccessible(true);
			
			addExtensionMethod = contextClass.getDeclaredMethod("addExtension", new Class[]{String.class,Object.class});
			addExtensionMethod.setAccessible(true);
			
			flag = true;
		} catch (Exception e) {
			logger.info("App does not have ExecutionContext");
		}
		
	}
	
	public static Object createContext(String serviceName,
			String methodName,String host,int port)throws NetException{
		if(flag){
			StringBuffer sb = new StringBuffer();
			sb.append(serviceName).append(".").append(methodName)
				.append("@").append(host).append(":").append(port);
			try {
				return createContextMethod.invoke(null, new Object[]{sb.toString()});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
		return null;
	}
	
	public static void setContext(Object context) throws NetException{
		if(flag ){
			try {
				setContextMethod.invoke(null, new Object[]{context});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
	}
	
	public static Object getContext() throws NetException{
		if(flag){
			try {
				return getContextMethod.invoke(null, defObjs);
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
		return null;
	}
	
	public static void addSuccessContext(Object context) throws NetException{
		if(flag && context != null){
			try {
				addSuccessContextMethod.invoke(null, new Object[]{context});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
	}
	
	public static void addFailedContext(Object context) throws NetException{
		if(flag && context != null){
			try {
				addFailedContextMethod.invoke(null, new Object[]{context});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
	}
	
	public static String getTooken(Object context) throws NetException{
		if(flag && context != null){
			try {
				return (String)getTokenMethod.invoke(context, defObjs);
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
		return null;
	}
	
	public static Integer getOrder(Object context) throws NetException{
		if(flag && context != null){
			try {
				return (Integer)getExtensionMethod.invoke(context, new Object[]{TRAC_ORDER});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
		return null;
	}
	
	public static void setOrder(Object context,Integer order) throws NetException{
		if(flag && context != null){
			try {
				addExtensionMethod.invoke(context, new Object[]{TRAC_ORDER,order});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
	}
	
	public static void addCatInfo(Object context,String key,String value) throws NetException{
		if(flag && context != null){
			try {
				addExtensionMethod.invoke(context, new Object[]{key,value});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
	}
	
	public static String getCatInfo(Object context,String key) throws NetException{
		if(flag && context != null){
			try {
				return (String)getExtensionMethod.invoke(context, new Object[]{key});
			} catch (Exception e) {
				throw new NetException(e);
			}
		}
		return null;
	}
}
