package com.aimir.dao.integration;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.integration.WSMeterConfigLog;
import com.aimir.model.integration.WSMeterConfigLogPk;

public interface WSMeterConfigLogDao extends GenericDao<WSMeterConfigLog, WSMeterConfigLogPk> {
	/**
	 * @param deviceId
	 * @param AsyncTrId
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public WSMeterConfigLog getByAsyncTrId(String deviceId, String AsyncTrId, String command) throws Exception;
	/**
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<WSMeterConfigLog> getLogListByCondition(Map<String,Object> condition) throws Exception;
	/**
	 * @param requestDate
	 * @param deviceId
	 * @param trId
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public WSMeterConfigLog  get(String requestDate, String deviceId, String trId, String command) throws Exception;
	
}
