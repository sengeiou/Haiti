package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.PrepaySendSMSTask;

public class PrepaySendSMSJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(PrepaySendSMSJob.class);
    private PrepaySendSMSTask prepaySendSMSTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ PrepaySendSMSJob Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.PrepaySendSMSTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        prepaySendSMSTask = (PrepaySendSMSTask)taskObject;

        if (prepaySendSMSTask == null) {
            log.error("@@@@@@ PrepaySendSMSJob is null @@@@@@");
        }

        prepaySendSMSTask.executeTask(context);
        log.debug("@@@@@@ PrepaySendSMSJob End @@@@@@");
    }
}
