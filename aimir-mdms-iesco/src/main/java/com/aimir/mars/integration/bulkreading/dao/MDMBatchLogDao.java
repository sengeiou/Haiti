package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;

public interface MDMBatchLogDao extends GenericDao<MDMBatchLog, Integer> {
	
	public void updateBatchStatus(Map<String, Object> condition);	
	
	public List<MDMBatchLog> selectBatchList(Map<String, Object> condition);
}