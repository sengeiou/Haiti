package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ECGBillingMonthlyTask;

public class ECGMonthlyBillingJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ECGMonthlyBillingJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("@@@@@@ ECGMonthlyEMBillingJob Start @@@@@@");
		
		ECGBillingMonthlyTask task = DataUtil.getBean(ECGBillingMonthlyTask.class);
		task.execute(context);
		
		log.debug("@@@@@@ ECGMonthlyEMBillingJob End @@@@@@");
	}
}
