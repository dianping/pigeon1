package com.dianping.dpsf.component;

import com.dianping.dpsf.net.channel.Client;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-4
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class InvocationInvokeContext extends InvocationContext {

    private Method                          method;
    private Object[]                        arguments;
    private DPSFMetaData                    metaData;
    private Client                          remoteClient;
    private Map<String, Serializable>       contextValues;
    //不会通过request传递到服务端，可用于filter之间传递参数
    private Map<String, Object>             transientContextValues;

    public InvocationInvokeContext(DPSFMetaData metaData, Method method, Object[] arguments) {
        this.metaData = metaData;
        this.method = method;
        this.arguments = arguments;
    }

    public DPSFMetaData getMetaData() {
        return metaData;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Client getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(Client remoteClient) {
        this.remoteClient = remoteClient;
    }

    public void putContextValue(String key, Serializable value) {
        if (contextValues == null) {
            contextValues = new HashMap<String, Serializable>();
        }
        contextValues.put(key, value);
    }

    public Map<String, Serializable> getContextValues() {
        return contextValues;
    }

    public void putTransientContextValue(String key, Object value) {
        if (transientContextValues == null) {
            transientContextValues = new HashMap<String, Object>();
        }
        transientContextValues.put(key, value);
    }

    public Object getTransientContextValue(String key) {
        if (transientContextValues == null) {
            return null;
        }
        return transientContextValues.get(key);
    }

    public void removeTransientContextValue(String key) {
        if (transientContextValues != null) {
            transientContextValues.remove(key);
        }
    }
}
