package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.EDHBalanceMonitorDemoTask;

public class EDHBalanceMonitorDemoJob extends QuartzJobBean
{
	private EDHBalanceMonitorDemoTask balanceMonitorTask;

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		this.balanceMonitorTask = DataUtil.getBean(EDHBalanceMonitorDemoTask.class);
	    balanceMonitorTask.executeTask(context);

	}
}