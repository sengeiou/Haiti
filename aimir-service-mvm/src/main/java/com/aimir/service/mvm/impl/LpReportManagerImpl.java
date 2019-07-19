package com.aimir.service.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.mvm.LpEMDao;
import com.aimir.service.mvm.LpReportManager;


@Service(value = "LpReportManager")
@Transactional(readOnly=false)
public class LpReportManagerImpl implements LpReportManager{
	
	private static Log logger = LogFactory.getLog(LpReportManagerImpl.class);
	
	@Autowired
    LpEMDao lpemDao;

	@Override
	public Map<String, Object> getValidLpRate(Map<String, Object> condition) {
		// return
        Map<String,Object> result = new HashMap<String,Object>();
        
		List<Object> daoResult = lpemDao.getLpReportByDcuSys();
		
		result.put("ERROR", "No Error on Manager");
		result.put("lpReport", daoResult);
		
		return result;
	}

	@Override
	public Map<String, Object> getValidMeterRate(Map<String, Object> condition) {
		// return
        Map<String,Object> result = new HashMap<String,Object>();
        
		List<Object> daoResult = lpemDao.getMeterReportByDcuSys();
		
		result.put("ERROR", "No Error on Manager");
		result.put("meterReport", daoResult);
		
		return result;
	}

}
