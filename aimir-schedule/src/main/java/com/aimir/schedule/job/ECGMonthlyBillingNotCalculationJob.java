package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ECGBillingMonthlyNotCalculationTask;

public class ECGMonthlyBillingNotCalculationJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ECGMonthlyBillingNotCalculationJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("@@@@@@ ECGMonthlyBillingNotCalculationJob Start @@@@@@");
		
		ECGBillingMonthlyNotCalculationTask task = DataUtil.getBean(ECGBillingMonthlyNotCalculationTask.class);
		task.execute(context);
		
		log.debug("@@@@@@ ECGMonthlyBillingNotCalculationJob End @@@@@@");
	}
}
