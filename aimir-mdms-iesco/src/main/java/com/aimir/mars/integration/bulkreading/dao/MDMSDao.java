package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;

public interface MDMSDao extends GenericDao<MDMBatchLog, Integer> {
		
	// getMDMSStatistics
	public List<Map<String, Object>> getMDMSStatistics(Map<String, Object> params) throws Exception;
	
	// getMDMSBatchCount
	public int getMDMSBatchCount(Map<String, Object> params) throws Exception;
	
	// getMDMSBatchList
	public List<Map<String, Object>> getMDMSBatchList(Map<String, Object> params) throws Exception;
	
	// getMDMSLPCount
	public int getMDMSLPCount(Map<String, Object> params) throws Exception;
		
	// getMDMSLPList
	public List<Map<String, Object>> getMDMSLPList(Map<String, Object> params) throws Exception;
	
	// getMDMSDailyCount
	public int getMDMSDailyCount(Map<String, Object> params) throws Exception;
			
	// getMDMSDailyList
	public List<Map<String, Object>> getMDMSDailyList(Map<String, Object> params) throws Exception;
	
	// getMDMSMonthlyCount
	public int getMDMSMonthlyCount(Map<String, Object> params) throws Exception;
			
	// getMDMSMonthlyList
	public List<Map<String, Object>> getMDMSMonthlyList(Map<String, Object> params) throws Exception;
	
	// getMDMSEventCount
	public int getMDMSEventCount(Map<String, Object> params) throws Exception;
			
	// getMDMSEventList
	public List<Map<String, Object>> getMDMSEventList(Map<String, Object> params) throws Exception;
	
}