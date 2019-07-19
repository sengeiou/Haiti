/**
 * (@)# ZeroConsumptionDaoImpl.java
 *
 * Copyright (c) 2012 NURITelecom, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * NURITelecom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITelecom, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ZeroConsumptionDao;
import com.aimir.model.system.ZeroConsumption;
import com.aimir.util.Condition;

@Repository(value = "ZeroConsumptionDao")
public class ZeroConsumptionDaoImpl extends AbstractJpaDao<ZeroConsumption, Long> implements ZeroConsumptionDao {

    public ZeroConsumptionDaoImpl() {
        super(ZeroConsumption.class);
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getZeroConsumptionContractData(Map<String, Object> conditionMap) {

        StringBuilder sb = new StringBuilder();
//        sb.append(" SELECT C.CONTRACT_NUMBER AS CONTRACT_NUMBER ");
//        sb.append(" FROM CONTRACT C, ( ");
//        sb.append(" 	SELECT DISTINCT CONTRACT_ID ");
//        sb.append(" 	FROM DAY_EM ");
//        sb.append(" 	WHERE SUPPLIER_ID = :supplierId ");
//        sb.append(" 		AND CONTRACT_ID IS NOT NULL ");
//        sb.append(" 		AND (TOTAL = 0 OR TOTAL IS NULL) ");
//        sb.append(" 		AND CHANNEL = 1 ");
//        sb.append("         AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
//        sb.append(" ) D ");
//        sb.append(" WHERE C.ID = D.CONTRACT_ID ");

        sb.append(" SELECT CONTRACT_NUMBER ");
        sb.append(" FROM ( ");
        sb.append(" 	SELECT  C.CONTRACT_NUMBER, SUM(E.TOTAL) AS RESULT ");
        sb.append(" 	FROM ( ");
        sb.append(" 		SELECT ID, CONTRACT_NUMBER FROM contract WHERE SUPPLIER_ID = :supplierId ");
        sb.append(" 	) C INNER JOIN ( ");
        sb.append(" 		SELECT CONTRACT_ID, YYYYMMDD, DEVICE_ID, TOTAL FROM DAY_EM ");
        sb.append(" 		WHERE ");
        sb.append(" 		   SUPPLIER_ID = :supplierId ");
        sb.append(" 		   AND CONTRACT_ID IS NOT NULL ");
        sb.append(" 		   AND CHANNEL = 1 ");
        sb.append(" 		   AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append(" 	) E ");
        sb.append(" 	ON C.ID = E.CONTRACT_ID ");
        sb.append(" 	GROUP BY CONTRACT_NUMBER ");
        sb.append(" )R ");
        sb.append(" WHERE RESULT = 0 ");

        sb.trimToSize();

        Query query = getEntityManager().createNativeQuery(sb.toString());
        query.setParameter("searchStartDate", String.valueOf(conditionMap.get("searchStartDate")));
        query.setParameter("searchEndDate", String.valueOf(conditionMap.get("searchEndDate")));
        query.setParameter("supplierId", Integer.parseInt(String.valueOf(conditionMap.get("supplierId"))));

        return query.getResultList();
	}

	@Override
	public List<Map<String, Object>> getZeroMiniChartData(Map<String, Object> conditionMap) {
        StringBuilder sb = new StringBuilder();
//        sb.append(" SELECT (T.TOTAL - Z.ZERO) AS NONEZERO, Z.ZERO, T.TOTAL ");
//        sb.append(" FROM ( ");
//        sb.append(" 	SELECT ");
//        sb.append(" 	   COUNT(DISTINCT(CONTRACT_ID)) AS ZERO ");
//        sb.append(" 	FROM ");
//        sb.append(" 	   DAY_EM    ");
//        sb.append(" 	WHERE ");
//        sb.append(" 	   SUPPLIER_ID = :supplierId ");
//        sb.append(" 	   AND CONTRACT_ID IS NOT NULL     ");
//        sb.append(" 	   AND ( ");
//        sb.append(" 	       TOTAL = 0  ");
//        sb.append(" 	       OR TOTAL IS NULL ");
//        sb.append(" 	   )     ");
//        sb.append(" 	   AND CHANNEL = 1           ");
//        sb.append(" 	   AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
//        sb.append(" ) Z, ( ");
//        sb.append(" 	SELECT  ");
//        sb.append(" 		COUNT(DISTINCT(CONTRACT_ID)) AS TOTAL ");
//        sb.append(" 	FROM ");
//        sb.append(" 	   DAY_EM    ");
//        sb.append(" 	WHERE ");
//        sb.append(" 	   SUPPLIER_ID = :supplierId ");
//        sb.append(" 	   AND CONTRACT_ID IS NOT NULL     ");
//        sb.append(" 	   AND TOTAL IS NOT NULL ");
//        sb.append(" 	   AND CHANNEL = 1           ");
//        sb.append(" 	   AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
//        sb.append(" ) T ");

        sb.append(" SELECT (T.TOTAL - Z.ZERO) AS NONEZERO, Z.ZERO, T.TOTAL ");
        sb.append(" FROM ( ");
        sb.append(" 	SELECT COUNT(CONTRACT_NUMBER) AS ZERO ");
        sb.append(" 	FROM ( ");
        sb.append(" 		SELECT C.CONTRACT_NUMBER, SUM(E.TOTAL) AS SUM_RESULT ");
        sb.append(" 		FROM ( ");
        sb.append(" 			SELECT ID, CONTRACT_NUMBER FROM contract WHERE SUPPLIER_ID = :supplierId ");
        sb.append(" 		) C INNER JOIN ( ");
        sb.append(" 			SELECT CONTRACT_ID, YYYYMMDD, DEVICE_ID, TOTAL FROM DAY_EM ");
        sb.append(" 			WHERE ");
        sb.append(" 			       SUPPLIER_ID = :supplierId ");
        sb.append(" 			   AND CONTRACT_ID IS NOT NULL ");
        sb.append(" 			   AND CHANNEL = 1 ");
        sb.append(" 			   AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append(" 		) E ");
        sb.append(" 		ON C.ID = E.CONTRACT_ID ");
        sb.append(" 		GROUP BY CONTRACT_NUMBER ");
        sb.append(" 	)R ");
        sb.append(" 	WHERE SUM_RESULT = 0 ");
        sb.append(" ) Z, ( ");
        sb.append(" 	SELECT COUNT(CONTRACT_NUMBER) AS TOTAL ");
        sb.append(" 	FROM ( ");
        sb.append(" 		SELECT CONTRACT_NUMBER ");
        sb.append(" 		FROM ( ");
        sb.append(" 			SELECT ID, CONTRACT_NUMBER FROM contract WHERE SUPPLIER_ID = :supplierId ");
        sb.append(" 		) C INNER JOIN ( ");
        sb.append(" 			SELECT CONTRACT_ID, YYYYMMDD, DEVICE_ID, TOTAL FROM DAY_EM ");
        sb.append(" 			WHERE ");
        sb.append(" 			       SUPPLIER_ID = :supplierId ");
        sb.append(" 			   AND CONTRACT_ID IS NOT NULL ");
        sb.append(" 			   AND CHANNEL = 1 ");
        sb.append(" 			   AND YYYYMMDD BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append(" 		) E ");
        sb.append(" 		ON C.ID = E.CONTRACT_ID ");
        sb.append(" 		GROUP BY CONTRACT_NUMBER ");
        sb.append(" 	) A ");
        sb.append(" ) T ");

        sb.trimToSize();

        Query query = getEntityManager().createNativeQuery(sb.toString());
        query.setParameter("searchStartDate", String.valueOf(conditionMap.get("searchStartDate")));
        query.setParameter("searchEndDate", String.valueOf(conditionMap.get("searchEndDate")));
        query.setParameter("supplierId", Integer.parseInt(String.valueOf(conditionMap.get("supplierId"))));

        //return query.setResultTransformer(Transformers.TO_LIST).list();
        return query.getResultList();
	}

    @Override
    public Class<ZeroConsumption> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

}
