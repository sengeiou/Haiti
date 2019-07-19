package com.aimir.bo.system.schedule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.adapter.ScheduleOperationUtil;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.ScheduleMgmtManager;
import com.aimir.service.system.ScheduleResultLogManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;

@Controller
public class ScheduleController {

    protected static Log log = LogFactory.getLog(ScheduleController.class);

    @Autowired
    ScheduleResultLogManager scheduleResultLogManager;

    @Autowired
    ScheduleMgmtManager scheduleMgmtManager;

    @Autowired
    RoleManager roleManager;

    @Autowired
    SupplierManager supplierManager;

    @RequestMapping(value = "/gadget/system/schedule/scheduleManagementMiniGadget")
    public ModelAndView getScheduleManagementMiniGadget() {
        ModelAndView mav = new ModelAndView(
                "gadget/system/scheduleManagementMiniGadget");
        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/scheduleManagementMaxGadget")
    public ModelAndView getScheduleManagementMaxGadget() {
        ModelAndView mav = new ModelAndView("gadget/system/scheduleManagementMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/getJobDetailList")
    public ModelAndView getJobDetailList() {

        ModelAndView mav = new ModelAndView("jsonView");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        List<Map<String, Object>> jobDetailNames = null;
        try {
            jobDetailNames = ScheduleOperationUtil.getJobDetailList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(jobDetailNames != null){
            Map<String, Object> jobStatistic = new HashMap<String, Object>();
            jobStatistic.put("BLOCKED", 0);
            jobStatistic.put("COMPLETE", 0);
            jobStatistic.put("ERROR", 0);
            jobStatistic.put("NONE", 0);
            jobStatistic.put("NORMAL", 0);
            jobStatistic.put("PAUSED", 0);
            jobStatistic.put("TOTAL", 0);

        	Supplier supplier = supplierManager.getSupplier(supplierId);
        	DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

            for (int i = 0; i < jobDetailNames.size(); i++) {
                Map<String, Object> jobDetail = jobDetailNames.get(i);

                setJobStatic(jobStatistic, "BLOCKED", (Integer) jobDetail
                        .get("BLOCKED"));
                setJobStatic(jobStatistic, "COMPLETE", (Integer) jobDetail
                        .get("COMPLETE"));
                setJobStatic(jobStatistic, "ERROR", (Integer) jobDetail.get("ERROR"));
                setJobStatic(jobStatistic, "NONE", (Integer) jobDetail.get("NONE"));
                setJobStatic(jobStatistic, "NORMAL", (Integer) jobDetail
                        .get("NORMAL"));
                setJobStatic(jobStatistic, "PAUSED", (Integer) jobDetail
                        .get("PAUSED"));
                setJobStatic(jobStatistic, "TOTAL", (Integer) jobDetail.get("TOTAL"));

                jobDetail.put("BLOCKED", dfMd.format(jobDetail.get("BLOCKED")));
                jobDetail.put("COMPLETE", dfMd.format(jobDetail.get("COMPLETE")));
                jobDetail.put("ERROR", dfMd.format(jobDetail.get("ERROR")));
                jobDetail.put("NONE", dfMd.format(jobDetail.get("NONE")));
                jobDetail.put("NORMAL", dfMd.format(jobDetail.get("NORMAL")));
                jobDetail.put("PAUSED", dfMd.format(jobDetail.get("PAUSED")));
                jobDetail.put("TOTAL", dfMd.format(jobDetail.get("TOTAL")));
            }

        	for (int i = 0; i < jobDetailNames.size(); i++) {
        		jobStatistic.put("BLOCKED", dfMd.format(Double.parseDouble(jobStatistic.get("BLOCKED").toString())));
        		jobStatistic.put("COMPLETE", dfMd.format(Double.parseDouble(jobStatistic.get("COMPLETE").toString())));
        		jobStatistic.put("ERROR", dfMd.format(Double.parseDouble(jobStatistic.get("ERROR").toString())));
        		jobStatistic.put("NONE", dfMd.format(Double.parseDouble(jobStatistic.get("NONE").toString())));
        		jobStatistic.put("NORMAL", dfMd.format(Double.parseDouble(jobStatistic.get("NORMAL").toString())));
        		jobStatistic.put("PAUSED", dfMd.format(Double.parseDouble(jobStatistic.get("PAUSED").toString())));
        		jobStatistic.put("TOTAL", dfMd.format(Double.parseDouble(jobStatistic.get("TOTAL").toString())));
    		}

            mav.addObject("result", jobDetailNames);
            List<Object> jobStatisticList = new ArrayList<Object>();
            jobStatisticList.add(jobStatistic);
            mav.addObject("statistic", jobStatisticList);
        }else{
        	log.debug("jobDetailNames is null !!");
        	mav.addObject("result", "");
        	mav.addObject("statistic", "");
        }


        return mav;
    }

    private void setJobStatic(Map<String, Object> jobStatistic, String key,
            int state) {
        int jobState = (Integer) jobStatistic.get(key);
        jobStatistic.put(key, (jobState + state));

    }

    @RequestMapping(value = "/gadget/system/schedule/getTriggerOfJob")
    public ModelAndView getTriggerOfJob(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("jobName") String jobName) {
        ModelAndView mav = new ModelAndView("jsonView");

        /**
         * supplier 정보를 읽어와 날짜 패턴을 구한다.
         */
        String datePattern = "yyyyMMddHHmmss"; //기본 패턴 설정.
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Locale locale = null;

        if (user != null && !user.isAnonymous()) {
            Supplier supplier = user.getRoleData().getSupplier();
            String lang = supplier.getLang().getCode_2letter();
            String country = supplier.getCountry().getCode_2letter();
            locale = new Locale(lang, country);
//            datePattern = TimeLocaleUtil.getDateFormat(12, lang, country);
            datePattern = TimeLocaleUtil.getDateFormat(14, lang, country);
        }

        // 데이터 조회 및 json 객체 생성.
        List<Map<String, Object>> triggerDetailNames = null;

        try {
            triggerDetailNames = ScheduleOperationUtil.getTriggerOfJob(jobName, datePattern, locale);
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("result", "");
            return mav;
        }

        Map<String, Object> conditionMap = null;
        Map<String, Object> resultLog = null;

        for (Map<String, Object> triggerDetail : triggerDetailNames) {
            conditionMap = new HashMap<String, Object>();
            conditionMap.put("jobName", jobName);
            conditionMap.put("triggerName", triggerDetail.get("name"));
            resultLog = scheduleResultLogManager.getLatestScheduleResultLogByJobTrigger(conditionMap);

            triggerDetail.put("operator", resultLog.get("OPERATOR"));
            triggerDetail.put("result", resultLog.get("RESULT"));
        }
        mav.addObject("result", triggerDetailNames);
        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/updateTrigger")
    public ModelAndView updateTrigger(
            @RequestParam("triggerName") String triggerName,
            @RequestParam("repeatInterval") Long repeatInterval,
            @RequestParam("expression") String expression,
            @RequestParam("cron") boolean cron,
            @RequestParam("groupType") String groupType,
            @RequestParam("jobGroup") String jobGroup,
            @RequestParam("startTimeArr[]") String[] startTimeArr,
            @RequestParam("endTimeArr[]") String[] endTimeArr,
            @RequestParam("repeatCount") String repeatCount,
            @RequestParam("loginId") String loginId,
            @RequestParam(value = "subJobData", required = false) String subJobData) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean updateResult = false;
        Date startTime = null;
        Date endTime = null;
        if(!"".equals(startTimeArr[0])) {
        	 startTime = new Date(Integer.parseInt(startTimeArr[0])-1900, Integer.parseInt(startTimeArr[1])-1,
        			Integer.parseInt(startTimeArr[2]), Integer.parseInt(startTimeArr[3]), Integer.parseInt(startTimeArr[4]));
        }

        if(!"".equals(endTimeArr[0])) {
        	endTime = new Date(Integer.parseInt(endTimeArr[0])-1900, Integer.parseInt(endTimeArr[1])-1,
        			Integer.parseInt(endTimeArr[2]), Integer.parseInt(endTimeArr[3]), Integer.parseInt(endTimeArr[4]));
        }

        try {
            if (repeatInterval == null) {
                repeatInterval = 0L;
            }

            if(subJobData != null && !subJobData.equals("")){
                updateResult = ScheduleOperationUtil.updateTrigger(triggerName, repeatInterval, expression, cron, loginId, groupType, jobGroup, getSubJobDataToMap(subJobData), startTime, endTime, repeatCount);
            } else {
                updateResult = ScheduleOperationUtil.updateTrigger(triggerName, repeatInterval, expression, cron, loginId, groupType, jobGroup, startTime, endTime, repeatCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", updateResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/addTrigger")
    public ModelAndView addTrigger(
            @RequestParam("jobName") String jobName,
            @RequestParam("triggerName") String triggerName,
            @RequestParam("repeatInterval") Long repeatInterval,
            @RequestParam("expression") String expression,
            @RequestParam("cron") boolean cron,
            @RequestParam("loginId") String loginId) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean addResult = false;

        try {
            if (repeatInterval == null) {
                repeatInterval = 0L;
            }
            addResult = ScheduleOperationUtil.addTrigger(jobName,triggerName, repeatInterval,expression,cron,loginId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", addResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/deleteTrigger")
    public ModelAndView deleteTrigger(
            @RequestParam("triggerName") String triggerName) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean deleteResult = false;

        try {
            deleteResult = ScheduleOperationUtil.deleteTrigger(triggerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", deleteResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/addJobTrigger")
    public ModelAndView addJobTrigger(
            @RequestParam("jobName") String jobName,
            @RequestParam("jobClassName") String jobClassName,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("groupType") String groupType,
            @RequestParam("jobGroup") String jobGroup,
            @RequestParam("triggerName") String triggerName,
            @RequestParam("repeatInterval") Long repeatInterval,
            @RequestParam("expression") String expression,
            @RequestParam("startTimeArr[]") String[] startTimeArr,
            @RequestParam("endTimeArr[]") String[] endTimeArr,
            @RequestParam("repeatCount") String repeatCount,
            @RequestParam("cron") boolean cron,
            @RequestParam("loginId") String loginId,
            @RequestParam(value = "subJobData", required = false) String subJobData){

        ModelAndView mav = new ModelAndView("jsonView");
        boolean addResult = false;
        Date startTime = null;
        Date endTime = null;
        if(!"".equals(startTimeArr[0])) {
        	 startTime = new Date(Integer.parseInt(startTimeArr[0])-1900, Integer.parseInt(startTimeArr[1])-1,
        			Integer.parseInt(startTimeArr[2]), Integer.parseInt(startTimeArr[3]), Integer.parseInt(startTimeArr[4]));
        }

        if(!"".equals(endTimeArr[0])) {
        	endTime = new Date(Integer.parseInt(endTimeArr[0])-1900, Integer.parseInt(endTimeArr[1])-1,
        			Integer.parseInt(endTimeArr[2]), Integer.parseInt(endTimeArr[3]), Integer.parseInt(endTimeArr[4]));
        }
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("jobName", jobName);
        conditionMap.put("jobClassName", jobClassName);
        conditionMap.put("jobDescription", jobDescription);
        conditionMap.put("jobGroup", jobGroup);
        conditionMap.put("groupType", groupType);
        conditionMap.put("triggerName", triggerName);
        if (repeatInterval == null) {
            repeatInterval = 0L;
        }
        conditionMap.put("interval", repeatInterval);
        conditionMap.put("repeatCount", repeatCount);
        conditionMap.put("startTime", startTime);
        conditionMap.put("endTime", endTime);
        conditionMap.put("expression", expression);
        conditionMap.put("cron", cron);
        conditionMap.put("loginId", loginId);

        if(subJobData != null && !subJobData.equals("")){
            conditionMap.put("subJobData", getSubJobDataToMap(subJobData));
        }

        try {
            //addResult = ScheduleOperationUtil.addJobTrigger(conditionMap);
        	  addResult = ScheduleOperationUtil.addJobTrigger2(conditionMap);        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", addResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/getScheduleResultLogByJobName")
    public ModelAndView getScheduleResultLogByJobName(
            @RequestParam("supplierId") String supplierId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("jobName") String jobName,
            @RequestParam("triggerName") String triggerName,
            @RequestParam("result") Integer result) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("jobName", jobName);
        conditionMap.put("triggerName", triggerName);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("result", result);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Map<String, Object>> resultList = scheduleResultLogManager.getScheduleResultLogByJobName(conditionMap);
        mav.addObject("total", scheduleResultLogManager.getScheduleResultLogByJobNameCount(conditionMap));
        mav.addObject("logList", resultList);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/getScheduleResultLogByJobNameCount")
    public ModelAndView getScheduleResultLogByJobNameCount(
            @RequestParam("supplierId") String supplierId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("jobName") String jobName,
            @RequestParam("triggerName") String triggerName,
            @RequestParam("result") String result) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("jobName", jobName);
        conditionMap.put("triggerName", triggerName);

        if (result != null && !result.isEmpty()) {
            conditionMap.put("result", new Integer(result));
        } else {
            conditionMap.put("result", null);
        }

        mav.addObject("total", scheduleResultLogManager.getScheduleResultLogByJobNameCount(conditionMap));
        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/pauseTrigger")
    public ModelAndView pauseTrigger(
            @RequestParam("triggerName") String triggerName,
            @RequestParam("loginId") String loginId) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean pauseResult = false;
        try {

            pauseResult = ScheduleOperationUtil.pauseTrigger(triggerName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mav.addObject("result", pauseResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/resumTrigger")
    public ModelAndView resumTrigger(
            @RequestParam("triggerName") String triggerName,
            @RequestParam("loginId") String loginId) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean resumResult = false;
        try {
            resumResult = ScheduleOperationUtil.resumTrigger(triggerName,loginId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", resumResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/validCronExpression")
    public ModelAndView validCronExpression(
            @RequestParam("cronExpression") String cronExpression) {

        ModelAndView mav = new ModelAndView("jsonView");

        boolean valid= CronExpression.isValidExpression(cronExpression);
        // System.out.println("jobDetailNames:::"+jobDetailNames);
        mav.addObject("result", valid);

        return mav;
    }

    /**
     * method name : getGroupComboDataByGroupType<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 GroupType 의 Group Combo Data 를 조회한다.
     *
     * @param groupType
     * @return
     */
    @RequestMapping(value = "/gadget/system/schedule/getGroupComboDataByGroupType")
    public ModelAndView getGroupComboDataByGroupType(@RequestParam("groupType") String groupType) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("groupType", groupType);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = null;

        if(user != null && !user.isAnonymous()) {
            try {
                operatorId = user.getOperator(new Operator()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        conditionMap.put("operatorId", operatorId);

        List<Map<String, Object>> result = scheduleMgmtManager.getGroupComboDataByType(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getScheduleResultComboData<b/>
     * method Desc : Task Management 맥스가젯에서 Result Combo Data 를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/schedule/getScheduleResultComboData")
    public ModelAndView getScheduleResultComboData() {

        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        for (ResultStatus constant : ResultStatus.values()) {
            map = new HashMap<String, Object>();
            map.put("id", constant.getCode());
            map.put("name", constant.name());
            result.add(map);
        }

        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getGroupTypeByGroup<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 Job 의 Group Type 을 조회한다.
     *
     * @param groupType
     * @return
     */
    @RequestMapping(value = "/gadget/system/schedule/getGroupTypeByGroup")
    public ModelAndView getGroupTypeByGroup(@RequestParam("group") String group) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", group);

        String result = scheduleMgmtManager.getGroupTypeByGroup(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/directRunJob")
    public ModelAndView directRunJob(
            @RequestParam("jobName") String jobName,
            @RequestParam("loginId") String loginId,
            @RequestParam(value = "triggerName", required = false) String triggerName) {

        ModelAndView mav = new ModelAndView("jsonView");
        boolean runResult = false;

        try {
            runResult = ScheduleOperationUtil.directRunJob(jobName, loginId,  triggerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("result", runResult);

        return mav;
    }

    @RequestMapping(value = "/gadget/system/schedule/getTriggerDataMap")
    public ModelAndView getTriggerDataMap(
            @RequestParam("jobName") String jobName,
            @RequestParam("loginId") String loginId) {

        ModelAndView mav = new ModelAndView("jsonView");
        try {
            Map<String, Object> map = ScheduleOperationUtil.getTriggerDataMap(jobName, loginId);
            mav.addObject("groupId", map.get("group"));
            mav.addObject("groupType", map.get("groupType"));

            if(map.containsKey("subJobData")){
                HashMap<String, String> str = (HashMap<String, String>)map.get("subJobData");
                mav.addObject("subJobData", str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    /**
     * JSON 배열로 넘어온 데이터를 Map으로 변환후 리턴
     * @param subJobData JSON형태의 String 데이터
     * @return
     */
    private HashMap<String, String> getSubJobDataToMap(String subJobData){
        HashMap<String, String> subJobDataMap = new HashMap<String, String>();

        subJobData.trim();
        try {
            if(subJobData != null && !subJobData.equals("")){
                JSONArray ja = new JSONArray(subJobData);
                for(int i=0; i<ja.length(); i++){
                    JSONObject jObj = ja.getJSONObject(i);

                    @SuppressWarnings("unchecked")
                    Iterator<String> keys = jObj.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        subJobDataMap.put(key, (String) jObj.get(key));
                    }
                }
            }else {
                subJobDataMap = null;
            }
        } catch (JSONException e) {
            log.debug("JSON Converting Error - " + e.getMessage());
        }
        return subJobDataMap;
    }
}