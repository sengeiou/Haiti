package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;



public interface MeteringSLAManager {
	
	public List<Object> getMeteringSLASummaryGrid(Map<String, Object> condition);
	public List<Object> getMeteringSLAMiniChart(Map<String, Object> condition);
	
	// 검침율(Missing)
	public List<Object> getMeteringSLAMissingData(Map<String, Object> condition);
	public List<Object> getMeteringSLAMissingDetailChart(Map<String, Object> condition);
	public List<Object> getMeteringSLAMissingDetailGrid(Map<String, Object> condition);
	
}
