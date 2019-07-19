package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 주기적으로 공급사의 요금표 (Tariff)의 변동 내역을 관리
 * 만약에 공급사의 요금에 대해 웹서비스나 연계 시스템이 제공되면 
 * 주기적으로 시스템의 요금표를 업데이트 함
 * @author goodjob
 *
 */
public abstract class TariffUpdateJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
