package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.SPASABlockDailyEMBillingInfoSaveV2Task;

public class SPASADailyBillingInfoSaveJob extends QuartzJobBean {
	

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		//EMDailyBillingSave
		SPASABlockDailyEMBillingInfoSaveV2Task emTask = DataUtil.getBean(SPASABlockDailyEMBillingInfoSaveV2Task.class);
//		SPASATariffDailyEMBillingInfoSaveTask emTask = DataUtil.getBean(SPASATariffDailyEMBillingInfoSaveTask.class);
		emTask.executeTask(context);

		//GMDailyBillingSave
		//DailyGMBillingInfoSaveTask gmTask = DataUtil.getBean(DailyGMBillingInfoSaveTask.class);
		//gmTask.execute();
		
		//WMDailyBillingSave
		//DailyWMBillingInfoSaveTask wmTask = DataUtil.getBean(DailyWMBillingInfoSaveTask.class);
		//wmTask.execute();

	}
	
}
