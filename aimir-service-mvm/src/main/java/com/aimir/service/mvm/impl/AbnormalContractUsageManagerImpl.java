package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.AbnormalContractUsageManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="abnormalContractUsageManager")
public class AbnormalContractUsageManagerImpl implements AbnormalContractUsageManager{

    Log logger = LogFactory.getLog(AbnormalContractUsageManagerImpl.class);
    	
	@Autowired
	DayEMDao dayEMDao;
	@Autowired
	SupplierDao supplierDao;
	
	public Map<String, Object> getAbnormalContractUsageEM(Map<String, Object> condition) {
        
		long total 		  = dayEMDao.getTotalCount(condition);
		List<Object> grid = dayEMDao.getAbnormalContractUsageEM(condition);

	    int count=1;
	 
		for(Object data: grid) {
			Map<String, Object> mapData = (Map<String, Object>) data;
			mapData.put("no", count);
			mapData.put("total", total);
			count++;
		}
	  
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", total);
		result.put("grid", grid);
		
		return result;
	}


    @SuppressWarnings("unchecked")
	public Map<String, Object> getAbnormalContractUsageEMList(Map<String, Object> condition) {
        
		Map<String, Object> result = new HashMap<String, Object>();
		
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(condition.get("supplierId").toString())).getMd());
        Supplier supplier = supplierDao.get(Integer.parseInt(condition.get("supplierId").toString()));
		
        long total = dayEMDao.getAbnormalContractUsageEMTotal(condition);
        result.put("total", total);

    	List<Object> grid = dayEMDao.getAbnormalContractUsageEMList(condition);
    	int page = (Integer)condition.get("page");
    	int rowPerPage = (Integer)condition.get("pageSize");
    	
    	int count=1;
    	for(Object obj: grid) {
    		Map<String, Object> data = (HashMap)obj;

    		Object contractUsage = data.get("contractUsage");
    		Object demandUsage   = data.get("demandUsage");

    		if ( StringUtil.nullToBlank(contractUsage).length() > 0 )
    			data.put("contractUsage", df.format(Double.parseDouble(contractUsage.toString())));

    		if ( StringUtil.nullToBlank(demandUsage).length() > 0 )
    			data.put("demandUsage", df.format(Double.parseDouble(demandUsage.toString())));
    		// 날짜 formatting
    		data.put("no", Integer.toString(CommonUtils2.makeIdxPerPage(String.valueOf(page), String.valueOf(rowPerPage), count)));
    		data.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)data.get("yyyymmdd") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
    		count++;
    	}

		result.put("grid", grid);

		return result;    
    }
}