package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.ECGSendSMSRecoveryTask;

public class ECGSendSMSRecoveryJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(ECGSendSMSRecoveryJob.class);
    private ECGSendSMSRecoveryTask ecgSendSMSRecoveryTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ ECGSendSMSRecoveryJob Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.ECGSendSMSRecoveryTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        ecgSendSMSRecoveryTask = (ECGSendSMSRecoveryTask)taskObject;

        if (ecgSendSMSRecoveryTask == null) {
            log.error("@@@@@@ ECGSendSMSRecoveryJob is null @@@@@@");
        }

        ecgSendSMSRecoveryTask.executeTask(context);
        log.debug("@@@@@@ ECGSendSMSRecoveryJob End @@@@@@");
    }
}
