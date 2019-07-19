package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 검침 실패 분석 
 * 검침 실패한 장비들에 대한 상태 및 원인을 파악하여 일자별로 저장
 * @author goodjob
 *
 */
public abstract class MeteringFailAnalysisJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
