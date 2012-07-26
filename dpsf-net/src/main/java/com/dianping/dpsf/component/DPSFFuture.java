/**
 * 
 */
package com.dianping.dpsf.component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Title: DPSFFuture.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 上午12:58:16
 */
public interface DPSFFuture extends DPSFCall {

	DPSFResponse get() throws InterruptedException;

	DPSFResponse get(long timeoutMillis) throws InterruptedException;

	DPSFResponse get(long timeout, TimeUnit unit) throws InterruptedException;

	boolean cancel();

	boolean isCancelled();

	boolean isDone();

}
