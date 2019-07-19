package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.aimir.model.device.MeterTimeSyncLog;

@WebService(name="meterTimeService", targetNamespace="http://aimir.com/services")
public interface MeterTimeManager {

	// TimeDiff
	public List<Object> getMeterTimeTimeDiffChart(Map<String, Object> condition);
	public List<Object> getMeterTimeTimeDiffComplianceChart(Map<String, Object> condition);
	public List<Object> getMeterTimeTimeDiffGrid(Map<String, Object> condition);
	
	// SyncLog 
	public List<Object> getMeterTimeSyncLogChart(Map<String, Object> condition);
	public List<Object> getMeterTimeSyncLogAutoChart(Map<String, Object> condition);
	public List<Object> getMeterTimeSyncLogManualChart(Map<String, Object> condition);
	public List<Object> getMeterTimeSyncLogGrid(Map<String, Object> condition);
	public List<Object> getMeterTimeThresholdGrid(Map<String, Object> condition);
	
	
	@WebMethod
	public void insertMeterTimeSycLog(MeterTimeSyncLog log);

}
