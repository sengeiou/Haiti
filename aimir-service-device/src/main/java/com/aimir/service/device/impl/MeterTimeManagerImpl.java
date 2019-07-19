package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterTimeDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MeterTimeSyncLog;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterTimeManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value="meterTimeManager")
@Transactional(readOnly=false)
public class MeterTimeManagerImpl implements MeterTimeManager{
	
	@Autowired
	MeterTimeDao meterTimeDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	MeterTimeSyncLogDao meterTimeSyncLogDao;


	public List<Object> getMeterTimeTimeDiffChart(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		
		result = meterTimeDao.getMeterTimeTimeDiffChart(condition);	
		return result;
	}

	public List<Object> getMeterTimeTimeDiffComplianceChart(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		
		result = meterTimeDao.getMeterTimeTimeDiffComplianceChart(condition);	
		return result;
	}
	
	public List<Object> getMeterTimeTimeDiffGrid(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		
		result = meterTimeDao.getMeterTimeTimeDiffGrid(condition);	
		return result;
	}
	
 
	public List<Object> getMeterTimeSyncLogChart(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		result = meterTimeDao.getMeterTimeSyncLogChart(condition);

		Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		List<Object> dataList = (List<Object>) result.get(0);
		for(Object obj: dataList) {
			HashMap chartDataMap = (HashMap) obj;
			String yyyyMMdd = String.valueOf(chartDataMap.get("xTag"));
			chartDataMap.put("xTag", TimeLocaleUtil.getLocaleDate(yyyyMMdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));						
		}
		
		return result;
	}

	public List<Object> getMeterTimeSyncLogAutoChart(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		result = meterTimeDao.getMeterTimeSyncLogAutoChart(condition);
		
		return result;
	}
	
	public List<Object> getMeterTimeSyncLogManualChart(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		result = meterTimeDao.getMeterTimeSyncLogManualChart(condition);
		
		return result;
	}	
	
	public List<Object> getMeterTimeSyncLogGrid(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		result = meterTimeDao.getMeterTimeSyncLogGrid(condition);
		
		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		if(supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
			List<Object> gridList = (List<Object>) result.get(1);
			for(Object data: gridList) {
				Map<String,Object> mapData = (Map<String, Object>) data;
				
				mapData.put("no", dfMd.format(Integer.parseInt((String)mapData.get("no"))));
				String timeDiff = (mapData.get("timeDiff") == null) ? "" : dfMd.format(mapData.get("timeDiff"));
				mapData.put("timeDiff", timeDiff);
				mapData.put("writeDate", TimeLocaleUtil.getLocaleDate((String)mapData.get("writeDate") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				mapData.put("previousDate", TimeLocaleUtil.getLocaleDate((String)mapData.get("previousDate") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				mapData.put("currentDate", TimeLocaleUtil.getLocaleDate((String)mapData.get("currentDate") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		return result;
	}

	public List<Object> getMeterTimeThresholdGrid(Map<String, Object> condition){
    	
		List<Object> result = new ArrayList<Object>();
		result = meterTimeDao.getMeterTimeThresholdGrid(condition);
		
		return result;
	}

	public void insertMeterTimeSycLog(MeterTimeSyncLog log) {
		log.setId(TimeUtil.getCurrentLongTime());
		meterTimeSyncLogDao.add(log);
		meterTimeSyncLogDao.flushAndClear();		
	}


	
}




