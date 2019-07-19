package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.SendSMSTask;

/**
 * @author MieUn
 * Send SMS to Customer Job.
 */
public class SendSMSJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(SendSMSJob.class);
	private SendSMSTask smsTask;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("@@@@@@ Send SMS to customer Job Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

		try{
	        taskClass = Class.forName("com.aimir.schedule.task.SendSMSTask");
		
			taskObject = DataUtil.getBean(taskClass);
			smsTask = (SendSMSTask)taskObject;

			if (smsTask == null) {
				log.error("@@@@@@ SendSMSTask is null @@@@@@");
			}
	
			smsTask.executeTask(context);
		}catch(Exception e){
			log.error(e, e);
		}
		log.info("@@@@@@ Send SMS to customer Job End @@@@@@");
	}
}
