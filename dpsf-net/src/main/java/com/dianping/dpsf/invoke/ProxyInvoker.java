/**
 *
 */
package com.dianping.dpsf.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.async.ServiceFutureImpl;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.component.impl.InvocationInvokeContextImpl;
import com.dianping.dpsf.component.impl.ServiceWarpCallback;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.protocol.DefaultRequest;
import com.site.helper.Splitters;

/**
 * <p>
 * Title: ProxyInvoker.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @author danson.liu
 * @version 1.0
 * @created 2010-9-7 下午09:58:35
 */
public class ProxyInvoker implements InvocationHandler {

	private Logger                     logger                 = DPSFLog.getLogger();
	private DPSFMetaData               metaData;
	private RemoteInvocationHandler    handler;
    private Invoker                    deprecatedInvoker;

	public ProxyInvoker(DPSFMetaData metaData, RemoteInvocationHandler handler, Invoker deprecatedInvoker) {
        this.metaData = metaData;
		this.handler = handler;
		this.deprecatedInvoker = deprecatedInvoker;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (PigeonConfig.isUseNewInvokeLogic()) {
			String methodName = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (method.getDeclaringClass() == Object.class) {
				return method.invoke(handler, args);
			}
			if ("toString".equals(methodName) && parameterTypes.length == 0) {
				return handler.toString();
			}
			if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
				return handler.hashCode();
			}
			if ("equals".equals(methodName) && parameterTypes.length == 1) {
				return handler.equals(args[0]);
			}
			return extractResult(handler.handle(new InvocationInvokeContextImpl(metaData, method, args)), method.getReturnType());
		} else {
			return oldInvoke(proxy, method, args);
		}
	}

	private Object extractResult(DPSFResponse response, Class<?> returnType) throws Throwable {
		Object responseReturn = response.getReturn();
		if (responseReturn != null) {
			int messageType = response.getMessageType();
			if (messageType == Constants.MESSAGE_TYPE_SERVICE) {
				return responseReturn;
			} else if (messageType == Constants.MESSAGE_TYPE_EXCEPTION
					|| messageType == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
				throw (Throwable) responseReturn;
			}
			throw new DPSFException(
					"Unsupported response to extract result with type[" + messageType + "].");
		}
		return getReturn(returnType);
	}

	@Deprecated
	private Object oldInvoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		MessageProducer cat = Cat.getProducer();
		List<String> serviceMeta = Splitters.by("/").noEmptyItem()
				.split(this.metaData.getServiceName());
		int length = serviceMeta.size();
		String name = "Unknown";

		if (length > 2) {
			StringBuilder sb = new StringBuilder(128);
			sb.append(serviceMeta.get(length - 2)).append(':')
					.append(serviceMeta.get(length - 1)).append(':')
					.append(method.getName());
			Class<?>[] parameterTypes = method.getParameterTypes();
			sb.append('(');
			int pLen = parameterTypes.length;
			for (int i = 0; i < pLen; i++) {
				Class<?> parameterType = parameterTypes[i];
				sb.append(parameterType.getSimpleName());
				if (i < pLen - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
			name = sb.toString();
		}

		Transaction t = cat.newTransaction("PigeonCall", name);

		t.setStatus(Transaction.SUCCESS);

		if (method.getName().equals("toString")) {
			return proxy.getClass().getName();
		} else if (method.getName().equals("equals")) {
			if (args == null || args.length != 1
					|| args[0].getClass() != proxy.getClass()) {
				return false;
			}
			return method.equals(args[0].getClass().getDeclaredMethod("equals",
					new Class[] { Object.class }));
		} else if (method.getName().equals("hashCode")) {
			return method.hashCode();
		}
		long now = 0;
		if (logger.isDebugEnabled()) {
			now = System.nanoTime();
		}
		DPSFRequest request = new DefaultRequest(this.metaData.getServiceName(), method.getName(), args,
				this.metaData.getSerialize(), Constants.MESSAGE_TYPE_SERVICE,
				this.metaData.getTimeout(), method.getParameterTypes());
		try {
			t.addData("CallType", this.metaData.getCallMethod());
			if (Constants.CALL_SYNC.equalsIgnoreCase(this.metaData.getCallMethod())) {
				DPSFResponse res = null;
				try {
					res = deprecatedInvoker.invokeSync(request, metaData, null);
				} catch (NetException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
				if (now > 0) {
					if (logger.isDebugEnabled()) {
						logger.debug("service:" + this.metaData.getServiceName()
								+ "_" + method.getName());
						logger.debug("execute time(microsecond):"
								+ (System.nanoTime() - now) / 1000);
						logger.debug("RequestId:" + res.getSequence());
					}
				}
				if (res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
					return res.getReturn();
				} else if (res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION
						|| res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
					Throwable cause = (Throwable) res.getReturn();
					logger.error(cause.getMessage(), cause);
					throw cause;
				}
				throw new DPSFException("no result to call");
			} else if (Constants.CALL_CALLBACK.equals(this.metaData
					.getCallMethod())) {
				try {
				    deprecatedInvoker.invokeCallback(request, metaData, null,
							new ServiceWarpCallback(metaData.getCallback()));
				} catch (NetException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
				return getReturn(method.getReturnType());
			} else if (Constants.CALL_FUTURE.equals(this.metaData
					.getCallMethod())) {
				try {
					CallbackFuture future = new ServiceFutureImpl(this.metaData.getTimeout());
					ServiceFutureFactory.setFuture((ServiceFuture) future);
					deprecatedInvoker.invokeCallback(request, metaData, null, future);
				} catch (Exception e) {
					ServiceFutureFactory.remove();
					logger.error(e.getMessage(), e);
					throw e;
				}
				return getReturn(method.getReturnType());
			} else if (Constants.CALL_ONEWAY.equals(this.metaData.getCallMethod())) {
				try {
				    deprecatedInvoker.invokeOneway(request, metaData, null);
				} catch (NetException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
				return getReturn(method.getReturnType());
			}
			DPSFException dpsfException = new DPSFException(
					"callmethod configure is error:" + this.metaData.getCallMethod());
			throw dpsfException;
		} catch (Exception e) {
			t.setStatus(e);
			Cat.getProducer().logError(e);
			throw e;
		} catch (Error e) {
			t.setStatus(e);
			Cat.getProducer().logError(e);
			throw e;
		} finally {
			t.complete();
		}
	}

	private Object getReturn(Class<?> returnType) {
		if (returnType == byte.class) {
			return (byte) 0;
		} else if (returnType == short.class) {
			return (short) 0;
		} else if (returnType == int.class) {
			return 0;
		} else if (returnType == boolean.class) {
			return false;
		} else if (returnType == long.class) {
			return 0l;
		} else if (returnType == float.class) {
			return 0.0f;
		} else if (returnType == double.class) {
			return 0.0d;
		} else {
			return null;
		}
	}

}
