package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.BalanceMonitorV2Task;

public class BalanceMonitorJob extends QuartzJobBean
{
	private BalanceMonitorV2Task balanceMonitorTask;

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		this.balanceMonitorTask = DataUtil.getBean(BalanceMonitorV2Task.class);
	    balanceMonitorTask.executeTask(context);

	}
}