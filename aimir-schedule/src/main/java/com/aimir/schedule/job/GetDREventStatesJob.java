package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.GetDREventStatesTask;

public class GetDREventStatesJob extends QuartzJobBean
{
	private GetDREventStatesTask getDREventStatesTask;

	public void setBalanceMonitorTask(GetDREventStatesTask getDREventStatesTask) {

		this.getDREventStatesTask = getDREventStatesTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {

		getDREventStatesTask.excute();
	}
}

