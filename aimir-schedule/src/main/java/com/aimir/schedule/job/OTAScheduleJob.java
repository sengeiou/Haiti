/**
 * 
 */
package com.aimir.schedule.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.OTAScheduleTask;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class OTAScheduleJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(OTAScheduleJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("");
		logger.debug("##### OTAScheduleJob Start. JobName=[" + context.getJobDetail().getKey().getName() + "]"
				+ ", JobGroup=[" + context.getJobDetail().getKey().getGroup() + "]" 
				+ ", TriggerName=[" + context.getTrigger().getKey().getName() + "]"
				+ ", TriggerGroup=[" + context.getTrigger().getKey().getGroup() + "] "
				+ ", TriggerPreviousTime=[" + DateTimeUtil.getDateString(context.getTrigger().getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "]"
				+ ", TriggerNextTime=[" + DateTimeUtil.getDateString(context.getTrigger().getNextFireTime(), "yyyy-MM-dd HH:mm:ss") + "]"
				+ ", TriggerFinalTime=[" + DateTimeUtil.getDateString(context.getTrigger().getFinalFireTime(), "yyyy-MM-dd HH:mm:ss") + "] #####");
		
		OTAScheduleTask otaScheduleTask = DataUtil.getBean(OTAScheduleTask.class);
		if (otaScheduleTask == null) {
			logger.error("##### OTAScheduleTask is null #####");
		}
		
		otaScheduleTask.execute(context);
		logger.debug("##### OTAScheduleJob End #####");
		logger.debug("");
	}

}
