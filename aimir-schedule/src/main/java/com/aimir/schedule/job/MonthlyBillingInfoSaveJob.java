package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.MonthlyBillingInfoSaveTask;

public class MonthlyBillingInfoSaveJob extends QuartzJobBean {
	private MonthlyBillingInfoSaveTask monthlyBillingInfoSaveTask;

	public void setBillingInfoSaveTask(MonthlyBillingInfoSaveTask monthlyBillingInfoSaveTask) {

		this.monthlyBillingInfoSaveTask = monthlyBillingInfoSaveTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {

		monthlyBillingInfoSaveTask.executeTask(context);

	}
}

