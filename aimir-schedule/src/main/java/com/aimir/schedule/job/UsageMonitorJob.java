package com.aimir.schedule.job;

import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Monitor Usage, Power
 * @author goodjob
 *
 */
public abstract class UsageMonitorJob extends QuartzJobBean
{
    public abstract String getDescription();
    public abstract String[] getParamList();
    public abstract String[] getParamListDescription();
    public abstract boolean[] getParamListRequired();
    public abstract String[] getParamListDefault();
}
