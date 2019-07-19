package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.RecollectMeteringSoriaTask;

@Service
@Transactional
public class RecollectMeteringSoriaJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(RecollectMeteringSoriaJob.class);

    private RecollectMeteringSoriaTask recollectMeteringSoriaTask;

	public void setRecollectMeteringTask(RecollectMeteringSoriaTask recollectMeteringTask) {
				
		this.recollectMeteringSoriaTask = recollectMeteringTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		recollectMeteringSoriaTask.execute(context);
		
	}
}
