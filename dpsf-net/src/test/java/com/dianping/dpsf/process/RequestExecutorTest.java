/**
 * 
 */
package com.dianping.dpsf.process;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.support.DemoService;
import com.dianping.dpsf.support.DpsfBaseFunctionalTest;

/**
 * @author sean.wang
 * @since Jun 28, 2012
 */
public class RequestExecutorTest extends DpsfBaseFunctionalTest {

	@Ignore
	public void testSyncWithJava() {
		DemoService demoServiceStub = createDemoServiceStub(Constants.SERIALIZE_JAVA, Constants.CALL_SYNC, 1000 * 1000, DEMO_SERVICE_HOST1, "1");
		String echoReturn = demoServiceStub.echo("xxx");
		assertEquals("hello xxx", echoReturn);
		
	}
	
	@Test
	public void test() {
		
	}

}
