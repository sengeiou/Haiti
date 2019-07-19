package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ChangeLog;
import com.aimir.model.device.ChangeLogVO;

public interface ChangeLogDao extends GenericDao<ChangeLog, Long> {

	public List<ChangeLogVO> getChanageLogMiniChartData();

	public List<ChangeLog> getChangeLogs(String[] strArray);

	public Integer getChangeLogCount(String[] array);
}
