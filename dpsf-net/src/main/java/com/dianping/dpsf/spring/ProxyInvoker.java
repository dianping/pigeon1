/**
 * 
 */
package com.dianping.dpsf.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
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
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.component.impl.ServiceWarpCallback;
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
 * @version 1.0
 * @created 2010-9-7 下午09:58:35
 */
public class ProxyInvoker implements InvocationHandler {

	private static Logger log = DPSFLog.getLogger();

	private DPSFMetaData metaData;
	private Set<String> ingoreMethods = new HashSet<String>();

	public ProxyInvoker(DPSFMetaData metaData) {
		this.metaData = metaData;
		Method[] objectMethodArray = Object.class.getMethods();
		for (Method method : objectMethodArray) {
			this.ingoreMethods.add(method.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MessageProducer cat = Cat.getProducer();
		List<String> serviceMeta = Splitters.by("/").noEmptyItem().split(this.metaData.getServiceName());
		int length = serviceMeta.size();
		String type = CatConstants.TYPE_CALL;
		String name = "Unknown";

		if (length > 2) {
			StringBuilder sb = new StringBuilder();
			sb.append(serviceMeta.get(length - 2)).append(":").append(serviceMeta.get(length - 1)).append(":").append(method.getName());
			Class<?>[] parameterTypes = method.getParameterTypes();
			sb.append('(');
			int pLen = parameterTypes.length;
			for (int i = 0; i < pLen; i++) {
				Class<?> parameterType = parameterTypes[i];
				sb.append(parameterType.getCanonicalName());
				if (i < pLen - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
			name = sb.toString();
		}

		Transaction t = cat.newTransaction(type, name);

		t.setStatus(Transaction.SUCCESS);

		if (method.getName().equals("toString")) {
			return proxy.getClass().getName();
		} else if (method.getName().equals("equals")) {
			if (args == null || args.length != 1 || args[0].getClass() != proxy.getClass()) {
				return false;
			}
			return method.equals(args[0].getClass().getDeclaredMethod("equals", new Class[] { Object.class }));
		} else if (method.getName().equals("hashCode")) {
			return method.hashCode();
		}
		long now = 0;
		if (log.isDebugEnabled()) {
			now = System.nanoTime();
		}
		DPSFRequest request = new DefaultRequest(this.metaData.getServiceName(), method.getName(), args, this.metaData.getSerialize(), Constants.MESSAGE_TYPE_SERVICE, this.metaData.getTimeout(), method.getParameterTypes());
		try {
			t.addData("CallType", this.metaData.getCallMethod());
			if (Constants.CALL_SYNC.equalsIgnoreCase(this.metaData.getCallMethod())) {
				DPSFResponse res = null;
				try {
					res = DefaultInvoker.getInstance().invokeSync(request, metaData, null);
				} catch (NetException e) {
					log.error(e.getMessage(), e);
					t.setStatus(e);
					throw e;
				}
				if (now > 0) {
					if (log.isDebugEnabled()) {
						log.debug("service:" + this.metaData.getServiceName() + "_" + method.getName());
						log.debug("execute time(microsecond):" + (System.nanoTime() - now) / 1000);
						log.debug("RequestId:" + res.getSequence());
					}
				}
				if (res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
					return res.getReturn();
				} else if (res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
					log.error(res.getCause());
					DPSFException dpsfException = new DPSFException(res.getCause());
					t.setStatus(dpsfException);
					throw dpsfException;
				} else if (res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
					Throwable throwable = (Throwable) res.getReturn();
					t.setStatus(throwable);
					throw throwable;
				}
				throw new DPSFException("no result to call");
			} else if (Constants.CALL_CALLBACK.equals(this.metaData.getCallMethod())) {
				try {
					DefaultInvoker.getInstance().invokeCallback(request, metaData, null, new ServiceWarpCallback(metaData.getCallback()));
				} catch (NetException e) {
					log.error(e.getMessage(), e);
					t.setStatus(e);
					throw e;
				}
				return getReturn(method.getReturnType());
			} else if (Constants.CALL_FUTURE.equals(this.metaData.getCallMethod())) {
				try {
					CallbackFuture future = new ServiceFutureImpl(this.metaData.getTimeout());
					ServiceFutureFactory.setFuture((ServiceFuture) future);
					DefaultInvoker.getInstance().invokeCallback(request, metaData, null, future);
				} catch (Exception e) {
					ServiceFutureFactory.remove();
					log.error(e.getMessage(), e);
					t.setStatus(e);
					throw e;
				}
				return getReturn(method.getReturnType());
			} else if (Constants.CALL_ONEWAY.equals(this.metaData.getCallMethod())) {
				try {
					DefaultInvoker.getInstance().invokeOneway(request, metaData, null);
				} catch (NetException e) {
					log.error(e.getMessage(), e);
					t.setStatus(e);
					throw e;
				}
				return getReturn(method.getReturnType());
			}
			DPSFException dpsfException = new DPSFException("callmethod configure is error:" + this.metaData.getCallMethod());
			t.setStatus(dpsfException);
			throw dpsfException;
		} catch (Exception e) {
			t.setStatus(e);
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
