/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.exception.NetException;

/**
 * <p>
 * Title: Invoker.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-18 下午03:14:21
 */
public interface Invoker {

 DPSFResponse invokeSync(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException, InterruptedException;

 void invokeCallback(DPSFRequest request, DPSFMetaData metaData, DPSFController controller, DPSFCallback callback) throws NetException;

 DPSFFuture invokeFuture(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException;

 void invokeOneway(DPSFRequest request, DPSFMetaData metaData, DPSFController controller) throws NetException;

 void invokeReponse(DPSFResponse response);

}
