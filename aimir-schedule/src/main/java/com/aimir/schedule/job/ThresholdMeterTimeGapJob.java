// INSERT SP-193
package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ThresholdMeterTimeGapTask;

public class ThresholdMeterTimeGapJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ThresholdMeterTimeGapJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
        log.debug("@@@@@@ ThresholdMeterTimeGapTask Start @@@@@@");
		
        ThresholdMeterTimeGapTask task = DataUtil.getBean(ThresholdMeterTimeGapTask.class);
        task.execute(context);


	}

		
	
}
