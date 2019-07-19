/**
 * EnergyBalanceMonitoringController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.energyBalanceMonitoring;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommonConstantsProperty;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.EnergyBalanceMonitoringManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.EnergyBalanceMonitoringReportExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * EnergyBalanceMonitoringController.java Description
 *
 *
 * Date          Version     Author   Description
 * 2012. 3. 12.  v1.0        문동규   Energy Balance Monitoring View Controller
 *
 */
@Controller
public class EnergyBalanceMonitoringController {

    protected static Log log = LogFactory.getLog(EnergyBalanceMonitoringController.class);

    @Autowired
    PrepaymentMgmtOperatorManager prepaymentMgmtOperatorManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    EnergyBalanceMonitoringManager energyBalanceMonitoringManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/system/energyBalanceMonitoringMini")
    public ModelAndView loadPrepaymentMgmtOperatorMini() {
        ModelAndView mav = new ModelAndView("/gadget/system/energyBalanceMonitoringMini");
        
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

    @RequestMapping(value="/gadget/system/energyBalanceMonitoringMax")
    public ModelAndView loadPrepaymentMgmtOperatorMax() {
        ModelAndView mav = new ModelAndView("/gadget/system/energyBalanceMonitoringMax");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        try {
            String currentDate = TimeUtil.getCurrentDay();
            String prevDate = TimeUtil.getPreDay(TimeUtil.getCurrentDay() + "000000").substring(0, 8);
            mav.addObject("_basicDate", prevDate);
            
            int du = TimeUtil.getDayDuration(currentDate, prevDate);
            String dpMax = null;
            if (du >= 0) {
                dpMax = "+" + du + "d";
            } else {
                dpMax = du + "d";
            }
            mav.addObject("_dpMax", dpMax);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value="/gadget/system/energyBalanceMonitoringExcelDownloadPopup")
    public ModelAndView mvmMaxExcelDownloadPopup() {      
        ModelAndView mav = new ModelAndView("/gadget/system/energyBalanceMonitoringExcelDownloadPopup");
        return mav;
    }

    /**
     * method name : getDefaultThreshold
     * method Desc : Substation의 Default 임계치를 DTS등록시 설정한다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/getDefaultThreshold")
    public ModelAndView getDefaultThreshold() {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("defaultThreshold", CommonConstantsProperty.getProperty("ebs.substation.threshold.default"));
        return mav;
    }
    /**
     * method name : getCurrentMonthSearchCondition<b/>
     * method Desc : Energy Balance Monitoring 미니가젯의 조회월 타이틀 및 조회일자 조건을 가져온다.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getCurrentMonthSearchCondition")
    public ModelAndView getCurrentMonthSearchCondition(@RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", energyBalanceMonitoringManager.getCurrentMonthSearchCondition(supplierId));
        return mav;
    }

    /**
     * method name : getEbsSuspectedDtsList
     * method Desc : Energy Balance Monitoring 미니가젯의 Suspected Substation List 를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsSuspectedDtsList")
    public ModelAndView getEbsSuspectedDtsList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        try {
            String searchPreStartDate = TimeUtil.getPreDay(searchStartDate, 7);
            conditionMap.put("searchPreStartDate", searchPreStartDate.substring(0, 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> result = energyBalanceMonitoringManager.getEbsSuspectedDtsList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", energyBalanceMonitoringManager.getEbsSuspectedDtsListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getEbsDtsStateChartData
     * method Desc : Energy Balance Monitoring 미니가젯의 Substation Pie Chart Data 를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsStateChartData")
    public ModelAndView getEbsDtsStateChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        try {
            String searchPreStartDate = TimeUtil.getPreDay(searchStartDate, 7);
            conditionMap.put("searchPreStartDate", searchPreStartDate.substring(0, 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> result = energyBalanceMonitoringManager.getEbsDtsStateChartData(conditionMap);
        mav.addObject("chartDatas", result);

        return mav;
    }

    /**
     * method name : getEbsDtsTreeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Data 를 조회한다.
     *
     * @param supplierId
     * @param searchStartDate
     * @param searchEndDate
     * @param suspected
     * @param locationId
     * @param threshold
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsTreeData")
    public ModelAndView getEbsDtsTreeData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchBasicDate") String searchBasicDate,
            @RequestParam("suspected") String suspected,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("dtsName") String dtsName,
            @RequestParam("threshold") Double threshold) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        
        try {
            String basicDate = null;
            if (!searchBasicDate.isEmpty()) {
                basicDate = searchBasicDate;
            } else {
                basicDate = TimeUtil.getCurrentDay();
            }

            if (searchStartDate.isEmpty()) {
                searchStartDate = basicDate;
            }
            if (searchEndDate.isEmpty()) {
                searchEndDate = basicDate;
            }
            String searchPreStartDate = TimeUtil.getPreDay(searchStartDate, 7);
            conditionMap.put("searchPreStartDate", searchPreStartDate.substring(0, 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("suspected", suspected);
        conditionMap.put("locationId", locationId);
        conditionMap.put("dtsName", dtsName);
        conditionMap.put("threshold", threshold);

        List<Map<String, Object>> result = null;
        
        result = energyBalanceMonitoringManager.getEbsDtsTreeData(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getEbsDtsTreeContractMeterNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Node Data 를 조회한다.
     *
     * @param supplierId
     * @param searchStartDate
     * @param searchEndDate
     * @param meterId
     * @param phaseId
     * @param node
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsTreeContractMeterNodeData")
    public ModelAndView getEbsDtsTreeContractMeterNodeData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("suspectedYn") Boolean suspectedYn,
            @RequestParam("node") String node) {
        ModelAndView mav = new ModelAndView("treeJsonView");
        List<Map<String, Object>> result = null;

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("suspectedYn", suspectedYn);
        conditionMap.put("node", node);

        try {
            conditionMap.put("searchPreStartDate", TimeUtil.getPreDay(searchStartDate, 7).substring(0, 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // phase nodeId 구조 : locationId_dtsId_meterId_phaseId
        String[] nodeArr = node.split("_");

        conditionMap.put("meterId", Integer.valueOf(nodeArr[2]));
        conditionMap.put("phaseId", Integer.valueOf(nodeArr[3]));

        result = energyBalanceMonitoringManager.getEbsDtsTreeContractMeterNodeData(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getEbsDtsList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다.
     *
     * @param supplierId
     * @param dtsName
     * @param locationId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsList")
    public ModelAndView getEbsDtsList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("dtsName") String dtsName,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("threshold") Double threshold) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("dtsName", dtsName);
        conditionMap.put("locationId", locationId);
        conditionMap.put("threshold", threshold);

        List<Map<String, Object>> result = energyBalanceMonitoringManager.getEbsDtsList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", energyBalanceMonitoringManager.getEbsDtsListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getEbsMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 를 조회한다.
     *
     * @param supplierId
     * @param mdsId
     * @param locationId
     * @param meterGroupId
     * @param installStartDate
     * @param installEndDate
     * @param deviceVendorId
     * @param deviceModelId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsMeterList")
    public ModelAndView getEbsMeterList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("meterGroup") Integer meterGroupId,
            @RequestParam("installStartDate") String installStartDate,
            @RequestParam("installEndDate") String installEndDate,
            @RequestParam("deviceVendor") Integer deviceVendorId,
            @RequestParam("deviceModel") Integer deviceModelId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("locationId", locationId);
        conditionMap.put("meterGroupId", meterGroupId);
        conditionMap.put("installStartDate", installStartDate);
        conditionMap.put("installEndDate", installEndDate);
        conditionMap.put("deviceVendorId", deviceVendorId);
        conditionMap.put("deviceModelId", deviceModelId);

        List<Map<String, Object>> result = energyBalanceMonitoringManager.getEbsMeterList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", energyBalanceMonitoringManager.getEbsMeterListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getEbsContractMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 를 조회한다.
     *
     * @param supplierId
     * @param customerId
     * @param customerName
     * @param contractNumber
     * @param contractGroupId
     * @param locationId
     * @param mdsId
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsContractMeterList")
    public ModelAndView getEbsContractList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("customerId") String customerId,
            @RequestParam("customerName") String customerName,
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("contractGroup") Integer contractGroupId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("mdsId") String mdsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("customerId", customerId);
        conditionMap.put("customerName", customerName);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("contractGroupId", contractGroupId);
        conditionMap.put("locationId", locationId);
        conditionMap.put("mdsId", mdsId);

        List<Map<String, Object>> result = energyBalanceMonitoringManager.getEbsContractMeterList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", energyBalanceMonitoringManager.getEbsContractMeterListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getEbsDtsNameDup<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Name 의 중복을 체크한다.
     *
     * @param supplierId
     * @param dtsName
     * @return true : 중복
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsNameDup")
    public ModelAndView getEbsDtsNameDup(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("dtsName") String dtsName) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("dtsName", dtsName);

        Boolean result = energyBalanceMonitoringManager.getEbsDtsNameDup(conditionMap);
        mav.addObject("result", result.toString());

        return mav;
    }

    /**
     * method name : insertEbsDts<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
     *
     * @param supplierId
     * @param dtsName
     * @param threshold
     * @param locationId
     * @param address
     * @param description
     * @return
     */
    @RequestMapping(value = "/gadget/system/insertEbsDts")
    public ModelAndView insertEbsDts(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("dtsName") String dtsName,
            @RequestParam("threshold") Double threshold,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("address") String address,
            @RequestParam("description") String description) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("dtsName", dtsName);
        conditionMap.put("threshold", threshold);
        conditionMap.put("locationId", locationId);
        conditionMap.put("address", address);
        conditionMap.put("description", description);

        energyBalanceMonitoringManager.insertEbsDts(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : updateEbsDtsList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 리스트를 수정한다.
     *
     * @param supplierId
     * @param dtsIds
     * @param dtsNames
     * @param thresholds
     * @param locationIds
     * @param addresses
     * @param descriptions
     * @return
     */
    @RequestMapping(value = "/gadget/system/updateEbsDtsList")
    public ModelAndView updateEbsDtsList(@RequestParam("supplierId") String supplierId,
            @RequestParam("dtsIds[]") String[] dtsIds,
            @RequestParam("dtsNames[]") String[] dtsNames,
            @RequestParam("thresholds[]") String[] thresholds,
            @RequestParam("locationIds[]") String[] locationIds,
            @RequestParam("addresses[]") String[] addresses,
            @RequestParam("descriptions[]") String[] descriptions) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (dtsIds == null || dtsIds.length <= 0) {
            mav.addObject("result", "failure");
            return mav;
        }

        Map<String, Object> conditionMap = new HashMap<String, Object>();
//        conditionMap.put("supplierId", supplierId);
        conditionMap.put("dtsIds", dtsIds);
        conditionMap.put("thresholds", thresholds);
        conditionMap.put("addresses", addresses);
        conditionMap.put("descriptions", descriptions);

        energyBalanceMonitoringManager.updateEbsDtsList(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : addEbsMeterNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 에 Meter Node 를 추가한다.
     *
     * @param supplierId
     * @param dtsId
     * @param meterIds
     * @return
     */
    @RequestMapping(value = "/gadget/system/addEbsMeterNode")
    public ModelAndView addEbsMeterNode(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("dtsId") Integer dtsId,
            @RequestParam("meterIds[]") String[] meterIds) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("dtsId", dtsId);
        conditionMap.put("meterIds", meterIds);

        energyBalanceMonitoringManager.addEbsMeterNode(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : addEbsContractMeterNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 Meter Phase 에 Contract Meter Node 를 추가한다.
     *
     * @param supplierId
     * @param meterId
     * @param phaseId
     * @param contractIds
     * @return
     */
    @RequestMapping(value = "/gadget/system/addEbsContractMeterNode")
    public ModelAndView addEbsContractNode(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("meterId") Integer meterId,
            @RequestParam("phaseId") Integer phaseId,
            @RequestParam("contMeterIds[]") String[] contMeterIds) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("meterId", meterId);
        conditionMap.put("phaseId", phaseId);
        conditionMap.put("contMeterIds", contMeterIds);

        energyBalanceMonitoringManager.addEbsContractMeterNode(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : deleteEbsDtsTreeNode<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Tree 의 Node 를 삭제한다.
     *
     * @param supplierId
     * @param deleteNodeId
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteEbsDtsTreeNode")
    public ModelAndView deleteEbsDtsTreeNode(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("deleteNodeId") String deleteNodeId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("deleteNodeId", deleteNodeId);

        energyBalanceMonitoringManager.deleteEbsDtsTreeNode(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : getEbsDtsImportChartData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 DTS/Meter/Phase Chart Data 를 조회한다.
     *
     * @param supplierId
     * @param nodeId
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsImportChartData")
    public ModelAndView getEbsDtsImportChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("nodeId") String nodeId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("nodeId", nodeId);

        String startDate = searchStartDate.substring(0, 6) + "01";
        
        String endDate = startDate.substring(0, 6)
                + StringUtil.frontAppendNStr('0',
                        CalendarUtil.getMonthLastDate(startDate.substring(0, 4), startDate.substring(4, 6)), 2);

        conditionMap.put("searchStartDate", startDate);
        conditionMap.put("searchEndDate", endDate);
  
        try{
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();   
            
            String country = user.getSupplier().getCountry().getCode_2letter();
            String lang    = user.getSupplier().getLang().getCode_2letter();
	        // 당월
        	String thisMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(searchStartDate.substring(0, 6), lang, country);
	        // 전월
	        String lastMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(TimeUtil.getAddedDay(startDate+"000000", -1).substring(0, 6), lang, country);
	        // 전년도 동월
	        String lastYearMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(TimeUtil.getAddedMonth(startDate+"000000", -12).substring(0, 6), lang, country);
	    
	        Map<String, Object> result = energyBalanceMonitoringManager.getEbsDtsImportChartData(conditionMap);
	        result.put("thisMonth", thisMonth);
	        result.put("lastMonth", lastMonth);
	        result.put("lastYearMonth", lastYearMonth);
	        mav.addAllObjects(result);

        }catch(ParseException e) {
	    	e.printStackTrace();
	    }

        return mav;
    }

    /**
     * method name : getEbsDtsConsumeChartData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Chart Data 를 조회한다.
     *
     * @param supplierId
     * @param nodeId
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsDtsConsumeChartData")
    public ModelAndView getEbsDtsConsumeChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("nodeId") String nodeId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("nodeId", nodeId);

        String startDate = searchStartDate.substring(0, 6) + "01";
        String endDate = startDate.substring(0, 6)
                + StringUtil.frontAppendNStr('0',
                        CalendarUtil.getMonthLastDate(startDate.substring(0, 4), startDate.substring(4, 6)), 2);

        conditionMap.put("searchStartDate", startDate);
        conditionMap.put("searchEndDate", endDate);

        try{
            Map<String, Object> result = energyBalanceMonitoringManager.getEbsDtsConsumeChartData(conditionMap);
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();   

            String country = user.getSupplier().getCountry().getCode_2letter();
            String lang    = user.getSupplier().getLang().getCode_2letter();
	        // 당월
        	String thisMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(searchStartDate.substring(0, 6), lang, country);
	        // 전월
	        String lastMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(TimeUtil.getAddedDay(startDate+"000000", -1).substring(0, 6), lang, country);
	        // 전년도 동월
	        String lastYearMonth = TimeLocaleUtil.getLocaleDateByMediumFormat(TimeUtil.getAddedMonth(startDate+"000000", -12).substring(0, 6), lang, country);

	        result.put("thisMonth", thisMonth);
	        result.put("lastMonth", lastMonth);
	        result.put("lastYearMonth", lastYearMonth);
	        mav.addAllObjects(result);
        }catch(ParseException e) {
	    	e.printStackTrace();
	    }

        return mav;
    }

    /**
     * method name : getEbsExportExcel<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Report Excel 을 생성한다.
     *
     * @param supplierId
     * @param searchStartDate
     * @param searchEndDate
     * @param searchDateType
     * @param suspected
     * @param locationId
     * @param dtsName
     * @param threshold
     * @return
     */
    @RequestMapping(value = "/gadget/system/getEbsExportExcel")
    public ModelAndView getEbsExportExcel(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("suspected") String suspected,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("dtsName") String dtsName,
            @RequestParam("threshold") Double threshold,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("filePath") String filePath,
            @RequestParam(value="week", required=false) Integer week,
            @RequestParam(value="weekMsg", required=false) String weekMsg,
            @RequestParam(value="title", required=false) String title) {
        String ebsPrefix = "ebsReport";     // excel file prefix
        int maxRows = 5000;     // excel 파일 당 최대 행수

        ModelAndView mav = new ModelAndView("jsonView");
        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
            }
            if (searchEndDate.isEmpty()) {
                searchEndDate = TimeUtil.getCurrentDay();
            }
            String searchPreStartDate = TimeUtil.getPreDay(searchStartDate, 7);
            conditionMap.put("searchPreStartDate", searchPreStartDate.substring(0, 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);

//        suspected = StringUtil.nullToBlank(conditionMap.get("suspected"));
//        conditionMap.put("suspected", (!StringUtil.nullToBlank(conditionMap.get("suspected")).isEmpty()) ? Boolean.TRUE : Boolean.FALSE);
        conditionMap.put("suspected", (!suspected.isEmpty()) ? Boolean.TRUE : Boolean.FALSE);
        conditionMap.put("locationId", locationId);
        conditionMap.put("dtsName", dtsName);
        conditionMap.put("threshold", threshold);

        List<String> headerList = Arrays.asList(headerMsg);
        List<List<Object>> result = energyBalanceMonitoringManager.getEbsExportExcelData(conditionMap);

        int total = result.size();

        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        String startDateFormat = null;
        String endDateFormat = null;
        Supplier supplier = supplierManager.getSupplier(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();

        try {
            if ((DateType.DAILY.getCode()).equals(searchDateType) || (DateType.PERIOD.getCode()).equals(searchDateType)) {  // 일간/기간
                startDateFormat = TimeLocaleUtil.getLocaleDateByMediumFormat(searchStartDate, lang, country);
                endDateFormat = TimeLocaleUtil.getLocaleDateByMediumFormat(searchEndDate, lang, country);
            } else if ((DateType.WEEKLY.getCode()).equals(searchDateType)) {    // 주간
                if (week > 1) {
                    startDateFormat = TimeLocaleUtil.getLocaleDateByMediumFormat(searchStartDate.substring(0, 6), lang, country);
                } else {
                    startDateFormat = TimeLocaleUtil.getLocaleDateByMediumFormat(searchEndDate.substring(0, 6), lang, country);
                }
                startDateFormat = startDateFormat + " " + week + " " + weekMsg;
            } else if ((DateType.MONTHLY.getCode()).equals(searchDateType)) {   // 월간
                startDateFormat = TimeLocaleUtil.getLocaleDateByMediumFormat(searchStartDate.substring(0, 6), lang, country);
            }

            sbFileName.append(ebsPrefix).append(TimeUtil.getCurrentTimeMilli());

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -1) + "235959"; // 10일 이전 일자

                    for (File file : files) {
                        filename = file.getName();

                        if (filename.startsWith(ebsPrefix)
                                && (filename.endsWith("xls") || filename.endsWith("zip"))
                                && (((filename.indexOf("(") != -1) ? filename.indexOf("(") : (filename.length() - 4)) == (ebsPrefix
                                        .length() + deleteDate.length()))
                                && filename.substring(ebsPrefix.length(),
                                        ((filename.indexOf("(") != -1) ? filename.indexOf("(") : (filename.length() - 4)))
                                        .compareTo(deleteDate) <= 0) {
                            file.delete();
                        }
                        filename = null;
                    }
                }
            } else {
                // directory 가 없으면 생성
                downDir.mkdir();
            }

            // create excel file
            EnergyBalanceMonitoringReportExcel wExcel = new EnergyBalanceMonitoringReportExcel();
            wExcel.setFilePath(filePath);
            wExcel.setHeader(headerList);
            wExcel.setTitle(title);
            wExcel.setSearchStartDate(startDateFormat);
            wExcel.setSearchEndDate(endDateFormat);
            wExcel.setSearchDateType(searchDateType);

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.setFileName(sbSplFileName.toString());
                wExcel.setData(result);
                wExcel.writeReportExcel();
                fileNameList.add(sbSplFileName.toString());
            } else {
                int filecnt = total / maxRows;
                int remind = total % maxRows;

                if (remind != 0) {
                    filecnt++;
                }

                List<List<Object>> sublist = null;

                for (int i = 0; i < filecnt; i++) {
                    sbSplFileName = new StringBuilder();
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(i+1).append(").xls");

                    if (i == (filecnt-1) && remind != 0) { // last && 나머지가 있을 경우
                        sublist = result.subList((i * maxRows), ((i * maxRows) + remind));
                    } else {
                        sublist = result.subList((i * maxRows), ((i * maxRows) + maxRows));
                    }
                    
                    wExcel.setFileName(sbSplFileName.toString());
                    wExcel.setData(sublist);
                    wExcel.writeReportExcel();
                    fileNameList.add(sbSplFileName.toString());
                    sublist = null;
                }
            }

            // create zip file
            StringBuilder sbZipFile = new StringBuilder();
            sbZipFile.append(sbFileName).append(".zip");

            ZipUtils zutils = new ZipUtils();
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

            mav.addObject("fileName", sbFileName.toString());
            mav.addObject("zipFileName", sbZipFile.toString());
            mav.addObject("fileNames", fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
}