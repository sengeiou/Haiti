/**
 * (@)# LogAnalysisDao.java
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
package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.LogAnalysis;

/**
 * @author nuri
 * 
 */
public interface LogAnalysisDao extends GenericDao<LogAnalysis, Long> {
    List<Map<String, Object>> getGridTreeOper(Map<String, String> conditionMap);

    List<Map<String, Object>> getGridTreeData(Map<String, String> conditionMap);
}
