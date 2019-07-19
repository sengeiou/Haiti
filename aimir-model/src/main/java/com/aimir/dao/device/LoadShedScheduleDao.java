package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;

public interface LoadShedScheduleDao extends GenericDao<LoadShedSchedule, Integer> {

	/**
	 * LoadShedGroup 과 각 그룹에 속한 LoadShedSchedule 목록 반환
	 * @return LoadShedGroup 목록을 반환한다. 각 LoadShedGroup 은 LoadShedSchedule
	 * 을 멤버로 가지고 있다.
	 */
	//public List<Object> getLoadShedScheduleWithGroup();
	
	/**
	 * 특정 그룹에 속한 LoadShedSchedule 목록 반환
	 * @param groupId 검색할 그룹명의 아이디
	 * @return LoadShedSchedule 목록
	 */
	public List<LoadShedSchedule> getLoadShedSchedule(Integer groupId);
	
	public List<LoadShedSchedule> getLoadShedSchedule(Integer targetId, ScheduleType scheduleType);
	
	
	
	/**
	 * LoadShedSchedule에 대해 특정 그룹, 시작일~종료일 검색 결과 반환
	 * @param groupType 그룹 타입
     * @param groupName 그룹 이름
     * @param scheduleType
	 * @param startDate 시작일
	 * @param endDate 종료일
     * @param dayOfWeek
	 * @return
	 */
	public List<LoadShedScheduleVO> getLoadShedSchedule(String groupType, String groupName, String scheduleType, String startDate, String endDate, String dayOfWeek);
	
	/**
	 * 그룹타입과 스케쥴 타입에 해당하는 LoadShedSchedule 검색 
	 * @param groupType
	 * @param scheduleType Date, S
	 * @return
	 */
	public List<LoadShedSchedule> getLoadShedSchedule(String groupType, String scheduleType);
	
	public List<LoadShedSchedule> searchLoadShedSchedule(Integer operatorId,
			String groupType, String startDate, String endDate);
	
}
