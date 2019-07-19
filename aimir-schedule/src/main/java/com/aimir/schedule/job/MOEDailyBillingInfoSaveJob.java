package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.MOEBlockDailyEMBillingInfoSaveV2Task;

public class MOEDailyBillingInfoSaveJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(MOEDailyBillingInfoSaveJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.debug("@@@@@@ MOEBlockDailyEMBillingInfoSaveV2Task Start @@@@@@");

		//EMDailyBillingSave
		MOEBlockDailyEMBillingInfoSaveV2Task emTask = DataUtil.getBean(MOEBlockDailyEMBillingInfoSaveV2Task.class);
		emTask.executeTask(context);

		//GMDailyBillingSave
		//DailyGMBillingInfoSaveTask gmTask = DataUtil.getBean(DailyGMBillingInfoSaveTask.class);
		//gmTask.execute();

		//WMDailyBillingSave
		//DailyWMBillingInfoSaveTask wmTask = DataUtil.getBean(DailyWMBillingInfoSaveTask.class);
		//wmTask.execute();

	}

}
