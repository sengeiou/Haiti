package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.IHDMonthBillSaveTask;
import com.aimir.schedule.task.ScheduleSampleTask;

public class IHDMonthBillSaveJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(IHDMonthBillSaveJob.class);
    private IHDMonthBillSaveTask ihdMonthBillSaveTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ IHDMonthBillSaveJob Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.IHDMonthBillSaveTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        ihdMonthBillSaveTask = (IHDMonthBillSaveTask)taskObject;

        if (ihdMonthBillSaveTask == null) {
            log.error("@@@@@@ IHDMonthBillSaveTask is null @@@@@@");
        }

        ihdMonthBillSaveTask.excute();
        log.debug("@@@@@@ IHDMonthBillSaveTask End @@@@@@");
    }
}
