package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.PrepaySendSMSSpasaTask;

public class PrepaySendSMSSpasaJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(PrepaySendSMSSpasaJob.class);
	private PrepaySendSMSSpasaTask prepaySendSMSTaskspasa;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.debug("@@@@@@ PrepaySendSMSJobSpasa Start @@@@@@");

		prepaySendSMSTaskspasa = DataUtil.getBean(PrepaySendSMSSpasaTask.class);

		if (prepaySendSMSTaskspasa == null) {
			log.error("@@@@@@ PrepaySendSMSSpasaTask is null @@@@@@");
		}

		prepaySendSMSTaskspasa.executeTask(context);

		log.debug("@@@@@@ PrepaySendSMSJobSpasa End @@@@@@");
	}
}
