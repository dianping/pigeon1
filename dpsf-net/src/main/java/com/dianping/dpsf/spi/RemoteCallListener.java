package com.dianping.dpsf.spi;

import com.dianping.dpsf.listener.RemoteCallEvent;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-22
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public interface RemoteCallListener extends EventListener {

    public void remoteCallEvent(RemoteCallEvent event);
    
}
