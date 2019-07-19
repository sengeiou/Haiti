package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.MOEBalanceMonitorDemo1Task;

public class MOEBalanceMonitorDemo1Job extends QuartzJobBean {
	private MOEBalanceMonitorDemo1Task balanceMonitorTask;

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		this.balanceMonitorTask = DataUtil.getBean(MOEBalanceMonitorDemo1Task.class);
		balanceMonitorTask.executeTask(context);
	}
}