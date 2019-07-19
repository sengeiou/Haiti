package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.MOEBalanceMonitorDemoTask;

public class MOEBalanceMonitorDemoJob extends QuartzJobBean {
	private MOEBalanceMonitorDemoTask balanceMonitorTask;

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		this.balanceMonitorTask = DataUtil.getBean(MOEBalanceMonitorDemoTask.class);
		balanceMonitorTask.executeTask(context);
	}
}