package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.BalanceMonitorTask;
import com.aimir.schedule.task.DailyBillingInfoSaveTask;

@Transactional
public class DailyBillingInfoSaveJob extends QuartzJobBean {
	private DailyBillingInfoSaveTask dailyBillingInfoSaveTask;
	private BalanceMonitorTask balanceMonitorTask;

	public void setDailyBillingInfoSaveTask(DailyBillingInfoSaveTask dailyBillingInfoSaveTask) {

		this.dailyBillingInfoSaveTask = dailyBillingInfoSaveTask;
	}

	public void setBalanceMonitorTask(BalanceMonitorTask balanceMonitorTask) {

		this.balanceMonitorTask = balanceMonitorTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {

		dailyBillingInfoSaveTask.excute();
		balanceMonitorTask.executeTask(context);
	}
}

