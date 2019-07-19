package com.aimir.schedule.job;
 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.GetTemperatureDataTask;
 
public class GetTemperatureDataJob extends QuartzJobBean
{
	private GetTemperatureDataTask getTemperatureDataTask;

	public void setTemperatureDataTask(GetTemperatureDataTask getTemperatureDataTask) {
				
		this.getTemperatureDataTask = getTemperatureDataTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		getTemperatureDataTask.excute();
		
	}
}