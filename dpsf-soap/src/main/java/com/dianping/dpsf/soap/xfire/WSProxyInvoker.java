/**
 * 
 */
package com.dianping.dpsf.soap.xfire;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.codehaus.xfire.client.Client;

import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: WSProxyInvoker.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-5-17 下午09:49:25   
 */
public class WSProxyInvoker implements InvocationHandler{
	
	private Class iface;
	private String wsdlUrl;
	private Map<String,String> method2Operation;
	private Map<String,QName> method2QName;
	private Client client;
	public WSProxyInvoker(Class iface,String wsdlUrl,int timeout) throws NetException{
		this.iface = iface;
		this.wsdlUrl = wsdlUrl;
		try {
			this.client = new Client(new URL(this.wsdlUrl));
			this.client.setTimeout(timeout);
		} catch (Exception e) {
			throw new NetException(e);
		}
		WebService ws = (WebService)this.iface.getAnnotation(WebService.class);
		if(ws != null){
			method2Operation = new ConcurrentHashMap<String,String>();
			method2QName = new ConcurrentHashMap<String,QName>();
			Method[] methods = this.iface.getDeclaredMethods();
			for(Method m : methods){
				WebMethod wm = (WebMethod)m.getAnnotation(WebMethod.class);
				if(wm != null){
					method2Operation.put(m.getName(), wm.operationName());
				}
				WebResult wr = (WebResult)m.getAnnotation(WebResult.class);
				if(wr != null){
					this.method2QName.put(m.getName(), new QName(wr.targetNamespace(),wr.name()));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String opName = method.getName();
		if(this.method2Operation != null){
			opName = this.method2Operation.get(method.getName());
			if(args != null){
				for(int i=0;i<args.length;i++){
					if(args[i] instanceof JAXBElement){
						JAXBElement arg = (JAXBElement)args[i];
						args[i] = arg.getValue();
					}
				}
			}
			
		}
		Object[] results = this.client.invoke(opName, args);
		if(results != null && results.length > 0){
			Object result = results[0];
			if(this.method2QName != null){
				QName qn = this.method2QName.get(method.getName());
				if(qn != null && method.getReturnType() == JAXBElement.class){
					return new JAXBElement(new QName(qn.getNamespaceURI(),qn.getLocalPart()),result.getClass(),result);
				}
			}
			return result;
		}else{
			return null;
		}
	}

}
