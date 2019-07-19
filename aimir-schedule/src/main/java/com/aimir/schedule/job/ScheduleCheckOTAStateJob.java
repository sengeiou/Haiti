package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.ScheduleCheckOTAStateTask;
import com.aimir.schedule.task.SensorUnitScanningTask;

@Service
@Transactional
public class ScheduleCheckOTAStateJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(ScheduleCheckOTAStateJob.class);

    private ScheduleCheckOTAStateTask scheduleCheckOTAStateTask;

	public void setScheduleCheckOTAStateTask(ScheduleCheckOTAStateTask scheduleCheckOTAStateTask) {
				
		this.scheduleCheckOTAStateTask = scheduleCheckOTAStateTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		scheduleCheckOTAStateTask.execute();
		
	}
}
