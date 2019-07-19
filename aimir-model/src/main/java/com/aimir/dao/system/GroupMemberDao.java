package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.GroupMember;

public interface GroupMemberDao extends GenericDao<GroupMember, Integer> {
	
	/**
	 * method name : updateData
	 * method Desc : GroupMember의 group id정보를 업데이트 한다.
	 * 
	 * @param id GroupMember.id
	 * @param groupId GroupMember.aimirGroup.id
	 * @return 
	 * @throws Exception
	 */
	public int updateData(Integer id, Integer groupId) throws Exception;
	
	/**
	 * method name : getGroupMember
	 * method Desc : 
	 * 
	 * @param condition - no use (파라미터 조건에 상관없이 GroupMember의 정보들을 리턴)
	 * 
	 * @return List of Object {GroupMember.id as data, 
	 * 							GroupMember.member as label}
	 */
	
	/**
	 * method name : updateMCURegirationList
	 * method Desc : mcu에 등록된 Memeber들의 isRegistration필드에 등록이 완료되었음을 표시한다.
	 */
	public int updateMCURegirationList(Boolean isRegistration, List<String> member, Integer groupId);
	
	@Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : 그룹 관리 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectedData(Map<String, Object> conditionMap);
    
    /**
     * method name : getHomeGroupMemberSelectedData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getHomeGroupMemberSelectedData(Map<String, Object> conditionMap);
	
	
    /**
	 * method name : getGroupMemberById
	 * method Desc : group id로 GroupMember Set를 리턴
	 * 
     * @param groupId GroupMember.aimirGroup.id
     * @return Set Of GroupMember @see com.aimir.model.system.GroupMember
     */
	public Set<GroupMember> getGroupMemberById(Integer groupId); 
	
	
	/**
	 * method name : getLoadShedGroupMembers
	 * method Desc : 특정 그룹에 속하는 member 중 타입과 이름이 매치되는 member들을 검색
	 * 
	 * @param targetType member의 타입(Loacation, Operator, etc..)
	 * @param targetName 찾는 member의 이름
	 * @param aimirGroupName 그룹 구분자. AimirGroup 의 DisriminatorValue
	 * @return 검색된 멤버 목록
	 * 			List of Object {GroupMember.id as targetId, 
	 * 							GroupMember.member as targetName, 
	 * 							Group.id as groupId}
	 */
	public List<Object> getLoadShedGroupMembers(String targetType, String targetName);
	
	/**
	 * method name : updateGroupMember
	 * method Desc : GroupMember  정보의 member 필드를 업데이트 한다.
	 * 
	 * @param id Group.id 
	 * @param member GroupMember.member
	 * @return
	 * @throws Exception
	 */
	public int updateGroupMember(Integer id, String member) throws Exception;
	
	/**
	 * method name : getMeterGroupMemberIds
	 * method Desc : 미터 그룹 맴버들의 mds id 목록을 조회한다. 해당 group id 가 미터 그룹이 아니면 빈 목록을 반환한다.
	 * @param groupId
	 * @return
	 */
	public List<String> getMeterGroupMemberIds(Integer groupId);

	/**
	 * method name : getGroupIdbyMember
	 * method Desc : 멤버 아이디로 그룹 아이디를 조회한다.
	 * 
	 * @param memberId
	 * @return Group Id.
	 */
	public Integer getGroupIdbyMember(String memberId);
	
	/**
	 * method name : getMemberByLocation
	 * method Desc : Location 기준으로 미터 목록을 읽어온다. 
	 * @param groupID
	 * @return
	 */
	public Set<GroupMember> getMeterSerialsByLocation(Integer groupId);
}
