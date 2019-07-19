package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class AimirJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
