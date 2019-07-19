package com.aimir.bo.system.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ModemSleepMode;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MCUManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;

@Controller
public class GroupManagementController {

	@Autowired
	GroupMgmtManager groupMgmtManager;

    @Autowired
    RoleManager roleManager;

    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    CodeManager codeManager;
    
    @Autowired
    MCUManager mcuManager;
    
    @Autowired
    DeviceModelManager deviceModelManager;

    @Autowired
    LocationManager locationManager;

    @RequestMapping(value="/gadget/system/groupManagementMiniGadget")
    public ModelAndView getGroupManagementMini() {
    	ModelAndView mav = new ModelAndView("/gadget/system/groupManagementMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        int operatorId = new Long(user.getAccountId()).intValue();

        List<Object> groupType = groupMgmtManager.getGroupTypeComboNotinHomeGroupIHD();
        mav.addObject("supplierId", supplierId);
        mav.addObject("operatorId", operatorId);
        mav.addObject("groupType", groupType);
        return mav;
    }
        
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/system/groupManagementMaxGadget")
    public ModelAndView getGroupManagementMax() {
    	ModelAndView mav = new ModelAndView("/gadget/system/groupManagementMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        int supplierId = user.getRoleData().getSupplier().getId();
        int operatorId = new Long(user.getAccountId()).intValue();

        List<Object> groupType = groupMgmtManager.getGroupTypeComboNotinHomeGroupIHD();
        String groupTypeFstValue = null;

        if (groupType != null && groupType.get(0) != null) {
            groupTypeFstValue = (String)((Map<String, Object>)groupType.get(0)).get("name");
        } else {
            groupTypeFstValue = "";
        }

        Supplier supplier = supplierManager.getSupplier(supplierId);
        String numberPattern = supplier.getMd().getPattern();
        Integer dot = numberPattern.indexOf(".");

        if(dot != -1)
            numberPattern = numberPattern.substring(0,dot);

    	mav.addObject("supplierId", supplierId);
        mav.addObject("operatorId", operatorId);
        mav.addObject("groupType", groupType);
        mav.addObject("groupTypeFstValue", groupTypeFstValue);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("numberFormat", numberPattern.replace("#","0"));
        //DCU
        List<Code> mcuTypeMap = codeManager.getChildCodes(Code.MCU_TYPE);
        List<Code> hwVersions = codeManager.getChildCodes(Code.MCU_HW_VERSION);
        List<Code> swVersions = codeManager.getChildCodes(Code.MCU_SW_VERSION);
        List<Code> protocols = codeManager.getChildCodes(Code.PROTOCOL);
        List<Code> mcuStatus = codeManager.getChildCodes(Code.MCU_STATUS);
        Map<String, List<String>> locationMap = mcuManager.getLocationTreeToRows(supplierId);
        List<Map<String, String>> deviceModels = deviceModelManager.getMCUDeviceModel();
        List<Location> locations = locationManager.getLocationsBySupplierId(supplierId);

        mav.addObject("mcuTypeMap", mcuTypeMap);
        mav.addObject("hwVersions", hwVersions);
        mav.addObject("swVersions", swVersions);
        mav.addObject("protocols", protocols);
        mav.addObject("keys", locationMap.get("keys"));
        mav.addObject("locationNames", locationMap.get("locationNames"));
        mav.addObject("deviceModels", deviceModels);
        mav.addObject("locations", locations);
        mav.addObject("mcuStatus", mcuStatus);
        //Meter
        List<Code> meterType = codeManager.getChildCodes(Code.METER_TYPE);
        List<Code> meterStatus = codeManager.getChildCodes(Code.METER_STATUS);
        List<Code> meterHwVer = codeManager.getChildCodes(Code.METER_HW_VERSION);
        List<Code> meterSwVer = codeManager.getChildCodes(Code.METER_SW_VERSION);
        
        mav.addObject("meterType", meterType);
        mav.addObject("meterStatus", meterStatus);
        mav.addObject("meterHwVer", meterHwVer);
        mav.addObject("meterSwVer", meterSwVer);
        //Modem
        List<Code> modemType = codeManager.getChildCodes(Code.MODEM_TYPE);
        List<Code> modemSwRev = codeManager.getChildCodes(Code.MODEM_SW_REVISION);
        List<Code> modemHwVer = codeManager.getChildCodes(Code.MODEM_HW_VERSION);
        List<Code> modemSwVer = codeManager.getChildCodes(Code.MODEM_SW_VERSION);
        List<Code> modemStatus = codeManager.getChildCodes(Code.MODEM_SLEEP_MODE);
        List<Code> mcuType = codeManager.getChildCodes(Code.MCU_TYPE);
        
        mav.addObject("modemType", modemType);
        mav.addObject("modemSwRev", modemSwRev);
        mav.addObject("modemHwVer", modemHwVer);
        mav.addObject("modemSwVer", modemSwVer);
        mav.addObject("modemStatus", modemStatus);
        mav.addObject("mcuType", mcuType);
        //Location
        
        //
        return mav;
    }

    /**
     * method name : getGroupListNotHomeGroupIHD<b/>
     * method Desc : Group Management 가젯에서 Group List 를 조회한다. HomeGroup/IHD 제외.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getGroupListNotHomeGroupIHD")
    public ModelAndView getGroupListNotHomeGroupIHD(@RequestParam("operatorId") Integer operatorId,
    		@RequestParam("supplierId") Integer supplierId,
            @RequestParam("groupType") String groupType,
            @RequestParam("groupName") String groupName) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("groupName", groupName);

        List<Map<String, Object>> result = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : dupCheckGroupName<b/>
     * method Desc : Group Management 가젯에서 그룹이름이 중복되었는지 체크
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/dupCheckGroupName")
    public ModelAndView dupCheckGroupName(@RequestParam("operatorId") Integer operatorId,
            @RequestParam("groupName") String groupName) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("groupName", groupName);

        List<Map<String, Object>> result = groupMgmtManager.dupCheckGroupName(conditionMap);

        if(result.size() > 0) {
        	//중복일 경우
        	mav.addObject("result", "Y");
        } else {
        	//중복이 아닐경우
        	mav.addObject("result", "N");
        }
        
        return mav;
    }
    
    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 등록 가능한 Member 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMemberSelectData")
    public ModelAndView getMemberSelectData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam(value="groupType", required=false) String groupType,
            @RequestParam("memberName") String memberName,
            @RequestParam(value="locationId", required=false) Integer locationId) {
        ModelAndView mav = new ModelAndView("jsonView");

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("locationId", locationId);
        
        List<Object> selectData = groupMgmtManager.getMemberSelectData(conditionMap);
        	
        mav.addObject("totalCnt", selectData.get(0));
        mav.addObject("result", selectData.get(1));
        
        return mav;
    }
    @RequestMapping(value = "/gadget/system/getMemberSelectDataMeter")
    public ModelAndView getMemberSelectDataMeter(/*@RequestParam("supplierId") Integer supplierId,*/
    		@RequestParam(value="groupId", required=false) Integer groupId,
    		@RequestParam(value="groupType", required=false) String groupType,
    		@RequestParam("memberName") String memberName,
    		//@RequestParam(value="locationId", required=false) Integer locationId,
    		@RequestParam("sMeterType") String sMeterType,
            @RequestParam("sMdsId") String sMdsId,
            @RequestParam("sStatus") String sStatus,
            @RequestParam(value = "sMeterGroup", required = false) String sMeterGroup,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sLocationId") String sLocationId,
            @RequestParam("sConsumLocationId") String sConsumLocationId,
            @RequestParam("sVendor") String sVendor,
            @RequestParam("sModel") String sModel,
            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,
            @RequestParam("sModemYN") String sModemYN,
            @RequestParam("sCustomerYN") String sCustomerYN,
            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            //@RequestParam(value = "curPage", required = false) String curPage,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            @RequestParam("sGroupOndemandYN") String sGroupOndemandYN,
            @RequestParam("supplierId") Integer supplierId,
            /*@RequestParam("supplierId") String supplierId,*/
            @RequestParam(value = "sCustomerId", required = false) String customerId,
            @RequestParam(value = "sCustomerName", required = false) String customerName,
            @RequestParam(value = "sPermitLocationId", required = false) String sPermitLocationId,
            @RequestParam("sMeterAddress") String sMeterAddress,
            @RequestParam("sHwVersion") String sHwVersion,
            @RequestParam("sFwVersion") String sFwVersion,
            @RequestParam("sGs1") String sGs1
    		) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
    	int page = Integer.parseInt(request.getParameter("page"));
    	int limit = Integer.parseInt(request.getParameter("limit"));
    	
    	Map<String, Object> condition = new HashMap<String, Object>();
    	 condition.put("sMeterType", sMeterType);
         condition.put("sMdsId", sMdsId);
         condition.put("sStatus", sStatus);
         condition.put("sMeterGroup", sMeterGroup);
         condition.put("sMeterAddress", sMeterAddress);

         condition.put("sMcuName", sMcuName);
         condition.put("sLocationId", sLocationId);
         condition.put("sConsumLocationId", sConsumLocationId);
         condition.put("sPermitLocationId", sPermitLocationId);

         condition.put("sVendor", sVendor);
         condition.put("sModel", sModel);
         condition.put("sInstallStartDate", sInstallStartDate);
         condition.put("sInstallEndDate", sInstallEndDate);

         condition.put("sModemYN", sModemYN);
         condition.put("sCustomerYN", sCustomerYN);
         condition.put("sLastcommStartDate", sLastcommStartDate);
         condition.put("sLastcommEndDate", sLastcommEndDate);

         //condition.put("curPage", curPage);
         //condition.put("limit", limit);
         condition.put("sOrder", sOrder);
         condition.put("sCommState", sCommState);
         condition.put("sGroupOndemandYN", sGroupOndemandYN);

         condition.put("supplierId", supplierId);

         condition.put("sCustomerId", customerId);
         condition.put("sCustomerName", customerName);
         
         condition.put("sHwVersion", sHwVersion);
         condition.put("sFwVersion", sFwVersion);
         condition.put("sGs1", sGs1);

         condition.put("groupId", groupId);
         condition.put("groupType", groupType);
         condition.put("memberName", memberName);
         //condition.put("supplierId", supplierId);
         condition.put("page", page);
         condition.put("limit", limit);
         //condition.put("locationId", locationId);
         
    	List<Object> selectData = groupMgmtManager.getMemberSelectData(condition);
    	
    	mav.addObject("totalCnt", selectData.get(0));
    	mav.addObject("result", selectData.get(1));
    	
    	return mav;
    }

    @RequestMapping(value = "/gadget/system/getMemberSelectDataDcu")
    public ModelAndView getMemberSelectDataDcu(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam(value="groupType", required=false) String groupType,
            @RequestParam("memberName") String memberName,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuSerial") String mcuSerial, 
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("swVersion") String swVersion,
            //@RequestParam("swRevison") String swRevison,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            //@RequestParam("filter") String filter,
            //@RequestParam("order") String order,
            @RequestParam("protocol") String protocol,
            @RequestParam("mcuStatus") String mcuStatus
            //@RequestParam("fwGadget") String fwGadget,
            //@RequestParam("modelId") String modelId
            ) {
        ModelAndView mav = new ModelAndView("jsonView");

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("locationId", locationId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuSerial", mcuSerial);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("swVersion", swVersion);
        //conditionMap.put("swRevison", swRevison);
        conditionMap.put("hwVersion",hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        //conditionMap.put("lastcommStartDate", lastcommStartDate);
        //conditionMap.put("lastcommEndDate", lastcommEndDate);
        //conditionMap.put("filter", filter);
        //conditionMap.put("order", order);
        conditionMap.put("protocol", protocol);
        conditionMap.put("mcuStatus", mcuStatus);
        //conditionMap.put("modelId", modelId);
        //conditionMap.put("fwGadget", fwGadget);
        
        List<Object> selectData = groupMgmtManager.getMemberSelectData(conditionMap);
        	
        mav.addObject("totalCnt", selectData.get(0));
        mav.addObject("result", selectData.get(1));
        
        return mav;
    }
    @RequestMapping(value = "/gadget/system/getMemberSelectDataModem")
    public ModelAndView getMemberSelectDataModem(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam(value="groupType", required=false) String groupType,
            @RequestParam("memberName") String memberName,
            //@RequestParam(value="locationId", required=false) Integer locationId,
            @RequestParam("sModemType") String sModemType,
            @RequestParam("sModemId") String sModemId,
            @RequestParam("sMcuType") String sMcuType,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sModemFwVer") String sModemFwVer,
            @RequestParam("sModemSwRev") String sModemSwRev,
            @RequestParam("sModemHwVer") String sModemHwVer,
            @RequestParam(value="sModomStatus", required=false) String sModemStatus,
            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,
            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            @RequestParam("sLocationId") String sLocationId,
            //@RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            //@RequestParam("gridType") String gridType,
            //@RequestParam("modelId") String modelId,
            @RequestParam("sMeterSerial") String sMeterSerial,
            //@RequestParam("fwGadget") String fwGadget,
            String sState,
            String sInstallState ) {
        ModelAndView mav = new ModelAndView("jsonView");

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        //conditionMap.put("locationId", sLocationId);
        // 검색조건
        conditionMap.put("sModemType", sModemType);
        conditionMap.put("sModemId", sModemId);
        conditionMap.put("sState", sState);
        conditionMap.put("sInstallState", sInstallState);
        conditionMap.put("sMcuType", sMcuType);
        conditionMap.put("sMcuName", sMcuName);
        conditionMap.put("sModemSwRev", sModemSwRev);
        conditionMap.put("sModemHwVer", sModemHwVer);
        conditionMap.put("sModemStatus", sModemStatus);
        conditionMap.put("sInstallStartDate", sInstallStartDate);
        conditionMap.put("sInstallEndDate", sInstallEndDate);
        conditionMap.put("sLastcommStartDate", sLastcommStartDate);
        conditionMap.put("sLastcommEndDate", sLastcommEndDate);
        conditionMap.put("sLocationId", sLocationId);

        conditionMap.put("sOrder", sOrder);
        conditionMap.put("sCommState", sCommState);
        
        Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
        conditionMap.put("deleteCodeId", deleteCode.getId());
        Code BreakDownCode = codeManager.getCodeByCode(ModemSleepMode.BreakDown.getCode());
        conditionMap.put("breakDownCodeId", BreakDownCode.getId());
        Code NormalCode = codeManager.getCodeByCode(ModemSleepMode.Normal.getCode());
        conditionMap.put("normalCodeId", NormalCode.getId());
        Code RepairCode = codeManager.getCodeByCode(ModemSleepMode.Repair.getCode());
        conditionMap.put("repairCodeId", RepairCode.getId());
        Code SecurityErrorCode = codeManager.getCodeByCode(ModemSleepMode.SecurityError.getCode());
        conditionMap.put("securityErrorCodeId", SecurityErrorCode.getId());
        Code CommErrorCode = codeManager.getCodeByCode(ModemSleepMode.CommError.getCode());
        conditionMap.put("commErrorCodeId", CommErrorCode.getId());
        List<Object> selectData = groupMgmtManager.getMemberSelectData(conditionMap);
        	
        mav.addObject("totalCnt", selectData.get(0));
        mav.addObject("result", selectData.get(1));
        
        return mav;
    }
    
    @RequestMapping(value = "/gadget/system/getMemberSelectDataContract")
    public ModelAndView getMemberSelectDataContract(@RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="groupId", required=false) Integer groupId,
            @RequestParam(value="groupType", required=false) String groupType,
            @RequestParam("memberName") String memberName,
            //@RequestParam(value="locationId", required=false) Integer locationId,
            @RequestParam("customerNo") String customerNo,
            @RequestParam("contractNumber") String contractNumber,
    		@RequestParam("customerName") String customerName,
            @RequestParam("location") Integer location,
            @RequestParam("tariffIndex") Integer tariffIndex,
            @RequestParam("contractDemand") String contractDemand,
            @RequestParam("creditType") Integer creditType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("status") Integer status,
            @RequestParam("dr") String dr,
            @RequestParam("sicIds") String sicIds,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceTypeTab") String serviceTypeTab ) {
        ModelAndView mav = new ModelAndView("jsonView");

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupType", groupType);
        conditionMap.put("memberName", memberName);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        //conditionMap.put("locationId", sLocationId);
        // 검색조건
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("location", location);
        conditionMap.put("tariffIndex", tariffIndex);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("creditType", creditType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("status", status);
        conditionMap.put("dr", dr);
        conditionMap.put("sicIds", sicIds);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("address", address);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("serviceTypeTab", serviceTypeTab);
        //conditionMap.put("supplierId", user.getSupplier().getId());
        
        List<Object> selectData = groupMgmtManager.getMemberSelectData(conditionMap);
        	
        mav.addObject("totalCnt", selectData.get(0));
        mav.addObject("result", selectData.get(1));
        
        return mav;
    }
    /**
     * method name : getMemberSelectedData<b/>
     * method Desc : Group Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getMemberSelectedData")
    public ModelAndView getMemberSelectedData(@RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Object> selectData = groupMgmtManager.getMemberSelectedData(conditionMap);
        
        mav.addObject("totalCnt", selectData.get(0));
        mav.addObject("result", selectData.get(1));
        
        return mav;
    }

    /**
     * method name : addGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 저장한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/addGroupMembers")
    public ModelAndView addGroupMembers(@RequestParam("groupId") Integer groupId,
            @RequestParam("members") String members) {
        String result = null;
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("members", members);

        Integer resultCnt = groupMgmtManager.addGroupMembers(conditionMap);

        if (resultCnt != null && resultCnt > 0) {
            result = "success";
        } else {
            result = "failure";
        }
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : removeGroupMembers<b/>
     * method Desc : Group Management 가젯에서 Group Member 를 삭제한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/removeGroupMembers")
    public ModelAndView removeGroupMembers(@RequestParam("memberIds") String memberIds) {
        String result = null;
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("memberIds", memberIds);

        Integer resultCnt = groupMgmtManager.removeGroupMembers(conditionMap);

        if (resultCnt != null && resultCnt > 0) {
            result = "success";
        } else {
            result = "failure";
        }
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : saveGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 등록/수정 한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/saveGroup")
    public ModelAndView saveGroup(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("operatorId") Integer operatorId,
            @RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("groupType") String groupType,
            @RequestParam(value="mobileNo" ,required=false) String mobileNo,
            @RequestParam("allUserAccess") String allUserAccess) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupName", groupName);
        conditionMap.put("groupType", groupType);
        conditionMap.put("allUserAccess", allUserAccess);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("mobileNo", mobileNo);
        conditionMap.put("supplierId", supplierId);

        groupMgmtManager.saveGroup(conditionMap);

        mav.addObject("result", "success");
        return mav;
    }

    /**
     * method name : copyGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 복사한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/copyGroup")
    public ModelAndView copyGroup(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("operatorId") Integer operatorId,
            @RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName,
            @RequestParam("groupType") String groupType,
            @RequestParam(value="mobileNo" ,required=false) String mobileNo,
            @RequestParam("allUserAccess") String allUserAccess) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);
        conditionMap.put("groupName", groupName);
        conditionMap.put("groupType", groupType);
        conditionMap.put("allUserAccess", allUserAccess);
        conditionMap.put("mobileNo", mobileNo);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("supplierId", supplierId);

        groupMgmtManager.copyGroup(conditionMap);

        mav.addObject("result", "success");
        return mav;
    }

    /**
     * method name : deleteGroup<b/>
     * method Desc : Group Management 가젯에서 Group 을 삭제 한다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteGroup")
    public ModelAndView deleteGroup(@RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);

        groupMgmtManager.deleteGroup(conditionMap);

        mav.addObject("result", "success");
        return mav;
    }

    // 그룹에 따른 미터 목록
    @RequestMapping(value = "/gadget/system/getMeterGroupBygroupId")
    public ModelAndView getDeviceModelsByVenendorId(
    		@RequestParam("supplierId") int supplierId,
    		@RequestParam("groupType") String groupType) {

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = null;

        if(user != null && !user.isAnonymous()) {
            try {
                operatorId = user.getOperator(new Operator()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("operatorId", operatorId);
        condition.put("groupType", groupType);
        List<Map<String, Object>> meterGroups    = groupMgmtManager.getGroupComboDataByType(condition);
    	
        ModelMap model = new ModelMap();
        model.addAttribute("NAME", meterGroups);

        return new ModelAndView("jsonView", model);
    }
    
    // 계약 그룹 목록
    @RequestMapping(value = "/gadget/system/getContractGroup")
    public ModelAndView getContractGroup(@RequestParam("supplierId") int supplierId) {

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = null;

        if(user != null && !user.isAnonymous()) {
            try {
                operatorId = user.getOperator(new Operator()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    	List<AimirGroup> contractGroup    = groupMgmtManager.getContractGroup(operatorId);
    	
        ModelMap model = new ModelMap();
        model.addAttribute("NAME", contractGroup);

        return new ModelAndView("jsonView", model);
    }
    
    // 멤버 아이디로 그룹 아이디를 조회
    @RequestMapping(value = "/gadget/system/getGroupIdbyMember")
    public ModelAndView getGroupIdbyMember(@RequestParam("mdsId") String mdsId) {

    	int groupId= groupMgmtManager.getGroupIdbyMember(mdsId);
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("groupId", groupId);
        return mav;
    }
    
    /**
     * 
     * @param meterGroupId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getGroupMemberList")
    public ModelAndView getGroupMemberList(@RequestParam("meterGroupId") Integer meterGroupId) {

    	List<String> meterList = groupMgmtManager.getMeterGroupMemberIds(meterGroupId);
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("meterList", meterList);
        return mav;
    }
}