/**
 *
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.RequestError;
import org.jboss.netty.channel.ChannelFuture;

/**
 * <p>
 * Title: DPSFCallback.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 *
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 上午12:17:03
 */
public interface DPSFCallback extends Runnable, DPSFCall {

    void callback(DPSFResponse response);

    DPSFFuture getFuture(ChannelFuture future);

    void fail(RequestError error);

    void setRequest(DPSFRequest request);

    DPSFRequest getRequest();

}
