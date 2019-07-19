package com.aimir.service.mvm.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.mvm.SAPIntegrationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.SAPIntegrationManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;


@WebService(endpointInterface = "com.aimir.service.mvm.SAPIntegrationManager")
@Service(value="SAPIntegrationManager")
@Transactional(readOnly=false)
public class SAPIntegrationManagerImpl implements SAPIntegrationManager {
	
	@Autowired
	SAPIntegrationDao sapIntegrationDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	public List<Object> getOutBoundGridData(Map<String, Object> condition) {
		List<Object> result = sapIntegrationDao.getOutBoundGridData(condition);
		
		List<Object> gridList = (List<Object>) result.get(0);
		
		//공급사에 맞춰서 Date format setting
		String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
		if(supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			
			for(Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;
				mapData.put("OUTBOUND_DATE", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("OUTBOUND_DATE")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		return result;
	}
	
	public List<Object> getInBoundGridData(Map<String, Object> condition) {
		
		String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
		if(!("".equals(condition.get("outboundDate")))) {
			if(supplierId.length() > 0) {
				Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
				//공급사에 맞춰서 formatting된 date를 DB검색을 위한 용도로 format 변경
				condition.put("outboundDate", TimeLocaleUtil.getDBDate(StringUtil.nullToBlank(condition.get("outboundDate")) , 14, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		List<Object> result = sapIntegrationDao.getInBoundGridData(condition);
		
		List<Object> gridList = (List<Object>) result.get(0);
		
		int inboundTotalCnt_Total = 0;
		int inboundMeterCnt_Total = 0;
		
		if(supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			
			for(Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;
				//공급사에 맞춰서 Date format setting,
				mapData.put("INBOUND_DATE", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("INBOUND_DATE")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				//DB에서 검색된 INBOUND_TOTALCNT 값의 total값을 구함
				inboundTotalCnt_Total += Integer.parseInt(mapData.get("INBOUND_TOTALCNT").toString());
				//DB에서 검색된 INBOUND_METERCNT 값의 total값을 구함
				inboundMeterCnt_Total += Integer.parseInt(mapData.get("INBOUND_METERCNT").toString());
			}
		}
		
		result.add(inboundTotalCnt_Total);
		result.add(inboundMeterCnt_Total);
		
		return result;
	}

	public List<Object> getErrorLogGridData(Map<String, Object> condition) {
		
		String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
		if(!("".equals(condition.get("outboundDate")))) {
			if(supplierId.length() > 0) {
				Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
				//공급사에 맞춰서 formatting된 date를 DB검색을 위한 용도로 format 변경
				condition.put("outboundDate", TimeLocaleUtil.getDBDate(StringUtil.nullToBlank(condition.get("outboundDate")) , 14, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		List<Object> result = sapIntegrationDao.getErrorLogGridData(condition);
		
		return result;
	}
}
