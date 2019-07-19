package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 검침 실패 분석 및 SLA테이블에 저장하여 
 * 기간별 SLA 지수를 저장
 * @author goodjob
 *
 */
public abstract class MeteringSLAAnalysisJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
