package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.mvm.MeteringSLADao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MeteringSLAManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="MeteringSLAManager")
@Transactional(readOnly=false)
public class MeteringSLAManagerImpl implements MeteringSLAManager{
	
	@Autowired
	MeteringSLADao meteringSLADao;
	
	@Autowired
	SupplierDao supplierDao;

	
	public List<Object> getMeteringSLASummaryGrid(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meteringSLADao.getMeteringSLASummaryGrid(condition);
		return result;
	}
	
	public List<Object> getMeteringSLAMiniChart(Map<String, Object> condition){
		String supplierId = (String) condition.get("supplierId");
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		List<Object> resultList = new ArrayList<Object>();
		List<Object> result = new ArrayList<Object>();
		result = meteringSLADao.getMeteringSLAList(condition);
		
		List<Object> dataList = (List<Object>) result.get(0);
		for(Object obj: dataList) {
			Map<String, String> data = (Map<String, String>) obj;
			data.put("xTag", TimeLocaleUtil.getLocaleDate(data.get("xTag") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
		}
		resultList.add(0,dataList);
		resultList.add(1,dataList.size());
		return resultList;
	}
	
	public List<Object> getMeteringSLAMissingData(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meteringSLADao.getMeteringSLAMissingData(condition);
		return result;
	}
	
	
	public List<Object> getMeteringSLAMissingDetailChart(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meteringSLADao.getMeteringSLAMissingDetailChart(condition);
		return result;
	}
	
	
	public List<Object> getMeteringSLAMissingDetailGrid(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meteringSLADao.getMeteringSLAMissingDetailGrid(condition);
		
		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		
		if(supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			
			List<Object> gridList = (List<Object>) result.get(1);
			for(Object data: gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;
				mapData.put("lastReadDate", TimeLocaleUtil.getLocaleDate((String)mapData.get("lastReadDate") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		return result;
	}
	
	
}




