package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.BalanceMonitorV2MOETask;

public class MOEBalanceMonitorJob extends QuartzJobBean
{
	private BalanceMonitorV2MOETask balanceMonitorTask;

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		this.balanceMonitorTask = DataUtil.getBean(BalanceMonitorV2MOETask.class);
	    balanceMonitorTask.executeTask(context);

	}
}