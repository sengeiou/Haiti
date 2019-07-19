package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.CircuitBreakerControlTask;

/**
 * 공급차단재개설정(CircuitBreakerSetting)에 의해 주기적으로 차단 재개 대상에 대해 
 * 차단 재개를 기동
 * @author goodjob
 *
 */
public class CircuitBreakerControllJob extends  QuartzJobBean 
{
    private static Log log = LogFactory.getLog(CircuitBreakerControllJob.class);

    private CircuitBreakerControlTask circuitBreakerControlTask;

    @Override
    protected void executeInternal(JobExecutionContext context)
    throws JobExecutionException {

        try {
            log.debug("======= circuitBreakerControlTask Start ========");
        	circuitBreakerControlTask = DataUtil.getBean(CircuitBreakerControlTask.class);
        	circuitBreakerControlTask.excute();
        } catch (Exception e) {
            log.error(e,e);
        }

        log.debug("======= circuitBreakerControlTask End ========");
    }
   
}
