package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ScheduleResultLogDao;
import com.aimir.model.system.ScheduleResultLog;
import com.aimir.util.StringUtil;

@Repository(value = "scheduleResultLogDao")
public class ScheduleResultLogDaoImpl extends
        AbstractHibernateGenericDao<ScheduleResultLog, Long> implements
        ScheduleResultLogDao {

    @Autowired
    protected ScheduleResultLogDaoImpl(SessionFactory sessionFactory) {
        super(ScheduleResultLog.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Object> getLatestScheduleResultLogByTrigger(String triggerName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT operator as OPERATOR, ");
        sb.append("\n       result as RESULT ");
        sb.append("\nFROM ScheduleResultLog ");
        sb.append("\nWHERE triggerName = :triggerName ");
        sb.append("\nAND   createTime = (SELECT MAX(createTime) ");
        sb.append("\n                    FROM ScheduleResultLog ");
        sb.append("\n                    WHERE triggerName = :triggerName) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("triggerName", triggerName);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @SuppressWarnings("unchecked")
    public List<Object> getLatestScheduleResultLogByJobTrigger(Map<String, Object> conditionMap) {
        String jobName = StringUtil.nullToBlank(conditionMap.get("jobName"));
        String triggerName = StringUtil.nullToBlank(conditionMap.get("triggerName"));

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT operator AS OPERATOR, ");
        sb.append("\n       result AS RESULT ");
        sb.append("\nFROM ScheduleResultLog ");
        sb.append("\nWHERE jobName = :jobName ");
        sb.append("\nAND   triggerName = :triggerName ");
        sb.append("\nAND   createTime = (SELECT MAX(createTime) ");
        sb.append("\n                    FROM ScheduleResultLog ");
        sb.append("\n                    WHERE jobName = :jobName ");
        sb.append("\n                    AND   triggerName = :triggerName) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("jobName", jobName);
        query.setString("triggerName", triggerName);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getScheduleResultLogByJobName<b/>
     * method Desc : TaskManagement 맥스가젯에서 스케줄러 실행 결과로그를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getScheduleResultLogByJobName(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String jobName = (String)conditionMap.get("jobName");
        String triggerName = (String)conditionMap.get("triggerName");
        Integer result = (Integer)conditionMap.get("result");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT log.responseTime AS responseTime, ");
            sb.append("\n       log.createTime AS createTime, ");
            sb.append("\n       log.jobName AS jobName, ");
            sb.append("\n       log.triggerName AS triggerName, ");
            sb.append("\n       log.result AS result, ");
            sb.append("\n       log.errorMessage AS errorMessage ");
        }
        sb.append("\nFROM ScheduleResultLog log ");
        sb.append("\nWHERE log.jobName = :jobName ");
        sb.append("\nAND   log.createTime BETWEEN :searchStartDate AND :searchEndDate ");

        if (!triggerName.isEmpty()) {
            sb.append("\nAND   log.triggerName = :triggerName ");
        }

        if (result != null) {
            sb.append("\nAND   result = :result ");
        }

        if (!isTotal) {
            sb.append("\nORDER BY log.createTime DESC ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("jobName", jobName);
        query.setString("searchStartDate", searchStartDate + "000000");
        query.setString("searchEndDate", searchEndDate + "235959");

        if (!triggerName.isEmpty()) {
            query.setString("triggerName", triggerName);
        }

        if (result != null) {
            ResultStatus resultStatus =  null;

            for (ResultStatus constant : ResultStatus.values()) {
                if (constant.getCode().equals(result)) {
                    resultStatus = constant;
                    break;
                }
            }

            query.setString("result", resultStatus.name());
        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
//            int count = 0;
//            for (Iterator<?> iterator = query.iterate(); iterator.hasNext();) {
//                iterator.next();
//                count++;
//            }
            Integer count = ((Number) query.uniqueResult()).intValue();

            map.put("total", count);
            resultList = new ArrayList<Map<String, Object>>();
            resultList.add(map);
        }else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return resultList;
    }
}