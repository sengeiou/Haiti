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
import com.aimir.schedule.task.CloneOnOffScheduleTask;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class CloneOnOffScheduleJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(CloneOnOffScheduleJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("");
		logger.debug("##### CloneOnOffScheduleJob Start. JobName=[" + context.getJobDetail().getKey().getName() + "]"
				+ ", JobGroup=[" + context.getJobDetail().getKey().getGroup() + "]" 
				+ ", TriggerName=[" + context.getTrigger().getKey().getName() + "]"
				+ ", TriggerGroup=[" + context.getTrigger().getKey().getGroup() + "] "
				+ ", TriggerPreviousTime=[" + DateTimeUtil.getDateString(context.getTrigger().getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "]"
				+ ", TriggerNextTime=[" + DateTimeUtil.getDateString(context.getTrigger().getNextFireTime(), "yyyy-MM-dd HH:mm:ss") + "]"
				+ ", TriggerFinalTime=[" + DateTimeUtil.getDateString(context.getTrigger().getFinalFireTime(), "yyyy-MM-dd HH:mm:ss") + "] #####");
		
		CloneOnOffScheduleTask task = DataUtil.getBean(CloneOnOffScheduleTask.class);
		if (task == null) {
			logger.error("##### CloneOnOffScheduleJob is null #####");
		}
		
		task.execute(context);
		logger.debug("##### CloneOnOffScheduleJob End #####");
		logger.debug("");
	}

}
