package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ChangeLogSetting;

public interface ChangeLogSettingDao extends GenericDao<ChangeLogSetting, Long> {

	List<ChangeLogSetting> getChangeLogSettings(String[] array);

}
