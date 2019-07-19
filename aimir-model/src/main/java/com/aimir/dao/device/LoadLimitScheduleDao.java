package com.aimir.dao.device;

import java.util.List;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.model.device.LoadShedSchedule;

public interface LoadLimitScheduleDao extends GenericDao<LoadLimitSchedule, Integer> {

	/**
	 * 그룹 유형(장비유형) 과 해당 ID 로 LoadShedScheduleDao 목록 구하기
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetType, String targetId);
	
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId);
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId, ScheduleType scheduleType);
}
