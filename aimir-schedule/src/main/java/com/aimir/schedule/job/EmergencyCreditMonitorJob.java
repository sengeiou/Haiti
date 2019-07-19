package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.EmergencyCreditMonitorTask;

public class EmergencyCreditMonitorJob extends QuartzJobBean
{
	private EmergencyCreditMonitorTask emergencyCreditMonitorTask;

	public void setEmergencyCreditMonitorTask(EmergencyCreditMonitorTask emergencyCreditMonitorTask) {
		this.emergencyCreditMonitorTask = emergencyCreditMonitorTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
	    emergencyCreditMonitorTask.excute();
	}
}