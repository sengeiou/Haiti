package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandLogPk;

public interface AsyncCommandLogDao extends GenericDao<AsyncCommandLog, AsyncCommandLogPk> {
	
	public Integer getCmdStatus(String deviceId, String cmd);
	
	public Integer getCmdStatusByTrId(String deviceId, long trId) throws Exception;
	
	public List<AsyncCommandLog> getLogListByCondition(Map<String,Object> condition) throws Exception;
	
	public Long getMaxTrId(String deviceId);

	public Long getMaxTrId(String deviceId, String cmd);
	
	public List<Object> getCommandLogList(Map<String, Object> condition) throws Exception;
	
	public List<Map<String, Object>> getCommandLogListTotalCount(Map<String, Object> condition) throws Exception;

}
