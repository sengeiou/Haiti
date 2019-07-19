// INSERT SP-193
package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ThresholdInvalidPacketTask;

public class ThresholdInvalidPacketJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ThresholdInvalidPacketJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
        log.debug("@@@@@@ ThresholdInvalidPacketTask Start @@@@@@");
		
        ThresholdInvalidPacketTask task = DataUtil.getBean(ThresholdInvalidPacketTask.class);
        task.execute(context);


	}

		
	
}
