/**
 * (@)# ZeroConsumptionDao.java
 *
 * 2014. 9. 18.
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
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

/**
 * @author nuri
 *
 */
public interface ZeroConsumptionDao {
	List<Object> getZeroConsumptionContractData(Map<String, Object> conditionMap);

	List<Map<String, Object>> getZeroMiniChartData(Map<String, Object> conditionMap);
}
