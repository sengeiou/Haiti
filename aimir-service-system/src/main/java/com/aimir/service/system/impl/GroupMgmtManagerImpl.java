package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.IHDDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;


@WebService(endpointInterface = "com.aimir.service.system.GroupMgmtManager")
@Service(value="groupMgmtManager")
@Transactional(readOnly=false)
public class GroupMgmtManagerImpl implements GroupMgmtManager{

    Log logger = LogFactory.getLog(GroupMgmtManagerImpl.class);

    @Autowired
    GroupDao groupDao;
    @Autowired
    HomeGroupDao homeGroupDao;
    @Autowired
    GroupMemberDao groupMemberDao;
    @Autowired
    OperatorDao operatorDao;
    @Autowired
    LocationDao locationDao;
    @Autowired
    ContractDao contractDao;
    @Autowired
    MCUDao mcuDao;
    @Autowired
    ModemDao modemDao;
    @Autowired
    MeterDao meterDao;
    @Autowired
    EndDeviceDao endDeviceDao;
    @Autowired
    IHDDao ihdDao;
    @Autowired
    SupplierDao supplierDao;
    @Autowired
    CmdOperationUtil cmdOperationUtil;

    /**
     * GroupType 콤보 조회
     */
    public List<Object> getGroupTypeCombo() {

        List<Object> resultList = new ArrayList<Object>();
        Map<String, Object> resultMap = null;

        for (GroupType g : GroupType.values()) {
            resultMap = new HashMap<String, Object>();
            resultMap.put("id", g.ordinal());
            resultMap.put("name", g.name());
            resultList.add(resultMap);
        }
        return resultList;
    }
    
    /**
     * HomeGroupType 콤보 조회(HomeGroup/IHD)
     */
    public List<Object> getHomeGroupTypeCombo() {

        List<Object> resultList = new ArrayList<Object>();
        Map<String, Object> resultMap = null;

        for (GroupType g : GroupType.values()) {
        	if(GroupType.HomeGroup.equals(g) || GroupType.IHD.equals(g)) {
        		resultMap = new HashMap<String, Object>();
                resultMap.put("id", g.ordinal());
                resultMap.put("name", g.name());
                resultList.add(resultMap);
        	}
        }
        return resultList;
    }

    /**
     * GroupType 콤보 조회 (HomeGroup/IHD 제외)
     */
    public List<Object> getGroupTypeComboNotinHomeGroupIHD() {
        List<Object> resultList = new ArrayList<Object>();
        Map<String, Object> resultMap = null;

        for (GroupType g : GroupType.values()) {
            switch (g) {
                case HomeGroup:
                case IHD:
                case Operator:
                    break;
                default:
                    resultMap = new HashMap<String, Object>();
                    resultMap.put("id", g.ordinal());
                    resultMap.put("name", g.name());
                    resultList.add(resultMap);
                    break;
            }
        }
        return resultList;
    }

    /**
     * getMeterGroup  조회
     */
    public List<AimirGroup> getMeterGroup(int operatorId) {
        return groupDao.getGroupListMeter(operatorId);
    }

    /**
     * Contract Group 조회
     *  (non-Javadoc)
     * @see com.aimir.service.system.GroupMgmtManager#getContractGroup(int)
     */
    public List<AimirGroup> getContractGroup(int operatorId) {
        return groupDao.getContractGroup(operatorId);
    }

    /**
     * operator의 id 를 받아 그룹 목록을 반환한다.
     */
    public Map<String, Object> getGroupList(String operatorId) {

        Integer id = 0;
        if (!"".equals(StringUtil.nullToBlank(operatorId))) {
            id = Integer.parseInt(operatorId);
        }

        List<Object> list = groupDao.getGroupListWithChild(id);

        List<Object> returnList = new ArrayList<Object>();

        if (list.size() > 0) {

            List<Object> children = null;
            List<GroupMember> members = null;

            Map<String,Object> group = null;
            Map<String,Object> member = null;

            Object[] objs = null;
            MCU mcu = null;

            for (Object obj : list) {
                objs = (Object[])obj;
                group = new HashMap<String,Object>();
                group.put("id",(Integer)objs[0]);
                group.put("name", (String)objs[1]);

                if (StringUtil.nullToBlank(((GroupType)objs[2]).name()).equals(GroupType.HomeGroup.name()) || StringUtil.nullToBlank(((GroupType)objs[2]).name()).equals(GroupType.IHD.name())) {
                    mcu = homeGroupDao.getHomeGroupMcuByGroupId((Integer)objs[0]);

                    if (mcu != null) {
                        group.put("name", (String)objs[1] + " (Mcu:" + mcu.getSysID() + ")");
                    }
                }

                group.put("groupType",((GroupType)objs[2]).name());
                group.put("oldGroupType",((GroupType)objs[2]).name());
                group.put("allUserAccess",(Boolean)objs[3] ? "Y" : "N" );
                group.put("oldAllUserAccess",(Boolean)objs[3] ? "Y" : "N" );

                children = new ArrayList<Object>();
                members = groupDao.getChildren((Integer)objs[0]);

                for (GroupMember m : members) {
                    if (members != null && members.size() > 0) {
                        member = new HashMap<String,Object>();
                        member.put("id", m.getId());
                        member.put("name", m.getMember());
                        member.put("groupType",((GroupType)objs[2]).name());
                        member.put("groupId", (Integer)objs[0]);
                        children.add(member);
                    }
                }
                group.put("children",children);

                returnList.add(group);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("grid", returnList);

        return result;
    }

    /**
     * operator의 id 를 받아 그룹 목록을 반환한다. HomeGroup/IHD 제외.
     */
    @Deprecated
    public Map<String, Object> getGroupListNotinHomeGroupIHD(String operatorId) {

        Integer id = 0;
        if (!"".equals(StringUtil.nullToBlank(operatorId))) {
            id = Integer.parseInt(operatorId);
        }

        List<Object> list = groupDao.getGroupListWithChildNotinHomeGroupIHD(id);

        List<Object> returnList = new ArrayList<Object>();

        if (list.size() > 0) {

            List<Object> children = null;
            List<GroupMember> members = null;

            Map<String, Object> group = null;
            Map<String, Object> member = null;

            Object[] objs = null;

            for (Object obj : list) {
                objs = (Object[]) obj;
                group = new HashMap<String, Object>();
                group.put("id", (Integer) objs[0]);
                group.put("name", (String) objs[1]);
                group.put("groupType", ((GroupType) objs[2]).name());
                group.put("oldGroupType", ((GroupType) objs[2]).name());
                group.put("allUserAccess", (Boolean) objs[3] ? "Y" : "N");
                group.put("oldAllUserAccess", (Boolean) objs[3] ? "Y" : "N");

                children = new ArrayList<Object>();
                members = groupDao.getChildren((Integer) objs[0]);

                for (GroupMember m : members) {
                    if (members != null && members.size() > 0) {
                        member = new HashMap<String, Object>();
                        member.put("id", m.getId());
                        member.put("name", m.getMember());
                        member.put("groupType", ((GroupType) objs[2]).name());
                        member.put("groupId", (Integer) objs[0]);
                        children.add(member);
                    }
                }
                group.put("children", children);

                returnList.add(group);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("grid", returnList);

        return result;
    }

    /**
     * method name : getGroupListNotHomeGroupIHD<b/>
     * method Desc : Group Management 가젯에서 Group List 를 조회한다. HomeGroup/IHD 제외.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getGroupListNotHomeGroupIHD(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        if (operatorId == null) {
            conditionMap.put("operatorId", 0);
        }

        List<Map<String, Object>> result = groupDao.getGroupListNotHomeGroupIHD(conditionMap);

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for(Map<String, Object> map : result) {
            map.put("memCount", dfMd.format(map.get("memCount")));
        }

        return result;
    }
    
    /**
     * method name : dupCheckGroupName<b/>
     * method Desc : Group Management 가젯에서 그룹이름이 중복되었는지 체크
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> dupCheckGroupName(Map<String, Object> conditionMap) {
    	List<Map<String, Object>> result = groupDao.dupCheckGroupName(conditionMap);
    	
        return result;
    }
    
    /**
     * method name : getHomeGroupList<b/>
     * method Desc : HomeGroup Management 가젯에서 HomeGroup/IHD Group List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getHomeGroupList(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        if (operatorId == null) {
            conditionMap.put("operatorId", 0);
        }

        List<Map<String, Object>> result = homeGroupDao.getHomeGroupList(conditionMap);

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for(Map<String, Object> map : result) {
            map.put("memCount", dfMd.format(map.get("memCount")));
        }
        
        return result;
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 등록 가능한 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	String groupTypeStr = (String)conditionMap.get("groupType");
    	List<Object> result = new ArrayList<Object>();
        GroupType groupType = null;
        if(groupTypeStr != null) {
        	groupType = GroupType.valueOf((String)conditionMap.get("groupType"));
            String subType = StringUtil.nullToBlank(conditionMap.get("subType"));

        	switch (groupType) {
                case Location:
                	if(!(ModemType.IHD.name().equals(subType))) {
                		result = locationDao.getMemberSelectData(conditionMap);
                	}
                	break;
                /*case Operator:
                	if(!(ModemType.IHD.name().equals(subType))) {
                		result = operatorDao.getMemberSelectData(conditionMap);
                	}
                   break;*/
                case Meter:
                	result = meterDao.getMemberSelectData(conditionMap);
                    break;
                case Contract:
                	if(!(ModemType.IHD.name().equals(subType))) {
                		result = contractDao.getMemberSelectData(conditionMap);
                	}
                	break;
                case DCU:
                	if(!(ModemType.IHD.name().equals(subType))) {
                		result = mcuDao.getMemberSelectData(conditionMap);
                	}
                    break;
                case Modem:
                	if(!subType.isEmpty()) {
                		result = modemDao.getHomeGroupMemberSelectData(conditionMap);
                	} else {
                		result = modemDao.getMemberSelectData(conditionMap);
                	}
                    break;
                case EndDevice:
                	if(!(ModemType.IHD.name().equals(subType))) {
                		result = endDeviceDao.getMemberSelectData(conditionMap);
                	}
                    break;
                case HomeGroup:
                	result = homeGroupDao.getMemberSelectData(conditionMap);
                   break;
                case IHD:
                   result = ihdDao.getMemberSelectData(conditionMap);
                   break;
        	}
        }
        
        return result;
    }

    /**
     * method name : getMemberSelectedData<b/>
     * method Desc : Group Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectedData(Map<String, Object> conditionMap) {
        List<Object> result = groupMemberDao.getMemberSelectedData(conditionMap);
        return result;
    }
    
    /**
     * method name : getHomeGroupMemberSelectedData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object[]> getHomeGroupMemberSelectedData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = groupMemberDao.getHomeGroupMemberSelectedData(conditionMap);
        List<Object[]> returnResult = new ArrayList<Object[]>();
        Object[] returnMember = new Object[] {};
        
        for (Map<String, Object> member : result) {
			
	        String supplierId = StringUtil.nullToBlank( conditionMap.get("supplierId"));
	        if (supplierId.length() > 0) {
	            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

	            member.put("LASTSYNCDATE", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(member.get("LASTSYNCDATE")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	        }
			
			returnMember = new Object[] {member.get("ID"), member.get("NAME"), member.get("TYPE").toString().trim(), member.get("ISREGISTRATION"), member.get("LASTSYNCDATE")};
			
			returnResult.add(returnMember);
		}
        
        
        return returnResult;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public int saveGroups(List<Object> groups) throws Exception {
        int returnCnt = 0;
        Map<String, Object> g = null;

        for (Object obj : groups) {
            g = (Map<String, Object>)obj;
            //----------------------
            //  Home Group이 아닌 경우
            //----------------------
            if (!"HomeGroup".equals((String)g.get("groupType")) && !"IHD".equals((String)g.get("groupType"))) {
                AimirGroup newGroup = new AimirGroup();
                newGroup.setOperator(operatorDao.getOperatorById(Integer.parseInt((String)g.get("operatorId"))));
                newGroup.setId((Integer)g.get("id"));
                newGroup.setName((String)g.get("name"));
                newGroup.setAllUserAccess(("Y".equals((String)g.get("allUserAccess"))) ? true : false);
                newGroup.setGroupType((String)g.get("groupType"));
                newGroup.setWriteDate(CalendarUtil.getCurrentDate());
                newGroup.setMembers(groupMemberDao.getGroupMemberById((Integer)g.get("id")));
                if ("U".equals((String)g.get("state"))) {
                    newGroup = groupDao.groupUpdate(newGroup);
                }
                else {
                    newGroup = groupDao.groupAdd(newGroup);
                }
                if (newGroup != null) returnCnt++;
            }
            //----------------------
            //  Home Group인 경우
            //----------------------
            else {
                HomeGroup newGroup = new HomeGroup();
                newGroup.setOperator(operatorDao.getOperatorById(Integer.parseInt((String)g.get("operatorId"))));
                newGroup.setId((Integer)g.get("id"));
                newGroup.setName((String)g.get("name"));
                newGroup.setAllUserAccess(("Y".equals((String)g.get("allUserAccess"))) ? true : false);
                newGroup.setGroupType((String)g.get("groupType"));
                newGroup.setWriteDate(CalendarUtil.getCurrentDate());
                newGroup.setMembers(groupMemberDao.getGroupMemberById((Integer)g.get("id")));
                //HomeGroup인 경우 groupKey와 해당 group의 mcu를 설정해줌
                logger.debug("homeGroupMcu: "+g.get("homeGroupMcu")+" groupKey:"+g.get("groupKey"));
                try {
                    newGroup.setHomeGroupMcu(mcuDao.get((String)g.get("homeGroupMcu")));
                } catch(Exception ex) {
                    throw new Exception("Home Group Mcu Is Invalid!");
                }
                try {
                    newGroup.setGroupKey((Integer)g.get("groupKey"));
                } catch(NullPointerException ex) {
                    throw new Exception("Group Key Is Invalid!");
                }
                if ("U".equals((String)g.get("state"))) {
                    newGroup = (HomeGroup) groupDao.groupUpdate(newGroup);
                }
                else {
                    newGroup = (HomeGroup) groupDao.groupAdd(newGroup);
                }
                if (newGroup != null) returnCnt++;
            }
        }
        return returnCnt;
    }

    /**
     * method name : saveGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 등록/수정 한다.
     *
     * @param conditionMap
     */
    public void saveGroup(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));
        String groupType = StringUtil.nullToBlank(conditionMap.get("groupType"));
        String allUserAccess = StringUtil.nullToBlank(conditionMap.get("allUserAccess"));
        String mobileNo = StringUtil.nullToBlank(conditionMap.get("mobileNo"));
        AimirGroup aimirGroup = null;

        if (groupId != null) {
            aimirGroup = groupDao.get(groupId);
            if (!groupName.isEmpty()) {
                aimirGroup.setName(groupName);
            }
            if (!groupType.isEmpty()) {
                aimirGroup.setGroupType(groupType);
            }
            if (!allUserAccess.isEmpty()) {
                aimirGroup.setAllUserAccess(allUserAccess.equals("Y"));
            }
            if (!mobileNo.isEmpty()) {
                aimirGroup.setMobileNo(mobileNo);
            }
        } else {
            Operator operator = operatorDao.get(operatorId);
            Supplier supplier = supplierDao.get(supplierId);
            aimirGroup = new AimirGroup();
            aimirGroup.setOperator(operator);
            aimirGroup.setSupplier(supplier);
            aimirGroup.setName(groupName);
            aimirGroup.setGroupType(groupType);
            aimirGroup.setMobileNo(mobileNo);
            aimirGroup.setAllUserAccess(allUserAccess.equals("Y"));
            try {
                aimirGroup.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        groupDao.groupSaveOrUpdate(aimirGroup);
    }
    
    /**
     * method name : saveIHDHomeGroup<b/>
     * method Desc : HomeGroup Management 가젯에서 IHD와 HomeGroup 을 등록/수정 한다.
     *
     * @param conditionMap
     */
    public void saveIHDHomeGroup(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String sysId = (String)conditionMap.get("sysId");
        Integer groupKey = (Integer)conditionMap.get("groupKey");
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));
        String groupType = StringUtil.nullToBlank(conditionMap.get("groupType"));
        HomeGroup homeGroup = null;

        if (groupId != null && groupId != -1) {
        	homeGroup = homeGroupDao.get(groupId);
            if (!groupName.isEmpty()) {
            	homeGroup.setName(groupName);
            }
            if (!groupType.isEmpty()) {
            	homeGroup.setGroupType(groupType);
            }
            if(!sysId.isEmpty()) {
            	MCU mcu = mcuDao.get(sysId);
            	homeGroup.setHomeGroupMcuId(mcu.getId());
            	homeGroup.setHomeGroupMcu(mcu);
            }
            if (groupKey != null && groupKey != -1) {
            	homeGroup.setGroupKey(groupKey);
            }
        } else {
            Operator operator = operatorDao.get(operatorId);
            Supplier supplier = supplierDao.get(supplierId);
            MCU mcu = mcuDao.get(sysId);
            
            homeGroup = new HomeGroup();
            homeGroup.setOperator(operator);
            homeGroup.setSupplier(supplier);
            homeGroup.setName(groupName);
            homeGroup.setGroupType(groupType);
            homeGroup.setHomeGroupMcu(mcu);
            
            if(groupKey != null) {
            	homeGroup.setGroupKey(groupKey);
            } else if (groupKey == null) {
            	//IHD의 경우 groupKey값이 리턴되지 않는데 groupKey값을 저장하지 않을경우 나중에 수정시 에러가 발생됨.
            	//따라서 임의의 값 -1을 지정해서 넣어줌
            	homeGroup.setGroupKey(-1);
            }
            
            try {
            	homeGroup.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        groupDao.groupSaveOrUpdate(homeGroup);
        
    }

    /**
     * method name : copyGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 복사한다.
     *
     * @param conditionMap
     */
    public void copyGroup(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));
        String groupType = StringUtil.nullToBlank(conditionMap.get("groupType"));
        String allUserAccess = StringUtil.nullToBlank(conditionMap.get("allUserAccess"));
        String mobileNo = StringUtil.nullToBlank(conditionMap.get("mobileNo"));
        Operator operator = operatorDao.get(operatorId);
        Supplier supplier = supplierDao.get(supplierId);

        AimirGroup aimirGroup = new AimirGroup();
        GroupMember groupMember = null;

        aimirGroup.setOperator(operator);
        aimirGroup.setSupplier(supplier);
        aimirGroup.setName(groupName);
        aimirGroup.setGroupType(groupType);
        aimirGroup.setAllUserAccess(allUserAccess.equals("Y"));
        aimirGroup.setMobileNo(mobileNo);
        try {
            aimirGroup.setWriteDate(TimeUtil.getCurrentTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AimirGroup newAimirGroup = groupDao.add(aimirGroup);

        Set<GroupMember> groupMemberList = groupMemberDao.getGroupMemberById(groupId);

        for (GroupMember obj : groupMemberList) {
            groupMember = new GroupMember();
            groupMember.setGroup(newAimirGroup);
            groupMember.setMember(obj.getMember());
            try {
                groupMember.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            groupMemberDao.groupAdd(groupMember);
        }
    }

    /**
     * method name : deleteGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 삭제한다.
     *
     * @param conditionMap
     */
    public void deleteGroup(Map<String, Object> conditionMap) {
        Integer groupId = Integer.parseInt(conditionMap.get("groupId").toString());

        AimirGroup aimirGroup = groupDao.get(groupId);
        groupDao.delete(aimirGroup);
    }

    public int updateGroups(List<Object> groups) throws Exception {
        int returnCnt = 0;
        AimirGroup newGroup = null;
        for (Object obj : groups) {
            newGroup = groupDao.update((AimirGroup)obj);
            if (newGroup != null) returnCnt++;
        }
        return returnCnt;
    }

    @SuppressWarnings("unchecked")
    public int deleteGroups(List<Object> groups) throws Exception {
        int returnCnt = 0;
        Map<String, Object> g = null;

        for (Object obj : groups) {
            g = (Map<String, Object>)obj;
            //-----------
            //  멤버 삭제
            //-----------
            if (g.get("groupId") != null) {
                GroupMember groupMember = groupMemberDao.get((Integer)g.get("id"));
                if (groupMember.getGroup() == null) throw new Exception("Member["+g.get("id")+"] dose not have a group!");
                //홈 그룹인 경우 멤버 삭제 커맨드를 날림
                if (groupMember.getGroup().getGroupType() == GroupType.HomeGroup) {
                    HomeGroup homeGroup = homeGroupDao.get(groupMember.getGroup().getId());
                    try {
                        logger.debug("cmdGroupDeleteMember("+homeGroup.getHomeGroupMcu().getSysID()+", "+homeGroup.getGroupKey()+", "+groupMember.getMember()+")");
                        cmdOperationUtil.cmdGroupDeleteMember(homeGroup.getHomeGroupMcu().getSysID(), homeGroup.getGroupKey(), groupMember.getMember());
                    } catch (Exception e) {
                        throw new Exception("cmdGroupDeleteMember is failed!",e);
                    }
                }
                returnCnt += groupMemberDao.deleteById((Integer)g.get("id"));
                groupMemberDao.flushAndClear();
            }
            //-----------
            // 그룹 삭제
            //-----------
            else {
                //홈 그룹인 경우 그룹 삭제 커맨드를 날림
                if (groupDao.get((Integer)g.get("id")).getGroupType() == GroupType.HomeGroup) {
                    HomeGroup homeGroup = homeGroupDao.get((Integer)g.get("id"));
                    logger.debug("cmdGroupDelete("+homeGroup.getHomeGroupMcu().getSysID()+", "+homeGroup.getGroupKey()+")");
                    cmdOperationUtil.cmdGroupDelete(homeGroup.getHomeGroupMcu().getSysID(), homeGroup.getGroupKey());
                }
                returnCnt += groupDao.deleteById((Integer)g.get("id"));
                groupDao.flushAndClear();
            }
        }
        return returnCnt;
    }

    @SuppressWarnings("unchecked")
    public int moveGroupMembers(List<Object> groups) throws Exception {
        int returnCnt = 0;
        Map<String, Object> g = null;

        for (Object obj : groups) {
            g = (Map<String, Object>)obj;
            returnCnt += groupMemberDao.updateData((Integer)g.get("id"), (Integer)g.get("groupId"));
        }
        return returnCnt;
    }

    @Deprecated
    public Map<String, Object> getGroupMember(Map<String, Object> condition) {

        GroupType groupType = GroupType.valueOf((String)condition.get("groupType"));
        logger.debug("==groupType["+groupType.name()+"]");
        List<Object> list = null;
        switch (groupType) {
            case Location:
                list = locationDao.getGroupMember(condition);
                break;
            /*case Operator:
                list = operatorDao.getGroupMember(condition);
                break;*/
            case Meter:
                list = meterDao.getGroupMember(condition);
                break;
            case Contract:
                list = contractDao.getGroupMember(condition);
                break;
            case DCU:
                list = mcuDao.getGroupMember(condition);
                break;
            case Modem:
                list = modemDao.getGroupMember(condition);
                break;
            case EndDevice:
                list = endDeviceDao.getGroupMember(condition);
                break;
            case HomeGroup:
                list = modemDao.getGroupMember(condition);
                break;
            case IHD:
                list = modemDao.getGroupMember(condition);
                List<Object> listTmp = meterDao.getGroupMember(condition);
                list.addAll(listTmp);
                break;
            default:
                list = groupMemberDao.getGroupMember(condition);
                break;
        }

        List<Object> returnList = new ArrayList<Object>();
        Map<String, Object> data = null;
        for (Object obj : list) {
            data = new HashMap<String, Object>();
            data.put("data", ((Object[])obj)[0]);
            data.put("label", ((Object[])obj)[1]);
            returnList.add(data);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("list", returnList);

        return result;
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public int saveGroupMembers(List<Object> members) throws Exception {
        int returnCnt = 0;
        GroupMember member = null;
        Map<String, Object> m = null;
        MCU mcu = null;
        Modem modem = null;
        for (Object obj : members) {
            m = (Map<String, Object>)obj;
            Boolean isSave = true;
            member = new GroupMember();
            member.setGroup(groupDao.get((Integer)m.get("groupId")));
            member.setMember((String)m.get("memberName"));
            member.setWriteDate(CalendarUtil.getCurrentDate());
            if (member.getGroup().getGroupType() == GroupType.HomeGroup) {
                System.out.println("#######Home Group");
                HomeGroup homeGroup = homeGroupDao.get(member.getGroup().getId());
//              try {
//                  cmdOperationUtil.cmdGroupAddMember(homeGroup.getHomeGroupMcu().getSysID(), homeGroup.getGroupKey(), member.getMember());
//              }catch(Exception ex) {
//                  throw new Exception("cmdGroupAddMember is failed!");
//              }
            }

            if ((GroupType.IHD).equals(member.getGroup().getGroupType())) {
                modem = modemDao.get(m.get("memberName").toString());
                if (modem == null) {
                    modem = meterDao.get(m.get("memberName").toString()).getModem();
                }
                mcu = homeGroupDao.get((Integer)m.get("groupId")).getHomeGroupMcu();
                if ((modem != null && !(modem.getMcuId().equals(mcu.getId())))) {
                    isSave = false;
                }
            }

            if (isSave) {
                member = groupMemberDao.groupAdd(member);
            }
            if(member != null) returnCnt++;
        }
        return returnCnt;
    }

    /**
     * method name : addGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 저장한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public Integer addGroupMembers(Map<String, Object> conditionMap) {
        Integer groupId = Integer.parseInt(conditionMap.get("groupId").toString());
        String members = StringUtil.nullToBlank(conditionMap.get("members"));
        String[] memberArray = members.split(",");
        int returnCnt = 0;
        GroupMember groupMember = null;
        AimirGroup aimirGroup = groupDao.get(groupId);

        for (String member : memberArray) {
            groupMember = new GroupMember();
            groupMember.setGroup(aimirGroup);
            groupMember.setMember(member);
            try {
                groupMember.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            groupMemberDao.groupAdd(groupMember);
            returnCnt++;
        }
        return returnCnt;
    }
    
    /**
     * method name : addGroupMemberList<b/>
     * method Desc : HomeGroup Management 가젯에서 Group Member List를 저장한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public Integer addGroupMemberList(Map<String, Object> conditionMap) {
        Integer groupId = Integer.parseInt(conditionMap.get("groupId").toString());
        String memberArray[] = (String[])conditionMap.get("members");
        
        int returnCnt = 0;
        GroupMember groupMember = null;
        HomeGroup homeGroup = homeGroupDao.get(groupId);

        for (String member : memberArray) {
            groupMember = new GroupMember();
            groupMember.setGroup(homeGroup);
            groupMember.setMember(member);
            try {
                groupMember.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            groupMemberDao.groupAdd(groupMember);
            returnCnt++;
        }
        return returnCnt;
    }

    /**
     * method name : removeGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 삭제한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public Integer removeGroupMembers(Map<String, Object> conditionMap) {
        String memberIds = StringUtil.nullToBlank(conditionMap.get("memberIds"));
        String[] memberIdArray = memberIds.split(",");
        int returnCnt = 0;
        GroupMember groupMember = null;

        for (String memberId : memberIdArray) {
            groupMember = groupMemberDao.get(Integer.valueOf(memberId));
            groupMemberDao.groupDelete(groupMember);
            returnCnt++;
        }
        return returnCnt;
    }
    
    /**
     * method name : removeGroupMemberList<b/>
     * method Desc : Group Management 가젯에서 Group Member의 리스트를 삭제한다.
     *
     * @param conditionMap
     * @return
     * @throws Exception
     */
    public Integer removeGroupMemberList(Map<String, Object> conditionMap) {
    	String memberIdArray[] = (String[])conditionMap.get("memberIds");
    	
        int returnCnt = 0;
        GroupMember groupMember = null;

        for (String memberId : memberIdArray) {
            groupMember = groupMemberDao.get(Integer.valueOf(memberId));
            groupMemberDao.groupDelete(groupMember);
            returnCnt++;
        }
        return returnCnt;
    }

    public Set<GroupMember> getGroupMemberById(Integer groupId) {
        return groupMemberDao.getGroupMemberById(groupId);
    }

    public HomeGroup saveHomeGroup(HomeGroup homeGroup, String saveOrUpdate) throws Exception {
//        int returnCnt = 0;
//        Map<String, Object> g = null;

        HomeGroup newGroup = new HomeGroup();
        newGroup.setOperator(homeGroup.getOperator());
        //newGroup.setId((Integer)g.get("id"));
        newGroup.setName(homeGroup.getName());
        newGroup.setAllUserAccess(homeGroup.getAllUserAccess());
        newGroup.setGroupType(homeGroup.getGroupType().name());
        newGroup.setWriteDate(CalendarUtil.getCurrentDate());
        newGroup.setSupplier(homeGroup.getSupplier());
        newGroup.setGroupKey(homeGroup.getGroupKey());
        //newGroup.setMembers(groupMemberDao.getGroupMemberById((Integer)g.get("id")));

        if (homeGroup.getHomeGroupMcu() != null) {
            logger.debug("####important###" + homeGroup.getHomeGroupMcu().getSysID());
            newGroup.setHomeGroupMcu(homeGroup.getHomeGroupMcu());
        }

        /*
        try {
            newGroup.setHomeGroupMcu(homeGroup.getHomeGroupMcu());
        }catch(Exception ex) {
            throw new Exception("Home Group Mcu Is Invalid!");
        }
        try {
            newGroup.setGroupKey(homeGroup.getGroupKey());
        }catch(NullPointerException ex) {
            throw new Exception("Group Key Is Invalid!");
        }*/

        if ("U".equals(saveOrUpdate)) {
            newGroup = (HomeGroup) groupDao.groupUpdate(newGroup);
        }
        else {
            newGroup = (HomeGroup) groupDao.groupAdd(newGroup);
        }
        return newGroup;
    }

    public int getGroupIdbyMember(
            @WebParam(name ="mdsId")String mdsId) {

        int groupId = groupMemberDao.getGroupIdbyMember(mdsId);

        return groupId;
    }

    public List<Map<String, Object>> getGroupComboDataByType(Map<String, Object> conditionMap) {
        return groupDao.getGroupComboDataByType(conditionMap);
    }
    
    @Override
	public List<String> getMeterGroupMemberIds(Integer meterGroupId) {
		List<String> meterList = groupMemberDao.getMeterGroupMemberIds(meterGroupId);
		return meterList;
	}

    /**
     * getMcuGroup  조회
     */
	@Override 
	public List<Map<String, Object>> getMcuGroup(int operatorId) {
	        return groupDao.getGroupListMcu(operatorId);
	}

	/**
     * getSelectedMcuList  조회
     */
	@Override
	public List<Map<String, Object>> getSelectedMcuList(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
		int idx = 1;
		Integer rowNo = 0;
		Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
		conditionMap.put("pageonoff", "on");
		//Boolean pageonoff = conditionMap.get("pageOnOff");
		/**
		 * if(pageonoff){
		 *  page =null, limit=null;
		 *  
		 */
		
		result = groupDao.getSelectedListMcu(conditionMap);
        
		//if(pageonoff){ pass }
		for (Map<String, Object> map : result) {
            if (page != null && limit != null) {
                rowNo = ((page-1) * limit) + idx;
            } else {
                rowNo = idx;
            }
            map.put("rowNo", dfMd.format(rowNo));
            idx++;
        }
		
		return result;
	}

	/**
     * getSelectedMcuCount  조회
     */
	@Override
	public int getSelectedMcuCount(Integer groupId) {
		return groupDao.getSelectedCountMcu(groupId);
	}
	
	/**
	 * addGroupFailList
	 */
	@Override
	public Integer addGroupFailList(Integer groupId, List<String> failList) {
		        
        int returnCnt = 0;
        GroupMember groupMember = null;
        AimirGroup aimirGroup = groupDao.get(groupId);

        for (String member : failList) {
        	groupMember = new GroupMember();
            groupMember.setGroup(aimirGroup);
            groupMember.setMember(member);
            try {
                groupMember.setWriteDate(TimeUtil.getCurrentTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            groupMemberDao.groupAdd(groupMember);
            returnCnt++;
        }
        return returnCnt;
	}

	
}
