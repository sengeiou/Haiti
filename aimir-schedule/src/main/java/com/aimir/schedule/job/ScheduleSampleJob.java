package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ScheduleSampleTask;

/**
 * ScheduleSampleJob.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 2. 27.  v1.0        문동규   변경된 스케줄러 Job 샘플소스. 변경사항: 동적인 Job 등록가능 / Job 실행결과 Log 에 저장
 * </pre>
 */
@Component
public class ScheduleSampleJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(ScheduleSampleJob.class);
    private ScheduleSampleTask scheduleSampleTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ ScheduleSampleJob Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.ScheduleSampleTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        }
        scheduleSampleTask = (ScheduleSampleTask)taskObject;

        if (scheduleSampleTask == null) {
            log.error("@@@@@@ ScheduleSampleTask is null @@@@@@");
        }

        scheduleSampleTask.executeTask(context);
        log.debug("@@@@@@ ScheduleSampleJob End @@@@@@");
    }
}