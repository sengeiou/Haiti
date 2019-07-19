package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.aimir.schedule.task.BillingInfoCreateTask;

public class BillingInfoCreateJob extends QuartzJobBean {
	private BillingInfoCreateTask billingInfoCreateTask;

	public void setbillingInfoCreateTask(BillingInfoCreateTask billingInfoCreateTask) {

		this.billingInfoCreateTask = billingInfoCreateTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		billingInfoCreateTask.executeTask(context);
	}
}
