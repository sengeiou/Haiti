package com.aimir.dao.system.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.AuditLogDao;
import com.aimir.model.system.AuditLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

@Repository(value = "auditLogDao")
public class AuditLogDaoImpl extends AbstractHibernateGenericDao<AuditLog, Integer> implements
        AuditLogDao {

    @Autowired
    protected AuditLogDaoImpl(SessionFactory sessionFactory) {
        super(AuditLog.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * method name : getAuditLogRankingList
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAuditLogRankingList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Map<String, Object> map;
        Calendar startDate = (Calendar)conditionMap.get("startDate");
        Calendar endDate = (Calendar)conditionMap.get("endDate");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT a.entityName AS entityName, ");
        sb.append("\n       a.propertyName AS propertyName, ");
        sb.append("\n       a.action AS action, ");
        sb.append("\n       count(*) AS count ");
        sb.append("\nFROM AuditLog a ");
        sb.append("\nWHERE a.createdDate BETWEEN :startDate AND :endDate ");
        sb.append("\nGROUP BY a.entityName, a.propertyName, a.action ");

        if (!isCount) {
            sb.append("\nORDER BY count(*) DESC ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setCalendar("startDate", startDate);
        query.setCalendar("endDate", endDate);

        if (isCount) {
            map = new HashMap<String, Object>();
            int cnt = 0;
            for (Iterator iterator = query.iterate(); iterator.hasNext();) {
                iterator.next();
                cnt++;
            }

            map.put("total", cnt);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getAuditLogList
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAuditLogList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Map<String, Object> map;
        
        String action = StringUtil.nullToBlank((String)conditionMap.get("action"));
        String equipType = StringUtil.nullToBlank((String)conditionMap.get("equipType"));
        String equipName = StringUtil.nullToBlank((String)conditionMap.get("equipName"));
        String propertyName = StringUtil.nullToBlank((String)conditionMap.get("propertyName"));
        String startDate = StringUtil.nullToBlank((String)conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank((String)conditionMap.get("endDate"));
        String loginId = StringUtil.nullToBlank((String)conditionMap.get("loginId"));

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT a.action AS action, ");
        sb.append("\n       a.createdDate AS createdDate, ");
        sb.append("\n       a.entityName AS entityName, ");
        sb.append("\n       a.instanceName AS equipName, ");
        sb.append("\n       a.propertyName AS propertyName, ");
        sb.append("\n       a.previousState AS previousState, ");
        sb.append("\n       a.currentState AS currentState, ");
        sb.append("\n       a.loginId AS loginId ");
        sb.append("\nFROM AuditLog a ");
        sb.append("\nWHERE 1=1 ");

        if (!action.isEmpty()) {
            sb.append("\nAND   a.action = :action ");
        }

        if (!equipType.isEmpty()) {
            // TODO - 추후 보완
//            sb.append("\nAND   a.entityName = :equipType ");
            sb.append("\nAND   a.entityName LIKE :equipType ");
        }

        if (!equipName.isEmpty()) {
            sb.append("\nAND   a.instanceName LIKE :equipName ");
        }

        if (!propertyName.isEmpty()) {
            sb.append("\nAND   a.propertyName LIKE :propertyName ");
        }

        if (!startDate.isEmpty()) {
            sb.append("\nAND   a.createdDate >= :startDate ");
        }

        if (!endDate.isEmpty()) {
            sb.append("\nAND   a.createdDate <= :endDate ");
        }

        if (!loginId.isEmpty()) {
            sb.append("\nAND   a.loginId like :loginId ");
        }
        
        if (!isCount) {
            sb.append("\nORDER BY a.createdDate DESC ");
        }

        Query query = getSession().createQuery(sb.toString());

        if (!action.isEmpty()) {
            query.setString("action", action);
        }

        if (!equipType.isEmpty()) {
            // TODO - 추후 보완
//            query.setString("equipType", equipType);
            query.setString("equipType", "%" + equipType + "%");
        }

        if (!equipName.isEmpty()) {
            query.setString("equipName", "%" + equipName + "%");
        }

        if (!propertyName.isEmpty()) {
            query.setString("propertyName", "%" + propertyName + "%");
        }

        try {
            if (!startDate.isEmpty()) {
                Calendar startDateCal = Calendar.getInstance();
                startDateCal.setTime(DateTimeUtil.getDateFromYYYYMMDD(startDate));

                query.setCalendar("startDate", startDateCal);
            }

            if (!endDate.isEmpty()) {
                Calendar endDateCal = Calendar.getInstance();
                endDateCal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(endDate+"235959"));
                endDateCal.set(Calendar.MILLISECOND, 999);

                query.setCalendar("endDate", endDateCal);
            }
        } catch(ParseException pe) {
            pe.printStackTrace();
        }

        if (!loginId.isEmpty()) {
            query.setString("loginId", "%" + loginId + "%");
        }

        if (isCount) {
            map = new HashMap<String, Object>();
            int cnt = 0;
            for (Iterator iterator = query.iterate(); iterator.hasNext();) {
                iterator.next();
                cnt++;
            }

            map.put("total", cnt);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
}