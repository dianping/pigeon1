package com.dianping.dpsf.process.filter;

import java.io.Serializable;
import java.util.Map;

import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationInvokeContext;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.spi.InvocationProcessFilter;
import com.dianping.dpsf.stat.ServiceStat;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
public class ContextTransferProcessFilter extends InvocationProcessFilter {

    private ServiceStat serverServiceStat = ServiceStat.getServerServiceStat();

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        DPSFRequest request = invocationContext.getRequest();
        serverServiceStat.countService(invocationContext.getRequest().getServiceName());
        
        transferContextValueToProcessor(invocationContext,request);
        
        DPSFResponse response = null;
        try {
            response = handler.handle(invocationContext);
            return response;
        } finally {
            if (response != null) {
                try {
                	transferContextValueToResponse(invocationContext, response);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
    
    private void transferContextValueToProcessor(final InvocationProcessContext processContext, final DPSFRequest request) {
        Object contextHolder = request.getContext();
        Map<String,Serializable> ctx = null;
        if(contextHolder instanceof Map){
        	ctx = (Map<String,Serializable>) contextHolder;
        }else{
        	ContextUtil.setContext(contextHolder);
        	ctx = ContextUtil.getContextValues(contextHolder);
        }
        
        if (ctx != null) {
            for (Map.Entry<String, Serializable> entry : ctx.entrySet()) {
            	processContext.putContextValue(entry.getKey(), entry.getValue());
            }
        }
        
    }
    
    private void transferContextValueToResponse(final InvocationProcessContext processContext, final DPSFResponse response) {
        Object contextHolder = ContextUtil.getContext();
        
        Map<String,Serializable> contextValues = processContext.getContextValues();
        if(contextHolder == null){
        	response.setContext(contextValues);
        }else{
        	 if (contextValues != null) {
                 for (Map.Entry<String, Serializable> entry : contextValues.entrySet()) {
                     ContextUtil.putContextValue(contextHolder, entry.getKey(), entry.getValue());
                 }
             }
        	 response.setContext(contextHolder);
        }
        
    }

    public void setServerServiceStat(ServiceStat serverServiceStat) {
        this.serverServiceStat = serverServiceStat;
    }

}
