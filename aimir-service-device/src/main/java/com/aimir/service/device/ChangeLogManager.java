package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.ChangeLog;
import com.aimir.model.device.ChangeLogSetting;
import com.aimir.model.device.ChangeLogVO;

public interface ChangeLogManager {

	public List<ChangeLogVO> getChanageLogMiniChartData();
	
	public List<ChangeLog> getChangeLogs(String[] strArray);
	
	public Map<String, String> getChangeLogCount(String[] strArray);
	
	public List<ChangeLogSetting> getChangeLogSettings(String[] strArray);
}
