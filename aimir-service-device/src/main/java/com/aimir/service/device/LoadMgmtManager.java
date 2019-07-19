package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.LoadControlSchedule;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;

public interface LoadMgmtManager {

	//################## 공통 #################
	/**
	 * 그룹타입 목록을 반환한다. 
	 * @return
	 */
	public List<Object> getGroupTypeCombo();

	/**
	 * LoadShedGroup 의 LoadType 목록을 반환한다. Emergency, Schedule, Ondemand
	 * @return
	 */
	public List<Object> getLoadTypeCombo();
	
	/**
	 * WeekDay 타입을 반환. ex) 월, 화, 수, 목 or Mon, Tue, Wed
	 * @return
	 */
	public List<Object> getWeekDayCombo();
	
	/**
	 * 0 ~ 23 시 목록
	 * @return
	 */
	public List<Object> getHourCombo();
	
	/**
	 * 0 ~ 59 분 목록
	 * @return
	 */
	public List<Object> getMinuteCombo();
	
	/**
	 * On / Off 선택 목록
	 * @return
	 */
	public List<Object> getOnOffCombo();
	
	public List<Object> getLimitTypeCombo();
	
	public List<Object> getPeakTypeCombo();
	/**
	 * 그룹명 목록을 반환한다. AimirGroup 의 name 컬럼
	 * @return
	 */
	public List<LoadShedGroup> getGroupListCombo(String operatorId);
	
	
	/**
	 * operator의 id를 받아 그룹별 DR고객 목록을 반환한다
	 * @param operatorId
	 * @return 각각의 고객목록이 담긴 그룹을 인자로 하는 리스트
	 */
	public Map<String, Object> getDRGroupList(String operatorId);
	
	public int saveGroups(List<Object> groups) throws Exception;
	//public int updateGroups(List<Object> groups) throws Exception;
	//public int deleteGroups(List<Object> groups) throws Exception;
	
	//################## LOAD CONTROL ###################
	public List getLoadShedGroupMembers(String targetType, String targetName);
	public LoadControlSchedule addLoadControlSchedule(Map<String, Object> condition);
	
	//################## LOAD LIMIT  #####################
	public LoadLimitSchedule addLoadLimitSchedule(Map<String, Object> condition);
	
	//################## LOAD SHED GROUP #################
	/**
	 * LoadShedGroup 목록과 각 그룹에 해당하는 LoadShedSchedule 을 반환한다.
	 */
	public Map<String, Object> getLoadShedGroupWithSchedule(String operatorId);
	public List<LoadShedGroup> getLoadShedGroupWithoutSchedule(String groupType, String groupName);
	//public Map<String, Object> getLoadShedGroupBySchedule(Map<String, Object> condition);
	public List<LoadShedScheduleVO> getLoadShedGroupBySchedule(Map<String, Object> condition);
	public LoadShedSchedule addLoadShedSchedule(Map<String, Object> condition);
	
	
	/**
	 * 특정 그룹의 LoadControlSchedule 리스트를 반환한다.
	 * @param groupId 그룹ID
	 * @return 해당 그룹의 LoadShedSchedule 리스트
	 */
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId);
	public List<LoadControlSchedule> getLoadControlScheduleByDate(String targetId);
	public List<LoadControlSchedule> getLoadControlScheduleByWeekday(String targetId);
	
	/**
	 * 특정 그룹의 LoadLimitSchedule 리스트를 반환한다.
	 * @param groupId 그룹ID
	 * @return 해당 그룹의 LoadLimitSchedule 리스트
	 */
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId);
	public List<LoadLimitSchedule> getLoadLimitScheduleByDate(String targetId);
	public List<LoadLimitSchedule> getLoadLimitScheduleByWeekday(String targetId);
	
	/**
	 * 특정 그룹의 LoadShedSchedule 리스트를 반환한다.
	 * @param groupId 그룹ID
	 * @return 해당 그룹의 LoadShedSchedule 리스트
	 */
	public List<LoadShedSchedule> getLoadShedSchedule(Integer targetId);
	public List<LoadShedSchedule> getLoadShedScheduleByDate(Integer targetId);
	public List<LoadShedSchedule> getLoadShedScheduleByWeekday(Integer targetId);
	
	
	public void deleteLoadControlSchedule(String[] targetIds);
	public void deleteLoadLimitSchedule(String[] targetIds);
	public void deleteLoadShedSchedule(String[] targetIds);
	
	public int deleteGroups(List<Object> groups); 
}
