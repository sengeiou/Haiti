package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.GroupRelayOffTask;

public class GroupRelayOffJob extends QuartzJobBean {
    private static Log log = LogFactory.getLog(GroupRelayOffJob.class);
    private GroupRelayOffTask groupRelayOffTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {
        log.debug("@@@@@@ Group RelayOff Start @@@@@@");

        Class<?> taskClass = null;
        Object taskObject = null;

        try {
            taskClass = Class.forName("com.aimir.schedule.task.GroupRelayOffTask");
            taskObject = DataUtil.getBean(taskClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            log.error(ce.getMessage());
        }
        groupRelayOffTask = (GroupRelayOffTask)taskObject;

        if (groupRelayOffTask == null) {
            log.error("@@@@@@ Group RelayRelayOff is null @@@@@@");
        }

        groupRelayOffTask.executeTask(context);
        log.debug("@@@@@@ Group RelayRelayOff End @@@@@@");
    }
}
