package com.aimir.schedule.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.listener.GlobalTriggerListener;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

public class ScheduleProxy implements ScheduleProxyMBean, MBeanRegistration {

	private static Log log = LogFactory.getLog(ScheduleProxy.class);
//	private ObjectName objectName = null;

	StdScheduler scheduler = null;
	
	public ScheduleProxy(StdScheduler scheduler) {

		this.scheduler = scheduler;
	}

	public void postDeregister() {
		// TODO Auto-generated method stub

	}

	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub

	}

	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub

	}

	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws java.lang.Exception {
		if (name == null) {
			name = new ObjectName(server.getDefaultDomain() + ":service="
					+ this.getClass().getName());
		}
//		this.objectName = name;
		return name;
	}

	public void start() throws Exception {
		log.debug("scheduler##################:" + this.scheduler.getSchedulerName());
	}

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getJobDetailList() throws Exception {
        Set<JobKey> jobKeys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP));

        List<Map<String, Object>> jobDetailList = new ArrayList<Map<String, Object>>();

        JobDetail jobDetail = null;

        for (JobKey jobKey : jobKeys) {
            jobDetail = this.scheduler.getJobDetail(jobKey);

            Map<String, Object> jobMap = new HashMap<String, Object>();
            jobMap.put("name", jobKey.getName());
            jobMap.put("className", jobDetail.getJobClass().getName());
            jobMap.put("description", jobDetail.getDescription());

            JobDataMap dataMap = jobDetail.getJobDataMap();
            jobMap.put("group", (dataMap != null) ? StringUtil.nullToBlank(dataMap.get("group")) : "");

            Map<String, Object> jobStatistic = new HashMap<String, Object>();
            jobStatistic.put(TriggerState.NONE.name(), 0);
            jobStatistic.put(TriggerState.NORMAL.name(), 0);
            jobStatistic.put(TriggerState.PAUSED.name(), 0);
            jobStatistic.put(TriggerState.COMPLETE.name(), 0);
            jobStatistic.put(TriggerState.ERROR.name(), 0);
            jobStatistic.put(TriggerState.BLOCKED.name(), 0);
            List<Trigger> triggers = (List<Trigger>)this.scheduler.getTriggersOfJob(jobKey);

            for (Trigger trigger : triggers) {
                TriggerState state = this.scheduler.getTriggerState(trigger.getKey());
                setJobStatic(jobStatistic, state);
            }
            jobMap.put("TOTAL", triggers.size());

            jobMap.put(TriggerState.NONE.name(), jobStatistic.get(TriggerState.NONE.name()));
            jobMap.put(TriggerState.NORMAL.name(), jobStatistic.get(TriggerState.NORMAL.name()));
            jobMap.put(TriggerState.PAUSED.name(), jobStatistic.get(TriggerState.PAUSED.name()));
            jobMap.put(TriggerState.COMPLETE.name(), jobStatistic.get(TriggerState.COMPLETE.name()));
            jobMap.put(TriggerState.ERROR.name(), jobStatistic.get(TriggerState.ERROR.name()));
            jobMap.put(TriggerState.BLOCKED.name(), jobStatistic.get(TriggerState.BLOCKED.name()));
            jobDetailList.add(jobMap);
        }
        return jobDetailList;
    }

    public void setJobStatic(Map<String, Object> jobStatistic, TriggerState state) {
        int stateCount = 0;
        switch(state) {
            case NONE:
                stateCount = (Integer) jobStatistic.get(TriggerState.NONE.name());
                jobStatistic.put(TriggerState.NONE.name(), (stateCount + 1));
                break;
            case NORMAL:
                stateCount = (Integer) jobStatistic.get(TriggerState.NORMAL.name());
                jobStatistic.put(TriggerState.NORMAL.name(), (stateCount + 1));
                break;
            case PAUSED:
                stateCount = (Integer) jobStatistic.get(TriggerState.PAUSED.name());
                jobStatistic.put(TriggerState.PAUSED.name(), (stateCount + 1));
                break;
            case COMPLETE:
                stateCount = (Integer) jobStatistic.get(TriggerState.COMPLETE.name());
                jobStatistic.put(TriggerState.COMPLETE.name(), (stateCount + 1));
                break;
            case ERROR:
                stateCount = (Integer) jobStatistic.get(TriggerState.ERROR.name());
                jobStatistic.put(TriggerState.ERROR.name(), (stateCount + 1));
                break;
            case BLOCKED:
                stateCount = (Integer) jobStatistic.get(TriggerState.BLOCKED.name());
                jobStatistic.put(TriggerState.BLOCKED.name(), (stateCount + 1));
                break;
        }
    }

	@Deprecated
	private String dateNullCheck(Date date, String datePattern){
		SimpleDateFormat format = new SimpleDateFormat(datePattern);
		if(date != null){
			return format.format(date.getTime());
		}else{
			return "0";
		}
	}

    private String dateNullCheck(Date date, String datePattern, Locale locale) {
        SimpleDateFormat format = null;

        if (locale != null) {
            format = new SimpleDateFormat(datePattern, locale);
        } else {
            format = new SimpleDateFormat(datePattern);
        }

        if (date != null) {
            return format.format(date.getTime());
        } else {
            return "0";
        }
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Map<String, Object>> getTriggerOfJob(String jobName, String datePattern) throws Exception {
        JobKey jobKey = new JobKey(jobName);
        List<Trigger> triggers = (List<Trigger>)this.scheduler.getTriggersOfJob(jobKey);

        List<Map<String, Object>> triggerList = new ArrayList<Map<String, Object>>();

        for (Trigger trigger : triggers) {
            Map<String, Object> triggerMap = new HashMap<String, Object>();

            if (trigger instanceof SimpleTrigger) {
                triggerMap.put("name", trigger.getKey().getName());
                triggerMap.put("startTime", dateNullCheck(((SimpleTrigger) trigger).getStartTime(), datePattern));
                triggerMap.put("previousFireTime", dateNullCheck(((SimpleTrigger) trigger).getPreviousFireTime(), datePattern));
                triggerMap.put("nextFireTime", dateNullCheck(((SimpleTrigger) trigger).getNextFireTime(), datePattern));
                triggerMap.put("repeatCount", ((SimpleTrigger) trigger).getRepeatCount());
                triggerMap.put("repeatInterval", ((SimpleTrigger) trigger).getRepeatInterval());
                triggerMap.put("status", this.scheduler.getTriggerState(trigger.getKey()).name());
                triggerMap.put("cron", false);
            } else {
                triggerMap.put("name", trigger.getKey().getName());
                triggerMap.put("startTime", dateNullCheck(((CronTrigger) trigger).getStartTime(), datePattern));
                triggerMap.put("previousFireTime", dateNullCheck(((CronTrigger) trigger).getPreviousFireTime(), datePattern));
                triggerMap.put("nextFireTime", dateNullCheck(((CronTrigger) trigger).getNextFireTime(), datePattern));
                triggerMap.put("cronExpression", ((CronTrigger) trigger).getCronExpression());
                triggerMap.put("status", this.scheduler.getTriggerState(trigger.getKey()).name());
                triggerMap.put("cron", true);
            }
            triggerList.add(triggerMap);
        }

        return triggerList;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTriggerOfJob(String jobName, String datePattern, Locale locale) throws Exception {
        JobKey jobKey = new JobKey(jobName);
        List<Trigger> triggers = (List<Trigger>)this.scheduler.getTriggersOfJob(jobKey);

        List<Map<String, Object>> triggerList = new ArrayList<Map<String, Object>>();

        for (Trigger trigger : triggers) {
            Map<String, Object> triggerMap = new HashMap<String, Object>();

            triggerMap.put("name", trigger.getKey().getName());
            triggerMap.put("startTime", dateNullCheck(trigger.getStartTime(), datePattern, locale));
            triggerMap.put("previousFireTime", dateNullCheck(trigger.getPreviousFireTime(), datePattern, locale));
            triggerMap.put("nextFireTime", dateNullCheck(trigger.getNextFireTime(), datePattern, locale));
            triggerMap.put("status", this.scheduler.getTriggerState(trigger.getKey()).name());

            if (trigger instanceof SimpleTrigger) {
                triggerMap.put("repeatCount", ((SimpleTrigger) trigger).getRepeatCount());
                triggerMap.put("repeatInterval", ((SimpleTrigger) trigger).getRepeatInterval());
                triggerMap.put("cron", false);
            } else {
                triggerMap.put("cronExpression", ((CronTrigger) trigger).getCronExpression());
                triggerMap.put("cron", true);
            }
            triggerList.add(triggerMap);
        }

        return triggerList;
    }

    public boolean addTrigger(String jobName, String triggerName, long interval, String expression, boolean cron,
            String operator) {

        try {
            GlobalTriggerListener.operatorMap.put(triggerName, operator);
            JobKey jobKey = new JobKey(jobName);
            JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);
            TriggerKey triggerKey = new TriggerKey(triggerName);
            long ctime = TimeUtil.getCurrentLongTime();

            if (cron) {
                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .forJob(jobKey)
                        .build();

                this.scheduler.scheduleJob(jobDetail, cronTrigger);
            } else {
                SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                simpleScheduleBuilder.withIntervalInMilliseconds(interval);
                simpleScheduleBuilder.withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

                SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(simpleScheduleBuilder)
                        .startAt(new Date(ctime))
                        .forJob(jobKey)
                        .build();

                this.scheduler.scheduleJob(jobDetail, simpleTrigger);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean addJobTrigger(Map<String, Object> conditionMap) {
        String jobName = (String)conditionMap.get("jobName");
        String jobClassName = (String)conditionMap.get("jobClassName");
        String jobDescription = (String)conditionMap.get("jobDescription");
        String jobGroup = (String)conditionMap.get("jobGroup");
        String groupType = (String)conditionMap.get("groupType");
        String triggerName = (String)conditionMap.get("triggerName");
        String operator = (String)conditionMap.get("operator");
        Boolean cron = (Boolean)conditionMap.get("cron");
        String expression = (String)conditionMap.get("expression");
        Long interval = (Long)conditionMap.get("repeatInterval");

        try {
            // jobName check
            JobKey jobKey = new JobKey(jobName);
            JobDetailImpl jobDetailImpl = (JobDetailImpl)this.scheduler.getJobDetail(jobKey);
            boolean isJobAdd = false;

            if (jobDetailImpl == null) {
                isJobAdd = true;
            }

            if (isJobAdd) {     // Add Job
                Class<?> jobClass = null;
                Object jobObject = null;

                try {
                    jobClass = Class.forName(jobClassName);
                    jobObject = DataUtil.getBean(jobClass);
                } catch (ClassNotFoundException ce) {
                    ce.printStackTrace();
                }
                // new job
                JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) jobObject.getClass());
                jobBuilder.withIdentity(jobKey);
                jobBuilder.withDescription(jobDescription);

                if (!StringUtil.nullToBlank(jobGroup).isEmpty()) {
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("group", new Integer(jobGroup));
                    jobBuilder.usingJobData(jobDataMap);
                }

                jobDetailImpl = (JobDetailImpl)jobBuilder.build();
            } else {
                JobDataMap jobDataMap = jobDetailImpl.getJobDataMap();

                if (!StringUtil.nullToBlank(jobGroup).isEmpty()) {
                    if (jobDataMap == null) {
                        jobDataMap = new JobDataMap();
                    }
                    jobDataMap.put("group", new Integer(jobGroup));     // group id
                    jobDetailImpl.setJobDataMap(jobDataMap);
                } else {
                    if (jobDataMap != null) {
                        jobDataMap.remove("group");
                    }
                }
            }

            GlobalTriggerListener.operatorMap.put(triggerName, operator);

            long ctime = TimeUtil.getCurrentLongTime();
            TriggerKey triggerKey = new TriggerKey(triggerName);

            if (cron) {
                JobDataMap cronTriggerJobDataMap = new JobDataMap();
                cronTriggerJobDataMap.put("group", new Integer(jobGroup)); // group id
                cronTriggerJobDataMap.put("groupType", groupType); // group type

                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .usingJobData(cronTriggerJobDataMap)
                        .forJob(jobKey)
                        .build();

                if (isJobAdd) {
                    this.scheduler.scheduleJob(jobDetailImpl, cronTrigger);
                } else {
                    this.scheduler.addJob(jobDetailImpl, true);
                    this.scheduler.scheduleJob(cronTrigger);
                }
            } else {
                JobDataMap simpleTriggerJobDataMap = new JobDataMap();
                simpleTriggerJobDataMap.put("group", new Integer(jobGroup));     // group id
                simpleTriggerJobDataMap.put("groupType", groupType);     // group type

                SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                simpleScheduleBuilder.withIntervalInMilliseconds(interval);
                simpleScheduleBuilder.withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

                SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(simpleScheduleBuilder)
                        .usingJobData(simpleTriggerJobDataMap)
                        .startAt(new Date(ctime))
                        .forJob(jobKey)
                        .build();

                if (isJobAdd) {
                    this.scheduler.scheduleJob(jobDetailImpl, simpleTrigger);
                } else {
                    this.scheduler.addJob(jobDetailImpl, true);
                    this.scheduler.scheduleJob(simpleTrigger);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
            return false;
        }

        return true;
    }
    
    public boolean addJobTrigger2(Map<String, Object> conditionMap) {
        String jobName = (String)conditionMap.get("jobName");
        String jobClassName = (String)conditionMap.get("jobClassName");
        String jobDescription = (String)conditionMap.get("jobDescription");
        String jobGroup = (String)conditionMap.get("jobGroup");
        String groupType = (String)conditionMap.get("groupType");
        String triggerName = (String)conditionMap.get("triggerName");
        String operator = (String)conditionMap.get("operator");
        Boolean cron = StringUtil.nullToBoolean(conditionMap.get("cron"), false);
        String expression = (String)conditionMap.get("expression");
        Date startTime = (Date)conditionMap.get("startTime");
        Date endTime = (Date)conditionMap.get("endTime");
        Long interval = (Long)conditionMap.get("interval");
        Integer repeatCount = (conditionMap.get("repeatCount") == null ? null : Integer.parseInt(String.valueOf(conditionMap.get("repeatCount"))));
        
        Map<String, Object> subJobData = null;
        if(conditionMap.containsKey("subJobData") && 0 < ((Map<String, Object>) conditionMap.get("subJobData")).size()){
        	subJobData = (Map<String, Object>) conditionMap.get("subJobData");
        }

        try {
            // jobName check
        	JobKey jobKey = new JobKey(jobName);
            JobDetailImpl jobDetailImpl = (JobDetailImpl)this.scheduler.getJobDetail(jobKey);
            boolean isJobAdd = false;

            if (jobDetailImpl == null) {
                isJobAdd = true;
            }
            log.debug("IsJobAdd ? = " + isJobAdd);
            
            if (isJobAdd) {     // Add Job
                Class<?> jobClass = null;
                Object jobObject = null;

                try {
                    jobClass = Class.forName(jobClassName);
                    jobObject = DataUtil.getBean(jobClass);
                } catch(Exception ex){
                	log.error("Class loading error - " + ex.getMessage(), ex);
                }
                // new job
                @SuppressWarnings("unchecked")
				JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) jobObject.getClass());
                jobBuilder.withIdentity(jobKey);
                jobBuilder.withDescription(jobDescription);

                // Job Group setting
                JobDataMap jobDataMap = null;
                if(!StringUtil.nullToBlank(jobGroup).isEmpty()){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("group", new Integer(jobGroup));
                }
                
                // Job GroupType setting
                if(!StringUtil.nullToBlank(jobGroup).isEmpty()){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("groupType", new Integer(jobGroup));
                }
                
                // Sub Job data setting
                if(subJobData != null){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("subJobData", subJobData);
                }
                
            	if(jobDataMap != null && 0 < jobDataMap.size()){
            		jobBuilder.usingJobData(jobDataMap);
            	}
            	
            	jobDetailImpl = (JobDetailImpl)jobBuilder.build();
            } else {
            	JobDataMap jobDataMap = jobDetailImpl.getJobDataMap();
                
            	// Job Group setting
                if(!StringUtil.nullToBlank(jobGroup).isEmpty()){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("group", new Integer(jobGroup));
                }else if(jobDataMap != null){
                	jobDataMap.remove("group");
                }
            	// Job GroupType setting
                if(!StringUtil.nullToBlank(jobGroup).isEmpty()){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("groupType", new Integer(jobGroup));
                }else if(jobDataMap != null){
                	jobDataMap.remove("groupType");
                }
            	// Sub Job data setting
                if(subJobData != null){
                	if(jobDataMap == null){
                		jobDataMap = new JobDataMap();
                	}
                	jobDataMap.put("subJobData", subJobData);
                }else if(jobDataMap != null){
                	jobDataMap.remove("subJobData");
                }
                
                if(jobDataMap != null && 0 < jobDataMap.size()){
                	jobDetailImpl.setJobDataMap(jobDataMap);
                }
            }

            GlobalTriggerListener.operatorMap.put(triggerName, operator);

            TriggerKey triggerKey = new TriggerKey(triggerName);            	

            if (cron) {
                JobDataMap cronTriggerJobDataMap = new JobDataMap();
                cronTriggerJobDataMap.put("group", new Integer(jobGroup)); // group id
                cronTriggerJobDataMap.put("groupType", groupType); // group type

                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                        .usingJobData(cronTriggerJobDataMap)
                        .forJob(jobKey)
                        .build();

                if (isJobAdd) {
                    this.scheduler.scheduleJob(jobDetailImpl, cronTrigger);
                } else {
                    this.scheduler.addJob(jobDetailImpl, true);
                    this.scheduler.scheduleJob(cronTrigger);
                }
            } else {
                JobDataMap simpleTriggerJobDataMap = new JobDataMap();
                if(jobGroup != null && !jobGroup.equals("")){
                    simpleTriggerJobDataMap.put("group", new Integer(jobGroup));     // group id                	
                }
                if(groupType != null && !groupType.equals("")){
                    simpleTriggerJobDataMap.put("groupType", groupType);     // group type                	
                }

                SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                //simpleScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();

                if(repeatCount != null && 1 < repeatCount){
                    simpleScheduleBuilder.withRepeatCount(repeatCount - 1);
                    
                    if(interval != null){
                    	simpleScheduleBuilder.withIntervalInHours(Math.toIntExact(interval));
                        //simpleScheduleBuilder.withIntervalInMilliseconds(interval);              // For test
                        //simpleScheduleBuilder.withIntervalInMinutes(Math.toIntExact(interval));  // For test
                    }
                }
                
                TriggerBuilder<SimpleTrigger> tb = TriggerBuilder.newTrigger()
                		.withIdentity(triggerKey)
                		.withSchedule(simpleScheduleBuilder)
                		.usingJobData(simpleTriggerJobDataMap)
                		.forJob(jobKey);
                
                if(startTime != null){
                	log.debug("Set Start time = " + DateTimeUtil.getDateString(startTime, "yyyy-MM-dd HH:mm:ss"));
                	tb.startAt(startTime);
                }else{
                	tb.startNow();
                }
                
                if(endTime != null){
                	log.debug("Set End time = " + DateTimeUtil.getDateString(endTime, "yyyy-MM-dd HH:mm:ss"));
                	tb.endAt(endTime);
                }
                
                SimpleTrigger simpleTrigger = tb.build();
                if (isJobAdd) {
                    this.scheduler.scheduleJob(jobDetailImpl, simpleTrigger);
                } else {
                    this.scheduler.addJob(jobDetailImpl, true);
                    this.scheduler.scheduleJob(simpleTrigger);
                }
                
                log.info("##### JOB Info :"
                		+ "  JobName=[" + scheduler.getJobDetail(jobKey).getKey().getName() + "]"
                		+ ", JobGroup=[" + scheduler.getJobDetail(jobKey).getKey().getGroup() + "]"
                		+ ", TriggerStartTime=[" + DateTimeUtil.getDateString(scheduler.getTrigger(triggerKey).getStartTime(), "yyyy-MM-dd HH:mm:ss") + "]"
                        + ", TriggerEndTime=[" + DateTimeUtil.getDateString(scheduler.getTrigger(triggerKey).getEndTime(), "yyyy-MM-dd HH:mm:ss") + "]"
                		+ ", TriggerName=[" + scheduler.getTrigger(triggerKey).getKey().getName() + "]"
                		+ ", TriggerGroup=[" + scheduler.getTrigger(triggerKey).getKey().getGroup() + "]"
                		+ ", TriggerInterval=[" + interval + "]"
                		+ ", TriggerCount=[" + repeatCount + "] #####");
                
                log.info("## Scheduler is Started? = " + scheduler.isStarted());
                
                if(!scheduler.isStarted()){
                	scheduler.start();
                }
            }
        } catch (Exception e) {
            log.error("Job, Trigger Setting error - " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    public boolean updateTrigger(String triggerName, long interval, String expression, boolean cron, String operator, String groupType, String jobGroup) {
        Trigger trigger;
        try {
            GlobalTriggerListener.operatorMap.put(triggerName, operator);
            TriggerKey triggerKey = new TriggerKey(triggerName, Scheduler.DEFAULT_GROUP);
            trigger = this.scheduler.getTrigger(triggerKey);

            log.debug("triggerName:"+triggerName);
            log.debug("interval:"+interval);
            log.debug("expression:"+expression);
            log.debug("cron:"+cron);
            log.debug("groupType:"+groupType);
            log.debug("jobGroup:"+jobGroup);

            JobKey jobKey = trigger.getJobKey();
            JobDetailImpl jobDetailImpl = (JobDetailImpl)this.scheduler.getJobDetail(jobKey);
            
            JobDataMap jobDataMap = jobDetailImpl.getJobDataMap();
            JobDataMap triggerJobDataMap = new JobDataMap(); 

            if (!StringUtil.nullToBlank(jobGroup).isEmpty()) {
                if (jobDataMap == null) {
                    jobDataMap = new JobDataMap();
                }
                jobDataMap.put("group", new Integer(jobGroup));     // group id
                jobDetailImpl.setJobDataMap(jobDataMap);

                triggerJobDataMap.put("group", new Integer(jobGroup));     // group id
                triggerJobDataMap.put("groupType", groupType);     // group id
            } else {
                if (jobDataMap != null) {
                    jobDataMap.remove("group");
                    jobDetailImpl.setJobDataMap(jobDataMap);
                }
            }

            long ctime = TimeUtil.getCurrentLongTime();
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger newTrigger = (SimpleTrigger) trigger;
                log.debug("SimpleTrigger:"+newTrigger);

                if (cron) {
                    CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(triggerKey)
                            .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                            .usingJobData(triggerJobDataMap)
                            .forJob(jobKey)
                            .build();
                    
                    this.scheduler.scheduleJob(jobDetailImpl, cronTrigger);
                } else {
                    this.scheduler.addJob(jobDetailImpl, true);
                    SimpleTriggerImpl simpleTriggerImpl = (SimpleTriggerImpl)newTrigger;
                    simpleTriggerImpl.setRepeatInterval(interval);
                    simpleTriggerImpl.setJobDataMap(triggerJobDataMap);

                    this.scheduler.rescheduleJob(triggerKey, simpleTriggerImpl);
                }
            } else {
                CronTrigger newTrigger = (CronTrigger) trigger;
                
                if (cron) {
                    this.scheduler.addJob(jobDetailImpl, true);
                    CronTriggerImpl cronTriggerImpl = (CronTriggerImpl)newTrigger;
                    cronTriggerImpl.setCronExpression(expression);
                    cronTriggerImpl.setJobDataMap(triggerJobDataMap);
                    this.scheduler.rescheduleJob(triggerKey, cronTriggerImpl);
                } else {
                    SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                    simpleScheduleBuilder.withIntervalInMilliseconds(interval);
                    simpleScheduleBuilder.withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
                
                    SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(triggerKey)
                            .withSchedule(simpleScheduleBuilder)
                            .usingJobData(triggerJobDataMap)
                            .startAt(new Date(ctime))
                            .forJob(jobKey)
                            .build();

                    this.scheduler.scheduleJob(jobDetailImpl, simpleTrigger);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
            return false;
        }

        return true;
    }

	public boolean deleteTrigger(String triggerName) {
		boolean result = false;

		try {
		    TriggerKey triggerKey = new TriggerKey(triggerName);
			result = this.scheduler.unscheduleJob(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e, e);
			return false;
		}
		return result;
	}

	public boolean pauseTrigger(String triggerName) {
		boolean result = true;

		try {
		    TriggerKey triggerKey = new TriggerKey(triggerName);
			this.scheduler.pauseTrigger(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e, e);
			return false;
		}
		return result;
	}

	public boolean resumTrigger(String triggerName,String operator) {
		boolean result = true;

		try {
			GlobalTriggerListener.operatorMap.put(triggerName, operator);
			TriggerKey triggerKey = new TriggerKey(triggerName);			
			this.scheduler.resumeTrigger(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e, e);
			return false;
		}
		return result;
	}

    public boolean directRunJob(String jobName, String operator) {
        try {
            JobKey jobKey = new JobKey(jobName);
            this.scheduler.triggerJob(jobKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
            return false;
        }

        return true;
    }
    
    public Map<String, Object> getTriggerDataMap(String triggerName, String operator) {
        Trigger trigger;
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            TriggerKey triggerKey = new TriggerKey(triggerName);
            trigger = this.scheduler.getTrigger(triggerKey);
            JobDataMap jobDataMap = trigger.getJobDataMap();

            map.put("group",(Integer)jobDataMap.get("group"));
            map.put("groupType",(String)jobDataMap.get("groupType"));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        }
        return map;
    }

    @Override
    public boolean updateTrigger(String triggerName, long interval,
            String expression, boolean cron, String operator, String groupType,
            String jobGroup, Date startTime, Date endTime, String repeatCnt) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateTrigger(String triggerName, long interval,
            String expression, boolean cron, String operator, String groupType,
            String jobGroup, HashMap<String, String> subJobDataToMap,
            Date startTime, Date endTime, String repeatCnt) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean directRunJob(String jobName, String operator,
            String triggerName) {
        // TODO Auto-generated method stub
        return false;
    }
}