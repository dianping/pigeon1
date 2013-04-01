/**
 *
 */
package com.dianping.dpsf;

import com.dianping.dpsf.exception.NetException;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: ContextUtil.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-12-9 下午05:21:27
 */
public class ContextUtil {
    private ContextUtil() {

    }

    private static Logger logger = DPSFLog.getLogger();

    public static final String TRAC_ORDER = "tracker_order";

    private static Constructor contextConstructor = null;
    private static Method createContextMethod = null;
    private static Method setContextMethod = null;
    private static Method clearContextMethod = null;
    private static Method getContextMethod = null;
    private static Method addSuccessContextMethod = null;
    private static Method addFailedContextMethod = null;
    private static Method getTokenMethod = null;

    private static Method getExtensionMethod = null;
    private static Method getExtensionsMethod = null;
    private static Method addExtensionMethod = null;

    private static boolean flag = false;
    private static Object[] defObjs = new Object[]{};

    private static ThreadLocal<Map> localContext = new ThreadLocal<Map>();

    static {

        try {
            Class contextHolderClass = Class.forName("com.dianping.avatar.tracker.ExecutionContextHolder");
            Class contextClass = Class.forName("com.dianping.avatar.tracker.TrackerContext");
            
            contextConstructor = contextClass.getConstructor(new Class[0]);

            createContextMethod = contextHolderClass.getDeclaredMethod("createRemoteTrackerContext", new Class[]{String.class});
            createContextMethod.setAccessible(true);

            setContextMethod = contextHolderClass.getDeclaredMethod("setTrackerContext", new Class[]{contextClass});
            setContextMethod.setAccessible(true);

            getContextMethod = contextHolderClass.getDeclaredMethod("getTrackerContext", new Class[]{});
            getContextMethod.setAccessible(true);

            clearContextMethod = contextHolderClass.getDeclaredMethod("clearContext", new Class[]{});
            clearContextMethod.setAccessible(true);

            addSuccessContextMethod = contextHolderClass.getDeclaredMethod("addSucceedRemoteTrackerContext", new Class[]{contextClass});
            addSuccessContextMethod.setAccessible(true);

            addFailedContextMethod = contextHolderClass.getDeclaredMethod("addFailedRemoteTrackerContext", new Class[]{contextClass});
            addFailedContextMethod.setAccessible(true);

            getTokenMethod = contextClass.getDeclaredMethod("getToken", new Class[]{});
            getTokenMethod.setAccessible(true);

            getExtensionMethod = contextClass.getDeclaredMethod("getExtension", new Class[]{String.class});
            getExtensionMethod.setAccessible(true);
            
            getExtensionsMethod = contextClass.getDeclaredMethod("getExtension", new Class[]{});
            getExtensionsMethod.setAccessible(true);

            addExtensionMethod = contextClass.getDeclaredMethod("addExtension", new Class[]{String.class, Object.class});
            addExtensionMethod.setAccessible(true);

            flag = true;
        } catch (Exception e) {
            logger.info("App does not have ExecutionContext");
        }
    }

    public static Object createContext(String serviceName,
                                       String methodName, String host, int port) {
        if (flag) {
        	Object context = null;
            StringBuffer sb = new StringBuffer();
            sb.append(serviceName).append(".").append(methodName)
                    .append("@").append(host).append(":").append(port);
            try {
                context = createContextMethod.invoke(null, new Object[]{sb.toString()});
                
                if(context == null){
                	context = contextConstructor.newInstance(defObjs);
                }
                setContext(context);
                return context;
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }

    public static void setContext(Object context) {
        if (flag && context != null) {
            try {
                setContextMethod.invoke(null, new Object[]{context});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    public static Object getContext() {
        if (flag) {
            try {
                return getContextMethod.invoke(null, defObjs);
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }

    public static void addSuccessContext(Object context) {
        if (flag && context != null) {
            try {
                addSuccessContextMethod.invoke(null, new Object[]{context});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    public static void addFailedContext(Object context) {
        if (flag && context != null) {
            try {
                addFailedContextMethod.invoke(null, new Object[]{context});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    public static String getToken(Object context) throws NetException {
        if (flag && context != null) {
            try {
                return (String) getTokenMethod.invoke(context, defObjs);
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }

    public static Integer getOrder(Object context) {
        if (flag && context != null) {
            try {
                return (Integer) getExtensionMethod.invoke(context, new Object[]{TRAC_ORDER});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }

    public static void setOrder(Object context, Integer order) {
        if (flag && context != null) {
            try {
                addExtensionMethod.invoke(context, new Object[]{TRAC_ORDER, order});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    public static void putContextValue(Object context, String key, Serializable value) {
        if (flag && context != null) {
            try {
                addExtensionMethod.invoke(context, new Object[]{key, value});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    public static <T> T getContextValue(Object context, String key) {
        if (flag && context != null) {
            try {
                return (T) getExtensionMethod.invoke(context, new Object[]{key});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }
    
    public static <T> T getContextValues(Object context) {
        if (flag && context != null) {
            try {
                return (T) getExtensionsMethod.invoke(context, new Object[]{});
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
        return null;
    }

    public static void clearContext() {
        if (flag) {
            try {
                clearContextMethod.invoke(null, new Object[0]);
            } catch (Exception e) {
                throw new NetException(e);
            }
        }

    }

    public static void putLocalContext(Object key, Object value) {
        Map context = localContext.get();
        if (context == null) {
            context = new HashMap();
            localContext.set(context);
        }
        context.put(key, value);
    }

    public static Object getLocalContext(Object key) {
        Map context = localContext.get();
        if (context == null) {
            return null;
        }
        return context.get(key);
    }

    public static void clearLocalContext() {
        Map context = localContext.get();
        if (context != null) {
            context.clear();
        }
    }
}
