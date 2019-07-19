package com.aimir.bo.mvm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.service.mvm.VEEManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;

@Controller
public class VEEManagementMaxController {
	@Autowired
    VEEManager VEEManager;

    @Autowired
    RoleManager roleManager;

	protected static Log log = LogFactory.getLog(VEEManagementMaxController.class);

	@RequestMapping(value="/gadget/mvm/VEEManagementEmMaxGadget.do")
    public final ModelAndView executeEM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMaxGadget");
        mav.addObject("mvmMiniType", CommonConstants.MeterType.EnergyMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        mav.addObject("veeRuleList", VEEManager.getVEERuleList());
        mav.addObject("veeParametersNameList", VEEManager.getVEEParamNames(null));
//        mav.addObject("mtrAuthority", getRoleMtrAuthority());//수정권한

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        String veeNumberFormat = user.getSupplier().getMd().getPattern();
        Integer dot = veeNumberFormat.indexOf(".");

        if(dot != -1)
        	veeNumberFormat =veeNumberFormat.substring(0,dot);

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
      
        mav.addObject("operatorId", Double.toString(user.getAccountId()));
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("veeEditAuth", authMap.get("vee"));  // 수정권한(write/command = true)
        mav.addObject("edititems", VEEManager.getVEEEditItemList());
        mav.addObject("veeNumberFormat", veeNumberFormat.replace("#", "0"));
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementGmMaxGadget.do")
    public final ModelAndView executeGM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMaxGadget");
        mav.addObject("mvmMiniType", CommonConstants.MeterType.GasMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        mav.addObject("veeRuleList", VEEManager.getVEERuleList());
        mav.addObject("veeParametersNameList", VEEManager.getVEEParamNames(null));
        mav.addObject("mtrAuthority", getRoleMtrAuthority());//수정권한
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
      
        mav.addObject("operatorId", Double.toString(user.getAccountId()));
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("veeEditAuth", authMap.get("vee"));  // 수정권한(write/command = true)
        mav.addObject("edititems", VEEManager.getVEEParameterNameList());
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementWmMaxGadget.do")
    public final ModelAndView executeWM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMaxGadget");
        mav.addObject("mvmMiniType", CommonConstants.MeterType.WaterMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        mav.addObject("veeRuleList", VEEManager.getVEERuleList());
        mav.addObject("veeParametersNameList", VEEManager.getVEEParamNames(null));
        mav.addObject("mtrAuthority", getRoleMtrAuthority());//수정권한
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
      
        mav.addObject("operatorId", Double.toString(user.getAccountId()));
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("veeEditAuth", authMap.get("vee"));  // 수정권한(write/command = true)
        mav.addObject("edititems", VEEManager.getVEEParameterNameList());
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementHmMaxGadget.do")
    public final ModelAndView executeHM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMaxGadget");
        mav.addObject("mvmMiniType", CommonConstants.MeterType.HeatMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        mav.addObject("veeRuleList", VEEManager.getVEERuleList());
        mav.addObject("veeParametersNameList", VEEManager.getVEEParamNames(null));
        mav.addObject("mtrAuthority", getRoleMtrAuthority());//수정권한
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
      
        mav.addObject("operatorId", Double.toString(user.getAccountId()));
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("veeEditAuth", authMap.get("vee"));  // 수정권한(write/command = true)
        mav.addObject("edititems", VEEManager.getVEEParameterNameList());
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementAutoEstimation.do", method=RequestMethod.GET)
    public final ModelAndView popupAutoEstimation(
    		@RequestParam("meterType") String meterType
    		,@RequestParam("item") String item
            ,@RequestParam("yyyymmdd") String yyyymmdd
            ,@RequestParam("channel") String channel                        
            ,@RequestParam("mdevType") String mdevType
            ,@RequestParam("mdevId") String mdevId
            ,@RequestParam("dst") String dst) {
		
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEAutoEstimation");
        mav.addObject("meterType", meterType);
        mav.addObject("item", item);
        mav.addObject("yyyymmdd", yyyymmdd);
        mav.addObject("channel", channel);
        mav.addObject("mdevType", mdevType);
        mav.addObject("mdevId", mdevId);
        mav.addObject("dst", dst);
        
        return mav;
    }
	
	public String getRoleMtrAuthority(){
		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		
		return user.getRoleData().getMtrAuthority();
	}
	
	//String meterType, String userId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst, String[] params
	@RequestMapping(value="/gadget/mvm/VEEManagementUpdateData.do")
    public final ModelAndView updateData(
    		 @RequestParam("meterType") String meterType
			 ,@RequestParam("userId") String userId
			 ,@RequestParam("supplierId") String supplierId
             ,@RequestParam("yyyymmddhh") String yyyymmddhh
             ,@RequestParam("yyyymmdd") String yyyymmdd
             ,@RequestParam("hh") String hh
             ,@RequestParam("channel") String channel
             ,@RequestParam("mdevType") String mdevType
             ,@RequestParam("mdevId") String mdevId
             ,@RequestParam("dst") String dst
             ,@RequestParam("params[]") String[] params 
             ) {
        ModelAndView mav = new ModelAndView("jsonView");
        String status = "";
        yyyymmddhh = yyyymmdd+hh;
        try {
        	VEEManager.updateLpData(meterType, userId, supplierId, yyyymmddhh, yyyymmdd,  hh,  channel,  mdevType,  mdevId,  dst,  params);
        	status = ResultStatus.SUCCESS.name();
        }catch(Exception e){
        	status = ResultStatus.FAIL.name();
        	e.printStackTrace();
        }
        mav.addObject("result", status);
        
        return mav;
    }
	
	//String meterType, String userId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst
	@RequestMapping(value="/gadget/mvm/VEEManagementEstimationData.do")
    public final ModelAndView estimationData(
    		 @RequestParam("meterType") String meterType
			 ,@RequestParam("userId") String userId
             ,@RequestParam("yyyymmddhh") String yyyymmddhh
             ,@RequestParam("yyyymmdd") String yyyymmdd
             ,@RequestParam("hh") String hh
             ,@RequestParam("channel") String channel
             ,@RequestParam("mdevType") String mdevType
             ,@RequestParam("mdevId") String mdevId
             ,@RequestParam("dst") String dst
             ) {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", VEEManager.estimationData(meterType, userId, yyyymmddhh, yyyymmdd,  hh,  channel,  mdevType,  mdevId,  dst));
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/updateVEEAutoEstimationData.do")
    public final ModelAndView updateAutoEstimationData(
    		 @RequestParam("meterType") String meterType
			 ,@RequestParam("item") String item
             ,@RequestParam("yyyymmdd") String yyyymmdd
             ,@RequestParam("channel") String channel
             ,@RequestParam("mdevType") String mdevType
             ,@RequestParam("mdevId") String mdevId
             ,@RequestParam("dst") String dst
             ,@RequestParam("supplierId") String supplierId
             ,@RequestParam("type1") String type1
             ,@RequestParam("type2") String type2
             ,@RequestParam("userId") String userId
             ) {
		
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("meterType", meterType);
		conditions.put("item", item);
		conditions.put("yyyymmdd", yyyymmdd);
		conditions.put("channel", channel);
		conditions.put("mdevType", mdevType);
		conditions.put("mdevId", mdevId);
		conditions.put("dst", dst);
		conditions.put("supplierId", supplierId);
		conditions.put("type1", type1);
		conditions.put("type2", type2);
		conditions.put("userId", userId);
		
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", VEEManager.updateAutoEstimation(conditions));
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getPreviewAutoEstimation.do")
    public final ModelAndView getPreviewAutoEstimation(
    		 @RequestParam("meterType") String meterType
			 ,@RequestParam("item") String item
             ,@RequestParam("yyyymmdd") String yyyymmdd
             ,@RequestParam("channel") String channel
             ,@RequestParam("mdevType") String mdevType
             ,@RequestParam("mdevId") String mdevId
             ,@RequestParam("dst") String dst
             ,@RequestParam("supplierId") String supplierId
             ,@RequestParam("type1") String type1
             ,@RequestParam("type2") String type2
             ,@RequestParam("userId") String userId) {
		
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("meterType", meterType);
		conditions.put("item", item);
		conditions.put("yyyymmdd", yyyymmdd);
		conditions.put("channel", channel);
		conditions.put("mdevType", mdevType);
		conditions.put("mdevId", mdevId);
		conditions.put("dst", dst);
		conditions.put("supplierId", supplierId);
		conditions.put("type1", type1);
		conditions.put("type2", type2);
		conditions.put("userId", userId);
		
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", VEEManager.getPreviewAutoEstimation(conditions));
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getMaxVEEValidationCheckManager.do")
    public final ModelAndView getMaxVEEValidationCheckManager(
    												@RequestParam("meterType") String meterType
													,@RequestParam("tabType") String tabType
										            ,@RequestParam("dateType") String dateType
										            ,@RequestParam("startDate") String startDate
										            ,@RequestParam("endDate") String endDate
										            ,@RequestParam("selectData") String selectData
										            ,@RequestParam("contractNo") String contractNo
										            ,@RequestParam("meterId") String meterId
										            ,@RequestParam("userId") String userId
										            ,@RequestParam("ParametersName") String ParametersName
										            ,@RequestParam("veeRule") String veeRule
										            ,@RequestParam("mtrAuthority") String mtrAuthority
										            ,@RequestParam("operatorId") String operatorId
										            ,@RequestParam("supplierId") String supplierId
										            ,@RequestParam("editItem") String editItem
										            ,@RequestParam("pageSize") String pageSize
										            ,@RequestParam("page") String page) {
		
		String[] values = new String[15];
		
		values[0] = meterType;
		values[1] = tabType;
		values[2] = dateType;
		values[3] = startDate;
		values[4] = endDate;
		values[5] = selectData;
		values[6] = contractNo;
		values[7] = meterId;
		values[8] = userId;
		values[9] = ParametersName;
		values[10] = veeRule;
		values[11] = mtrAuthority;
		values[12] = operatorId;
		values[13] = supplierId;
		values[14] = editItem;

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData", VEEManager.getMaxVEEValidationCheckManager(values, page,pageSize));
        Map<String, String> totalMap = VEEManager.getMaxVEEValidationCheckManagerTotal(values);
        mav.addObject("totalCnt", Integer.parseInt(totalMap.get("total")));

        return mav;
    }

	@RequestMapping(value="/gadget/mvm/getLpData.do")
    public final ModelAndView getLpData(
										@RequestParam("meterType") String meterType
										,@RequestParam("table") String table
							            ,@RequestParam("yyyymmdd") String yyyymmdd
							            ,@RequestParam("channel") String channel
							            ,@RequestParam("mdevType") String mdevType
							            ,@RequestParam("mdevId") String mdevId
							            ,@RequestParam("dst") String dst
							            ,@RequestParam("supplierId") String supplierId) {
	
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String,Object> resultMap =  VEEManager.getLpData(meterType, table, yyyymmdd, channel, mdevType, mdevId, dst, supplierId);
        mav.addObject("gridData", resultMap.get("lpList"));
        mav.addObject("lpinterval", resultMap.get("lpinterval"));
        mav.addObject("mdFormat", resultMap.get("mdFormat"));
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getMaxVEEHistoryManager.do")
    public final ModelAndView getMaxVEEHistoryManager(@RequestParam("meterType") String meterType,
            @RequestParam("tabType") String tabType,
			@RequestParam("dateType") String dateType,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("selectData") String selectData,
			@RequestParam("contractNo") String contractNo,
			@RequestParam("meterId") String meterId,
			@RequestParam("userId") String userId,
			@RequestParam("ParametersName") String ParametersName,
			@RequestParam("veeRule") String veeRule,
			@RequestParam("mtrAuthority") String mtrAuthority,
			@RequestParam("operatorId") String operatorId,
			@RequestParam("supplierId") String supplierId,
			@RequestParam("veeEditItemList") String veeEditItemList,
			@RequestParam("page") String page,
			@RequestParam("limit") String limit) {
		
		String[] values = new String[15];
		
		values[0] = meterType;
		values[1] = tabType;
		values[2] = dateType;
		values[3] = startDate;
		values[4] = endDate;
		values[5] = selectData;
		values[6] = contractNo;
		values[7] = meterId;
		values[8] = userId;
		values[9] = ParametersName;
		values[10] = veeRule;
		values[11] = mtrAuthority;
		values[12] = operatorId;
		values[13] = supplierId;
		values[14] = veeEditItemList;

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData", VEEManager.getMaxVEEHistoryManager(values, page, limit));
        mav.addObject("totalCnt", VEEManager.getMaxVEEHistoryManagerTotal(values).get("total"));
        return mav;
    }
	
	
	@RequestMapping(value="/gadget/mvm/getMaxVEEParametersManager.do")
    public final ModelAndView getMaxVEEParameterManager(@RequestParam("veeRule") String veeRule) {
		
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData", VEEManager.getMaxVEEParametersManager(veeRule));

        return mav;
    }
}
