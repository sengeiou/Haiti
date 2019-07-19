package com.aimir.dao.integration;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.integration.WSMeterConfigResult;
import com.aimir.model.integration.WSMeterConfigResultPk;


public interface WSMeterConfigResultDao extends GenericDao<WSMeterConfigResult, WSMeterConfigResultPk> {

	/**
	 * @param deviceId
	 * @param asyncTrId
	 * @param command
	 * @return
	 */
	public List<WSMeterConfigResult> getResultsByAsyncTrId(String deviceId, String asyncTrId, String command) ;

	/**
	 * @param requestDate
	 * @param deviceId
	 * @param trId
	 * @param command
	 * @return
	 */
	public List<WSMeterConfigResult> getResults(String requestDate,String deviceId, String trId, String command) ;
	
	/**
	 * @param deviceId
	 * @param asyncTrId
	 * @param resultValue
	 * @param command
	 */
	public void addByAsyncTrId(String deviceId, String asyncTrId, String resultValue, String command) ;

	
}

