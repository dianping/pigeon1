package com.dianping.dpsf.process.filter;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.InvocationProcessContext;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.invoke.RemoteInvocationHandler;
import com.dianping.dpsf.process.ResponseFactory;
import com.dianping.dpsf.repository.DPSFMethod;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.dpsf.stat.ServiceStat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-5
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class BusinessProcessFilter extends InvocationProcessFilter {

    private ServiceStat serverServiceStat = ServiceStat.getServerServiceStat();

    public BusinessProcessFilter(int order) {
        super(order);
    }

    @Override
    public DPSFResponse invoke(RemoteInvocationHandler handler, InvocationProcessContext invocationContext) throws Throwable {
        ServiceRepository serviceRepository = invocationContext.getServiceRepository();
        DPSFRequest request = invocationContext.getRequest();
        if (request.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
            ContextUtil.putLocalContext(Constants.REQUEST_CREATE_TIME, request.getCreateMillisTime());
            ContextUtil.putLocalContext(Constants.REQUEST_TIMEOUT, request.getTimeout());

            DPSFResponse response = null;
            DPSFMethod method = serviceRepository.getMethod(request.getServiceName(), request.getMethodName(), request.getParamClassName());
            Method method_ = method.getMethod();
            try {
                long currentTime = 0;
                if (logger.isDebugEnabled()) {
                    currentTime = System.nanoTime();
                }
                Object returnObj = method_.invoke(method.getService(), request.getParameters());
                if (currentTime > 0) {
                    logger.debug("service:" + request.getServiceName() + "_" + request.getMethodName());
                    logger.debug("execute time:" + (System.nanoTime() - currentTime) / 1000);
                    logger.debug("RequestId:" + request.getSequence());
                }
                if (request.getCallType() == Constants.CALLTYPE_REPLY) {
                    response = ResponseFactory.createSuccessResponse(request, returnObj);
                }
            } catch (InvocationTargetException e) {
                Throwable e2 = e.getTargetException();
                if (e2 != null) {
                    logger.error(e2.getMessage(), e2);
                }
                if (request.getCallType() == Constants.CALLTYPE_REPLY) {
                    response = ResponseFactory.createServiceExceptionResponse(request, e2);
                }
                invocationContext.setServiceError(e2);
            } catch (Exception e) {
                invocationContext.setServiceError(e);
                throw e;
            }
            serverServiceStat.timeService(request.getServiceName(), request.getCreateMillisTime());
            return response;
        }
        throw new DPSFException("Message type[" + request.getMessageType() + "] is not supported!");
    }

    public void setServerServiceStat(ServiceStat serverServiceStat) {
        this.serverServiceStat = serverServiceStat;
    }
}
