package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.GroupOndemandMeterTask;
import com.aimir.schedule.task.ScheduleTask;

public class GroupOndemandMeterJob extends QuartzJobBean {

	private static Log log = LogFactory.getLog(GroupOndemandMeterJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		log.debug("GroupOndemandMeterJob");
		GroupOndemandMeterTask task = DataUtil.getBean(GroupOndemandMeterTask.class);
//        ScheduleTask task = (ScheduleTask)DataUtil.getBean("groupOndemandMeterTask");
        task.executeTask(arg0);
	}

}
