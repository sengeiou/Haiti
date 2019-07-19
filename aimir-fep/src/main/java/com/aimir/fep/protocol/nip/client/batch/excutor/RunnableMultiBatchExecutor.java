/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.excutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;

/**
 * Executor의 인스턴스를 1개씩만 띄워야 해서 RunnableSingleBatchExecutor와
 * RunnableMultiBatchExecutor 두개를 생성함. 두 Executor의 차이점은 CORE_POOL_SIZE가 1로 고정이냐
 * 고정이 아니냐 차이밖에 없음. 나머지 코드는 모두 동일. 추후 좋은 방법이 있으면 개선할것.
 * 
 * @author simhanger
 *
 */
@Deprecated
public class RunnableMultiBatchExecutor {
	private static Logger logger = LoggerFactory.getLogger(RunnableMultiBatchExecutor.class);

	private static RunnableMultiBatchExecutor rsBatchExecutor = new RunnableMultiBatchExecutor();
	private static ThreadPoolExecutor executor;
	private static String executorName;

	// Default : Single, 10개부터는 db connection 에러남.
	private static int CORE_POOL_SIZE = Integer.parseInt(FMPProperty.getProperty("batch.executor.thread.poolSize", "1"));
	private static int TERMINATOR_THREAD_SIZE = 1;
	private static final int MAXIMUM_POOL_SIZE = 10000; // 의미없음
	private static long KEEP_ALIVE_TIME = 1;
	private static TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

	private static BatchRejectedExecutionHandler rejectedExecutionHandler;
	private static List<IBatchRunnable> dupList;

	/** Cannot instantiate. */
	private RunnableMultiBatchExecutor() {

	}

	private static Date startDate = new Date();
	private static long startTime = startDate.getTime();

	/*
	 * 1. RunnableMultiBatchExcutor init.
	 */
	public static synchronized RunnableMultiBatchExecutor getInstance(String requestor) {
		logger.debug("RunnableMultiBatchExecutor [JobName={}] getInstance...", requestor);

		/*
		 * ThreadPoolExecutor create.
		 */
		if (executor == null) {
			executorName = "RMBExecutor-" + DateTimeUtil.getCurrentDateTimeByFormat(null);

			logger.info("########### Create RunnableMultiBatchExecutor[Name = {}] : Thread Count = {}, Keep Alive Time = {}/{}  - {} ###############", executorName, CORE_POOL_SIZE + TERMINATOR_THREAD_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT.name(), CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss"));
			rejectedExecutionHandler = new BatchRejectedExecutionHandler();
			executor = new ThreadPoolExecutor(CORE_POOL_SIZE + TERMINATOR_THREAD_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>(), rejectedExecutionHandler);

			if (dupList == null) {
				dupList = new ArrayList<>();
			} else {
				dupList.clear();
				logger.debug("Duplicated Job List initialized...");
			}

			/** Executor Terminator execute. */
			executor.execute(new ExecutorTerminatorForMulti(executor));
		}

		return rsBatchExecutor;
	}

	/*
	 * 2. ThreadPoolExecutor init & execute
	 */
	public synchronized boolean execute(String title, List<IBatchRunnable> targetList) {
		logger.debug("RMBExecutor execute. title={}, targetList={}", title, targetList.size());
		boolean result = false;
		List<IBatchRunnable> executeList = new ArrayList<>();

		if (targetList == null || targetList.size() <= 0) {
			logger.warn("Have no taget list. please check target list.");
		} else {
			try {
				/*
				 *  Duplicated job delete.
				 */
				for (IBatchRunnable rb : targetList) {
					if (executor.getQueue().contains(rb)) {
						dupList.add(rb);
						logger.warn("Duplicated target ==> [{}]", rb.getName());
					} else {
						executeList.add(rb);
					}
				}

				int count = 0;
				logger.info("------ Excuted Target List. Total jobSize = {} -----", executeList.size());
				for (IBatchRunnable job : executeList) {
					logger.info("{}. ==> [Add] RunnableJob=[{}]", ++count, job.getName());
				}
				logger.info("------------------------------------------------");

				// Duplicated list logging
				count = 0;
				for (IBatchRunnable job : dupList) {
					logger.debug("{}. ==> [Duplicated] Deleted JobName=[{}]", ++count, job.getName());
				}

				/*
				 *  Excute Job.
				 */
				logger.debug("Remaining Capacity of Queue = {}, Target list size = {}", executor.getQueue().remainingCapacity(), executeList.size());
				count = 0;
				for (IBatchRunnable job : executeList) {
					if (executor.getQueue().remainingCapacity() <= 0) {
						logger.warn("{}. ==> [Skip] Queue capacity is full. JobName=[{}]", ++count, job.getName());
					} else {
						logger.debug("{}. ==> [execute] JobName=[{}]", ++count, job.getName());
						executor.execute(job);
					}
				}

				/*
				 * Logging
				 */
				logger.info(" ");

				if (0 < rejectedExecutionHandler.getSize()) {
					count = 0;
					logger.warn("=========== REJECTED EXECUTION LIST. Total job = {} ==========", rejectedExecutionHandler.getSize());
					for (String failJobName : rejectedExecutionHandler.getList()) {
						logger.warn("{}. {}", ++count, failJobName);
					}
					logger.warn("=======================================");

				}
			} catch (Exception e) {
				logger.error("Job execute fail - " + e.getMessage(), e);
				return false;
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info("FINISHED - Elapse Time : {}s", DateTimeUtil.getElapseTimeToString(endTime - startTime));

		logger.info("########### END RunnableMultiBatchExecutor. Job Size Total/Excuted = {}/{}, Duplicated Job size = {}, Reject Job size = {}  ############", targetList.size(), executeList.size(), dupList.size(), rejectedExecutionHandler.getSize());

		result = true;
		return result;

	}

	/**
	 * Executor Shutdown.
	 */
	void executorShutDown() {
		try {
			logger.debug("[RunnableMultiBatchExecutor shutown start. ExecutorName={}]", executorName);
			executor.shutdown();

			if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}

			logger.debug("Is Executor ShutDonw? = {}", executor.isShutdown());
		} catch (Exception e) {
			logger.error("Exception-", e);
		} finally {
			executor = null;

			logger.debug("Is Executor null? = {}", executor == null ? true : false);
			logger.debug("[RunnableMultiBatchExecutor shutown Finished. ExecutorName={}]", executorName);
		}
	}

	public Object getName() {
		return executorName;
	}
}

/**
 * ExecutorTerminatorForMulti Terminator
 * 
 * @author simhanger
 *
 */
class ExecutorTerminatorForMulti implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(ExecutorTerminatorForMulti.class);
	private final String name = "EXECUTOR_TERMINATOR_FOR_MULTI";
	private ThreadPoolExecutor executor;
	private boolean finishFlag = true;

	public ExecutorTerminatorForMulti(ThreadPoolExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void run() {
		while (finishFlag) {
			try {
				Thread.sleep(1000);

				long totalTaskCount = executor.getTaskCount() - 1; // 1 is ExecutorTerminator Task.
				long completedTaskCount = executor.getCompletedTaskCount();

				//logger.debug("################ Task Monitoring : Executor PoolSize = {} ################", executor.getPoolSize() - 1); // 1 is ExecutorTerminator Task.
				//logger.debug("Total Task Count = {}, Active Task Count = {}, Completed Task Count = {}, Queue size = {} ", totalTaskCount, executor.getActiveCount() - 1, completedTaskCount, executor.getQueue().size());

				if (totalTaskCount <= completedTaskCount) {
					finishFlag = false;
					logger.debug("## All Task completed. Total={}, Active={}, Completed={}, Queue={} ##", totalTaskCount, executor.getActiveCount() - 1, completedTaskCount, executor.getQueue().size());
				}

				//logger.debug("#################################################");
			} catch (InterruptedException e) {
				logger.error("ExecutorTerminatorForMulti execute error - " + e.getMessage(), e);
			}
		}

		RunnableMultiBatchExecutor.getInstance(name).executorShutDown();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info(title + "," + status.name() + "," + desc);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = ((IBatchRunnable) obj).getName().equals(getName());
		logger.debug("[Equals Check] ThisObj=[{}], ParamObj=[{}], is equals?=[{}]", getName(), ((IBatchRunnable) obj).getName(), result);

		return result;
	}
}
