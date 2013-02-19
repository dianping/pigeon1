package com.dianping.dpsf.invoke;

import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;

public interface RemoteInvocationRepository {

    void    put(long sequence, RemoteInvocationBean invocation);
    void    remove(long sequence);
    void    receiveResponse(DPSFResponse response);
    
    public static class RemoteInvocationBean {
        public DPSFRequest  request;
        public DPSFCallback callback;
    }
}
