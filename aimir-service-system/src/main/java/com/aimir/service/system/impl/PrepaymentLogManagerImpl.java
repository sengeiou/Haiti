package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.PrepaymentLogManager;
import com.aimir.util.Condition;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.system.PrepaymentLogManager")
@Service(value = "prepaymentLogManager")
@Transactional
@RemotingDestination
public class PrepaymentLogManagerImpl implements PrepaymentLogManager {

	@Autowired
	PrepaymentLogDao dao;
	
	@Autowired
	SupplierDao supplierDao;
	
	public void updatePrepaymentLog(PrepaymentLog prepaymentLog) {
		dao.update(prepaymentLog);
	}
	
	public void addPrepaymentLog(PrepaymentLog prepaymentLog) {
		dao.add(prepaymentLog);
	}

	public PrepaymentLog getPrepaymentLog(Long id) {
		return dao.get(id);
	}

	public List<PrepaymentLog> getPrepaymentLogByListCondition(
			Set<Condition> set) {
		return dao.getPrepaymentLogByListCondition(set);
	}
	
	public List<Map<String,Object>> getPrepaymentLogByListCondition(
			Set<Condition> set, String supplierId) {
		
		List<Map<String,Object>> list = ReflectionUtils.getDefineListToMapList(dao.getPrepaymentLogByListCondition(set));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));		
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getCd());
		
		for(Map<String, Object> data: list) {
			data.put("lastTokenDate", TimeLocaleUtil.getLocaleDate((String)data.get("lastTokenDate"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			data.put("chargedCredit", df.format(Double.parseDouble(String.valueOf(data.get("chargedCredit")))));
		}
		
		return list;
	}

	public List<Object> getPrepaymentLogCountByListCondition(Set<Condition> set) {
		return dao.getPrepaymentLogCountByListCondition(set);
	}

    public Double getMonthlyCredit(Map<String, Object> condition) {
        List<PrepaymentLog> prepaymentLogList = dao.getMonthlyCredit(condition);
        Double monthlyCredit = 0.0;
        for (PrepaymentLog prepaymentLog : prepaymentLogList) {
            monthlyCredit += prepaymentLog.getUsedCost() == null ? 0.0 : prepaymentLog.getUsedCost();
        }
        return monthlyCredit;
    }
}
