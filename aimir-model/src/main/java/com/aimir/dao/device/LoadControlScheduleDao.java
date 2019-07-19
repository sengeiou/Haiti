package com.aimir.dao.device;

import java.util.List;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.LoadControlSchedule;
import com.aimir.model.device.LoadShedSchedule;

public interface LoadControlScheduleDao extends GenericDao<LoadControlSchedule, Integer> {

	/**
	 * 그룹 유형(장비유형) 과 해당 ID 로 LoadControlSchedule 목록 구하기
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public List<LoadControlSchedule> getLoadControlSchedule(String targetType, String targetId);
	
	/**
	 * 특정 그룹에 속한 LoadControlSchedule 목록 반환
	 * @param targetId 검색할 그룹명의 아이디
	 * @return LoadControlSchedule 목록
	 */
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId);
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId, ScheduleType type);
}
