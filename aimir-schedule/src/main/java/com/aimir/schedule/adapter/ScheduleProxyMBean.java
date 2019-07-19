package com.aimir.schedule.adapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ScheduleProxyMBean {
    /**
     * start ScheduleProxy
     */
    public void start() throws Exception;

    public List<Map<String,Object>> getJobDetailList() throws Exception;

    @Deprecated
    public List<Map<String,Object>> getTriggerOfJob(String jobName, String datePattern) throws Exception;
    public List<Map<String,Object>> getTriggerOfJob(String jobName, String datePattern, Locale locale) throws Exception;
    public boolean addTrigger(String jobName, String triggerName,long interval, String expression, boolean cron,String operator);
    public boolean addJobTrigger(Map<String, Object> conditionMap);
    public boolean addJobTrigger2(Map<String, Object> conditionMap);

    public boolean deleteTrigger(String triggerName);
    public boolean updateTrigger(String triggerName, long interval, String expression, boolean cron, String operator, String groupType, String jobGroup, Date startTime, Date endTime, String repeatCnt);
    public boolean updateTrigger(String triggerName, long interval, String expression, boolean cron, String operator, String groupType, String jobGroup, HashMap<String, String> subJobDataToMap, Date startTime, Date endTime, String repeatCnt);

    public boolean pauseTrigger(String triggerName);
    public boolean resumTrigger(String triggerName,String operator);

    /**
     * method name : directRunJob<b/>
     * method Desc : 해당 Job 을 즉시 실행시킨다. 실제로는 즉시 실행되는 SimpleTrigger 를 추가한다.
     *
     * @param jobName
     * @param operator
     * @return
     */
    public boolean directRunJob(String jobName, String operator, String triggerName);

    /**
     * method name : getGroupId
     * method Desc : 스케줄의 Group ID를 취득한다.
     *
     * @param triggerName
     * @param operator
     * @return
     */
    public Map<String, Object> getTriggerDataMap(String triggerName, String operator);
}