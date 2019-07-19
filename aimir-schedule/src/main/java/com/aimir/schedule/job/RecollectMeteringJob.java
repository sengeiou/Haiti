package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.RecollectMeteringTask;

@Service
@Transactional
public class RecollectMeteringJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(RecollectMeteringJob.class);

    private RecollectMeteringTask recollectMeteringTask;

	public void setRecollectMeteringTask(RecollectMeteringTask recollectMeteringTask) {
				
		this.recollectMeteringTask = recollectMeteringTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		recollectMeteringTask.execute();
		
	}
}
