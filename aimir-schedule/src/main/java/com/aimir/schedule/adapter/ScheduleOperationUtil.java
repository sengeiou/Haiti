package com.aimir.schedule.adapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ScheduleOperationUtil {

	private static Log log = LogFactory.getLog(ScheduleOperationUtil.class);

	public static List<Map<String, Object>> getJobDetailList() throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			return proxy.getJobDetailList();
		} catch (Exception e) {
			throw e;
		} finally {
			sm.close();
		}
	}

	public static boolean addJobTrigger(Map<String, Object> conditionMap) throws Exception {

	    ScheduleManager sm = null;
	    Boolean result = null;
	    try {
	        sm = new ScheduleManager();
	        ScheduleProxyMBean proxy = sm.getScheduleProxy();
	        result=proxy.addJobTrigger(conditionMap);
	    } catch (Exception e) {
	    	log.error(e, e);
	        return false;

	    } finally {
	        sm.close();
	    }
	    return result;
	}
	
	public static boolean addJobTrigger2(Map<String, Object> conditionMap) throws Exception {

	    ScheduleManager sm = null;
	    Boolean result = null;
	    try {
	        sm = new ScheduleManager();

	        ScheduleProxyMBean proxy = null;
	        if(conditionMap != null && conditionMap.containsKey("toTargetProperty")) {
	        	proxy = sm.getScheduleProxy(conditionMap.get("toTargetProperty").toString());	
	        }else {
	        	proxy = sm.getScheduleProxy();
	        }
	        result=proxy.addJobTrigger2(conditionMap);
	    } catch (Exception e) {
	    	log.error(e, e);
	        return false;

	    } finally {
	        sm.close();
	    }
	    return result;
	}

	@Deprecated
	public static List<Map<String, Object>> getTriggerOfJob(String jobName, String datePattern)
			throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			return proxy.getTriggerOfJob(jobName, datePattern);
		} catch (Exception e) {
			throw e;
		} finally {
			sm.close();
		}
	}

    public static List<Map<String, Object>> getTriggerOfJob(String jobName, String datePattern, Locale locale) throws Exception {
        ScheduleManager sm = null;

        try {
            sm = new ScheduleManager();
            ScheduleProxyMBean proxy = sm.getScheduleProxy();
            return proxy.getTriggerOfJob(jobName, datePattern, locale);
        } catch (Exception e) {
            throw e;
        } finally {
            sm.close();
        }
    }

	public static boolean addTrigger(String jobName,String triggerName,long repeatInterval,String expression,boolean cron,String operator) throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			proxy.addTrigger(jobName,triggerName, repeatInterval,expression,cron,operator);
		} catch (Exception e) {
			return false;

		} finally {
			sm.close();
		}
		return true;
	}

    public static boolean updateTrigger(String triggerName, long repeatInterval, String expression, boolean cron, String operator, String groupType, String jobGroup, Date startTime, Date endTime, String repeatCount) throws Exception {
        return updateTrigger(triggerName, repeatInterval, expression, cron, operator, groupType, jobGroup, null, startTime, endTime, repeatCount);
    }

	public static boolean updateTrigger(String triggerName, long repeatInterval, String expression, boolean cron, String operator, String groupType, String jobGroup, HashMap<String, String> subJobDataToMap, Date startTime, Date endTime, String repeatCount) throws Exception {
        ScheduleManager sm = null;
        boolean result = false;
        try {
            sm = new ScheduleManager();
            ScheduleProxyMBean proxy = sm.getScheduleProxy();

            if(subJobDataToMap != null){
                result = proxy.updateTrigger(triggerName, repeatInterval, expression, cron, operator, groupType, jobGroup, subJobDataToMap, startTime, endTime, repeatCount);
            } else {
                result = proxy.updateTrigger(triggerName, repeatInterval, expression, cron, operator, groupType, jobGroup, startTime, endTime, repeatCount);
            }

            return result;
        } catch (Exception e) {
            return false;

        } finally {
            sm.close();
        }
	}

	public static boolean deleteTrigger(String triggerName) throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			return proxy.deleteTrigger(triggerName);
		} catch (Exception e) {
			return false;

		} finally {
			sm.close();
		}
	}

	public static boolean pauseTrigger(String triggerName) throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			return proxy.pauseTrigger(triggerName);
		} catch (Exception e) {
			return false;

		} finally {
			sm.close();
		}
	}

	public static boolean resumTrigger(String triggerName,String operator) throws Exception {

		ScheduleManager sm = null;

		try {
			sm = new ScheduleManager();
			ScheduleProxyMBean proxy = sm.getScheduleProxy();
			return proxy.resumTrigger(triggerName,operator);
		} catch (Exception e) {
			return false;

		} finally {
			sm.close();
		}
	}

    public static boolean directRunJob(String jobName, String operator,  String triggerName) throws Exception {

        ScheduleManager sm = null;

        try {
            sm = new ScheduleManager();
            ScheduleProxyMBean proxy = sm.getScheduleProxy();
            return proxy.directRunJob(jobName, operator, triggerName);
        } catch (Exception e) {
            return false;

        } finally {
            sm.close();
        }
    }

    public static Map<String, Object> getTriggerDataMap(String jobName, String operator) throws Exception {

        ScheduleManager sm = null;
        try {
            sm = new ScheduleManager();
            ScheduleProxyMBean proxy = sm.getScheduleProxy();
            return proxy.getTriggerDataMap(jobName, operator);
        } catch (Exception e) {
            return null;
        } finally {
            sm.close();
        }
    }


}