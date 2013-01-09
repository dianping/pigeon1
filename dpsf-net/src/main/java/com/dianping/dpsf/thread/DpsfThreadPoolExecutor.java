package com.dianping.dpsf.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DpsfThreadPoolExecutor extends ThreadPoolExecutor{

	AtomicInteger executingThreadCount = new AtomicInteger(0);
	AtomicInteger waitingThreadCount = new AtomicInteger(0);

	private volatile long lastCreateThreadTime;
	private int intervalTime = 60 * 1000;

	AtomicInteger destoryThreadCount = new AtomicInteger(0);
	AtomicInteger createThreadCount = new AtomicInteger(0);

	public DpsfThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit,
				new TestLinkedBlockingQueue<Runnable>(10000), threadFactory);
		TestLinkedBlockingQueue<Runnable> workQueue = (TestLinkedBlockingQueue<Runnable>) getQueue();
		workQueue.setPool(this);
		allowCoreThreadTimeOut(true);
	}


	public int getCreateThreadCount() {
		return this.createThreadCount.get();
	}

	public int getDestoryThreadCount() {
		return this.destoryThreadCount.get();
	}
	
	public int getWaitingThreadCount(){
		return this.waitingThreadCount.get();
	}

	private static class TestLinkedBlockingQueue<E> extends
			LinkedBlockingQueue<E> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1998262332006293935L;

		private DpsfThreadPoolExecutor pool;

		public TestLinkedBlockingQueue(int capacity) {
			super(capacity);
		}

		public E poll(long timeout, TimeUnit unit) throws InterruptedException {
			int wtc = this.pool.waitingThreadCount.incrementAndGet();
			try {
				E res = null;
				if (System.currentTimeMillis() - this.pool.lastCreateThreadTime < this.pool.intervalTime
						|| wtc < size() + 5) {
					res = super.poll(timeout, unit);
				} 
				if(res == null){
					this.pool.destoryThreadCount.incrementAndGet();
				}
				return res;
			} finally {
				this.pool.waitingThreadCount.decrementAndGet();
			}
		}

		public boolean offer(E e) {
			boolean res = false;
			if (this.pool.waitingThreadCount.get() > size() - 5) {
				res = super.offer(e);
			}
			if (!res) {
				this.pool.createThreadCount.incrementAndGet();
				this.pool.lastCreateThreadTime = System.currentTimeMillis();
			}

			return res;
		}

		public void setPool(DpsfThreadPoolExecutor pool) {
			this.pool = pool;
		}

	}
}
