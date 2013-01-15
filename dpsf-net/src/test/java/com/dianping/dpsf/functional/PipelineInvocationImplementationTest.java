package com.dianping.dpsf.functional;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.invoke.filter.cluster.FailoverClusterInvokeFilter;
import com.dianping.dpsf.misc.EchoService;
import com.dianping.dpsf.spring.ProxyBeanFactory;
import com.dianping.dpsf.support.DemoService;
import com.dianping.dpsf.support.DpsfBaseFunctionalTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-14
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class PipelineInvocationImplementationTest extends DpsfBaseFunctionalTest {

    private static PigeonConfig pigeonConfig = PigeonConfig.getInstance();
    private static Boolean oldUseNewProcessSet;
    private static Boolean oldUseNewInvokeSet;

    @BeforeClass
    public static void setUp() {
        oldUseNewProcessSet = pigeonConfig.getUseNewProcessSetByJmx();
        oldUseNewInvokeSet = pigeonConfig.getUseNewInvokeSetByJmx();
        pigeonConfig.setUseNewProcessSetByJmx(true);
        pigeonConfig.setUseNewInvokeSetByJmx(true);
        DpsfBaseFunctionalTest.setUp();
    }

    @Test
    public void testFailoverCluster() throws Exception {
        ProxyBeanFactory serviceStubFactory = createServiceStubFactory(DEMO_SERVICE_1_0_0, DemoService.class, Constants.SERIALIZE_HESSIAN,
                Constants.CALL_SYNC, 800, null, null, false, DEMO_SERVICE_TWO_HOSTS, "1,1");
        Map<String, String> clusterConfig = new HashMap<String, String>();
        clusterConfig.put("sleep", "cluster:" + FailoverClusterInvokeFilter.NAME + ",retry:1,timeout-retry:1");
        serviceStubFactory.setClusterConfig(clusterConfig);
        initInitializableObject(serviceStubFactory);
        DemoService demoServiceStub = (DemoService) serviceStubFactory.getObject();
        try {
            for (int i = 0; i < 5; i++) {
                demoServiceStub.sleep();
            }
        } catch (Exception e) {
            fail("expect never be here, the exception[" + e.getClass().getName() + "] should be overwhelm by 'failover' cluster strategy.");
        }

    }

    @Test
    public void testEcho() {
        EchoService echoService = createServiceStub("http://service.dianping.com/pigeonService/echoService_1.0.0",
                EchoService.class, Constants.SERIALIZE_HESSIAN, Constants.CALL_SYNC, 5000, false, DEMO_SERVICE_HOST1, "1");
        String fooMessage = "foo message";
        String result = echoService.$echo(fooMessage);
        assertEquals(fooMessage, result);
    }

    @AfterClass
    public static void tearDown() {
        pigeonConfig.setUseNewProcessSetByJmx(oldUseNewProcessSet);
        pigeonConfig.setUseNewInvokeSetByJmx(oldUseNewInvokeSet);
    }

}
