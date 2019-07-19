package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface EmsReportManager {
	public Map<String,Object> getEmsReportInfo(Map<String,Object> params);
	public Map<String,Object> getEnergySavingReportInfo(Map<String,Object> params);
	
	public Map<String, Object> getMonthlyEnergySavingInfo(Map<String, Object> params);
	public Map<String, Object> getYearlyEnergySavingInfo(Map<String, Object> params);
	public Map<String, Object> getZoneUsageInfo(Map<String, Object> params);
	public Map<String, Object> getLocationUsageInfo(Map<String, Object> params);

	public List<Map<String, Object>> getYearlyUsageStatisticReport( String yyyy, int locationId );
	public List<Map<String, Object>> getEnergyUsageInfo( String yyyymm, String energyType );
}
