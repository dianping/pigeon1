package com.dianping.dpsf.fail;

import java.lang.reflect.UndeclaredThrowableException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.net.channel.protocol.DPSFDecoder;

public class FailTest {
	private static BeanFactory beanFactory = null;
	private static Fail fail;
	
	private static FailTimeout fail1;
	private static FailTimeoutImpl failTimeout1;
	private static FailTimeoutImpl failTimeout2;
	private static FailTimeoutImpl failTimeout3;
	@BeforeClass
	public static void init(){
		beanFactory = new ClassPathXmlApplicationContext(new String[]{"fail-server.xml","fail-client.xml"});
		fail = (Fail)beanFactory.getBean("fail");
		fail1 = (FailTimeout)beanFactory.getBean("fail1");
		failTimeout1 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl1");
		failTimeout2 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl2");
		failTimeout3 = (FailTimeoutImpl)beanFactory.getBean("failTimeoutImpl3");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@AfterClass
	public static void done(){
		DefaultInvoker.setInvoker(null);
		ClientManagerFactory.setManager(null);
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

}
