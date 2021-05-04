/**
 * PrepaymentMgmtOperatorController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.prepaymentMgmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Role;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;

/**
 * PrepaymentMgmtOperatorController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 8. 17.   v1.0       eunmiae  선불 고객 관리 관리자View Controller         
 *
 */
@Controller
public class PrepaymentMgmtOperatorController {

    @Autowired
    PrepaymentMgmtOperatorManager prepaymentMgmtOperatorManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtOperatorMini")
    public ModelAndView loadPrepaymentMgmtOperatorMini() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtOperatorMini");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("supplierId", supplierId);
        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtOperatorMax")
    public ModelAndView loadPrepaymentMgmtOperatorMax() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtOperatorMax");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        List<Code> serviceType = codeManager.getChildCodes(Code.ENERGY);
        List<Code> status = codeManager.getChildCodes(Code.STATUS);
        List<Code> meterStatus = codeManager.getChildCodes(Code.METER_STATUS);

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("operatorId", operatorId);
        mav.addObject("supplierId", supplierId);
        mav.addObject("loginId", user.getLoginId());
        mav.addObject("serviceTypeCodeList", serviceType);
        mav.addObject("statusCodeList", status);
        mav.addObject("meterStatus", meterStatus);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    /**
     * method name : getEmergencyCreditContractList
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getEmergencyCreditContractList")
    public ModelAndView getEmergencyCreditContractList() {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        
        int supplierId = user.getRoleData().getSupplier().getId();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);

        List<Map<String, Object>> result = prepaymentMgmtOperatorManager.getEmergencyCreditContractList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", prepaymentMgmtOperatorManager.getEmergencyCreditContractListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getPrepaymentContractStatusChartData
     * method Desc : 관리자 선불관리 미니가젯의 선불고객 Pie Chart Data 를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getPrepaymentContractStatusChartData")
    public ModelAndView getPrepaymentContractStatusChartData(@RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);

        Map<String, Object> result = prepaymentMgmtOperatorManager.getPrepaymentContractStatusChartData(conditionMap);
        mav.addObject("chartDatas", result);

        return mav;
    }

    /**
     * method name : notifyEmergencyCredit
     * method Desc : 선택한 선불고객들에게 Emergency Credit Mode 정보를 통보한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/notifyEmergencyCredit")
    public ModelAndView notifyEmergencyCredit(@RequestParam("checkedData") String checkedData) {
        ModelAndView mav = new ModelAndView("jsonView");

//        Map<String, Object> conditionMap = new HashMap<String, Object>();
            
        // TODO - 통보프레임워크 호출
        //List<Map<String, Object>> result = prepaymentMgmtOperatorManager.getPrepaymentContractStatusChartData(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : getSelectBoxData
     * method Desc : SelectBox 데이터를 가져온다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/getSelectBoxData")
    public ModelAndView getSelectBoxData() {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Code> serviceType = codeManager.getChildCodes(Code.ENERGY);
        List<Code> status = codeManager.getChildCodes(Code.STATUS);
        
        mav.addObject("serviceType", serviceType);
        mav.addObject("status", status);
        
        return mav;
    }

    /**
     * method name : getPrepaymentContractList
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getPrepaymentContractList")
    public ModelAndView getPrepaymentContractList(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("customerName") String customerName,
            @RequestParam("customerNumber") String customerNumber,
            @RequestParam("statusCode") String statusCode,
            @RequestParam("amountStatus") String amountStatus,
            @RequestParam("meterStatus") String meterStatus,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("gs1") String gs1,
            //@RequestParam("address") String address,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("serviceTypeCode") String serviceTypeCode,
            @RequestParam("searchLastChargeDate") String searchLastChargeDate,
            @RequestParam("lastChargeStartDate") String lastChargeStartDate,
            @RequestParam("lastChargeEndDate") String lastChargeEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = user.getRoleData().getSupplier().getId();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("customerNumber", StringUtil.nullToBlank(customerNumber));
        conditionMap.put("contractNumber", StringUtil.nullToBlank(contractNumber));
        conditionMap.put("customerName", StringUtil.nullToBlank(customerName));
        conditionMap.put("statusCode", StringUtil.nullToBlank(statusCode));
        conditionMap.put("amountStatus", StringUtil.nullToBlank(amountStatus));
        conditionMap.put("meterStatus", StringUtil.nullToBlank(meterStatus));
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("gs1", gs1);
//        conditionMap.put("address", StringUtil.nullToBlank(address));
        conditionMap.put("locationId", locationId);
        conditionMap.put("serviceTypeCode", StringUtil.nullToBlank(serviceTypeCode));
        conditionMap.put("searchLastChargeDate", StringUtil.nullToBlank(searchLastChargeDate));
        conditionMap.put("lastChargeStartDate", StringUtil.nullToBlank(lastChargeStartDate));
        conditionMap.put("lastChargeEndDate", StringUtil.nullToBlank(lastChargeEndDate));
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);

        List<Map<String, Object>> result = prepaymentMgmtOperatorManager.getPrepaymentContractList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", prepaymentMgmtOperatorManager.getPrepaymentContractListTotalCount(conditionMap));

        return mav;
    }
    
    /**
     * method name : getRelayEnableModel
     * method Desc : Relay기능이 가능한 모델을 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getRelayEnableModel")
    public ModelAndView getRelayEnableModel(@RequestParam("devicemodelName") String devicemodelName) {
        ModelAndView mav = new ModelAndView("jsonView");
        
        String[] namesOfContain = CommonConstants.EnableCommandModel.getNameOfContain(devicemodelName);
        
        mav.addObject("namesOfContain",namesOfContain);

        return mav;
    } 

    /**
     * method name : getAuthDeviceList
     * method Desc : 관리자 선불관리 맥스가젯의 인증장비 리스트를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getAuthDeviceList")
    public ModelAndView getAuthDeviceList(@RequestParam("contractNumber") String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : updateEmergencyCreditInfo
     * method Desc : 관리자 선불관리 맥스가젯의 Emergency Credit 정보를 저장한다.
     *
     * @param contractNumber
     * @param autoChange
     * @param duration
     * @param limitPower
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/updateEmergencyCreditInfo")
    public ModelAndView updateEmergencyCreditInfo(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("autoChange") Boolean autoChange,
            @RequestParam("duration") Integer duration,
            @RequestParam("limitPower") Double limitPower) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("autoChange", autoChange);
        conditionMap.put("duration", duration);
        conditionMap.put("limitPower", limitPower);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        if (contract == null) {
            mav.addObject("status", "fail");
            return mav;
        }
        conditionMap.put("supplierId", contract.getSupplier().getId());

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        
        conditionMap.put("operatorId", new Long(user.getAccountId()).intValue());

        prepaymentMgmtOperatorManager.updateEmergencyCreditInfo(conditionMap);
        mav.addObject("status", "success");

        return mav;
    }
}