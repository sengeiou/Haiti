// INSERT SP-193
package com.aimir.dao.device;


import java.util.List;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.ThresholdWarning;

public interface ThresholdWarningDao extends GenericDao<ThresholdWarning, Integer> {
	
	public ThresholdWarning getThresholdWarning(DeviceType type, Integer deviceId, Integer thresholdId);
	
	public ThresholdWarning getThresholdWarning(String  ip, Integer thresholdId);
	
	public List<ThresholdWarning>getOverThresholdDevices(Integer thresholdId, Integer limit);
	
	public List<ThresholdWarning>getThresholdWarningList(Integer thresholdId);
}
