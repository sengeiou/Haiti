package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.ConverterOnDemandTask;

/**
 * Converter Type 의 모뎀 On Demand Job.
 * @author kskim
 *
 */
public class ConverterOnDemandJob extends QuartzJobBean {

	private static Log log = LogFactory.getLog(ConverterOnDemandJob.class);
	
	ConverterOnDemandTask converterOnDemandTask;

	public ConverterOnDemandTask getConverterOnDemandTask() {
		return converterOnDemandTask;
	}

	public void setConverterOnDemandTask(ConverterOnDemandTask converterOnDemandTask) {
		this.converterOnDemandTask = converterOnDemandTask;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.info("ConverterOnDemandJob");
		converterOnDemandTask.excute();
	}

}
