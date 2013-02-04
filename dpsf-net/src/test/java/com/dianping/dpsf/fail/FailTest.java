package com.dianping.dpsf.fail;

import java.lang.reflect.UndeclaredThrowableException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import com.dianping.dpsf.PigeonBootStrap;
import com.dianping.dpsf.net.channel.protocol.DPSFDecoder;

public class FailTest {
	private static BeanFactory beanFactory = null;
	private static Fail fail;
	
	private static FailTimeout fail1;
	private static FailTimeoutImpl failTimeout1;
	private static FailTimeoutImpl failTimeout2;
	private static FailTimeoutImpl failTimeout3;

	private static FailTimeout fail4;
	private static FailTimeoutImpl failTimeout4;
	private static FailTimeoutImpl failTimeout5;
	private static FailTimeoutImpl failTimeout6;
	@BeforeClass
	public static void init(){
		beanFactory = new ClassPathXmlApplicationContext(new String[]{"fail-server.xml","fail-client.xml"});
		fail = (Fail)beanFactory.getBean("fail");
		fail1 = (FailTimeout)beanFactory.getBean("fail1");
		failTimeout1 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl1");
		failTimeout2 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl2");
		failTimeout3 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl3");

		fail4 = (FailTimeout)beanFactory.getBean("fail4");
		failTimeout4 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl4");
		failTimeout5 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl5");
		failTimeout6 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl6");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@AfterClass
	public static void done() {
	    PigeonBootStrap.shutdown();
	}
	
	@Test
	public void testSuccess(){
		int p = 5;
		Assert.isTrue(p+1==fail.testSuccess(new TestParameter(p)).getResult());
		Assert.isTrue(p+2==fail.testSuccess(new TestParameter(p+1)).getResult());
	}
	
	@Test
	public void testClientSerializeFail(){
		int p = 5;
		try{
			fail.testClientSerializeFail(new TestParameterNoSerializable(p)).getResult();
		}catch(Exception e){
			Assert.isTrue(e.getMessage().indexOf("host:") > -1
					&&e.getMessage().indexOf("TestParameterNoSerializable") > -1);
			return;
		}
		Assert.isTrue(false);
	}
	
	@Test
	public void testServerSerializeFail(){
		int p = 5;
		try{
			Assert.isTrue(p+1==fail.testServerSerializeFail(new TestParameter(p)).getResult());
		}catch(Exception e){
			Assert.isTrue(e.getMessage().indexOf("host:") > -1
					&&e.getMessage().indexOf("TestResultNoSerializable") > -1);
			return;
		}
		Assert.isTrue(false);
	}
	
	@Test
	public void testClientDeserializeFail(){
		int p = 5;
		DPSFDecoder.setClientTest(true);
		try{
			int result = fail.testSuccess(new TestParameter(p)).getResult();
			System.out.println(result);
		}catch(UndeclaredThrowableException e_){
			
			Throwable e = e_.getUndeclaredThrowable();
			Assert.isTrue(e.getMessage().indexOf("host:") > -1
					&&e.getMessage().indexOf("com.dianping.test.client.xxx") > -1);
			return;
		}finally{
			DPSFDecoder.setClientTest(false);
		}
		Assert.isTrue(false);
	}
	
	@Test
	public void testServerDeserializeFail(){
		int p = 5;
		DPSFDecoder.setServerTest(true);
		try{
			fail.testSuccess(new TestParameter(p)).getResult();
		}catch(UndeclaredThrowableException e_){
			Throwable e = e_.getUndeclaredThrowable();
			Assert.isTrue(e.getMessage().indexOf("host:") > -1
					&&e.getMessage().indexOf("com.dianping.test.server.xxx") > -1);
			return;
		}finally{
			DPSFDecoder.setServerTest(false);
		}
		Assert.isTrue(false);
	}
	
	@Test
	public void testException(){
		try {
			fail.testException();
		} catch (TestException e) {
			Assert.isTrue(e instanceof TestException);
			e.printStackTrace();
		}
	}
	
	//串联测试
	@Test
	public void testTimeoutNormal(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int p = 5;
		Assert.isTrue(p+3 == this.fail1.testCallNormal1(p));
		Assert.isTrue(this.failTimeout1.getCreateTime()==this.failTimeout2.getCreateTime()
				&& this.failTimeout2.getCreateTime() == this.failTimeout3.getCreateTime());
		Assert.isTrue(this.failTimeout1.getTimeout() == 8000
				&& this.failTimeout2.getTimeout() == 4000
				&& this.failTimeout3.getTimeout() == 4000);

	}
	
	//串联测试
	@Test
	public void testTimeoutException(){
		int p = 5;
		try{
		this.fail1.testCallTimeout1(p);
		}catch(Exception e){
			Assert.isTrue(e.getMessage().indexOf("serviceName:http://service.dianping.com/failService2") > -1);
		}
		Assert.isTrue(this.failTimeout1.getCreateTime()==this.failTimeout2.getCreateTime()
				&& this.failTimeout2.getCreateTime() == this.failTimeout3.getCreateTime());
		Assert.isTrue(this.failTimeout1.getTimeout() == 8000
				&& this.failTimeout2.getTimeout() == 4000
				&& this.failTimeout3.getTimeout() == 4000);

	}
	
	//串联并联混合测试
	@Test
	public void testTimeoutNormal1(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try{
		int p = 5;
		Assert.isTrue(2*(p+2)+1 == this.fail4.testCallNormal4(p));
		Assert.isTrue(this.failTimeout4.getCreateTime() == this.failTimeout5.getCreateTime());
		Assert.isTrue(this.failTimeout5.getCreateTime() < this.failTimeout6.getCreateTime());
		Assert.isTrue(this.failTimeout4.getTimeout() == 9000);
		Assert.isTrue(this.failTimeout5.getTimeout() == 4000);
		Assert.isTrue(this.failTimeout6.getTimeout() == 3000);
//		}catch(Exception e){
//			try {
//				Thread.sleep(500000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
	}
	
	//串联并联混合测试
	@Test
	public void testTimeoutException2(){


		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int p = 5;

//		try{

		try{
		this.fail4.testCallTimeout4(p);
		}catch(Exception e){
			Assert.isTrue(e.getMessage().indexOf("serviceName:http://service.dianping.com/failService4") > -1);
		}
		Assert.isTrue(this.failTimeout4.getCreateTime()==this.failTimeout5.getCreateTime()
				&& this.failTimeout5.getCreateTime() < this.failTimeout6.getCreateTime());
		Assert.isTrue(this.failTimeout4.getTimeout() == 9000
				&& this.failTimeout5.getTimeout() == 4000
				&& this.failTimeout6.getTimeout() < 1000);
//		}catch(Exception e){
//		try {
//			Thread.sleep(500000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
	}

}
