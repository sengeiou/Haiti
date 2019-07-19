package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.MeterProgramTask;
import com.aimir.schedule.task.ScheduleTask;

/**
 * TOU, SummerTime 등 MX2미터 설정을 적용하는 스케쥴
 * @author kskim
 *
 */
public class MeterProgramJob extends QuartzJobBean {

	private static Log log = LogFactory.getLog(MeterProgramJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		log.info("MeterProgramJob");
		//MeterProgramTask task = new MeterProgramTask();
    	ScheduleTask task = (ScheduleTask)DataUtil.getBean("meterProgramTask");
    	task.executeTask(context);
	}

}
