/**
 * (@)# ZeroConsumptionCustomerManagerImpl.java
 *
 * 2014. 9. 18.
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
package com.aimir.service.system.impl.zeroconsumption;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.ZeroConsumptionDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.zeroConsumption.ZeroConsumptionCustomerManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "zeroConsumptionCustomerManager")
@Transactional(readOnly = false)
public class ZeroConsumptionCustomerManagerImpl implements ZeroConsumptionCustomerManager {
	private static Log logger = LogFactory.getLog(ZeroConsumptionCustomerManagerImpl.class);

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	public ZeroConsumptionDao zeroConsumptionDao;

	@Override
	public List<Object> getZeroConsumptionContractData(Map<String, Object> conditionMap) {
		return zeroConsumptionDao.getZeroConsumptionContractData(conditionMap);
	}

	@Override
	public Map<String, Object> getZeroConsumChartData(Map<String, Object> conditionMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String today = null;
		try {
			today = TimeUtil.getCurrentTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		conditionMap.put("today", today);

		Supplier supplier = supplierDao.get(supplierId);
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		result.put("today", TimeLocaleUtil.getLocaleDate(today.substring(0, 8), lang, country));

		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

		List<Map<String, Object>> chartData = zeroConsumptionDao.getZeroMiniChartData(conditionMap);

		if(0 < chartData.size()){
			Map<String, Object> map = chartData.get(0);

			Integer noneZeroCount = DecimalUtil.ConvertNumberToInteger(map.get("NONEZERO"));
			result.put("nonezero", noneZeroCount);
			result.put("nonezeroFormat", dfMd.format(noneZeroCount));

			Integer zeroCount = DecimalUtil.ConvertNumberToInteger(map.get("ZERO"));
			result.put("zero", zeroCount);
			result.put("zeroFormat", dfMd.format(zeroCount));

			Integer totalCount = DecimalUtil.ConvertNumberToInteger(map.get("TOTAL"));
			result.put("totalCount", totalCount);
			result.put("totalCountFormat", dfMd.format(totalCount));
		}else {
			result.put("nonezero", 0);
			result.put("nonezeroFormat", "0");

			result.put("zero", 0);
			result.put("zeroFormat", "0");

			result.put("totalCount", 0);
			result.put("totalCountFormat", "0");
		}

		return result;
	}

}
