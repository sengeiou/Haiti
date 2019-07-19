package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.model.device.MeterEventLog;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "metereventlogDao")
public class MeterEventLogDaoImpl extends AbstractHibernateGenericDao<MeterEventLog, Long> implements MeterEventLogDao {

	Log log = LogFactory.getLog(MeterEventLogDaoImpl.class);
	
	@Autowired
	protected MeterEventLogDaoImpl(SessionFactory sessionFactory) {
		super(MeterEventLog.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * 미터 이벤트 로그 - 미니가젯 차트 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMiniChartData(Map<String, Object> conditionMap) {
        String searchStartDate   = StringUtil.nullToBlank(conditionMap.get("searchStartDate"))+"000000";
        String searchEndDate     = StringUtil.nullToBlank(conditionMap.get("searchEndDate"))+"235959";
        String searchDateType    = StringUtil.nullToBlank(conditionMap.get("searchDateType"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String userEvents        = StringUtil.nullToBlank(conditionMap.get("userEvents"));
        String supplierId        = StringUtil.nullToBlank(conditionMap.get("supplierId"));
        
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT evn.name AS eventName, ");
        sb.append("\n       COUNT(*) AS eventCount ");
        sb.append("\nFROM MeterEventLog log, ");
        sb.append("\n MeterEvent evn, ");
        sb.append("\n Meter mtr ");
        sb.append("\nWHERE log.id.meterEventId = evn.id ");
        sb.append("\nAND   log.id.activatorId = mtr.mdsId ");
        sb.append("\nAND   log.id.openTime BETWEEN :startDate AND :endDate ");
        
        if (!"".equals(supplierId)) {
            sb.append("\nAND   log.supplier.id = :supplierId ");
        }
        
        if (!"".equals(locationCondition)) {
            sb.append("\nAND   mtr.location.id IN (").append(locationCondition).append(") ");
        }

        if (!"".equals(userEvents)) {
            sb.append("\nAND   evn.id IN (").append(userEvents).append(") ");
        }

        sb.append("\nGROUP BY evn.name ");
        sb.append("\nORDER BY COUNT(*) DESC ");

        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

        //        if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            query.setString("startDate", searchStartDate);
//        } else {
//            query.setString("startDate", searchStartDate);
//            query.setString("endDate", searchEndDate);
//        }
	    query.setString("startDate", searchStartDate);
	    query.setString("endDate", searchEndDate);
	    
        if (!"".equals(supplierId)) {
            query.setInteger("supplierId",Integer.parseInt(supplierId));
        }

        if ("".equals(userEvents)) {
            query.setMaxResults(0);
        }
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 미터 이벤트 로그 - Profile 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogProfileData(Map<String, Object> conditionMap) {
        String userId = StringUtil.nullToBlank(conditionMap.get("userId"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mev.name AS METEREVENTNAME, ");
        sb.append("\n       CASE WHEN SUM(CASE WHEN prf.meterevent_id IS NULL THEN 0 ELSE 1 END) > 0 THEN 'Y' ELSE 'N' END AS HASPROFILE ");
        sb.append("\nFROM meterevent mev ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     profile prf ");
        sb.append("\n     ON  prf.meterevent_id = mev.id ");
        sb.append("\n     AND prf.operator_id = :userId ");
        sb.append("\nGROUP BY mev.name ");
        sb.append("\nORDER BY mev.name ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        
        query.setInteger("userId", new Integer(userId));
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /**
     * 미터 이벤트 로그 - 맥스가젯 차트 데이터 조회
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMaxChartData(Map<String, Object> conditionMap) {
    	log.debug("getMeterEventLogMaxChartData");
        String searchStartDate   = StringUtil.nullToBlank(conditionMap.get("searchStartDate"))+"000000";
        String searchEndDate     = StringUtil.nullToBlank(conditionMap.get("searchEndDate"))+"235959";
        String searchDateType    = StringUtil.nullToBlank(conditionMap.get("searchDateType"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String eventName         = StringUtil.nullToBlank(conditionMap.get("eventName"));
        String meterType         = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String userEvents        = StringUtil.nullToBlank(conditionMap.get("userEvents"));
        String supplierId        = StringUtil.nullToBlank(conditionMap.get("supplierId"));
        String userId			 = StringUtil.nullToBlank(conditionMap.get("userId"));	
//        int occurFreq            = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("occurFreq")));

        StringBuilder sb = new StringBuilder();
//        StringBuilder sbLikeParam = null;
        // OPF-697 쿼리 변경
        sb.append("\n SELECT * FROM ( ");
        sb.append("\n 	SELECT evn.name EVENTNAME, count(distinct(log.activator_id)) EVENTCOUNT ");
        sb.append("\n 	FROM meterevent_log log ");
        sb.append("\n 	INNER JOIN meter mtr on (log.activator_id = mtr.mds_id) ");   
        sb.append("\n 	INNER JOIN meterevent evn on (log.meterevent_id = evn.id) ");      
//        sb.append("\n 	INNER JOIN PROFILE p on (evn.id = p.METEREVENT_ID) ");
        sb.append("\n 	WHERE  log.OPEN_TIME BETWEEN :startDate AND :endDate ");   
//        sb.append("\n 		AND p.OPERATOR_ID = :userId  ");
        
//        sb.append("\nSELECT vwr.eventName AS EVENTNAME, ");
//        sb.append("\n       COUNT(*) AS EVENTCOUNT ");
//        sb.append("\nFROM ( ");
//        sb.append("\n    SELECT evn.name AS eventName, ");
//        sb.append("\n           mtr.mds_id AS mdsId ");
//        sb.append("\n    FROM meterevent_log log, ");
//        sb.append("\n         meterevent evn, ");
//        sb.append("\n         meter mtr ");
//        sb.append("\n    WHERE log.meterevent_id = evn.id ");
//        sb.append("\n    AND   log.activator_id = mtr.mds_id ");

//        if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            sb.append("\nAND   log.open_time BETWEEN :startDate AND :startDate2 ");
//        } else {
//            sb.append("\nAND   log.open_time BETWEEN :startDate AND :endDate ");
//        }
        if (!"".equals(locationCondition)) {
            sb.append("\nAND   mtr.location_id IN (").append(locationCondition).append(") ");
        }
        
        if (!"".equals(userEvents)) {
        	sb.append("\nAND   evn.id IN (").append(userEvents).append(") ");
        }
        
        if (!"".equals(eventName)) {
            sb.append("\nAND   evn.name = :eventName ");
        }

        if (!"".equals(meterType)) {
            sb.append("\nAND   log.activator_type = :meterType ");
        }
        
        if (!"".equals(supplierId)) {
            sb.append("\nAND   log.supplier_id = :supplierId ");
        }
        
        if (!"".equals(meterId)) {
        	if(meterId.indexOf('%') == 0 || meterId.indexOf('%') == (meterId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   log.activator_Id LIKE :meterId ");
        	}else {
                sb.append("\nAND   log.activator_Id = :meterId ");
        	}
        }
        
        sb.append("\n    GROUP BY evn.name ");
        
//        if (occurFreq > 0) {
//        	sb.append("\nHAVING count(distinct(log.activator_id)) >= :occurFreq ");
//        }
        
        sb.append("\n ) a ORDER BY EVENTCOUNT DESC ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

            query.setString("startDate", searchStartDate);
            query.setString("endDate", searchEndDate);

        if (!"".equals(eventName)) {
            query.setString("eventName", eventName);
        }

        if (!"".equals(meterType)) {
            query.setString("meterType", meterType);
        }
        
        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        if (!"".equals(meterId)) {
        	 query.setString("meterId", meterId);
        }

//        if (occurFreq > 0) {
//            query.setInteger("occurFreq", occurFreq);
//        }
//        
//        if (!"".equals(userId)) {
//        	query.setInteger("userId", new Integer(userId));
//        }

        if ("".equals(userEvents)) {
            query.setMaxResults(0);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /**
     * 미터 이벤트 로그 - 맥스가젯 이벤트별 미터기 데이터 조회
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogMeterByEventGridData(Map<String, Object> conditionMap, boolean isTotal) {
        log.debug("getMeterEventLogMeterByEventGridData");
        String searchStartDate   = StringUtil.nullToBlank(conditionMap.get("searchStartDate"))+"000000";
        String searchEndDate     = StringUtil.nullToBlank(conditionMap.get("searchEndDate"))+"235959";
        String searchDateType    = StringUtil.nullToBlank(conditionMap.get("searchDateType"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String eventName         = StringUtil.nullToBlank(conditionMap.get("eventName"));
        String meterType         = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String eventCondition    = StringUtil.nullToBlank(conditionMap.get("eventCondition"));
        String userEvents   	 = StringUtil.nullToBlank(conditionMap.get("userEvents"));
        int occurFreq            = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("occurFreq")));
        int page = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("page")));
        int pageSize = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("pageSize")));
        String userId			= StringUtil.nullToBlank(conditionMap.get("userId"));

        StringBuilder sb = new StringBuilder();
//        StringBuilder sbLikeParam = null;

        //sp-1002 쿼리변경
        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }
        
        sb.append("\n SELECT evn.name as eventName, ");
        sb.append("\n	lo.name  AS locationName, ");
        sb.append("\n	COUNT(*)           AS meterCount, ");
        sb.append("\n	log.activator_id AS meterId, ");
        sb.append("\n	log.activator_type  AS meterType ");
		sb.append("\n FROM METEREVENT_LOG log ");
		sb.append("\n INNER JOIN METEREVENT evn ON (log.METEREVENT_ID = evn.id) ");
		// sb.append("\n INNER JOIN PROFILE p ON (evn.id = p.METEREVENT_ID) ");
		sb.append("\n INNER JOIN METER mtr ON (log.activator_id = mtr.mds_id) ");
		sb.append("\n LEFT OUTER JOIN Location lo ON (mtr.location_id = lo.id) ");
		sb.append("\n WHERE 1 = 1 ");
        
//        sb.append("\nSELECT evn.name           AS eventName, ");
//        sb.append("\n       lo.name  AS locationName, ");
//        sb.append("\n       COUNT(*)           AS meterCount, ");
//        sb.append("\n       log.id.activatorId AS meterId, ");
//        sb.append("\n       log.activatorType  AS meterType ");
//        sb.append("\nFROM MeterEventLog log, ");
//        sb.append("\n     MeterEvent evn, ");
//        sb.append("\n     Meter mtr  LEFT JOIN mtr.location lo");
//        sb.append("\nWHERE log.id.meterEventId = evn.id ");
//        sb.append("\nAND   log.id.activatorId = mtr.mdsId ");
//
//		if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            sb.append("\nAND   log.id.openTime BETWEEN :startDate AND :startDate2 ");
//        } else {
//            sb.append("\nAND   log.id.openTime BETWEEN :startDate AND :endDate ");
//        }
		sb.append("\nAND   log.open_time BETWEEN :startDate AND :endDate ");

        if (!"".equals(locationCondition)) {
            sb.append("\nAND   mtr.location_id IN (").append(locationCondition).append(") ");
        }

        if (!"".equals(userEvents)) {
            sb.append("\nAND   evn.id IN (").append(userEvents).append(") ");
        }
        
        if (!"".equals(eventName)) {
            sb.append("\nAND   evn.name = :eventName ");
        }

        if (!"".equals(eventCondition)) {
            sb.append("\nAND   evn.name IN (").append(eventCondition).append(") ");
        }
        
//        if (!"".equals(meterType)) {
//            sb.append("\nAND   log.activatorType = :meterType ");
//        }
//        
//        if (!"".equals(meterId)) {
//            sb.append("\nAND   log.id.activatorId LIKE :meterId ");
//        }
        if (!"".equals(meterType)) {
            sb.append("\nAND   log.activator_Type = :meterType ");
        }
        
        if (!"".equals(meterId)) {
        	if(meterId.indexOf('%') == 0 || meterId.indexOf('%') == (meterId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   log.activator_Id LIKE :meterId ");
        	}else {
                sb.append("\nAND   log.activator_Id = :meterId ");
        	}
        }        
        
//        if (!"".equals(userId)) {
//            sb.append("\nAND   p.OPERATOR_ID = :userId ");
//        }
//        
        sb.append("\nGROUP BY evn.name, lo.name, log.activator_Id, log.activator_Type ");
        
        if (occurFreq > 0) {
            sb.append("\nHAVING COUNT(*) >= :occurFreq ");
        }
        
        if (!isTotal) {
            sb.append("\nORDER BY log.activator_Id ");
        } else {
        	sb.append("\n ) A ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());
//        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());

//        if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            query.setString("startDate", searchStartDate);
//        } else {
//            query.setString("startDate", searchStartDate);
//            query.setString("endDate", searchEndDate);
//        }
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        
        if (!"".equals(eventName)) {
            query.setString("eventName", eventName);
        }

        if (!"".equals(meterType)) {
            query.setString("meterType", meterType);
        }
        
        if (!"".equals(meterId)) {
        	query.setString("meterId", meterId);
        }
        
//        if (!"".equals(userId)) {
//        	query.setInteger("userId", new Integer(userId));
//        }

        if (occurFreq > 0) {
            query.setInteger("occurFreq", occurFreq);
        }
        
        if (isTotal) {
        	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
            return result;
        } else {
        	
        	if (!isTotal && pageSize > 0) {
        		query.setFirstResult(page * pageSize);
        		query.setMaxResults(pageSize);
        	}
        	
        	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

    }

    /**
     * 미터 이벤트 로그 - 맥스가젯 미터기별 이벤트 데이터 조회
     *
     * @param conditionMap
     * @param isTotal Total Count 조회여부
     * @return
     */
    public List<Map<String, Object>> getMeterEventLogEventByMeterGridData(Map<String, Object> conditionMap, boolean isTotal) {
        log.debug("getMeterEventLogEventByMeterGridData");
        String searchStartDate   = StringUtil.nullToBlank(conditionMap.get("searchStartDate"))+"000000";
        String searchEndDate     = StringUtil.nullToBlank(conditionMap.get("searchEndDate"))+"235959";
        String searchDateType    = StringUtil.nullToBlank(conditionMap.get("searchDateType"));
        String locationCondition = StringUtil.nullToBlank(conditionMap.get("locationCondition"));
        String eventName         = StringUtil.nullToBlank(conditionMap.get("eventName"));
        String meterType         = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String meterId           = StringUtil.nullToBlank(conditionMap.get("meterId"));
        String activatorId           = StringUtil.nullToBlank(conditionMap.get("activatorId"));
        String eventCondition    = StringUtil.nullToBlank(conditionMap.get("eventCondition"));
        String userEvents   	 = StringUtil.nullToBlank(conditionMap.get("userEvents"));
//        int occurFreq            = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("occurFreq")));
        int page = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("page")));
        int pageSize = Integer.parseInt(StringUtil.nullToZero(conditionMap.get("pageSize")));
        
        String userId    = StringUtil.nullToBlank(conditionMap.get("userId"));
        StringBuilder sb = new StringBuilder();
//        StringBuilder sbLikeParam = null;
        
        
        // OPF-697 쿼리 변경
        if (isTotal) {
        	sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }
        
        sb.append("\n SELECT ");
        sb.append("\n	rownum, ");
        sb.append("\n	log.open_time AS openTime, ");
        sb.append("\n	log.WRITETIME AS writeTime, ");
        sb.append("\n	evn.name AS eventName, ");
        sb.append("\n	evn.name AS eventModel, ");
        sb.append("\n	evn.name AS eventValue, ");
        sb.append("\n	lo.name AS locationName, ");
        sb.append("\n	log.activator_id AS meterId, ");
        sb.append("\n	log.ACTIVATOR_TYPE AS meterType, ");
        sb.append("\n	log.message AS message, ");
        sb.append("\n	evn.TROUBLE_ADVICE AS troubleAdvice ");
        sb.append("\n FROM METEREVENT_LOG log ");
        sb.append("\n INNER JOIN METEREVENT evn ON (log.meterevent_id = evn.id) ");
//        sb.append("\n INNER JOIN PROFILE p ON (evn.id = p.meterevent_id) ");
        sb.append("\n INNER JOIN METER mtr ON (log.activator_id = mtr.mds_id) ");
        sb.append("\n LEFT OUTER JOIN LOCATION lo ON (mtr.location_id = lo.id) ");
        sb.append("\n WHERE 1 = 1");
        
//        sb.append("\nSELECT log.id.openTime    AS openTime, ");
//        sb.append("\n       log.writeTime      AS writeTime, ");
//        sb.append("\n       evn.name           AS eventName, ");
//        sb.append("\n       evn.model           AS eventModel, ");
//        sb.append("\n       evn.value           AS eventValue, ");
//        sb.append("\n       lo.name  AS locationName, ");
//        sb.append("\n       log.id.activatorId AS meterId, ");
//        sb.append("\n       log.activatorType  AS meterType, ");
//        sb.append("\n       coalesce(log.message, '') AS message, ");
//        sb.append("\n       coalesce(evn.troubleAdvice, '') AS troubleAdvice ");
//        sb.append("\nFROM MeterEventLog log, ");
//        sb.append("\n     MeterEvent evn, ");
//        sb.append("\n     Meter mtr LEFT JOIN mtr.location as lo");
//        sb.append("\nWHERE log.id.meterEventId = evn.id ");
//        sb.append("\nAND   log.id.activatorId = mtr.mdsId ");
//
//		if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            sb.append("\nAND   log.id.openTime BETWEEN :startDate AND :startDate2 ");
//        } else {
//            sb.append("\nAND   log.id.openTime BETWEEN :startDate AND :endDate ");
//        }
        sb.append("\nAND   log.open_time BETWEEN :startDate AND :endDate ");

        if (!"".equals(locationCondition)) {
            sb.append("\nAND   mtr.location_id IN (").append(locationCondition).append(") ");
        }
        
        if (!"".equals(userEvents)) {
            sb.append("\nAND   evn.id IN (").append(userEvents).append(") ");
        }
        
        if (!"".equals(eventName)) {
            sb.append("\nAND   evn.name = :eventName ");
        }
        
        if (!"".equals(eventCondition)) {
            sb.append("\nAND   evn.name IN (").append(eventCondition).append(") ");
        }
        
        if (!"".equals(meterType)) {
            sb.append("\nAND   log.activator_Type = :meterType ");
        }
        
        if (!"".equals(activatorId)) {
            sb.append("\nAND   log.activator_Id = :activatorId ");
        } else if (!"".equals(meterId)) {
        	if(meterId.indexOf('%') == 0 || meterId.indexOf('%') == (meterId.length()-1)) { // %문자가 양 끝에 있을경우
                sb.append("\nAND   log.activator_Id LIKE :meterId ");
        	}else {
                sb.append("\nAND   log.activator_Id = :meterId ");
        	}
        }

//        if (!"".equals(userId)) {
//            sb.append("\nAND   p.OPERATOR_ID = :userId ");
//        }

        if (!isTotal) {
            sb.append("\nORDER BY log.open_Time DESC, log.writeTime DESC, evn.name ");
        }else {
        	sb.append("\n) A ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
//        Query query = getSession().createQuery(sb.toString());
        // criteria.setProjection(Projections.rowCount());
        
//        if((DateType.DAILY.getCode()).equals(searchDateType)) {
//            query.setString("startDate", searchStartDate);
//            query.setString("startDate2", searchStartDate2);
//        } else {
//            query.setString("startDate", searchStartDate);
//            query.setString("endDate", searchEndDate);
//        }
        
        if (!"".equals(eventName)) {
            query.setString("eventName", eventName);
        }

        if (!"".equals(meterType)) {
            query.setString("meterType", meterType);
        }
        
        if (!"".equals(activatorId)) {
            query.setString("activatorId", activatorId);
        } else if (!"".equals(meterId)) {
            query.setString("meterId", meterId);
        }
//        
//        if (!"".equals(userId)) {
//        	query.setInteger("userId", new Integer(userId));
//        }

//        if (occurFreq > 0) {
//            query.setInteger("occurFreq", occurFreq);
//        }


        if (isTotal) {
        	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
            return result;
        } else {
        	if (!isTotal && pageSize > 0) {
        		query.setFirstResult(page * pageSize);
        		query.setMaxResults(pageSize);
        	}
        	
        	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
    }

    /**
     * method name : getMeterEventLogNotIntegratedData<b/>
     * method Desc :
     *
     * @param useInsert insert 에서 사용여부(true : insert 에서 사용, false : update 에서 사용) 
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMeterEventLogNotIntegratedData(boolean useInsert) {
        List<Object> result = null;
        StringBuilder sb = new StringBuilder();

        if (useInsert) {
            sb.append("\nSELECT log.id.meterEventId AS meterEventId, ");
            sb.append("\n       mtr.mdsId AS meterId, ");
            sb.append("\n       mtr.contract.contractNumber AS customerId, ");
            sb.append("\n       log.id.openTime AS eventDate, ");
            sb.append("\n       evt.value AS value ");
        } else {
            sb.append("\nSELECT log ");
        }
        sb.append("\nFROM MeterEventLog log, ");
        sb.append("\n     MeterEvent evt, ");
        sb.append("\n     Meter mtr ");
//        sb.append("\n     Meter mtr LEFT OUTER JOIN mtr.contract co");
//        sb.append("\n               LEFT OUTER JOIN co.customer cu");
        sb.append("\nWHERE log.id.meterEventId = evt.id ");
        sb.append("\nAND   log.id.activatorId = mtr.mdsId ");
        sb.append("\nAND   log.integrated IS NULL ");
        // sb.append("\nAND   evt.value IN (3, 4, 8, 9, 12, 13, 16, 17, 18, 19, 20, 23, 25, 113, 117, 118) ");
        if (useInsert) {
            // sb.append("\nORDER BY mtr.contract.customer.customerNo, log.id.activatorId, log.id.openTime, evt.value ");
        }

        Query query = getSession().createQuery(sb.toString());
        if (useInsert) {
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        } else {
            result = query.list();
        }
        return result;
    }

    /**
     * method name : batchUpdateMeterEventLogIntegrated<b/>
     * method Desc :
     *
     * @param meterEventLogList
     */
    public void batchUpdateMeterEventLogIntegrated(List<MeterEventLog> meterEventLogList) {
        Session ses = getSession();
        int idx = 0;
        int batch_size = 50;    // hibernate.jdbc.batch_size

        for (MeterEventLog eventLog : meterEventLogList) {
            eventLog.setIntegrated(true);
            ses.update(eventLog);

            if ((++idx) % batch_size == 0) {
                log.debug("session flush and clear!!");
                ses.flush();
                ses.clear();
            }
        }
    }
    
    /**
     * ActivatorId와 MeterEventId에 해당하는 로그 조회
     * @param condition (yyyymmdd, activatorId)
     * @param eId (meterEventId)
     * @return
     */
    public List<MeterEventLog> getEventLogListByActivator(Map<String,Object> condition, String eId){
        StringBuilder sb = new StringBuilder();
        sb.append("\n FROM MeterEventLog");
        sb.append("\n WHERE ACTIVATOR_ID = :activatorId");
        sb.append("\n AND METEREVENT_ID = :meterEventId");
        sb.append("\n AND YYYYMMDD = :yyyymmdd");

        Query query = getSession().createQuery(sb.toString());
        query.setString("activatorId", condition.get("mdevId").toString());
        query.setString("meterEventId", eId);
        query.setString("yyyymmdd", condition.get("yyyymmdd").toString());

        return query.list();
    }

    public List<Object> getLastEventLogByEventId(Map<String,Object> condition, String[] eId){
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("  SELECT meterevent_id, open_time FROM METEREVENT_LOG \n")
                .append(" WHERE activator_id = :activatorId \n")
                .append(" AND open_time in \n")
                .append(" (SELECT MAX(open_time) AS mx FROM METEREVENT_LOG WHERE activator_id = :activatorId ")
                .append(" AND yyyymmdd < :yyyymmdd AND meterevent_id IN (:eventIds)) \n");

        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("activatorId", condition.get("mdevId").toString());
        dataQueryObj.setString("yyyymmdd", condition.get("yyyymmdd").toString());
        dataQueryObj.setParameterList("eventIds", eId);

        List<Object> result = dataQueryObj.list();

        return result;
    }
    
    public List<Object> getEventLogByMds_id(String mdsId){
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("  SELECT open_time FROM METEREVENT_LOG \n")
                .append(" WHERE activator_id = :activatorId \n")
                .append("\nORDER BY open_time DESC ");
                

        SQLQuery dataQueryObj = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
        dataQueryObj.setString("activatorId", mdsId);

        List<Object> result = dataQueryObj.list();

        return result;
    }
}