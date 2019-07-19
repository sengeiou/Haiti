package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Data Estimation Rule에 의해 주기적으로 검침상태를 체크하여
 * Rule에 의해 데이터 자동 보정 (데이터 예측)
 * @author goodjob
 *
 */
public abstract class DataEstimationJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
