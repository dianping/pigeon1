package com.dianping.dpsf.listener;

import com.dianping.dpsf.spi.RemoteCallListener;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-22
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class RemoteCallListenerRegistry {

    private RemoteCallListener[]    listeners           = new RemoteCallListener[0];

    private final Object            listenersLock       = new Object();
    
    private volatile boolean        registryEnabled     = true;
    
    public void addRemoteCallListener(RemoteCallListener listener) {
        if (registryEnabled) {
            synchronized (listenersLock) {
                RemoteCallListener[] newListeners = new RemoteCallListener[listeners.length + 1];
                for (int i = 0; i < listeners.length; i++) {
                    newListeners[i] = listeners[i];
                }
                newListeners[listeners.length] = listener;
                listeners = newListeners;
            }
        }
    }
    
    public void fireRemoteCallEvent(Object source, String type, Object data) {
        RemoteCallEvent event = new RemoteCallEvent(source, type, data);
        RemoteCallListener[] listeners_ = listeners;
        for (int i = 0; i < listeners_.length; i++) {
            listeners_[i].remoteCallEvent(event);
        }
    }
    
    public void setRegistryEnabled(boolean enabled) {
        this.registryEnabled = enabled;
    }

}
