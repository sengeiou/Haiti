package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMMeterEventLog;

public interface MDMMeterEventLogDao extends GenericDao<MDMMeterEventLog, Integer> {
	
	public List<Map<String, Object>> select(Map<String, Object> condition);
	
	public void delete(Map<String, Object> condition);
	
	public void updateTransferDate(int batchId);
	
	public void updateInitTransferDate(List<MDMMeterEventLog> mdmMeterEventLogList);
}