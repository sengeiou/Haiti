package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.EndDeviceLog;

public interface EndDeviceLogManager {
	
	public List<EndDeviceLog> getEndDeviceLogsExt(int locationId,int endDeviceId, int start, int limit, int supplierId);
	public List<EndDeviceLog> getEndDeviceLogs(int locationId,int endDeviceId);
	public List<EndDeviceLog> getEndDeviceLogsByZone(Map<String,Object> params);
	public long getTotalSize(int locationId,int endDeviceId);
	
	public void addEndDeviceLogs(EndDeviceLog endDeviceLog);
	
	
}
