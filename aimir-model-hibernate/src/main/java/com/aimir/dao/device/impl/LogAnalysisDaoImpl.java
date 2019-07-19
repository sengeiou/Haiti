/**
 * (@)# LogAnalysisDaoImpl.java
 *
 * 2014. 7. 14.
 *
 * Copyright (c) 2013 NuriTelecom, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * ITCOMM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NuriTelecom, Inc.
 *
 * For more information on this product, please see
 * www.nuritelecom.co.kr
 *
 */
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LogAnalysisDao;
import com.aimir.model.device.LogAnalysis;
import com.aimir.util.SQLWrapper;

/**
 * @author nuri
 * 
 */
@Repository(value = "logAnalysisDao")
public class LogAnalysisDaoImpl extends AbstractHibernateGenericDao<LogAnalysis, Long> implements LogAnalysisDao {
    private static Log logger = LogFactory.getLog(LogAnalysisDaoImpl.class);

    @Autowired
    protected LogAnalysisDaoImpl(SessionFactory sessionFactory) {
        super(LogAnalysis.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getGridTreeOper(Map<String, String> conditionMap) {
        StringBuilder sb = new StringBuilder();
        /*
         * 날짜(표기용)/로그타입/송신(comm:hidden)/장비/수행자(oper:hidden)/명령/결과/메시지(event:)
         */
        sb.append("\n SELECT  ");
        sb.append("\n       YYYYMMDDHHMMSS AS DATE_BY_VIEW ");
        sb.append("\n     , 'O' AS LOG_TYPE ");
        sb.append("\n     , '-' AS SENDER_ID ");
        sb.append("\n     , TARGET_NAME AS DEVICE ");
        sb.append("\n     , USER_ID ");
        sb.append("\n     , (SELECT CODE.NAME FROM CODE WHERE CODE.ID = OP.OPERATION_COMMAND_CODE) AS OPERATION_CODE ");
        sb.append("\n     , ERROR_REASON AS RESULT ");
        sb.append("\n     , '-' AS MESSAGE ");
        sb.append("\n FROM OPERATION_LOG OP ");
        sb.append("\n WHERE SUPPLIER_ID = :supplierId ");
        sb.append("\n    AND YYYYMMDDHHMMSS >= :startDate ");
        sb.append("\n    AND YYYYMMDDHHMMSS <= :endDate ");
        sb.append("\n ORDER BY DATE_BY_VIEW DESC, DEVICE ASC ");

        sb.trimToSize();

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", conditionMap.get("startDate"));
        query.setString("endDate", conditionMap.get("endDate"));
        query.setInteger("supplierId", Integer.parseInt(conditionMap.get("supplierId")));

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    public List<Map<String, Object>> getGridTreeData(Map<String, String> conditionMap) {
        StringBuilder sb = new StringBuilder();
        /*
         * 날짜(표기용)/로그타입/송신(comm:hidden)/장비/수행자(oper:hidden)/명령/결과/메시지(event:)
         */
        sb.append("\n SELECT DATE_BY_VIEW, LOG_TYPE, SENDER_ID, DEVICE, USER_ID, OPERATION_CODE, RESULT, MESSAGE ");
        sb.append("\n FROM ( ");
        sb.append("\n   SELECT  ");
        sb.append("\n         COMM.START_DATE_TIME AS DATE_BY_VIEW ");
        sb.append("\n       , 'C' AS LOG_TYPE ");
        sb.append("\n       , COMM.SENDER_ID ");
        sb.append("\n       , COMM.RECEIVER_ID AS DEVICE ");
        sb.append("\n       , '-' AS USER_ID ");
        sb.append("\n       , COMM.OPERATION_CODE ");
        sb.append("\n       , COMM.COMM_RESULT AS RESULT ");
        sb.append("\n       , '-' as MESSAGE ");
        sb.append("\n   FROM COMMLOG COMM ");
        sb.append("\n   WHERE COMM.SUPPLIERED_ID = :supplierId ");
        sb.append("\n      AND COMM.SVC_TYPE_CODE = (SELECT ID FROM CODE WHERE CODE = :svcTypeCode) ");
        sb.append("\n      AND COMM.START_DATE_TIME <= :endDate ");
        sb.append("\n      AND COMM.START_DATE_TIME > :startDate ");
        sb.append("\n      AND COMM.RECEIVER_ID = :device "); // device 가 같은것
        sb.append("\n   UNION ALL ");
        sb.append("\n SELECT ");
        sb.append("\n     OPENTIME AS DATE_BY_VIEW ");
        sb.append("\n   , 'E' AS LOG_TYPE ");
        sb.append("\n   , '-' AS SENDER_ID ");
        sb.append("\n   , EV.ACTIVATORID AS DEVICE ");
        sb.append("\n   , '-' AS USER_ID ");
        sb.append("\n   , '-' AS OPERATION_CODE ");
        sb.append("\n   , '-' AS RESULT ");
        sb.append("\n   , EV.MESSAGE  ");
        sb.append("\n FROM EVENTALERTLOG EV ");
        sb.append("\n WHERE EV.SUPPLIER_ID = :supplierId ");
        sb.append("\n    AND EV.OPENTIME <= :endDate ");
        sb.append("\n    AND EV.OPENTIME > :startDate ");
        sb.append("\n   AND EV.ACTIVATORID = :device "); // device 가 같은것
        sb.append("\n   AND EVENTALERT_ID = :eventAlertClass ");
        sb.append("\n ) A ");
        sb.append("\n ORDER BY DATE_BY_VIEW DESC, DEVICE ASC ");
        sb.trimToSize();

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", conditionMap.get("startDate"));
        query.setString("endDate", conditionMap.get("endDate"));
        query.setString("device", conditionMap.get("device"));
        query.setInteger("supplierId", Integer.parseInt(conditionMap.get("supplierId")));
        query.setString("svcTypeCode", conditionMap.get("svcTypeCode"));
        query.setString("eventAlertClass", conditionMap.get("eventAlertClass"));

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

}
