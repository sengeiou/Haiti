package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.PrepaySendUsageSMSTask;

public class PrepaySendUsageSMSJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(PrepaySendUsageSMSJob.class);
    private PrepaySendUsageSMSTask prepaySendUsageSMSTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ PrepaySendUsageSMSJob Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.PrepaySendUsageSMSTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        prepaySendUsageSMSTask = (PrepaySendUsageSMSTask)taskObject;

        if (prepaySendUsageSMSTask == null) {
            log.error("@@@@@@ PrepaySendUsageSMSJob is null @@@@@@");
        }

        prepaySendUsageSMSTask.executeTask(context);
        log.debug("@@@@@@ PrepaySendUsageSMSJob End @@@@@@");
    }
}
