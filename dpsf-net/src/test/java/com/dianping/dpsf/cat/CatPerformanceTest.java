/**
 * 
 */
package com.dianping.dpsf.cat;

import static com.dianping.cat.message.Message.SUCCESS;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.cat.Cat;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import com.dianping.dpsf.jmetertest.JmeterTestJavaIFace;

/**
 * @author yong.you
 * @version 1.0
 * @created 2012-3-18 下午03:58:15
 */
public class CatPerformanceTest {

	private final static String BEAN_SERVICES_NAME = "jmeter-client.xml";

	private static int s_count = 10000000;

	private static int s_threadNumber = 500;

	private static int s_error = 0;

	private static double[] s_avg = new double[s_threadNumber];

	private static AtomicInteger s_index = new AtomicInteger(-1);

	private static JmeterTestJavaIFace s_face;

	static {
		String catClientXml = "/data/appdatas/cat/client.xml";
		Cat.initialize(new File(catClientXml));
		
		try {
			ApplicationContext beanFactory = new ClassPathXmlApplicationContext(BEAN_SERVICES_NAME);
			s_face = (JmeterTestJavaIFace) beanFactory.getBean("jmeterClient");
		} catch (BeansException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			s_threadNumber = Integer.parseInt(args[0]);
		}
		CountDownLatch startLatch = new CountDownLatch(s_threadNumber);
		CountDownLatch endLatch = new CountDownLatch(s_threadNumber);
		long start = System.currentTimeMillis();

		for (int i = 0; i < s_threadNumber; i++) {
			TestThread thread = new TestThread(startLatch, endLatch);
			thread.start();
			startLatch.countDown();
		}
		try {
			endLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		int total = s_index.get()+1;
		double sum = 0;
		for (int i = 0; i < total; i++) {
			sum = sum + s_avg[i];
		}
		System.out.println("Current Thread Number:" + s_threadNumber);
		System.out.println("Send Number per second:" + s_threadNumber * s_count * 1000 / (end - start));
		System.out.println("Avg time per request:" + sum / total);
		System.out.println("Error Number:" + s_error);
		System.exit(0);
	}

	public static class TestThread extends Thread {

		CountDownLatch m_end;

		CountDownLatch m_latch;

		public TestThread(CountDownLatch latch, CountDownLatch end) {
			m_latch = latch;
			m_end = end;
		}

		@Override
		public void run() {
			try {
				m_latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long sum = 0;
			for (int i = 0; i < s_count; i++) {
				long time = System.nanoTime();
				Cat.setup("");
				MessageProducer cat = Cat.getProducer();
				Transaction t = cat.newTransaction("PigeonTest", "test");
				t.setStatus(Transaction.SUCCESS);
				try {
					// do your business here
					t.addData("k1", "v1");
					t.addData("k2", "v2");
					t.addData("k3", "v3");

					cat.logEvent("Type1", "Name1", SUCCESS, "data1");
					cat.logEvent("Type2", "Name2", SUCCESS, "data2");
					cat.logEvent("RemoteCall", "Service1", SUCCESS, cat.createMessageId());
					cat.logEvent("Type3", "Name3", SUCCESS, "data3");
					cat.logEvent("RemoteCall", "Service1", SUCCESS, cat.createMessageId());
					cat.logEvent("Exception", "java.lang.NullPointException", "ERROR1", "data4");
					cat.logEvent("Exception", "java.lang.RuntimeException", "ERROR1", "data5");
					t.setStatus("ERROR");
				} catch (Exception e) {
					t.setStatus(e);
				} finally {
					t.complete();
				}
				try {
	            Thread.sleep(10);
            } catch (InterruptedException e) {
	            e.printStackTrace();
            }
				Cat.reset();
				long endtime = System.nanoTime();
				sum = sum + (endtime - time);
			}

			s_avg[s_index.addAndGet(1)] = (double) (sum) / 1000000 / s_count;
			m_end.countDown();
		}
	}

}
