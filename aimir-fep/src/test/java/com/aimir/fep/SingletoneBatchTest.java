package com.aimir.fep;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchRunnable;
import com.aimir.fep.protocol.nip.client.batch.excutor.RunnableSingleBatchExecutor;
import com.aimir.fep.protocol.nip.client.batch.job.SORIADcuOTARunnable;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath:spring.xml" })
public class SingletoneBatchTest {
	private static Logger logger = LoggerFactory.getLogger(SingletoneBatchTest.class);

	@Test
	public void singleToneTest() {
		logger.debug("########## Singletone Test start. #######");

		try {
			for (int i = 0; i < 2; i++) {
				Thread t = new Thread(new JobJob("AA" + i));
				t.start();
			}

			Thread.sleep(5000);
			System.out.println("===============================");

			for (int j = 0; j < 2; j++) {
				Thread t = new Thread(new JobJob("BB" + j));
				t.start();
			}
			
			Thread.sleep(5000);
			System.out.println("===============================");
		} catch (Exception e) {
			logger.error("Test Error - " + e.getMessage(), e);
		}

		logger.debug("########## Singletone Test stop. #######");
	}
}

class JobJob implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(JobJob.class);

	private String name;

	public JobJob(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		try {
			List<IBatchRunnable> runnableList = new LinkedList<>();
			for (int i = 0; i < 3; i++) {
				TestRunnable tr = new TestRunnable(name +"-" + i);
				runnableList.add(tr);
			}

			RunnableSingleBatchExecutor rsbe = RunnableSingleBatchExecutor.getInstance(name);
			logger.debug("RunnableSingleBatchExcutor name = {}", rsbe.getName());
			
			boolean executeResult = rsbe.execute("[REQ. from " + name + " Job]", runnableList);
			logger.debug("JobName=[{}], ExecutorName=[{}], execute result=[{}]",name, rsbe.getName(), executeResult);
		} catch (Exception e) {
			logger.error("Job running error - " + e.getMessage(), e);
		}
	}
}

class TestRunnable implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(SORIADcuOTARunnable.class);
	private String name;

	public TestRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < 5; i++) {
				logger.debug("RunnableName=[{}], count=[{}]", getName(), i);
				Thread.sleep(50);
			}
			logger.debug("");
		} catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
			e.printStackTrace();
		}
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
		//return super.equals(obj);
	}

}