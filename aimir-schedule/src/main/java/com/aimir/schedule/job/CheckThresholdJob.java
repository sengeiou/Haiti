package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.CheckThresholdTask;

public class CheckThresholdJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(CheckThresholdJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
        log.debug("@@@@@@ CheckThresholdTask Start @@@@@@");
		
        CheckThresholdTask task = DataUtil.getBean(CheckThresholdTask.class);
        task.execute(context);


	}

		
	
}
