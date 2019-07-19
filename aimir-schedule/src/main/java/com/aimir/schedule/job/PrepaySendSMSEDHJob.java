package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.PrepaySendSMSEDHTask;

public class PrepaySendSMSEDHJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(PrepaySendSMSEDHJob.class);
	private PrepaySendSMSEDHTask prepaySendSMSEDHTask;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.debug("@@@@@@ PrepaySendSMSEDHTask Start @@@@@@");

		prepaySendSMSEDHTask = DataUtil.getBean(PrepaySendSMSEDHTask.class);

		if (prepaySendSMSEDHTask == null) {
			log.error("@@@@@@ PrepaySendSMSEDHTask is null @@@@@@");
		}

		prepaySendSMSEDHTask.executeTask(context);

		log.debug("@@@@@@ PrepaySendSMSEDHTask End @@@@@@");
	}
	
	
	
}
