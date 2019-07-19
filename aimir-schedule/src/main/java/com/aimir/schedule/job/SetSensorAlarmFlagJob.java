package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.SetSensorAlarmFlagTask;

/**
 * @author goodjob
 *
 */
@Service
@Transactional
public class SetSensorAlarmFlagJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(SetSensorAlarmFlagJob.class);

    private SetSensorAlarmFlagTask setSensorAlarmFlagTask;

	public void setSensorUnitScanningTask(SetSensorAlarmFlagTask setSensorAlarmFlagTask) {
				
		this.setSensorAlarmFlagTask = setSensorAlarmFlagTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		setSensorAlarmFlagTask.execute();
		
	}

}