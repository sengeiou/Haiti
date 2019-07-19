package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface PowerQualityManager {

	public List<Object> getTypeViewCombo();
	
	public Map<String, Object> getPowerQuality(Map<String, Object> condition);    
    public Map<String, Object> getPowerQualityList(Map<String, Object> condition);
 	public Map<String, Object> getPowerQualityListForSoria(Map<String, Object> condition);	   // INSERT SP-204
 	public Map<String, Object> getPowerInstrumentList(Map<String, Object> condition);
	public Map<String, Object> getPowerDetailList(Map<String, Object> condition);
	/**
	 * @MethodName getDailyPowerQualityData
	 * @Date 2014. 1. 17.
	 * @param condition (String) date, (double) seg, (double) swell, (double) vol, (Integer) supplierId
	 * @return
	 * @Modified
	 * @Description
	 */
	public Map<String, Object> getDailyPowerQualityData(Map<String, Object> condition);
}
