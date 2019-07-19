package com.aimir.bo.system.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;

@Controller
public class HomeGroupMgmtController {
	private static Log log = LogFactory.getLog(HomeGroupMgmtController.class);

	@Autowired
	GroupMgmtManager groupMgmtManager;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	GroupMemberDao groupMemberDao;

    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    SupplierManager supplierManager;

	@RequestMapping(value="/gadget/system/homeGroupMiniGadget")
	public ModelAndView getHomeGroupManagementMini() {
		ModelAndView mav = new ModelAndView("/gadget/system/homeGroupMiniGadget");
	
	    AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
	    AimirUser user = (AimirUser)instance.getUserFromSession();
	    int supplierId = user.getRoleData().getSupplier().getId();
	    int operatorId = new Long(user.getAccountId()).intValue();
	
	    List<Object> groupType = groupMgmtManager.getHomeGroupTypeCombo();
	   
	    
	    mav.addObject("supplierId", supplierId);
	    mav.addObject("operatorId", operatorId);
	    mav.addObject("groupType", groupType);
	    return mav;
	}
	    
	@SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/system/homeGroupMaxGadget")
	public ModelAndView getHomeGroupManagementMax() {
		ModelAndView mav = new ModelAndView("/gadget/system/homeGroupMaxGadget");
	
	    AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
	    AimirUser user = (AimirUser)instance.getUserFromSession();
	    int supplierId = user.getRoleData().getSupplier().getId();
	    int operatorId = new Long(user.getAccountId()).intValue();
	
	    List<Object> groupType = groupMgmtManager.getHomeGroupTypeCombo();
	    List<Object> memberType = groupMgmtManager.getGroupTypeComboNotinHomeGroupIHD();
	    String groupTypeFstValue = null;
	
	    if (groupType != null && groupType.get(0) != null) {
	        groupTypeFstValue = (String)((Map<String, Object>)groupType.get(0)).get("name");
	    } else {
	        groupTypeFstValue = "";
	    }
		mav.addObject("supplierId", supplierId);
	    mav.addObject("operatorId", operatorId);
	    mav.addObject("groupType", groupType);
	    mav.addObject("groupTypeFstValue", groupTypeFstValue);
	    mav.addObject("memberType", memberType);

	    Role role = roleManager.getRole(user.getRoleData().getId());
	    Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
	    
	    Supplier supplier = supplierManager.getSupplier(supplierId);
        String numberPattern = supplier.getMd().getPattern();
        Integer dot = numberPattern.indexOf(".");

        if(dot != -1)
        	numberPattern = numberPattern.substring(0,dot);

	    mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
	    mav.addObject("numberFormat", numberPattern.replace("#","0"));
	    return mav;
	}
	
    /**
     * method name : getHomeGroupList<b/>
     * method Desc : HomeGroup Management 가젯에서 HomeGroup/IHD Group List 를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getHomeGroupList")
    public ModelAndView getHomeGroupList(@RequestParam("operatorId") Integer operatorId,
            @RequestParam("groupType") String groupType,
            @RequestParam("groupName") String groupName,
            @RequestParam("supplierId") Integer supplierId) {
    	
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("groupName", groupName);
        conditionMap.put("supplierId", supplierId);
        
        List<Map<String, Object>> result = groupMgmtManager.getHomeGroupList(conditionMap);
        
        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : getHomeGroupMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 등록 가능한 Member 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMemberSelectAndSelectedData")
    public ModelAndView getMemberSelectAndSelectedData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam("mcuId") Integer mcuId,
            @RequestParam("groupType") String groupType,
            @RequestParam(value="subType", required=false) String subType,
            @RequestParam("memberName") String memberName) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("subType", subType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        
        if(groupId == null) {
        	return mav;
        }
        List<Object[]> selectedData = groupMgmtManager.getHomeGroupMemberSelectedData(conditionMap);
        List<Object> selectData = groupMgmtManager.getMemberSelectData(conditionMap);

        mav.addObject("selectedData", selectedData);
        mav.addObject("selectData", selectData);
        return mav;
    }
    
    /**
     * method name : getHomeGroupMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 등록 가능한 Member 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getHomeGroupMemberSelectData")
    public ModelAndView getHomeGroupMemberSelectData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam("mcuId") Integer mcuId,
            @RequestParam("groupType") String groupType,
            @RequestParam(value="subType", required=false) String subType,
            @RequestParam("memberName") String memberName) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("subType", subType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        
        if(groupId == null) {
        	return mav;
        }
        List<Object> result = groupMgmtManager.getMemberSelectData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : getHomeGroupMemberSelectedData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getHomeGroupMemberSelectedData")
    public ModelAndView getHomeGroupMemberSelectedData(
    		@RequestParam("groupId") Integer groupId,
    		@RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("supplierId", supplierId);
        
        if(groupId == null) {
        	return mav;
        }

        List<Object[]> result = groupMgmtManager.getHomeGroupMemberSelectedData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : saveIHDHomeGroup<b/>
     * method Desc : HomeGroup Management 가젯에서 Group 을 등록/수정 한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/saveIHDHomeGroup")
    public ModelAndView saveIHDHomeGroup(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("operatorId") Integer operatorId,
            @RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("groupType") String groupType,
            @RequestParam("mcuId") String sysId) {
        ModelAndView mav = new ModelAndView("jsonView");

        MCU mcu = mcuDao.get(sysId);
        
        if(mcu == null) {
        	mav.addObject("result", "mcuNull");
        	return mav;
        }
        
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupName", groupName);
        conditionMap.put("groupType", groupType);
        conditionMap.put("groupType", groupType);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("sysId", sysId);
        
        groupMgmtManager.saveIHDHomeGroup(conditionMap);

        mav.addObject("result", "success");
        return mav;
    }

}
