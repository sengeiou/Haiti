package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.ECGBillingIntegrationTask;

public class ECGBillingIntegrationJob extends QuartzJobBean {

    ECGBillingIntegrationTask task;
    
    public void setECGBillingIntegrationTask(ECGBillingIntegrationTask task) {
        this.task = task;
    }
    
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
        task.execute(context);
	}

}
