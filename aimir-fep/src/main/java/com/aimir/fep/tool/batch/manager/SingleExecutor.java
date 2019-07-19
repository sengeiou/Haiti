/**
 * 
 */
package com.aimir.fep.tool.batch.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.tool.notiplug.NotiGeneratorForSingleObserver;
import com.aimir.util.DateTimeUtil;

/**
 * @file : SingleExecutor.java
 * @author : simhanger
 * @date : 2018. 8. 22.
 * @desc :
 */
public class SingleExecutor extends NotiGeneratorForSingleObserver implements IBatchSingleExecutor {
	private static Logger logger = LoggerFactory.getLogger(SingleExecutor.class);

	private ThreadPoolExecutor executor;
	private String executorName;

	private BatchRejectedExecutionHandler rejectedExecutionHandler;
	private List<IBatchJob> dupList;

	private long startTime;

	List<IBatchJob> targetList = null;
	private boolean isJobRunning = true;
	private Map<String, Object> notifyParams;

	/*
	 * 1. SingleExecutor init.
	 */
	public SingleExecutor(String executorName) {
		logger.info("### [SingleExecutor Create][ExecutorName = {}] ###", executorName);

		startTime = System.currentTimeMillis();
		this.executorName = executorName;

		rejectedExecutionHandler = new BatchRejectedExecutionHandler();
		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), rejectedExecutionHandler);

		targetList = new LinkedList<IBatchJob>();
		dupList = new ArrayList<>();
	}

	@Override
	public void addJob(IBatchJob job) {
		targetList.add(job);
		logger.info("[ADD Job] [ExecutorName = {}] [JobName = {}] Queue Info =[Size = {}, ActiveCount={}, TaskCount={}, CompletedTaskCount={}", executorName, job.getName(), executor.getQueue().size(), executor.getActiveCount(), executor.getTaskCount(), executor.getCompletedTaskCount());
	}

	@Override
	public void execute(IBatchJob job) {
		executor.execute(job);
		logger.info("[ADD Job to Running Queue] [ExecutorName = {}] [JobName = {}] Queue Info =[Size = {}, ActiveCount={}, TaskCount={}, CompletedTaskCount={}", executorName, job.getName(), executor.getQueue().size(), executor.getActiveCount(), executor.getTaskCount(), executor.getCompletedTaskCount());
	}

	@Override
	public String getExecutorName() {
		return executorName;
	}

	@Override
	public String toStringExecutorInfo() {
		StringBuilder sb = new StringBuilder();
		int qSize = executor.getQueue().size();
		
		sb.append("    Executor Name = " + executorName).append("\n");
		sb.append("    Executor Task Info = [Total TaskCount=" + executor.getTaskCount() + ", ActiveCount=" + executor.getActiveCount() + ", CompletedTaskCount=" + executor.getCompletedTaskCount() + "]").append("\n");
		sb.append("    Ececutor Remain Queue size = " + qSize + ", Remain Task list = [").append("");
		Iterator<Runnable> it = executor.getQueue().iterator();
		int count = 0;
		while(it.hasNext()) {
			IBatchJob job = (IBatchJob) it.next();
			sb.append(job.getName());
			count++;
			
			if(count < qSize ) {
				sb.append(", ");
			}
		}
		sb.append("]\n").append("");
		
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof SingleExecutor) {
			if (((SingleExecutor) obj).getExecutorName().equals(this.executorName)) {
				result = true;
			}
		}

		return result;
	}

	@Override
	public String getNotiGeneratorName() {
		return this.executorName;
	}

	/**
	 * Notification to Observers
	 * 
	 * @param notifyParams
	 */
	private void notiFire(Map<String, Object> notifyParams) {
		logger.debug("[ExecutorName = {}] Noti Fire~~!! ==> {}", executorName, notifyParams.toString());
		super.notifyToObserver(notifyParams);
	}

	@Override
	public void run() {
		logger.info("### [SingleExecutor Start][ExecutorName = {}] ###", executorName);

		/** Notify to Observers */
		notifyParams = new HashMap<String, Object>();

		try {
			runExecute();

			while (isJobRunning) {
				Thread.sleep(1000);

				long totalTaskCount = executor.getTaskCount();
				long completedTaskCount = executor.getCompletedTaskCount();

				if (totalTaskCount <= completedTaskCount) {
					isJobRunning = false;
					//					logger.debug("# [SingleExecutor Task Monitoring][ExecutorName={}] All Task completed. Total={}, Active={}, Completed={}, Queue={} ##", executorName, totalTaskCount, executor.getActiveCount(), completedTaskCount, executor.getQueue().size());
				} else {
					//					logger.debug("# [SingleExecutor Task Monitoring][ExecutorName={}] Task Running...     Total={}, Active={}, Completed={}, Queue={} ##", executorName, totalTaskCount, executor.getActiveCount(), completedTaskCount, executor.getQueue().size());
				}
			}

			notifyParams.put("result", true);
		} catch (Exception e) {
			notifyParams.put("result", false);
			logger.error("[ExecutorName = " + executorName + "] SingleExecutor running error - " + e.getMessage(), e);
		} finally {
			// Executor 종료
			executorShutDown();

			long endTime = System.currentTimeMillis();
			String elapseTime = DateTimeUtil.getElapseTimeToString(endTime - startTime);

			notifyParams.put("elapseTime", elapseTime);
			notiFire(notifyParams);

			logger.info("### [SingleExecutor Finished][ExecutorName = {}] Elapse Time : {} ###", executorName, elapseTime);
		}
	}

	/*
	 * 2. execute
	 */
	public void runExecute() {
		logger.debug("ExecutorName={}. Target List Size = {}", executorName, targetList.size());
		List<IBatchJob> executeList = new ArrayList<>();

		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Have no taget list. please check target list.");
		} else {
			try {
				/*
				 *  Duplicated job delete.
				 */
				for (IBatchJob rb : targetList) {
					if (executor.getQueue().contains(rb)) {
						dupList.add(rb);
					} else {
						executeList.add(rb);
					}
				}

				int count = 0;
				logger.info("------ [ExecutorName = {}] Excuted Target List. Total jobSize = {} -----", executorName, executeList.size());
				for (IBatchJob job : executeList) {
					logger.info("{}. ==> [Add] RunnableJob=[{}]", ++count, job.getName());
				}

				// Duplicated list logging
				count = 0;
				for (IBatchJob job : dupList) {
					logger.info("{}. ==> [Duplicated] Deleted JobName=[{}]", ++count, job.getName());
				}

				/*
				 *  Excute Job.
				 */
				logger.info("Remaining Capacity of Queue = {}, Target list size = {}", executor.getQueue().remainingCapacity(), executeList.size());
				count = 0;
				for (IBatchJob job : executeList) {
					if (executor.getQueue().remainingCapacity() <= 0) {
						logger.warn("{}. ==> [Skip] Queue capacity is full. JobName=[{}]", ++count, job.getName());
					} else {
						logger.info("{}. ==> [execute] JobName=[{}]", ++count, job.getName());
						executor.execute(job);
					}
				}
				logger.info("----------------------------------------------------");

				/*
				 * Logging
				 */
				logger.info(" ");

				if (0 < rejectedExecutionHandler.getSize()) {
					count = 0;
					logger.warn("=========== [ExecutorName = {}] REJECTED EXECUTION LIST. Total job = {} ==========", executorName, rejectedExecutionHandler.getSize());
					for (String failJobName : rejectedExecutionHandler.getList()) {
						logger.warn("{}. {}", ++count, failJobName);
					}
					logger.warn("=======================================");

				}
			} catch (Exception e) {
				logger.error("Job execute fail - " + e.getMessage(), e);
			}
		}

		logger.info("Execute Info. ExecutorName={}, Job Size Total/Excuted = {}/{}, Duplicated Job size = {}, Reject Job size = {}", executorName, targetList.size(), executeList.size(), dupList.size(), rejectedExecutionHandler.getSize());
	}

	/**
	 * Executor Shutdown.
	 */
	void executorShutDown() {
		try {
			logger.debug("### [SingleExecutor shutdown start][ExecutorName = {}] ###", executorName);
			executor.shutdown();

			if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
			logger.debug("### [SingleExecutor shutdown ......   ][ExecutorName = {}] Is ShutDown ? = {} ###", executorName, executor.isShutdown());
		} catch (Exception e) {
			logger.error("### [SingleExecutor shutdown Error.   ][ExecutorName = " + executorName + "] - " + e.getMessage(), e);
		} finally {
			executor = null;
			logger.info("### [SingleExecutor shutdown Finished.][ExecutorName = {}] ###", executorName);
		}
	}

}
