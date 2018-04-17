package com.dang.practice.apitest.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
	public static void main(String[] args) {
		Runnable[] taskList = new Runnable[20];
		for (int i = 0; i < 20; i++) {
			taskList[i] = new ThreadPool().new TestTask("" + i);
		}
		// taskList[0].run();
		long start = System.currentTimeMillis();
		ThreadPool.submitWork(taskList);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		;
	}

	private static final int DefaultPoolQueueCount = Runtime.getRuntime().availableProcessors();
	private static final long DefaultPoolKeepAliveTime = 60L;
	private ExecutorService defaultExecutor = new ThreadPoolExecutor(DefaultPoolQueueCount, DefaultPoolQueueCount * 2, DefaultPoolKeepAliveTime,
			TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
	private ConcurrentHashMap<String, ExecutorService> poolList = new ConcurrentHashMap<String, ExecutorService>();

	private void submit(Runnable task) {
		while (true) {
			try {
				defaultExecutor.submit(task);
				break;
			} catch (RejectedExecutionException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private void submit(Runnable... taskList) {
		if (taskList == null)
			return;
		for (Runnable task : taskList) {
			submit(task);
		}
	}

	private void submit(String poolName, int parallelCount, Runnable task) {
		while (true) {
			try {
				getPool(poolName, parallelCount).submit(task);
				break;
			} catch (RejectedExecutionException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private void submit(String poolName, int parallelCount, Runnable... taskList) {
		if (taskList == null)
			return;
		for (Runnable task : taskList) {
			submit(poolName, parallelCount, task);
		}
	}

	private ExecutorService getPool(String poolName, int parallelCount) {
		if (poolName == null || poolName.length() == 0) {
			return defaultExecutor;
		}
		int newParallelCount = parallelCount > DefaultPoolQueueCount ? DefaultPoolQueueCount : parallelCount;
		ExecutorService pool = null;
		if (poolList.containsKey(poolName)) {
			pool = poolList.get(poolName);
		} else {
			pool = new ThreadPoolExecutor(DefaultPoolQueueCount, newParallelCount, DefaultPoolKeepAliveTime, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
			this.poolList.put(poolName, pool);
		}
		return pool;
	}

	public static void submitWork(Runnable task) {
		Holder.instance.submit(task);
	}

	public static void submitWork(Runnable... taskList) {
		Holder.instance.submit(taskList);
	}

	public static void submitWork(String poolName, int parallelCount, Runnable task) {
		if (parallelCount <= 0) {
			throw new IllegalArgumentException("parallelCount");
		}
		Holder.instance.submit(poolName, parallelCount, task);
	}

	public static void submitWork(String poolName, int parallelCount, Runnable... taskList) {
		if (parallelCount <= 0) {
			throw new IllegalArgumentException("parallelCount");
		}
		Holder.instance.submit(poolName, parallelCount, taskList);
	}

	public static void submitWork(String poolName, Runnable task) {
		Holder.instance.submit(poolName, DefaultPoolQueueCount, task);
	}

	public static void submitWork(String poolName, Runnable... taskList) {
		Holder.instance.submit(poolName, DefaultPoolQueueCount, taskList);
	}

	public static boolean awaitCountDownLatch(CountDownLatch downLatch, long timeout, TimeUnit unit) {
		try {
			return downLatch.await(timeout, unit);
		} catch (InterruptedException e) {
		}
		return false;
	}

	public static boolean awaitCountDownLatch(CountDownLatch downLatch, long timeout) {
		try {
			return downLatch.await(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		return false;
	}

	private static class Holder {
		public static ThreadPool instance = new ThreadPool();
	}

	public static AtomicInteger counter = new AtomicInteger(0);

	class TestTask implements Runnable {
		private String taskName;
		private int index;

		public TestTask(String taskName) {
			this.taskName = taskName;
			this.index = counter.addAndGet(1);
		}

		public void run() {
			System.out.println(String.format("%s : %s start ! ", this.index, this.taskName));
			long i = 0;
			long maxValue = Integer.MAX_VALUE;
			while (true) {
				i++;
				if (i == maxValue) {
					break;
				}
				if (i % 50000000 == 0) {
					System.out.println(String.format("[%s : %s]  i=%s ! ", this.index, this.taskName, i));
				}
			}
			System.out.println(String.format("%s : %s  stop ! ", this.index, this.taskName));
		}

	}
}
