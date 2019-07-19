package com.aimir.service.mvm;

import java.util.Map;

/**
 * 
 * @author SeJin Han
 *
 */
public interface LpReportManager {
	
	/**
     * LP를 기준으로 검침율 조사
     * @param condition : dcuName(String) timeType(String) startDate(String) endDate(String)
     * @return
     */
    public Map<String,Object> getValidLpRate(Map<String,Object> condition);
    
    
    /**
     * Meter를 기준으로 검침율 조사
     * @param condition : dcuName(String) timeType(String) startDate(String) endDate(String)
     * @return
     */
    public Map<String,Object> getValidMeterRate(Map<String,Object> condition);
    

}
