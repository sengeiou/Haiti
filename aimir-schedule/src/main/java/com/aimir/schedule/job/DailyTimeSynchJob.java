package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.DailyTimeSynchTask;
import com.aimir.schedule.task.ScheduleTask;

/**
 * 매일 서버 시간으로 미터 시간을 동기화 한다.
 * @author kskim
 *
 */
public class DailyTimeSynchJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(DailyTimeSynchJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("DailyTimeSynchJob");
		//DailyTimeSynchTask task = new DailyTimeSynchTask();
		ScheduleTask task = (ScheduleTask)DataUtil.getBean("dailyTimeSynchTask");
		task.executeTask(context);
	}

}
