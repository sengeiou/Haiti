package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.LoadManagementTask;

/**
 * Load Control, Load Limit, Load Shed등의 스케줄 작업에 대해 해당 스케줄을 읽어서
 * 부하에 대한 관리를 실행 (ACD 전원 공급 차단 재개)
 * @author goodjob
 *
 */
public class LoadManagementJob extends QuartzJobBean
{
    private static Log log = LogFactory.getLog(LoadManagementJob.class);

    private LoadManagementTask loadManagementTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {

        try {
            log.debug("======= loadManagementTask Start ========");
            loadManagementTask = DataUtil.getBean(LoadManagementTask.class);
            loadManagementTask.excute();
        } catch (Exception e) {
            log.error(e,e);
        }

        log.debug("======= loadManagementTask End ========");
    }
}
