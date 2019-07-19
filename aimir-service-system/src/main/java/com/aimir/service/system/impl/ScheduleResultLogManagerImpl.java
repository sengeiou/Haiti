package com.aimir.service.system.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ScheduleResultLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ScheduleResultLogManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.system.ScheduleResultLogManager")
@Service(value="scheduleResultLogManager")
@Transactional
public class ScheduleResultLogManagerImpl implements ScheduleResultLogManager {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(ScheduleResultLogManagerImpl.class);

    @Autowired
    ScheduleResultLogDao scheduleResultLogDao;

    @Autowired
    SupplierDao supplierDao;

    public List<Map<String, Object>> getScheduleResultLogByJobName(Map<String, Object> conditionMap) {

        String supplierId  = (String)conditionMap.get("supplierId");
        String startDate   = (String)conditionMap.get("startDate");
        String endDate     = (String)conditionMap.get("endDate");

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        conditionMap.put("searchStartDate", TimeLocaleUtil.getDBDate(startDate, 8, lang, country));
        conditionMap.put("searchEndDate", TimeLocaleUtil.getDBDate(endDate, 8, lang, country));

        List<Map<String, Object>> result = scheduleResultLogDao.getScheduleResultLogByJobName(conditionMap, false);

        for (Map<String, Object> obj : result) {
            if (obj.get("responseTime") != null) {
//                try {
//                    obj.put("responseTime", TimeLocaleUtil.getLocaleDate(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)obj.get("responseTime")), 14, lang, country));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                obj.put("responseTime", TimeLocaleUtil.getLocaleDate((String)obj.get("responseTime"), lang, country));
            } else {
                obj.put("responseTime", "");
            }

            if (obj.get("createTime") != null) {
//                try {
//                    obj.put("createTime", TimeLocaleUtil.getLocaleDate(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)obj.get("createTime")), 14, lang, country));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                obj.put("createTime", TimeLocaleUtil.getLocaleDate((String)obj.get("createTime"), lang, country));
            } else {
                obj.put("createTime", "");
            }
        }
        return result;
    }

//    public String getScheduleResultLogByJobNameCount(String supplierId,String startDate,String endDate,String jobName,String triggerName,Integer result) {
//        Set<Condition> conditions = new HashSet<Condition>(0);
//
//        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
//        String searchStartDate= TimeLocaleUtil.getDBDate(startDate, 8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
//        String searchEndDate= TimeLocaleUtil.getDBDate(endDate, 8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
//
//        if (result != -1) {
//            ResultStatus resultStatus =  null;
////          if(ResultStatus.SUCCESS.getIntValue()== result){
////              resultStatus=ResultStatus.SUCCESS ;
////          }else if(ResultStatus.FAIL.getIntValue()== result){
////              resultStatus=ResultStatus.FAIL ;
////          }else if(ResultStatus.INVALID_PARAMETER.getIntValue()== result){
////              resultStatus=ResultStatus.INVALID_PARAMETER ;
////          }else if(ResultStatus.COMMUNICATION_FAIL.getIntValue()== result){
////              resultStatus=ResultStatus.COMMUNICATION_FAIL ;
////          }
//
//            for (ResultStatus constant : ResultStatus.values()) {
//                if (constant.getCode().equals(result)) {
//                    resultStatus = constant;
//                    break;
//                }
//            }
//
//            conditions.add(new Condition("result", new Object[] { resultStatus}, null, Restriction.EQ));
//        }
//
//        if(triggerName != null && triggerName.length()>0 && (!triggerName.equalsIgnoreCase("null"))){
//            conditions.add(new Condition("triggerName", new Object[] { triggerName }, null, Restriction.EQ));
//        }
//
//        conditions.add(new Condition("endDate", new Object[] { searchStartDate, searchEndDate }, null, Restriction.BETWEEN));
//        conditions.add(new Condition("jobName", new Object[] { jobName }, null, Restriction.EQ));
//
//        List<ScheduleResultLog> scheduleResultLogList = scheduleResultLogDao.findByConditions(conditions);
//
//        return String.valueOf(scheduleResultLogList.size());
//    }

    public Integer getScheduleResultLogByJobNameCount(Map<String, Object> conditionMap) {
        Integer count = 0;
        String supplierId  = (String)conditionMap.get("supplierId");
        String startDate   = (String)conditionMap.get("startDate");
        String endDate     = (String)conditionMap.get("endDate");

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        conditionMap.put("searchStartDate", TimeLocaleUtil.getDBDate(startDate, 8, lang, country));
        conditionMap.put("searchEndDate", TimeLocaleUtil.getDBDate(endDate, 8, lang, country));

        List<Map<String, Object>> result = scheduleResultLogDao.getScheduleResultLogByJobName(conditionMap, true);

        if (result != null && result.size() > 0 &&result.get(0).get("total") != null) {
            count = (Integer)(result.get(0).get("total"));
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public Map<String, Object> getLatestScheduleResultLogByTrigger(String triggerName){
        List<Object> resultLogList = scheduleResultLogDao.getLatestScheduleResultLogByTrigger(triggerName);
        Map<String, Object> resultLogMap = new HashMap<String, Object>();

        if (resultLogList != null && resultLogList.size() > 0) {
            resultLogMap = (Map<String, Object>) resultLogList.get(0);
        }

        return resultLogMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getLatestScheduleResultLogByJobTrigger(Map<String, Object> conditionMap){
        List<Object> resultLogList = scheduleResultLogDao.getLatestScheduleResultLogByJobTrigger(conditionMap);
        Map<String, Object> resultLogMap = new HashMap<String, Object>();

        if (resultLogList != null && resultLogList.size() > 0) {
            resultLogMap = (Map<String, Object>) resultLogList.get(0);
        }

        return resultLogMap;
    }
}