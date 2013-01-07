package com.dianping.dpsf.component;

import com.dianping.dpsf.net.channel.Client;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-4
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class InvocationInvokeContext extends InvocationContext {

    private Method          method;
    private Object[]        arguments;
    private DPSFMetaData    metaData;
    private Client          remoteClient;
    private Object          trackerContext;

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

    public Object getTrackerContext() {
        return trackerContext;
    }

    public void setTrackerContext(Object trackerContext) {
        this.trackerContext = trackerContext;
        if (request != null) {
            request.setContext(trackerContext);
        }
    }

}
