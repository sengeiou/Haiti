/**
 * (@)# ZeroConsumptionCustomerManager.java
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
package com.aimir.service.system.zeroConsumption;

import java.util.List;
import java.util.Map;

/**
 * @author nuri
 *
 */
public interface ZeroConsumptionCustomerManager {

	List<Object> getZeroConsumptionContractData(Map<String, Object> p);

	Map<String, Object> getZeroConsumChartData(Map<String, Object> conditionMap);

}
