package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.system.GroupMember;

public interface LoadShedGroupDao extends GenericDao<LoadShedGroup, Integer> {

	/**
	 * LoadShedGroup 리스트를 반환(그룹명)
	 * @return
	 */
	public List<Object> getLoadShedGroupList();
	
	/**
	 * LoadShedGroup 리스트를 반환(그룹명)
	 * @param operatorId
	 * @return
	 */
	public List<LoadShedGroup> getLoadShedGroupList(Integer operatorId);
	
	/**
	 * 스케쥴이 없는 LoadShedGroup 리스트를 반환
	 * @param groupType
	 * @param groupName
	 * @return LoadShedGroup 리스트
	 */
	public List<LoadShedGroup> getLoadShedGroupListWithoutSchedule(String groupType, String groupName);
	
	/**
	 * LoadShedGroup 목록과 각 그룹의 멤버 반환.
	 * @param operatorId
	 * @return 각 LoadShedGroup의 정보를 담은 리스트. 각 그룹 정보는 Map 형태.
	 */
	public List<Object> getGroupListWithChild(Integer operatorId);
	public List<Object> getGroupListWithChild2(Integer operatorId);
	
	/**
	 * 특정 그룹의 자식 노드 리스트를 반환
	 * @param groupId
	 * @return
	 */
	public List<GroupMember> getChildren(Integer groupId);

	/**
	 * LoadShedGroup 에 저장된 그룹 개수를 반환
	 * @return
	 */
	public Integer count();

}
