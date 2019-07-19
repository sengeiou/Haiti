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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LogAnalysisDao;
import com.aimir.model.device.LogAnalysis;
import com.aimir.util.Condition;

/**
 * @author nuri
 * 
 */
@Repository(value = "logAnalysisDao")
public class LogAnalysisDaoImpl extends AbstractJpaDao<LogAnalysis, Long> implements LogAnalysisDao {
    private static Log logger = LogFactory.getLog(LogAnalysisDaoImpl.class);

    public LogAnalysisDaoImpl() {
        super(LogAnalysis.class);
    }

    @Override
    public List<Map<String, Object>> getGridTreeOper(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getGridTreeData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LogAnalysis> getPersistentClass() {
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
