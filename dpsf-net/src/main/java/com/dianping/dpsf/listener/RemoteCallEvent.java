package com.dianping.dpsf.listener;

import java.util.EventObject;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-22
 * Time: 下午4:24
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
public class RemoteCallEvent extends EventObject {

    private final String type;
    private final Object data;

    public RemoteCallEvent(Object source, String type, Object data) {
        super(source);
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

}
