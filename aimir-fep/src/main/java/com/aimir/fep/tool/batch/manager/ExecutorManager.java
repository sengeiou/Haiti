/**
 * 
 */
package com.aimir.fep.tool.batch.manager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.fep.tool.notiplug.NotiGeneratorForSingleObserver;
import com.aimir.fep.tool.notiplug.NotiObserver;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.DateTimeUtil;

/**
 * 
 * @file : ExecutorManager.java
 * @author : simhanger
 * @date : 2018. 8. 20.
 * @desc :
 */
public class ExecutorManager implements NotiObserver {
	private static Logger logger = LoggerFactory.getLogger(ExecutorManager.class);

	private static ExecutorManager rsBatchExecutor = new ExecutorManager();
	private static ThreadPoolExecutor managerExecutor;
	private static String managerName;

	// Default : 5.   10개부터는 db connection 에러남.
	private static int CORE_POOL_SIZE = Integer.parseInt(FMPProperty.getProperty("batch.executor.thread.poolSize", "5"));
	private static int TERMINATOR_THREAD_SIZE = 1;
	private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE; // 의미없음
	private static long KEEP_ALIVE_TIME = 1; // 이것도 의미 없는것 같음.
	private static TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS; // 이것도 의미 없는것 같음.

	private static BatchRejectedExecutionHandler rejectedExecutionHandler;
	private static Map<String, IBatchSingleExecutor> executorMap;

	/** Cannot instantiate. */
	private ExecutorManager() {

	}

	private static long startTime;

	/*
	 * 1. RunnableMultiBatchExcutor init.
	 */
	public static synchronized ExecutorManager getInstance(String name) {
		/*
		 * ThreadPoolExecutor create.
		 */
		if (managerExecutor == null) {
			startTime = System.currentTimeMillis();

			// SingleExecutor map.
			executorMap = new ConcurrentHashMap<String, IBatchSingleExecutor>();

			managerName = name + "_" + DateTimeUtil.getCurrentDateTimeByFormat(null);

			logger.info("###### Create ExecutorManager[ExecutorManagerName = {}] : Work Thread Count = {}, Keep Alive Time = {}/{} ########", managerName, CORE_POOL_SIZE + TERMINATOR_THREAD_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT.name());
			rejectedExecutionHandler = new BatchRejectedExecutionHandler();
			managerExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE + TERMINATOR_THREAD_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>(), rejectedExecutionHandler);

			/** Executor Terminator execute. */
			managerExecutor.execute(new ExecutorManagerTerminator(managerName, managerExecutor));
		}

		logger.info("[ExecutorManagerName = {}] ExecutorManager getInstance...", managerName);

		return rsBatchExecutor;
	}

	/*
	 * 2. ThreadPoolExecutor init & execute
	 */
	public boolean execute(List<IBatchJob> targetList) {
		logger.info("[ExecutorManagerName = {}] ExecutorManager execute. EM_QueueSize = {}, targetList={}", managerName, managerExecutor.getQueue().size(), targetList.size());
		boolean result = false;

		if (targetList == null || targetList.size() <= 0) {
			logger.warn("[ExecutorManagerName = {}] Have no taget list. please check target list.", managerName);
		} else {
			try {
				/*
				 * 각 SingleExecutor를 뒤져서 add 하거나 create 함.
				 */
				int count = 0;

				for (IBatchJob job : targetList) {
					
					IBatchSingleExecutor sExecutor = null;
					if(executorMap != null && job.getExecutorName() != null && !"".equals(job.getExecutorName())) {
						sExecutor = executorMap.get(job.getExecutorName());
					}

					if (sExecutor != null) { // 이미있는경우 
						logger.info("{}. [ExecutorManagerName = {}] [ADD   ] ==> [ExecutorName = {}] JobName=[{}]", ++count, managerName, job.getExecutorName(), job.getName());
						sExecutor.execute(job);
					} else { // 신규
						logger.info("{}. [ExecutorManagerName = {}] [CREATE] ==> [ExecutorName = {}] JobName=[{}]", ++count, managerName, job.getExecutorName(), job.getName());
						sExecutor = new SingleExecutor(job.getExecutorName());
						sExecutor.addJob(job);
						((NotiGeneratorForSingleObserver) sExecutor).addObserver(this);

						if(executorMap != null && sExecutor.getExecutorName() != null && !"".equals(sExecutor.getExecutorName())) {
							executorMap.putIfAbsent(sExecutor.getExecutorName(), sExecutor);
						}

						managerExecutor.execute(sExecutor); // managerExecutor 등록
						Thread.sleep(500);
					}
					logger.info("{}. [ExecutorManagerName = {}] [INFO  ] ExecutorManager ManagerMapInfo=[Size={}, List = {}] QueueInfo=[Size = {}, ActiveCount={}, TaskCount={}, CompletedTaskCount={}]"
							, count, managerName, executorMap.size(), executorMap.keySet().toString(), managerExecutor.getQueue().size(), (managerExecutor.getActiveCount()-1), (managerExecutor.getTaskCount()-1), managerExecutor.getCompletedTaskCount());
				}

				/*
				 * Logging
				 */
				logger.info(" ");

				if (0 < rejectedExecutionHandler.getSize()) {
					count = 0;
					logger.warn("=========== [ExecutorManagerName = {}] REJECTED EXECUTION LIST. Total job = {} ==========", managerName, rejectedExecutionHandler.getSize());
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

		logger.info("### [ExecutorManager Finished.][ExecutorManagerName = {}] Elapse Time : {}", managerName, DateTimeUtil.getElapseTimeToString(endTime - startTime));
		logger.info("### [ExecutorManager Info.    ][ExecutorManagerName = {}] Job Size Total = {}, Reject Job size = {} ###", managerName, targetList.size(), rejectedExecutionHandler.getSize());

		result = true;
		return result;
	}

	/**
	 * Executor Shutdown.
	 */
	void executorShutDown() {
		logger.debug("[ExecutorManagerName = {}] [INFO  ] Executor Map Info. TotalSize={}, List = {}", managerName, executorMap.size(), executorMap.keySet().toString());

		try {
			logger.debug("### [ExecutorManager shutown start][ExecutorManagerName = {}] ###", managerName);
			managerExecutor.shutdown();

			if (!managerExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
				managerExecutor.shutdownNow();
			}
			logger.debug("### [ExecutorManager shutown ......   ][ExecutorManagerName = {}] Is ShutDown ? = {} ###", managerName, managerExecutor.isShutdown());
		} catch (Exception e) {
			logger.error("### [ExecutorManager shutown Error.   ][ExecutorManagerName = " + managerName + "] - " + e.getMessage(), e);
		} finally {
			managerExecutor = null;
			logger.info("### [ExecutorManager shutown Finished.][ExecutorManagerName = {}] ###", managerName);
		}
	}

	public Object getName() {
		return managerName;
	}

	@Override
	public synchronized void observerNotify(String notiGeneratorName, Map<?, ?> params) {
		logger.debug("### [NOTI  ][ExecutorManagerName = {}] ### SimgleExecutor Finished. Received Noti from Generator=[{}], params= {} #####", managerName, notiGeneratorName, params.toString());
		executorMap.remove(notiGeneratorName);
		logger.debug("### [REMOVE][ExecutorManagerName = {}] ### Executor Map Info. TotalSize={}, List = {} ##", managerName, executorMap.size(), executorMap.keySet().toString());
	}

	@Override
	public String getNotiObserverName() {
		return managerName;
	}

	@Override
	public Map<?, ?> getNotiParams() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toStringExecutorManagerInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("ExecutorManager Name = " + managerName).append("\n");
		sb.append("ExecutorManager Map Info = [Size=" + executorMap.size() + ", List=" + executorMap.keySet().toString() + "]").append("\n");
		sb.append("ExecutorManager Queue Info = [Size=" + managerExecutor.getQueue().size() + ", ActiveCount="+(managerExecutor.getActiveCount()-1)+", TaskCount="+(managerExecutor.getTaskCount()-1)+", CompletedTaskCount=" + managerExecutor.getCompletedTaskCount() + "]").append("\n");
		
		int count = 0;
		Iterator<String> it = executorMap.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			IBatchSingleExecutor executor = executorMap.get(key);
			sb.append(++count + ". Executor Info ===>").append("\n");
			sb.append("  [").append("\n");;
			sb.append(executor.toStringExecutorInfo());
			sb.append("  ]").append("\n");;
		}
		logger.info("### ExecutorManager Info ###\n"
				+ "### ======================== ###\n"
				+ "{}"
				+ "### ======================== ###", sb.toString());
		return sb.toString();
	}
}

/**
 * ExecutorTerminatorForMulti Terminator
 * 
 * @author simhanger
 *
 */
class ExecutorManagerTerminator implements IBatchJob {
	private static Logger logger = LoggerFactory.getLogger(ExecutorManagerTerminator.class);
	private final String name = "EM_TERMINATOR";
	private String managerName;
	private ThreadPoolExecutor executor;
	private boolean isJobRunning = true;

	public ExecutorManagerTerminator(String managerName, ThreadPoolExecutor executor) {
		this.managerName = managerName;
		this.executor = executor;
	}

	@Override
	public void run() {
		while (isJobRunning) {
			try {
				Thread.sleep(1000);

				long totalTaskCount = executor.getTaskCount() - 1; // 1 is ExecutorTerminator Task.
				long completedTaskCount = executor.getCompletedTaskCount();
				int executorPoolSize = executor.getPoolSize() - 1; // 1 is ExecutorTerminator Task.
				int executorActiveCount = executor.getActiveCount() - 1; // 1 is ExecutorTerminator Task.

				if (totalTaskCount <= completedTaskCount) {
					isJobRunning = false;
//					logger.debug("###### [ExecutorManager Task Monitoring][ExecutorManagerName={}] All Task completed. TotalTask/PoolSize={}/{}, Active={}, Completed={}, Queue={} #######", managerName, totalTaskCount, executorPoolSize, executorActiveCount, completedTaskCount, executor.getQueue().size());
				} else {
//					logger.debug("###### [ExecutorManager Task Monitoring][ExecutorManagerName={}] Task Running...     TotalTask/PoolSize={}/{}, Active={}, Completed={}, Queue={} #######", managerName, totalTaskCount, executorPoolSize, executorActiveCount, completedTaskCount, executor.getQueue().size());
				}
			} catch (InterruptedException e) {
//				logger.error("[ExecutorManager Task Monitoring][ExecutorManagerName=" + managerName + "] running error - " + e.getMessage(), e);
			}
		}

		ExecutorManager.getInstance(name).executorShutDown();
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
		boolean result = ((IBatchJob) obj).getName().equals(getName());
		logger.debug("[Equals Check] ThisObj=[{}], ParamObj=[{}], is equals?=[{}]", getName(), ((IBatchJob) obj).getName(), result);

		return result;
	}

	@Override
	public String getExecutorName() {
		return null;
	}
}
