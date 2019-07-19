package com.aimir.service.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;

@WSDLDocumentation("AIMIR Group Information Management( for grouping Location, Device, Meter, Customer etc..)")
@WebService(name="GroupMgmtService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface GroupMgmtManager {

    @WebMethod
    @WebResult(name="GroupTypeComboList")
    public List<Object> getGroupTypeCombo();
    
    /**
     * HomeGroupType 콤보 조회 (HomeGroup/IHD)
     */
    @WebMethod
    @WebResult(name="HomeGroupTypeCombo")
    public List<Object> getHomeGroupTypeCombo();

    /**
     * GroupType 콤보 조회 (HomeGroup/IHD 제외)
     */
    @WebMethod
    @WebResult(name="GroupTypeComboNotinHomeGroupIHD")
    public List<Object> getGroupTypeComboNotinHomeGroupIHD();

    @WebMethod
    @WebResult(name="MeterGroupList")
    public List<AimirGroup> getMeterGroup(
            @WebParam(name ="operatorId")int operatorId);

    @WebMethod
    @WebResult(name="ContractGroupList")
    public List<AimirGroup> getContractGroup(
            @WebParam(name ="operatorId")int operatorId);

    @WebMethod
    @WebResult(name="GroupListList")
    public Map<String, Object> getGroupList(
            @WebParam(name ="operatorId")String operatorId);

    /**
     * operator의 id 를 받아 그룹 목록을 반환한다. HomeGroup/IHD 제외.
     */
    @WebMethod
    @WebResult(name="GroupListNotinHomeGroupIHD")
    @Deprecated
    public Map<String, Object> getGroupListNotinHomeGroupIHD(@WebParam(name="operatorId") String operatorId);

    /**
     * method name : getGroupListNotHomeGroupIHD<b/>
     * method Desc : Group Management 가젯에서 Group List 를 조회한다. HomeGroup/IHD 제외.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="GroupListNotHomeGroupIHD")
    public List<Map<String, Object>> getGroupListNotHomeGroupIHD(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
   
    /**
     * method name : dupCheckGroupName<b/>
     * method Desc : Group Management 가젯에서 그룹이름이 중복되었는지 체크
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="dupCheckGroupName")
    public List<Map<String, Object>> dupCheckGroupName(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    
    
    /**
     * method name : getHomeGroupList<b/>
     * method Desc : HomeGroup Management 가젯에서 HomeGroup/IHD Group List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="GroupList")
    public List<Map<String, Object>> getHomeGroupList(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 등록 가능한 Group Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MemberSelectData")
    public List<Object> getMemberSelectData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getMemberSelectedData<b/>
     * method Desc : Group Management 가젯에서 Group Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MemberSelectedData")
    public List<Object> getMemberSelectedData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    /**
     * method name : getMemberSelectedData<b/>
     * method Desc : HomeGroup Management 가젯에서 Group Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="HomeGroupMemberSelectedData")
    public List<Object[]> getHomeGroupMemberSelectedData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : addGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 저장한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult(name="addGroupMembers")
    public Integer addGroupMembers(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : addGroupMemberList<b/>
     * method Desc : HomeGroup Management 가젯에서 Group Member List 를 저장한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult(name="addGroupMemberList")
    public Integer addGroupMemberList(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    
    /**
     * method name : removeGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 삭제한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult(name="removeGroupMembers")
    public Integer removeGroupMembers(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    /**
     * method name : removeGroupMemberList<b/>
     * method Desc : Group Management 가젯에서 Group Member List 를 삭제한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult(name="removeGroupMemberList")
    public Integer removeGroupMemberList(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    @WebMethod
    @WebResult(name="saveGroups")
    @Deprecated
    public int saveGroups(@WebParam(name ="groups")List<Object> groups) throws Exception;

    /**
     * method name : saveGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 등록/수정 한다.
     *
     * @param conditionMap
     */
    @WebMethod
    @WebResult(name="saveGroup")
    public void saveGroup(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    /**
     * method name : saveIHDHomeGroup<b/>
     * method Desc : HomeGroup Management 가젯에서 IHD와 HomeGroup 을 등록/수정 한다.
     *
     * @param conditionMap
     */
    @WebMethod
    @WebResult(name="saveIHDHomeGroup")
    public void saveIHDHomeGroup(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : copyGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 복사한다.
     *
     * @param conditionMap
     */
    @WebMethod
    @WebResult(name="copyGroup")
    public void copyGroup(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : deleteGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 삭제한다.
     *
     * @param conditionMap
     */
    @WebMethod
    @WebResult(name="deleteGroup")
    public void deleteGroup(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name="updateGroups")
    @Deprecated
    public int updateGroups(
            @WebParam(name ="groups")List<Object> groups) throws Exception;

    @WebMethod
    @WebResult(name="deleteGroups")
    @Deprecated
    public int deleteGroups(
            @WebParam(name ="groups")List<Object> groups) throws Exception;

    @WebMethod
    @WebResult(name="GroupMemberMap")
    @Deprecated
    public Map<String, Object> getGroupMember(
            @WebParam(name ="condition")Map<String, Object> condition);

    @WebMethod
    @WebResult(name="saveGroupMembers")
    public int saveGroupMembers(
            @WebParam(name ="members")List<Object> members) throws Exception;

    @WebMethod
    @WebResult(name="GroupMemberByIdSet")
    public Set<GroupMember> getGroupMemberById(
            @WebParam(name ="groupId")Integer groupId);

    /*
     * 멤버 아이디로 그룹 아이디를 조회
     */
    @WebMethod
    @WebResult(name="getGroupIdbyMember")
    public int getGroupIdbyMember(
            @WebParam(name ="mdsId")String mdsId);

    @WebMethod
    @WebResult(name="HomeGroup")
    public HomeGroup saveHomeGroup(
            @WebParam(name ="homeGroup")HomeGroup homeGroup,
            @WebParam(name ="saveOrUpdate")String saveOrUpdate) throws Exception;

    @WebMethod
    @WebResult(name="getGroupComboDataByType")
    public List<Map<String, Object>> getGroupComboDataByType(
            @WebParam(name ="conditionMap")Map<String, Object> conditionMap);
    
    @WebMethod
    @WebResult(name="getMeterGroupMemberIds")
    public List<String> getMeterGroupMemberIds(Integer meterGroupId);
    
    /**
     * getMcuGroup  조회
     */
    @WebMethod
    @WebResult(name="McuGroupList")
    public List<Map<String, Object>> getMcuGroup(
            @WebParam(name ="operatorId")int operatorId);
    /**
     * getSelectedMcuList 조회
     */
    @WebMethod
    @WebResult(name="getSelectedMcuList")
    public List<Map<String, Object>> getSelectedMcuList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);
    /**
     * getSelectedMcuCount 조회
     */
    @WebMethod
    @WebResult(name="getSelectedMcuCount")
    public int getSelectedMcuCount(
    		@WebParam(name ="groupId")Integer groupId);

    /**
     * addGroupFailList 
     */
    @WebMethod
    @WebResult(name="addGroupMemberList")
    public Integer addGroupFailList(
    		@WebParam(name="groupId") Integer groupId,
    		@WebParam(name="failList") List<String> failList);

    
}
