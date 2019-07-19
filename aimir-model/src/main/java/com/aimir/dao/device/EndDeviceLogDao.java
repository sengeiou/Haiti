package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.EndDeviceLog;

public interface EndDeviceLogDao extends GenericDao<EndDeviceLog, Long> {
	public long getTotalSize(List<Integer> location);
	public List<EndDeviceLog> getEndDeviceLogs(int start, int limit);
	public List<EndDeviceLog> getEndDeviceLogByEndDeviceId(List<Integer> endDeviceId);
	public List<EndDeviceLog> getEndDeviceLogByLocationId(List<Integer> location, int start, int limit);
}
