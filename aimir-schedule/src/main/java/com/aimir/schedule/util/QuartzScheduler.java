/*
 * Created on 2007. 08. 27.
 */
package com.aimir.schedule.util;

public class QuartzScheduler
{
    public static final String[] DefaultScheduler = {"AimirJobScheduler","AimirJobQueue","AimirJobQueueMulti"};
    public static final String[] DefaultSchedulerDesc = {"AimirJobScheduler(Cron)","AimirJobQueue(One Thread)","AimirJobQueue(Multi Thread)"};
    public static final int[] DefaultSchedulerType = { 0, 1, 1 };

    public static final int SchedulerStatus_STARTED = 0;
    public static final String SchedulerStatus_STARTED_TEXT = "Started";
    public static final int SchedulerStatus_STOPPED = 1;
    public static final String SchedulerStatus_STOPPED_TEXT = "Stopped";
    public static final int SchedulerStatus_PAUSED = 2;
    public static final String SchedulerStatus_PAUSED_TEXT = "Paused";
    public static final String[] SchedulerStatus_TEXT = {SchedulerStatus_STARTED_TEXT,
                                                         SchedulerStatus_STOPPED_TEXT,
                                                         SchedulerStatus_PAUSED_TEXT};

    public static final int PersistentType_MEMORY = 0;
    public static final String PersistentType_MEMORY_TEXT = "Memory";
    public static final int PersistentType_DATABASE = 1;
    public static final String PersistentType_DATABASE_TEXT = "Database";
    public static final String[] PersistentType_TEXT = {PersistentType_MEMORY_TEXT,
                                                        PersistentType_DATABASE_TEXT };

    public static final int TriggerType_CRON = 0;
    public static final String TriggerType_CRON_TEXT = "Cron";
    public static final String TriggerType_CRON_CLASS = "org.quartz.CronTrigger";
    public static final int TriggerType_SIMPLE = 1;
    public static final String TriggerType_SIMPLE_TEXT = "Simple";
    public static final String TriggerType_SIMPLE_CLASS = "org.quartz.SimpleTrigger";
    public static final int TriggerType_UNKNOWN = 99;
    public static final String TriggerType_UNKNOWN_TEXT = "Unknown";
    public static final String TriggerType_UNKNOWN_CLASS = "";
    public static final int[] TriggerType = { TriggerType_CRON,
                                             TriggerType_SIMPLE,
                                             TriggerType_UNKNOWN };
    public static final String[] TriggerType_TEXT = { TriggerType_CRON_TEXT,
                                                      TriggerType_SIMPLE_TEXT,
                                                      TriggerType_UNKNOWN_TEXT };
    
    public static final int TargetType_METERING = 0;
    public static final String TargetType_METERING_TEXT = "MeteringSystem";
    public static final int TargetType_SENSOR = 1;
    public static final String TargetType_SENSOR_TEXT = "ZigbeeSensor";
    public static final String[] TargetType_TEXT = {TargetType_METERING_TEXT,
                                               TargetType_SENSOR_TEXT};
    
    public static int getTargetType(String targetType)
    {
        if(targetType != null && targetType.length() > 0){
            for(int i=0;i<TargetType_TEXT.length;i++)
                if(targetType.equals(TargetType_TEXT[i]))
                    return i;
        }

        return 0;
    }

    public static final String[] WEEK = { "aimir.quartz.week.sun",
                                         "aimir.quartz.week.mon",
                                         "aimir.quartz.week.tue",
                                         "aimir.quartz.week.wed",
                                         "aimir.quartz.week.thu",
                                         "aimir.quartz.week.fri",
                                         "aimir.quartz.week.sat" };

    public static final String[] MONTH = { "aimir.quartz.month.jan",
                                          "aimir.quartz.month.feb",
                                          "aimir.quartz.month.mar",
                                          "aimir.quartz.month.apr",
                                          "aimir.quartz.month.may",
                                          "aimir.quartz.month.jun",
                                          "aimir.quartz.month.jul",
                                          "aimir.quartz.month.aug",
                                          "aimir.quartz.month.sep",
                                          "aimir.quartz.month.oct",
                                          "aimir.quartz.month.nov",
                                          "aimir.quartz.month.dec" };
    public static final int ResultType_FILE = 0;
    public static final String ResultType_FILE_TEXT = "FILE";
    public static final int ResultType_EMAIL = 1;
    public static final String ResultType_EMAIL_TEXT = "EMAIL";
    public static final int ResultType_FTP = 2;
    public static final String ResultType_FTP_TEXT = "FTP";
    public static final int[] ResultType = { ResultType_FILE, ResultType_EMAIL,
                                            ResultType_FTP };
    public static final String[] ResultType_TEXT = { ResultType_FILE_TEXT,
                                                     ResultType_EMAIL_TEXT,
                                                     ResultType_FTP_TEXT };

    public static int getTriggerType(String triggerClass)
    {
        if(triggerClass.equals("org.quartz.CronTrigger"))
            return TriggerType_CRON;
        else if(triggerClass.equals("org.quartz.SimpleTrigger"))
            return TriggerType_SIMPLE;
        else 
            return TriggerType_UNKNOWN;
    }
    public static String getTriggerTypeString(String triggerClass)
    {
        if(triggerClass.equals("org.quartz.CronTrigger"))
            return TriggerType_CRON_TEXT;
        else if(triggerClass.equals("org.quartz.SimpleTrigger"))
            return TriggerType_SIMPLE_TEXT;
        else
            return TriggerType_UNKNOWN_TEXT;
    }
    public static String getTriggerTypeString(int triggerType)
    {
        if(triggerType == TriggerType_CRON)
            return TriggerType_CRON_TEXT;
        else if(triggerType == TriggerType_SIMPLE)
            return TriggerType_SIMPLE_TEXT;
        else
            return TriggerType_UNKNOWN_TEXT;
    }
    public static int getPersistentType(boolean jobStoreSupportsPersistence)
    {
        if(jobStoreSupportsPersistence)
            return PersistentType_DATABASE;
        else
            return PersistentType_MEMORY;
    }
    public static String getPersistentTypeString(int jobStoreSupportsPersistence)
    {
        if(PersistentType_MEMORY == jobStoreSupportsPersistence)
            return PersistentType_MEMORY_TEXT;
        else if(PersistentType_DATABASE == jobStoreSupportsPersistence)
            return PersistentType_DATABASE_TEXT;
        else
            return null;
    }
    public static int getShedulerStatus(boolean isShutdown,boolean isInStandbyMode)
    {
        if(isShutdown)
            return SchedulerStatus_STOPPED;
        else if(isInStandbyMode)
            return SchedulerStatus_PAUSED;
        else
            return SchedulerStatus_STARTED;
    }
    public static String getSchedulerStatusString(int schedulerStatus)
    {
        if(SchedulerStatus_STARTED == schedulerStatus)
            return SchedulerStatus_STARTED_TEXT;
        else if(SchedulerStatus_STOPPED == schedulerStatus)
            return SchedulerStatus_STOPPED_TEXT;
        else if(SchedulerStatus_PAUSED == schedulerStatus)
            return SchedulerStatus_PAUSED_TEXT;

        return null;
    }
    public static String getParamDesc(String[] param,String[] desc, String key)
    {
        for(int i=0;param!=null && i<param.length;i++)
        {
            if(param[i].equals(key))
                return desc[i];
        }
        return "";
    }
}