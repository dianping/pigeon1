/**
 *
 */
package com.dianping.dpsf.process;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.ExceptionUtil;
import com.dianping.dpsf.exception.RemoteServiceError;
import com.dianping.dpsf.protocol.DefaultResponse;

/**
 * <p>
 * Title: ResponseFactory.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-9-1 上午10:24:07
 */
public class ResponseFactory {

    public static DPSFResponse createThrowableResponse(long seq, byte serialization, Throwable e) {

        DPSFResponse response = null;
        switch (serialization) {
            case Constants.SERILIZABLE_JAVA:
                response = new DefaultResponse(serialization, seq, Constants.MESSAGE_TYPE_EXCEPTION, e);
                break;
            case Constants.SERILIZABLE_HESSIAN:
            case Constants.SERILIZABLE_HESSIAN1:
                response = new DefaultResponse(serialization, seq, Constants.MESSAGE_TYPE_EXCEPTION, e);
                break;
        }
        return response;
    }

    public static DPSFResponse createFailResponse(DPSFRequest request, Throwable e) {
        DPSFResponse response = null;
        byte serialization = request.getSerializ();
        if (request.getMessageType() == Constants.MESSAGE_TYPE_HEART) {
            response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_HEART, e);
        } else {
            response = createThrowableResponse(request.getSequence(), request.getSerializ(), e);
        }
        return response;
    }

    public static DPSFResponse createServiceExceptionResponse(DPSFRequest request, Throwable e) {
        DPSFResponse response = null;
        byte serialization = request.getSerializ();
        switch (serialization) {
            case Constants.SERILIZABLE_JAVA:
                response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_SERVICE_EXCEPTION, e);
                break;
            case Constants.SERILIZABLE_HESSIAN:
                response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_SERVICE_EXCEPTION, e);
                break;
            case Constants.SERILIZABLE_HESSIAN1:
                String stackTrace = "UnknownTrace";
                stackTrace = ExceptionUtil.extractStackTrace(e);
                RemoteServiceError serviceException = new RemoteServiceError(e.getClass().getName(), e.getMessage(), stackTrace);
                response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_SERVICE_EXCEPTION, serviceException);
                break;
        }
        return response;
    }

    @SuppressWarnings("rawtypes")
    public static DPSFResponse createSuccessResponse(DPSFRequest request, Object returnObj) {
        DPSFResponse response = null;
        byte serialization = request.getSerializ();
        switch (serialization) {
            case Constants.SERILIZABLE_JAVA:
                response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_SERVICE, returnObj);
                break;
            case Constants.SERILIZABLE_HESSIAN:
            case Constants.SERILIZABLE_HESSIAN1:
                response = new DefaultResponse(serialization, request.getSequence(), Constants.MESSAGE_TYPE_SERVICE, returnObj);
                break;
        }
        return response;
    }

    public static DPSFResponse createHeartResponse(DPSFRequest request) {
        DPSFResponse response = new DefaultResponse(Constants.MESSAGE_TYPE_HEART, request.getSerializ());
        response.setSequence(request.getSequence());
        response.setReturn(Constants.VERSION_150);
        return response;
    }

    public static boolean isHeartErrorResponse(DPSFResponse response) {
        try {
            return response != null && response.getMessageType() == Constants.MESSAGE_TYPE_HEART && response.getCause() != null;
        } catch (Exception e) {
            return false;
        }
    }

}
