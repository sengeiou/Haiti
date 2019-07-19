package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;

public interface GroupDao extends GenericDao<AimirGroup, Integer> {
	
	/**
     * method name : getGroupList
     * method Desc : 조회조건에 해당하는 그룹 리스트를 리턴한다.
	 * 
	 * @param condition
	 * @return List of AimirGroup @see com.aimir.model.system.AimirGroup
	 */
	public List<AimirGroup> getGroupList(Map<String, Object> condition);

	/**
     * method name : getGroupListWithChild
     * method Desc : 조회조건에 해당하는 그룹리스트와 멤버 목록을 리턴한다.
     * 
	 * @param operatorId Operator.id
	 * @return
	 */
	public List<Object> getGroupListWithChild(Integer operatorId);

    /**
     * method name : getGroupListWithChildNotinHomeGroupIHD
     * method Desc : 조회조건에 해당하는 그룹리스트와 멤버 목록을 리턴한다. HomeGroup/IHD 제외.
     * 
     * @param operatorId Operator.id
     * @return
     */
	@Deprecated
    public List<Object> getGroupListWithChildNotinHomeGroupIHD(Integer operatorId);

    /**
     * method name : getGroupListNotHomeGroupIHD<b/>
     * method Desc : Group Management 가젯에서 Group List 를 조회한다. HomeGroup/IHD 제외.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getGroupListNotHomeGroupIHD(Map<String, Object> conditionMap);

	/**
     * method name : getGroupListMeter
     * method Desc : 공급사 아이디에 해당하는 그룹 목록(그룹타입이 미터인) 을 리턴한다.
     * 
	 * @param supplierId 
	 * @return List of AimirGroup @see com.aimir.model.system.AimirGroup
	 */
	public List<AimirGroup> getGroupListMeter(Integer operatorId);
	
	/**
     * method name : getContractGroup
     * method Desc : 공급사 아이디에 해당하는 그룹 목록(그룹타입이 Contract인) 을 리턴한다.
     * 
	 * @param supplierId
	 * @return List of AimirGroup @see com.aimir.model.system.AimirGroup
	 */
	public List<AimirGroup> getContractGroup(Integer operatorId);
	
	/**
     * method name : getChildren
     * method Desc : group id에 해당하는 멤버 child 목록을 리턴한다.
     * 
	 * @param groupId
	 * @return List of GroupMember @see com.aimir.model.system.GroupMember
	 */
	public List<GroupMember> getChildren(Integer groupId);
	
	/**
     * method name : count
     * method Desc : 전체 그룹 카운트를 리턴
     * 
	 * @return
	 */
	public Integer count();

    /**
     * method name : getGroupComboDataByType<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 GroupType 의 Group Combo Data 를 조회한다. 
     *
     * @param conditionMap
     * {@code}
     *         Integer operatorId = (Integer)conditionMap.get("operatorId");
     *         String groupType = (String)conditionMap.get("groupType");
     * @return List of Map {AimirGroup.id AS id, AimirGroup.name AS name }
     */
    public List<Map<String, Object>> getGroupComboDataByType(Map<String, Object> conditionMap);

    /**
     * method name : getGroupTypeByGroup<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 Job 의 Group Type 을 조회한다. 
     *
     * @param conditionMap
     * {@code}
     *        String groupId = (String)conditionMap.get("groupId");
     * @return groupType Name
     */
    public String getGroupTypeByGroup(Map<String, Object> conditionMap);
    
    /**
     * method name : dupCheckGroupName<b/>
     * method Desc : Group Management 가젯에서 그룹이름이 중복되었는지 체크
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> dupCheckGroupName(Map<String, Object> conditionMap);
    
    /**
     * method name : getGroupListMcu<b/>
     * method Desc : Group Schedule 팝업창에서 DCU 그룹 리스트 출력
     *
     * @param Integer
     * @return
     */
    public List<Map<String, Object>> getGroupListMcu(Integer operatorId);
    
    /**
     * method name : getSelectedListMcu<b/>
     * method Desc : Group Schedule 팝업창에서 Selected DCU 리스트 출력
     *
     * @param conditionMap
     * @return
     */
	public List<Map<String, Object>> getSelectedListMcu(Map<String, Object> conditionMap);
	
	/**
     * method name : getGroupListMcu<b/>
     * method Desc : Group Schedule 팝업창에서 Selected DCU Total 수 출력
     *
     * @param Integer
     * @return
     */
	public int getSelectedCountMcu(Integer groupId);
    
}