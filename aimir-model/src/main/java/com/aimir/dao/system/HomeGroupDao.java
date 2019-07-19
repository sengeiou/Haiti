package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MCU;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;

public interface HomeGroupDao extends GenericDao<HomeGroup, Integer> {
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public List<HomeGroup> getGroupList(Map<String, Object> condition);
	
	/**
	 * method Name : getHomeGroupList
	 * method Desc : HomeGroup Management 가젯에서 HomeGroup과 IHD 리스트를 조회해 온다.
	 *
	 * @param condition
	 * @return
	 */
	public List<Map<String, Object>> getHomeGroupList(Map<String, Object> condition);
	
	/**
	 * 
	 * @param operatorId
	 * @return
	 */
	public List<Object> getGroupListWithChild(Integer operatorId);
	
	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public List<GroupMember> getChildren(Integer groupId);
	
	/**
	 * 
	 * @return
	 */
	public Integer count();
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public HomeGroup getHomeGroup(String name);	

    /**
     * method name : getHomeGroupMcuByGroupId
     * method Desc : Group ID 로 MomeGroup 의 MCU 클래스를 조회한다.
     *
     * @param groupId
     * @return
     */
    public MCU getHomeGroupMcuByGroupId(Integer groupId);
    
    /**
     * method name : getMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 로 등록 가능한 HomeGroup 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);
}
