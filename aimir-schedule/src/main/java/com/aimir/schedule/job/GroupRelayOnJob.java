package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.GroupRelayOnTask;

public class GroupRelayOnJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(GroupRelayOnJob.class);
    private GroupRelayOnTask groupRelayOnTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ Group RelayOn Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.GroupRelayOnTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        groupRelayOnTask = (GroupRelayOnTask)taskObject;

        if (groupRelayOnTask == null) {
            log.error("@@@@@@ Group RelayOn is null @@@@@@");
        }

        groupRelayOnTask.executeTask(context);
        log.debug("@@@@@@ Group RelayOn End @@@@@@");
    }
}
