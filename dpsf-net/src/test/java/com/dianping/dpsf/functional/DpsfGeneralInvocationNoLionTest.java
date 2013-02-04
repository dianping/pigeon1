/**
 * Project: ${dpsf-net.aid}
 *
 * File Created at 2012-5-15
 * $Id$
 *
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.functional;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetTimeoutException;
import com.dianping.dpsf.net.channel.cluster.loadbalance.LoadAutoawareLoadBalance;
import com.dianping.dpsf.net.channel.cluster.loadbalance.RandomLoadBalance;
import com.dianping.dpsf.support.BusinessException;
import com.dianping.dpsf.support.DemoService;
import com.dianping.dpsf.support.DpsfBaseFunctionalTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * @author danson.liu
 */
public class DpsfGeneralInvocationNoLionTest extends DpsfBaseFunctionalTest {

    @Test
    public void testSyncWithNoHostSet() {
        try {
            DemoService demoServiceStub = createServiceStub(DEMO_SERVICE_1_0_1, DemoService.class, Constants.SERIALIZE_HESSIAN,
                    Constants.CALL_SYNC, 2000, false, "", "");
            demoServiceStub.echo(DEMO_MESSAGE);
            fail("expect no client exception thrown.");
        } catch (Exception e) {
            assertNoClientException(e, DEMO_SERVICE_1_0_1);
        }
    }

    @Test
    public void testSyncWithZeroWeight() {
        try {
            DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "0");
            demoServiceStub.echo(DEMO_MESSAGE);
            fail("expect no available client exception thrown.");
        } catch (Exception e) {
            assertNoAvailableServerException(e, DEMO_SERVICE_1_0_0);
        }
    }

    @Test
    public void testSyncWithJava() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        String echoReturn = demoServiceStub.echo(DEMO_MESSAGE);
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test
    public void testFutureWithJava() throws DPSFException, InterruptedException {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_FUTURE, DEMO_SERVICE_HOST1, "1");
        demoServiceStub.echo(DEMO_MESSAGE);
        ServiceFuture future = ServiceFutureFactory.getFuture();
        Object echoReturn = future._get();
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test
    public void testCallbackWithJava() throws DPSFException, InterruptedException {
        final StringBuilder echoReturn = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);
        ServiceCallback callback = new ServiceCallbackAdapter() {
            @Override
            public void callback(Object result) {
                echoReturn.append(result);
                latch.countDown();
            }
        };
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_CALLBACK, DEMO_SERVICE_HOST1, "1", callback);
        demoServiceStub.echo(DEMO_MESSAGE);
        latch.await();
        assertEquals(DEMO_EXPECT_RETURN, echoReturn.toString());
    }

    @Test
    public void testOnewayWithJava() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_ONEWAY, DEMO_SERVICE_HOST1, "1");
        demoServiceStub.echo(DEMO_MESSAGE);
    }

    @Test
    public void testFutureWithHessian() throws DPSFException, InterruptedException {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_FUTURE, DEMO_SERVICE_HOST1, "1");
        demoServiceStub.echo(DEMO_MESSAGE);
        ServiceFuture future = ServiceFutureFactory.getFuture();
        Object echoReturn = future._get();
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test
    public void testSyncWithJavaAndError() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        try {
            demoServiceStub.echoWithError(false);
            fail("expect BusinessException thrown.");
        } catch (BusinessException e) {
        } catch (Throwable e) {
            fail("expect BusinessException thrown.");
        }
    }

    @Test
    public void testSyncWithJavaAndRuntimeError() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        try {
            demoServiceStub.echoWithError(true);
            fail("expect RuntimeException thrown.");
        } catch (RuntimeException e) {
        } catch (Throwable e) {
            fail("expect RuntimeException thrown.");
        }
    }

    @Test
    public void testSyncWithHessian() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        String echoReturn = demoServiceStub.echo(DEMO_MESSAGE);
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test
    public void testSyncWithHessianAndError() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        try {
            demoServiceStub.echoWithError(false);
            fail("expect BusinessException thrown.");
        } catch (BusinessException e) {
        } catch (Throwable e) {
            fail("expect BusinessException thrown.");
        }
    }

    @Test
    public void testSyncWithHessianAndRuntimeError() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        try {
            demoServiceStub.echoWithError(true);
            fail("expect RuntimeException thrown.");
        } catch (RuntimeException e) {
        } catch (Throwable e) {
            fail("expect RuntimeException thrown.");
        }
    }

    @Test
    public void testSyncWithHessianAndTwoHosts() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_TWO_HOSTS, "1,2");
        String echoReturn = demoServiceStub.echo(DEMO_MESSAGE);
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test
    public void testSyncWithHessianAndHasInvalidHost() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOSTS_WITH_INVALID, "1,1");
        String echoReturn = demoServiceStub.echo(DEMO_MESSAGE);
        assertEquals(DEMO_EXPECT_RETURN, echoReturn);
    }

    @Test(expected = NetTimeoutException.class)
    public void testSyncTimeout() {
        DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, DEMO_SERVICE_HOST1, "1");
        demoServiceStub.sleep(3);
    }

    @Test
    public void testInvokeWithSpecifiedHost() {
        for (int i = 0; i < 100; i++) {
            ClientContext.setUseClientAddress(DEMO_SERVICE_HOST1);
            DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, RandomLoadBalance.NAME,
                    DEMO_SERVICE_TWO_HOSTS, "1,1");
            demoServiceStub.echo("foo");
            assertEquals(DEMO_SERVICE_HOST1, ClientContext.getUsedClientAddress());
        }
    }

    @Test
    public void testRandomLoadbalance() throws InterruptedException {
        final Map<String, AtomicLong> stats = createRpcInvocationStats(Arrays.asList(DEMO_SERVICE_HOST1, DEMO_SERVICE_HOST2));
        final DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, RandomLoadBalance.NAME,
                DEMO_SERVICE_TWO_HOSTS, "1,1");
        final CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        demoServiceStub.sleep();
                        String usedHost = ClientContext.getUsedClientAddress();
                        stats.get(usedHost).incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }
        latch.await();
        assertTrue(stats.get(DEMO_SERVICE_HOST2).intValue() > 4);
    }

    @Test
    public void testAutoawareLoadbalance() throws InterruptedException {
        final Map<String, AtomicLong> stats = createRpcInvocationStats(Arrays.asList(DEMO_SERVICE_HOST1, DEMO_SERVICE_HOST2));
        final DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, LoadAutoawareLoadBalance.NAME,
                DEMO_SERVICE_TWO_HOSTS, "1,1");
        final CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        demoServiceStub.sleep();
                        String usedHost = ClientContext.getUsedClientAddress();
                        stats.get(usedHost).incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }
        latch.await();
        assertTrue(stats.get(DEMO_SERVICE_HOST2).intValue() < 4);
    }

}
