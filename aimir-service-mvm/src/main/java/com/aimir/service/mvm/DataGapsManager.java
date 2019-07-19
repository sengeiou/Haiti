package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface DataGapsManager {
	public Map<String,Object> getDataGaps(Map<String,Object> params);
	
	
	public Map<String,Object> getDataGaps2(Map<String,Object> params);
	
	/**
	 * @desc : fetch LpMissingMeters
	 * @param params
	 * @return
	 */
	public List<Object> getLpMissingMeters(Map<String,Object> params);
	
	
	
	/**
	 * @DEsc : 누락된 미터 list fetch manager 
	 * @param params
	 * @return
	 */
	public List<Object> getLpMissingMeters2(Map<String,Object> params);

	
	
	public List<Object> getLpMissingMetersExcel(Map<String,Object> params);
	public List<Object> getLpMissingCount(Map<String,Object> params);
}
