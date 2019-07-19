package com.aimir.schedule.job;
 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.AnsansiSumTask;
 
public class AnsansiSumJob extends QuartzJobBean
{
	private AnsansiSumTask ansansiSumTask;

	public void setAnsansiSumTask(AnsansiSumTask ansansiSumTask) {
				
		this.ansansiSumTask = ansansiSumTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		ansansiSumTask.execute();
		
	}
}