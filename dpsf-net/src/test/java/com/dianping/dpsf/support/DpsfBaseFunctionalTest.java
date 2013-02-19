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
package com.dianping.dpsf.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.dianping.dpsf.PigeonBootStrap;
import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.other.route.PigeonClientMock;
import com.dianping.dpsf.spring.ProxyBeanFactory;
import com.dianping.dpsf.spring.ServiceRegistry;
import com.dianping.lion.pigeon.ServiceChange;

/**
 * @author danson.liu
 */
public abstract class DpsfBaseFunctionalTest {

    public static final String DEMO_SERVICE_1_0_0 = "http://service.dianping.com/pigeonTestService/demoService_1.0.0";
    public static final String DEMO_SERVICE_1_0_1 = "http://service.dianping.com/pigeonTestService/demoService_1.0.1";
    public static final String ORDER_SERVICE_1_0_0 = "http://service.dianping.com/pigeonTestService/orderService_1.0.0";
    public static final String CUSTOMER_SERVICE_1_0_0 = "http://service.dianping.com/pigeonTestService/customerService_1.0.0";
    public static final String DEMO_MESSAGE = "foo_message";
    public static final String DEMO_EXPECT_RETURN = "hello " + DEMO_MESSAGE;
    public static final int TEST_SERVICE_PORT1 = 11000;
    public static final int TEST_SERVICE_PORT2 = 11001;
    public static final String DEMO_SERVICE_ADDR = "127.0.0.1";
    public static final String DEMO_SERVICE_INVALID_ADDR = "127.0.0.2";
    public static final String DEMO_SERVICE_HOST1 = DEMO_SERVICE_ADDR + ":" + TEST_SERVICE_PORT1;
    public static final String DEMO_SERVICE_HOST2 = DEMO_SERVICE_ADDR + ":" + TEST_SERVICE_PORT2;
    public static final String DEMO_SERVICE_INVALID_HOST = DEMO_SERVICE_INVALID_ADDR + ":" + TEST_SERVICE_PORT1;
    public static final String DEMO_SERVICE_TWO_HOSTS = DEMO_SERVICE_HOST1 + "," + DEMO_SERVICE_HOST2;
    public static final String DEMO_SERVICE_HOSTS_WITH_INVALID = DEMO_SERVICE_HOST1 + "," + DEMO_SERVICE_INVALID_HOST;

    protected ExecutorService threadPool = Executors.newCachedThreadPool();

    @BeforeClass
    public static void setUp() {
        try {
            setUpLionEnvironment();
            setUpPigeonTestService(TEST_SERVICE_PORT1, 1, true);
            setUpPigeonTestService(TEST_SERVICE_PORT2, 1000, false);
        } catch (Exception e) {
            throw new RuntimeException("Setup PigeonTestService failed.", e);
        }
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty(NettyClientManager.LION_CLIENT_CLASS);
        PigeonBootStrap.shutdown();
    }

    private static void setUpLionEnvironment() {
        System.setProperty(NettyClientManager.LION_CLIENT_CLASS, PigeonClientMock.class.getName());
        new PigeonClientMock(new ServiceChangeMock());
    }

    private static void setUpPigeonTestService(int port, int demoSleepMs, boolean demoSayEvenError) throws Exception {
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        serviceRegistry.setPort(port);
        Map<String, Object> services = new HashMap<String, Object>();
        DemoService demoService = new DemoServiceImpl(demoSleepMs, demoSayEvenError);
        services.put(DEMO_SERVICE_1_0_0, demoService);
        serviceRegistry.setServices(services);
        serviceRegistry.init();
    }

    @SuppressWarnings("unchecked")
    protected <T> T createServiceStub(String serviceName, Class<?> iface, String serialize, String callMethod, int timeout, boolean useLion,
                                      String hosts, String weights) {
        return (T) createServiceStub(serviceName, iface, serialize, callMethod, timeout, null, useLion, hosts, weights);
    }

    @SuppressWarnings("unchecked")
    protected <T> T createServiceStub(String serviceName, Class<?> iface, String serialize, String callMethod, int timeout, String loadbalance,
                                      boolean useLion, String hosts, String weights) {
        return (T) createServiceStub(serviceName, iface, serialize, callMethod, timeout, loadbalance, null, useLion, hosts, weights);
    }

    @SuppressWarnings("unchecked")
    protected <T> T createServiceStub(String serviceName, Class<?> iface, String serialize, String callMethod, int timeout, String loadbalance,
                                      ServiceCallback callback, boolean useLion, String hosts, String weights) {
        try {
            ProxyBeanFactory beanFactory = createServiceStubFactory(serviceName, iface, serialize, callMethod, timeout, loadbalance, callback, useLion, hosts, weights);
            initInitializableObject(beanFactory);
            return (T) beanFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException("Create service stub[" + serviceName + "] failed.", e);
        }
    }

    protected ProxyBeanFactory createServiceStubFactory(String serviceName, Class<?> iface, String serialize, String callMethod, int timeout, String loadbalance,
                                                        ServiceCallback callback, boolean useLion, String hosts, String weights) {
        ProxyBeanFactory beanFactory = new ProxyBeanFactory();
        beanFactory.setServiceName(serviceName);
        beanFactory.setIface(iface.getName());
        beanFactory.setSerialize(serialize);
        beanFactory.setCallMethod(callMethod);
        beanFactory.setCallback(callback);
        beanFactory.setTimeout(timeout);
        beanFactory.setLoadBalance(loadbalance);
        if (!useLion) {
            beanFactory.setIsTest(true);
            beanFactory.setHosts(hosts);
            beanFactory.setWeight(weights);
        }
        return beanFactory;
    }

    protected DemoService createDemoServiceStub(String serialize, String callMethod, String hosts, String weights) {
        return createDemoServiceStub(serialize, callMethod, 2000, hosts, weights);
    }

    protected DemoService createDemoServiceStub(String serialize, String callMethod, int timeout, String hosts, String weights) {
        return createServiceStub(DEMO_SERVICE_1_0_0, DemoService.class, serialize, callMethod, timeout, false, hosts, weights);
    }


    protected DemoService createDemoServiceStub(String serialize, String callMethod, String loadbalance, String hosts, String weights) {
        return createServiceStub(DEMO_SERVICE_1_0_0, DemoService.class, serialize, callMethod, 2000, loadbalance, false, hosts, weights);
    }

    protected DemoService createDemoServiceStub(String serialize, String callMethod, String hosts, String weights, ServiceCallback callback) {
        return createServiceStub(DEMO_SERVICE_1_0_0, DemoService.class, serialize, callMethod, 2000, null, callback, false, hosts, weights);
    }

    protected DemoService createDemoServiceStub(String serialize, String callMethod, String loadbalance, String hosts, String weights, ServiceCallback callback) {
        return createServiceStub(DEMO_SERVICE_1_0_0, DemoService.class, serialize, callMethod, 2000, loadbalance, callback, false, hosts, weights);
    }

    protected void assertNoClientException(Exception e, String serviceName) {
        Assert.assertTrue(e instanceof NetException);
        Assert.assertTrue(e.getMessage().contains("no connection for serviceName:" + serviceName));
    }

    protected void assertNoAvailableServerException(Exception e, String serviceName) {
        Assert.assertTrue(e instanceof NetException);
        String errorMessage = e.getMessage();
        Assert.assertTrue(errorMessage.contains("No available server") && errorMessage.contains(serviceName));
    }

    static class ServiceChangeMock implements ServiceChange {
        @Override
        public void onHostWeightChange(String host, int weight) {
        }
        @Override
        public void onServiceHostChange(String serviceName, List<String[]> hostList) {
        }
    }

    protected void initInitializableObject(Object initializableObj) {
        try {
            Method initMethod = initializableObj.getClass().getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(initializableObj);
        } catch (Exception e) {
            throw new RuntimeException("Init object[" + initializableObj + "] failed.", e);
        }
    }

    protected Map<String, AtomicLong> createRpcInvocationStats(List<String> hosts) {
        Map<String, AtomicLong> invocationStats = new HashMap<String, AtomicLong>();
        for (String host : hosts) {
            invocationStats.put(host, new AtomicLong());
        }
        return invocationStats;
    }

    protected class ServiceCallbackAdapter implements ServiceCallback {
        @Override
        public void serviceException(Exception e) {
        }

        @Override
        public void frameworkException(DPSFException e) {
        }

        @Override
        public void callback(Object result) {
        }
    }

}
