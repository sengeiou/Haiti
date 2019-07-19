package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ECGBillingMonthlyExcelTask;

public class ECGMonthlyReportJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ECGMonthlyReportJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("@@@@@@ ECGMonthlyReportJob Start @@@@@@");
		ECGBillingMonthlyExcelTask excelTask = DataUtil.getBean(ECGBillingMonthlyExcelTask.class);
		excelTask.execute(context);
		log.debug("@@@@@@ ECGMonthlyReportJob End @@@@@@");
	}
	
}
