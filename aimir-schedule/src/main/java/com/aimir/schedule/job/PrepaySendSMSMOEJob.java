package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.PrepaySendSMSMOETask;

public class PrepaySendSMSMOEJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(PrepaySendSMSMOEJob.class);
	private PrepaySendSMSMOETask task;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.debug("@@@@@@ PrepaySendSMSMOETask Start @@@@@@");

		task = DataUtil.getBean(PrepaySendSMSMOETask.class);

		if (task == null) {
			log.error("@@@@@@ PrepaySendSMSMOETask is null @@@@@@");
		}

		task.executeTask(context);

		log.debug("@@@@@@ PrepaySendSMSMOETask End @@@@@@");
	}

}
