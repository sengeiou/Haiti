// INSERT SP-193
package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ThresholdThroughputTask;

public class ThresholdThroughputJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ThresholdThroughputJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
        log.debug("@@@@@@ ThresholdThroughputTask Start @@@@@@");
		
        ThresholdThroughputTask task = DataUtil.getBean(ThresholdThroughputTask.class);
        task.execute(context);


	}

		
	
}
