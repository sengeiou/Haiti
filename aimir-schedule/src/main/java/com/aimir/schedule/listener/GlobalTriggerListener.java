package com.aimir.schedule.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.ScheduleJobErrorMsg;
import com.aimir.constants.CommonConstants.TriggerType;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.ScheduleResultLogDao;
import com.aimir.model.system.ScheduleResultLog;
import com.aimir.util.TimeUtil;

@Transactional
public class GlobalTriggerListener implements TriggerListener {
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(GlobalTriggerListener.class);

    @Autowired
    ScheduleResultLogDao scheduleResultLogDao;

    @Autowired
    GroupDao groupDao;

    String listenerType = "Non global";

    public static Map<String,String> operatorMap = new HashMap<String,String>();

    public void setListenerType(String listenerType) {
        this.listenerType = listenerType;
    }

    private String dateNullCheck(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        if (date != null) {
            return format.format(date.getTime());
        } else {
            return "0";
        }
    }

    public String getName() {
        return "GlobalTriggerListener";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void triggerComplete(Trigger arg0, JobExecutionContext arg1, CompletedExecutionInstruction arg2) {
    	log.debug("Trigger Complete ~! - " + arg0.getKey().getName());
        JobDetail jobDetail = arg1.getJobDetail();
        Map<String, Object> scheduleResult = (Map<String, Object>)arg1.get("scheduleResult");
        OperatorType opType = null;
        TriggerType tType = null;
        String result = null;
        String errorMessage = null;
        String groupType = null;
        String operator = null;
        String cronExp = null;
        Integer repeatCount = null;
        Long repeatInterval = null;
        Integer groupId = null;

        if (arg0.getJobDataMap().containsKey("group")) {
            groupId = (Integer)arg0.getJobDataMap().get("group");
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            conditionMap.put("groupId", groupId.toString());
            // get groupType
            groupType = groupDao.getGroupTypeByGroup(conditionMap);
        }

        if (scheduleResult != null && scheduleResult.get("result") != null) {
            result = (String)scheduleResult.get("result");
            errorMessage = (String)scheduleResult.get("errorMessage");
        } else {
            result = ResultStatus.FAIL.name();
            errorMessage = ScheduleJobErrorMsg.JOB_EXECUTE_ERROR.getMessage();    // TODO - 메세지 보완
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        if (arg0 instanceof SimpleTrigger) {
            tType = TriggerType.Simple;
            repeatCount = ((SimpleTrigger)arg0).getRepeatCount();
            repeatInterval = ((SimpleTrigger)arg0).getRepeatInterval();
        } else {
            tType = TriggerType.Cron;
            cronExp = ((CronTrigger)arg0).getCronExpression();
        }

        operator = operatorMap.get(arg0.getKey().getName());

        if (operator != null && operator.length() > 0) {
            opType = OperatorType.OPERATOR;
        } else {
            opType = OperatorType.SYSTEM;
            operator = OperatorType.SYSTEM.name();
        }

        long ctime = TimeUtil.getCurrentLongTime();
        String createTime = dateNullCheck(new Date(ctime));
        long fTime = arg1.getFireTime().getTime();
        long responseTime = fTime + arg1.getJobRunTime();
//        log.debug("fTime:" + fTime + ":arg1.getJobRunTime():" + arg1.getJobRunTime() + ":responseTime:" + responseTime);
        ScheduleResultLog srl = new ScheduleResultLog();

        srl.setCreateTime(createTime);  // 현재시간
        srl.setResponseTime(dateNullCheck(new Date(responseTime))); // fireTime + jobRunTime
        srl.setJobName(jobDetail.getKey().getName());
        srl.setTriggerName(arg0.getKey().getName());
        srl.setOperatorType(opType.name());
        srl.setOperator(operator);
        srl.setCommandParameter((groupId != null) ? groupId.toString() : null);
        srl.setTargetType(groupType);
        srl.setTriggerType(tType.name());
        srl.setStartDate(format.format(new Date(fTime)));   // fireTime
        srl.setEndDate(format.format(new Date(responseTime)));

        if (tType == TriggerType.Simple) {
            srl.setRepeatCount(repeatCount);
            srl.setRepeatInterval(repeatInterval);
        } else {
            srl.setCronExp(cronExp);
        }

        srl.setNextFired(dateNullCheck(arg0.getNextFireTime()));
        srl.setResult(result);
        srl.setErrorMessage(errorMessage);

//        scheduleResultLogDao.saveOrUpdate(srl);
        scheduleResultLogDao.add(srl);
    }

    public void triggerFired(Trigger arg0, JobExecutionContext arg1) {
    	log.debug("Trigger Fired ~! - " + arg0.getKey().getName());    	
    }

    public void triggerMisfired(Trigger arg0) {
        log.debug("################# Misfired Log Start ["+ arg0.getKey().getName() +"] ###########################");
        OperatorType opType = null;
        TriggerType tType = null;
        String result = null;
        String errorMessage = null;
        String groupType = null;
        String operator = null;
        String cronExp = null;
        Integer repeatCount = null;
        Long repeatInterval = null;
        Integer groupId = null;

        result = ResultStatus.FAIL.name();
        errorMessage = ScheduleJobErrorMsg.TRIGGER_MISFIRED.getMessage();   // TODO - 메세지 보완

        if (arg0 instanceof SimpleTrigger) {
            tType = TriggerType.Simple;
            repeatCount = ((SimpleTrigger)arg0).getRepeatCount();
            repeatInterval = ((SimpleTrigger)arg0).getRepeatInterval();
        } else {
            tType = TriggerType.Cron;
            cronExp = ((CronTrigger)arg0).getCronExpression();
        }
        operator =operatorMap.get(arg0.getKey().getName());

        if (operator != null && operator.length() > 0) {
            opType = OperatorType.OPERATOR;
        } else {
            opType = OperatorType.SYSTEM;
            operator = OperatorType.SYSTEM.name();
        }

        long ctime = TimeUtil.getCurrentLongTime();
        String createTime = dateNullCheck(new Date(ctime));
        ScheduleResultLog srl = new ScheduleResultLog();

        srl.setCreateTime(createTime);  // 현재시간
        srl.setJobName(arg0.getJobKey().getName());
        srl.setTriggerName(arg0.getKey().getName());
        srl.setOperatorType(opType.name());
        srl.setOperator(operator);
        srl.setCommandParameter((groupId != null) ? groupId.toString() : null);
        srl.setTargetType(groupType);
        srl.setTriggerType(tType.name());

        if (tType == TriggerType.Simple) {
            srl.setRepeatCount(repeatCount);
            srl.setRepeatInterval(repeatInterval);
        } else {
            srl.setCronExp(cronExp);
        }

        srl.setNextFired(dateNullCheck(arg0.getNextFireTime()));
        srl.setResult(result);
        srl.setErrorMessage(errorMessage);

        scheduleResultLogDao.add(srl);
    }

    public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
        return false;
    }
}