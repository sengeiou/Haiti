package com.aimir.service.device.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.ChangeLogSettingDao;
import com.aimir.model.device.ChangeLog;
import com.aimir.model.device.ChangeLogSetting;
import com.aimir.model.device.ChangeLogVO;
import com.aimir.service.device.ChangeLogManager;

@Service(value = "changeLogManager")
public class ChangeLogManagerImpl implements ChangeLogManager {

	@Autowired
	ChangeLogDao changeLogDao;
	
	@Autowired
	ChangeLogSettingDao changeLogSettingDao;	
	
	public List<ChangeLogVO> getChanageLogMiniChartData() {
		
		return changeLogDao.getChanageLogMiniChartData();
	}

	public List<ChangeLog> getChangeLogs(String[] strArray) {

		return changeLogDao.getChangeLogs(strArray);
	}

	public Map<String, String> getChangeLogCount(String[] array) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("totalRecordCount", changeLogDao.getChangeLogCount(array) + "");
		
		return map;		
	}

	public List<ChangeLogSetting> getChangeLogSettings(String[] array) {

		return changeLogSettingDao.getChangeLogSettings(array);
	}
}
