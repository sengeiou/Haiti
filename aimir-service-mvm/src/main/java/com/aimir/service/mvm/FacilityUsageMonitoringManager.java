package com.aimir.service.mvm;

import java.util.Map;

public interface FacilityUsageMonitoringManager {
	
	/**
	 * 주기별(일,주,월,분기) 설비별 사용량을 조회한다.
	 * @param params 
	 * - 주기구분(일,주,월,분기)
	 * - Enddevice 분류구분코드
	 * @return
	 */
	public Map<String,Object> getFacilityUsageByPeriod(Map<String,Object> params);
	
	/**
	 * 모든 주기(일,주,월,분기)의 설비별 사용량을 조회한다.
	 * @param params
	 * @return
	 */
	public Map<String,Object> getFacilityUsageAllPeriod(Map<String,Object> params);
	
	/**
	 * 주기별(일,주,월,분기) Zone별 사용량을 조회한다.
	 * @param params 
	 * - 주기구분(일,주,월,분기)
	 * - zoneId 분류구분코드
	 * @param params
	 * @return
	 */
	public Map<String,Object> getZoneUsageByPeriod(Map<String,Object> params);
	/**
	 * 모든 주기(일,주,월,분기)의 Zone별 사용량을 조회한다.
	 * @param params
	 * @return
	 */
	public Map<String,Object> getZoneUsageAllPeriod(Map<String,Object> params);

}
