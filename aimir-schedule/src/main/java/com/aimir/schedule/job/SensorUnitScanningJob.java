package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.schedule.task.SensorUnitScanningTask;

@Service
@Transactional
public class SensorUnitScanningJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(SensorUnitScanningJob.class);

    private SensorUnitScanningTask sensorUnitScanningTask;

	public void setSensorUnitScanningTask(SensorUnitScanningTask sensorUnitScanningTask) {
				
		this.sensorUnitScanningTask = sensorUnitScanningTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		sensorUnitScanningTask.execute();
		
	}
}
