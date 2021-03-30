/**
 * PrepaymentMgmtCustomerController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.prepaymentMgmt;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
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

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Contract;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.ComparisonChartManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtCustomerManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.ChargeAndBalanceHistoryMakeExcel;
import com.aimir.util.PrepaymentBalanceHistoryMakeExcel;
import com.aimir.util.PrepaymentBalanceMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * PrepaymentMgmtCustomerController.java Description
 *
 *
 * Date          Version    Author   Description
 * 2011. 8. 9.   v1.0       eunmiae  선불관리 고객 View Controller
 *
 */
@Controller
public class PrepaymentMgmtCustomerController {
	private static Log logger = LogFactory.getLog(PrepaymentMgmtCustomerController.class);
	
    @Autowired
    OperatorManager operatorManager;

    @Autowired
    OperatorContractManager operatorContractManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    PrepaymentMgmtCustomerManager prepaymentMgmtCustomerManager;
    
    @Autowired
    PrepaymentMgmtOperatorManager prepaymentMgmtOperatorManager;

    @Autowired
    ComparisonChartManager comparisonChartManager;

    @Autowired
    SupplierManager supplierManager;

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtEmCustomerMini")
    public ModelAndView loadPrepaymentMgmtEmCustomerMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMini");
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.EnergyMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtEmCustomerMax")
    public ModelAndView loadPrepaymentMgmtEmCustomerMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMax");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.EnergyMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtGmCustomerMini")
    public ModelAndView loadPrepaymentMgmtGmCustomerMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMini");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.GasMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtGmCustomerMax")
    public ModelAndView loadPrepaymentMgmtGmCustomerMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMax");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.GasMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtWmCustomerMini")
    public ModelAndView loadPrepaymentMgmtWmCustomerMini() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMini");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.WaterMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentMgmtWmCustomerMax")
    public ModelAndView loadPrepaymentMgmtWmCustomerMax() {
    	// 에너지원별 가젯 한개의 JSP에서 통합 관리
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentMgmtCustomerMax");

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int operatorId = (int) user.getAccountId();
    	String serviceType = MeterType.WaterMeter.getServiceType();

        mav.addObject("operatorId", operatorId);
        mav.addObject("serviceType", serviceType);

        return mav;
    }

    /**
     * method name : getContract
     * method Desc : 고객 선불관리 화면의 계약번호 combo data 조회
     *
     * @param operatorId
     * @param serviceType
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/getContract")
    public ModelAndView getContract(
            @RequestParam("operatorId") Integer operatorId,
            @RequestParam("serviceType") String serviceType) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("serviceType", serviceType);

        List<OperatorContract> operatorContracts = operatorContractManager.getPrepaymentOperatorContractByOperator(conditionMap);
        List<Map<String, Object>> contracts = new ArrayList<Map<String,Object>>();
        Map<String, Object> contract;
        boolean isNotService = false;

        // 계약정보 콤보 박스 생성을 위한 정보 설정
        for (OperatorContract operatorContract : operatorContracts) {

            if ( serviceType.equals(operatorContract.getContract().getServiceTypeCode().getCode())
                    && 1 == operatorContract.getContractStatus() ) {

                contract = new HashMap<String, Object>();

                contract.put("id", operatorContract.getContract().getContractNumber());
                //contract.put("name", operatorContract.getContract().getContractNumber() + " (" + operatorContract.getContract().getCustomer().getAddress2() + ")");
                // 계약정보 선택 콤보 박스 name : 명칭으로 변경
                contract.put("name", operatorContract.getFriendlyName());

                contracts.add(contract);
            }
        }

        if(contracts.size() == 0) {
        	isNotService = true;
        }

        mav.addObject("isNotService",isNotService);
        mav.addObject("contracts", contracts);

        return mav;
    }

    /**
     * method name : getAllHours
     * method Desc : 통보설정시간 combo data 조회
     *
     * @param contractNumber
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/getAllHours")
    public ModelAndView getAllHours(@RequestParam("contractNumber") String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");
        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = (contract != null) ? contract.getSupplier().getId() : -1;

        // 통보설정시간 Combo 데이터 조회
        List<String> allHours = prepaymentMgmtCustomerManager.getLocaleFormatAllHours(supplierId);
        List<Map<String, Object>> hours = new ArrayList<Map<String,Object>>();
        Map<String, Object> map;
        Integer count = 0;

        // 통보설정시간 combobox 생성을 위한 정보 설정
        for (String hour : allHours) {
            map = new HashMap<String, Object>();
            map.put("id", count.toString());
            map.put("name", hour);
            hours.add(map);
            count++;
        }
        mav.addObject("hours", hours);

        return mav;
    }

    /**
     * method name : getChargeInfo
     * method Desc : 고객 선불관리 화면의 잔액정보를 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeInfo")
    public ModelAndView getChargeInfo(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("serviceType") String serviceType) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("serviceType", serviceType);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = (contract != null) ? contract.getSupplier().getId() : -1;
        conditionMap.put("supplierId", supplierId);

        Map<String, Object> result = prepaymentMgmtCustomerManager.getChargeInfo(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getBalanceNotifySetting
     * method Desc : 고객 선불관리 화면의 잔액통보 설정 정보를 조회한다.
     *
     * @param contractNumber
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getBalanceNotifySetting")
    public ModelAndView getBalanceNotifySetting(@RequestParam("contractNumber") String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = (contract != null) ? contract.getSupplier().getId() : -1;
        conditionMap.put("supplierId", supplierId);

        Map<String, Object> result = prepaymentMgmtCustomerManager.getBalanceNotifySetting(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getPrepaymentTariff
     * method Desc : 고객 선불관리 화면의 요금단가를 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getPrepaymentTariff")
    public ModelAndView getPrepaymentTariff(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("serviceType") String serviceType) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("serviceType", StringUtil.nullToBlank(serviceType));

        Contract contract = contractManager.getContractByContractNumber(contractNumber);

        if (StringUtil.nullToBlank(contractNumber).isEmpty() || contract == null) {
            mav.addObject("result", result);
            return mav;
        }
        Integer supplierId = contract.getSupplier().getId();
        conditionMap.put("supplierId", supplierId);

        result = prepaymentMgmtCustomerManager.getPrepaymentTariff(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : updateBalanceNotifySetting
     * method Desc : 고객 선불관리 화면의 잔액통보 설정 정보를 저장한다.
     *
     * @param contractNumber
     * @param operatorId
     * @param serviceType
     * @param period
     * @param interval
     * @param hour
     * @param threshold
     * @param mon
     * @param tue
     * @param wed
     * @param thu
     * @param fri
     * @param sat
     * @param sun
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/updateBalanceNotifySetting")
    public ModelAndView updateBalanceNotifySetting(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("operatorId") Integer operatorId,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("period") Integer period,
            @RequestParam("interval") Integer interval,
            @RequestParam(value="hour", required=false) Integer hour,
            @RequestParam("threshold") Integer threshold,
            @RequestParam("mon") Boolean mon,
            @RequestParam("tue") Boolean tue,
            @RequestParam("wed") Boolean wed,
            @RequestParam("thu") Boolean thu,
            @RequestParam("fri") Boolean fri,
            @RequestParam("sat") Boolean sat,
            @RequestParam("sun") Boolean sun) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("period", period);
        conditionMap.put("interval", interval);
        conditionMap.put("hour", hour);
        conditionMap.put("threshold", threshold);
        conditionMap.put("mon", mon);
        conditionMap.put("tue", tue);
        conditionMap.put("wed", wed);
        conditionMap.put("thu", thu);
        conditionMap.put("fri", fri);
        conditionMap.put("sat", sat);
        conditionMap.put("sun", sun);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        if (contract == null) {
            mav.addObject("status", "fail");
            return mav;
        }
        conditionMap.put("supplierId", contract.getSupplier().getId());

        prepaymentMgmtCustomerManager.updateBalanceNotifySetting(conditionMap);
        mav.addObject("status", "success");

        return mav;
    }

    /**
     * method name : getChargeSetting
     * method Desc : 고객 선불관리 맥스가젯 충전 및 통보설정 Tab 의 잔액정보를 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeSetting")
    public ModelAndView getChargeSetting(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("serviceType") String serviceType) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, Object> chargeInfo = new HashMap<String, Object>();
        Map<String, Object> notifySetting = new HashMap<String, Object>();

        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("serviceType", serviceType);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        if (contract == null) {
            result.put("chargeInfo", chargeInfo);
            result.put("notifySetting", notifySetting);
            mav.addObject("result", result);
            return mav;
        }
        conditionMap.put("supplierId", contract.getSupplier().getId());

        chargeInfo = prepaymentMgmtCustomerManager.getChargeInfo(conditionMap);
        notifySetting = prepaymentMgmtCustomerManager.getBalanceNotifySetting(conditionMap);

        result.put("chargeInfo", chargeInfo);
        result.put("notifySetting", notifySetting);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getYear
     * method Desc : 검색조건의 연도콤보데이터를 가져온다. 범위는 (현재년도 - 9) ~ 현재년도.
     *
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/getYear")
    public ModelAndView getYear() {
        ModelAndView mav = new ModelAndView("jsonView");

        String currentDate = null;
        String lastDate = null;
        String strYear = null;
        String lastYear = null;
        String currYear = null;
        String currMonth = null;
        String lastMonth = null;

        try {
            currentDate = TimeUtil.getCurrentDay();
            lastDate = CalendarUtil.getDate(currentDate, Calendar.MONTH, -11);
            strYear = CalendarUtil.getDateUsingFormat(currentDate, Calendar.YEAR, -9).substring(0, 4);
            lastYear = lastDate.substring(0, 4);
            currYear = currentDate.substring(0, 4);
            currMonth = currentDate.substring(4, 6);
            lastMonth = lastDate.substring(4, 6);
        } catch(Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> fromYearCombo = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> toYearCombo = new ArrayList<Map<String,Object>>();
        Map<String, Object> map;
        int intYear = new Integer(strYear).intValue();

        // 조회조건 연도 combo 설정
        for (int i = 0 ; i < 10 ; i++) {
            map = new HashMap<String, Object>();
            map.put("id", intYear);
            map.put("name", intYear);

            fromYearCombo.add(map);
            toYearCombo.add(map);

            intYear++;
        }

        mav.addObject("fromYearCombo", fromYearCombo);
        mav.addObject("toYearCombo", toYearCombo);
        mav.addObject("fstYear", strYear);
        mav.addObject("lstYear", currYear);
        mav.addObject("fromYear", lastYear);
        mav.addObject("toYear", currYear);
        mav.addObject("fromMonth", new Integer(lastMonth));
        mav.addObject("toMonth", new Integer(currMonth));

        return mav;
    }

    /**
     * method name : getMonth
     * method Desc : 검색조건의 월콤보데이터를 가져온다. 현재년도일 경우 현재월까지만 가져온다.
     *
     * @param year
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/getMonth")
    public ModelAndView getMonth(@RequestParam("year") String year) {
        ModelAndView mav = new ModelAndView("jsonView");
        String maxMonth = null;

        try {
            String currentDate = TimeUtil.getCurrentDay();

            if (currentDate.substring(0, 4).equals(year)) {
                maxMonth = currentDate.substring(4, 6);
            } else {
                maxMonth = "12";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> monthCombo = new ArrayList<Map<String,Object>>();
        Map<String, Object> map;
        int intMaxMonth = new Integer(maxMonth).intValue();

        // 조회조건 연도 combo 설정
        for (int i = 1 ; i <= intMaxMonth ; i++) {
            map = new HashMap<String, Object>();
            map.put("id", StringUtil.frontAppendNStr('0', new Integer(i).toString(), 2));
            map.put("name", new Integer(i).toString());

            monthCombo.add(map);
        }

        mav.addObject("monthCombo", monthCombo);
        mav.addObject("lstMonth", maxMonth);

        return mav;
    }

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 맥스가젯의 충전 이력 리스트를 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @param searchStartMonth
     * @param searchEndMonth
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeHistory")
    public ModelAndView getChargeHistory(
    		@RequestParam("contractNumber") String contractNumber,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("searchStartMonth") String searchStartMonth,
            @RequestParam("searchEndMonth") String searchEndMonth,
            @RequestParam(value = "allFlag", defaultValue = "false") boolean allFlag) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("searchStartMonth", searchStartMonth);
        conditionMap.put("searchEndMonth", searchEndMonth);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = contract.getSupplier().getId();
        Integer contractId = contract.getId();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);
       	conditionMap.put("allFlag", allFlag);

        List<Map<String, Object>> result = prepaymentMgmtCustomerManager.getChargeHistory(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", prepaymentMgmtCustomerManager.getChargeHistoryTotalCount(conditionMap));

        return mav;
    }
    

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 맥스가젯의 충전,일정산,월정산 이력 리스트를 한번에 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @param searchStartMonth
     * @param searchEndMonth
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeAndBalanceHistory")
    public ModelAndView getChargeAndBalanceHistory(
    		@RequestParam("contractNumber") String contractNumber,
    		@RequestParam("SPN") String SPN,
            @RequestParam("searchType") String searchType,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("mdsId") String mdsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("SPN", SPN);
        conditionMap.put("searchType", searchType);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = contract.getSupplier().getId();
        Integer contractId = contract.getId();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("mdsId", mdsId);

        	
        List<Map<String, Object>> result = prepaymentMgmtCustomerManager.getChargeAndBalanceHistory(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", prepaymentMgmtCustomerManager.getChargeAndBalanceHistoryTotalCount(conditionMap));


        return mav;
    }

    /**
     * method name : getChargeHistoryDetailChartData
     * method Desc : 고객 선불관리 맥스가젯의 충전 이력의 상세 차트 데이터를 조회한다.
     *
     * @param contractId
     * @param serviceType
     * @param minDate
     * @param maxDate
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeHistoryDetailChartData")
    public ModelAndView getChargeHistoryDetailChartData(@RequestParam("contractId") Integer contractId,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("minDate") String minDate,
            @RequestParam("maxDate") String maxDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractId", contractId);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("startDate", minDate);
        conditionMap.put("endDate", maxDate);

        Contract contract = contractManager.getContract(contractId);
        Integer supplierId = contract.getSupplier().getId();
        conditionMap.put("supplierId", supplierId);

        List<Map<String, Object>> result = prepaymentMgmtCustomerManager.getChargeHistoryDetailChartData(conditionMap);
        mav.addObject("chartDatas", result);

        return mav;
    }

    /**
     * method name : changeEmergencyCreditMode
     * method Desc : 고객 선불관리 미니가젯에서 Credit Type 을 Emergency Credit Mode 로 전환한다.
     *
     * @param contractNumber
     * @param operatorId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/changeEmergencyCreditMode")
    public ModelAndView changeEmergencyCreditMode(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("operatorId") Integer operatorId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("operatorId", operatorId);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        if (contract == null) {
            mav.addObject("status", "fail");
            return mav;
        }
        conditionMap.put("supplierId", contract.getSupplier().getId());

        prepaymentMgmtCustomerManager.changeEmergencyCreditMode(conditionMap);
        mav.addObject("status", "success");

        return mav;
    }

    /**
     * method name : prepaymentBalanceHistoryExcelDownloadPopup
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/prepaymentBalanceExcelDownloadPopup")
    public ModelAndView prepaymentBalanceHistoryExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentBalanceExcelDownloadPopup");
        return mav;
    }

    /**
     * method name : prepaymentBalanceExcelMake
     */
    @RequestMapping(value = "gadget/prepaymentMgmt/prepaymentBalanceExcelMake")
    public ModelAndView prepamentBalanceExcelMake(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("filePath") String filePath) {
    	
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();


		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
		
		try {
			Integer supplierId = Integer.parseInt(condition[6]);
			
	        conditionMap.put("contractNumber", StringUtil.nullToBlank(condition[0]));
	        conditionMap.put("customerName", StringUtil.nullToBlank(condition[1]));
	        conditionMap.put("statusCode", StringUtil.nullToBlank(condition[2]));
	        conditionMap.put("mdsId", condition[3]);
	        conditionMap.put("locationId", (condition[4] == null || condition[4].equals("")) ? null : condition[4]);
	        conditionMap.put("serviceTypeCode", StringUtil.nullToBlank(condition[5]));
	        conditionMap.put("amountStatus", StringUtil.nullToBlank(condition[7]));
	        conditionMap.put("searchLastChargeDate", StringUtil.nullToBlank(condition[8]));
	        conditionMap.put("lastChargeStartDate", StringUtil.nullToBlank(condition[9]));
	        conditionMap.put("lastChargeEndDate", StringUtil.nullToBlank(condition[10]));
	        conditionMap.put("page", 1);
	        conditionMap.put("limit", 10000000);
	        conditionMap.put("supplierId", supplierId);
	        conditionMap.put("gs1", condition[12]);

	        List<Map<String, Object>> result = prepaymentMgmtOperatorManager.getPrepaymentContractList(conditionMap);
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav; 
			}
			
	        /**
	         *  Excel Title 생성
	         */
	        msgMap.put("contractNo", fmtMessage[0]);
	        msgMap.put("accountNo", fmtMessage[1]);
	        msgMap.put("customername", fmtMessage[2]);
	        msgMap.put("celluarphone", fmtMessage[3]);
	        msgMap.put("lastchargedate", fmtMessage[4]);
	        msgMap.put("currentbalance", fmtMessage[5]);
	        msgMap.put("meterid", fmtMessage[6]);
	        msgMap.put("stsnumber", fmtMessage[7]);
	        msgMap.put("supplyType", fmtMessage[8]);
	        msgMap.put("tariffType", fmtMessage[9]);
	        msgMap.put("meterstatus", fmtMessage[10]);
	        msgMap.put("lastreaddate", fmtMessage[11]);
	        msgMap.put("validperiod", fmtMessage[12]);
	        msgMap.put("address", fmtMessage[13]);
	        msgMap.put("title", fmtMessage[14]);
	        msgMap.put("gs1", fmtMessage[15]);
	        
			sbFileName.append(fmtMessage[14]+"_");
			sbFileName.append(TimeUtil.getCurrentTimeMilli());
			Supplier supplier = supplierManager.getSupplier(supplierId);

			/**
			 * 파일 삭제
			 */
			File downDir = new File(filePath);
			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),	Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {
						filename = file.getName();
						isDel = false;

						// 파일길이 : 30이상, 확장자 : xls|zip
						if (filename.length() > 30 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(fmtMessage[9]+"_") && filename.substring(17, 25).compareTo(deleteDate) < 0) {
								isDel = true;
							}

							if (isDel) {
								file.delete();
							}
						}
						filename = null;
					}
				}
			} else {
				// directory 가 없으면 생성
				downDir.mkdir();
			}

			/**
			 * 파일 생성
			 */
			PrepaymentBalanceMakeExcel wExcel = new PrepaymentBalanceMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = result.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, isLast, filePath,	sbSplFileName.toString(), supplier);
						fileNameList.add(sbSplFileName.toString());
						list = null;
						splCnt = cnt;
						cnt = 0;
						idx = (i + 1);
					}
					cnt++;
				}
			}

			// create zip file
			StringBuilder sbZipFile = new StringBuilder();
			sbZipFile.append(sbFileName).append(".zip");

			ZipUtils zutils = new ZipUtils();
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

			// return object
			mav.addObject("filePath", filePath);
			mav.addObject("fileName", fileNameList.get(0));
			mav.addObject("zipFileName", sbZipFile.toString());
			mav.addObject("fileNames", fileNameList);
		} catch (ParseException pe) {
			logger.debug(pe,pe);
		} catch (Exception e) {
			logger.debug(e,e);
		}

		return mav;
    }
    
    /**
     * method name : prepaymentBalanceHistoryExcelMake
     */
    @RequestMapping(value = "gadget/prepaymentMgmt/prepaymentBalanceHistoryExcelMake")
    public ModelAndView prepaymentBalanceHistoryExcelMake(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("filePath") String filePath) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();


		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

		try {
	        Contract contract = contractManager.getContractByContractNumber(condition[0]);
	        Integer supplierId = contract.getSupplierId();
	        Integer contractId = contract.getId();
	        conditionMap.put("supplierId", supplierId);
	        conditionMap.put("contractId", contractId);
	        conditionMap.put("contractNumber", condition[0]);
	        conditionMap.put("serviceType", condition[1]);
	        conditionMap.put("searchStartMonth", condition[2]);
	        conditionMap.put("searchEndMonth", condition[3]);
	        conditionMap.put("isExcel", true);
	        conditionMap.put("limit", 0);
	        conditionMap.put("endDate", condition[3]);
	        if(condition[4] != null && !condition[4].equals("")){
	        	conditionMap.put("allFlag", Boolean.valueOf(condition[4]));
	        }else {
	        	conditionMap.put("allFlag", false);
	        }

	        // Excel Title 생성
	        msgMap.put("date", fmtMessage[0]);
	        msgMap.put("cost", fmtMessage[1]);
	        msgMap.put("consumption", fmtMessage[2]);
	        msgMap.put("amount", fmtMessage[3]);
	        msgMap.put("balanceTot", fmtMessage[4]);
	        msgMap.put("transactionNo", fmtMessage[5]);
	        msgMap.put("authorizationCode", fmtMessage[6]);
	        msgMap.put("municipalityCode", fmtMessage[7]);
	        msgMap.put("title", fmtMessage[8]);
	        msgMap.put("meterValue", fmtMessage[9]);
	        msgMap.put("activeImport", fmtMessage[10]);
	        msgMap.put("activeExport", fmtMessage[11]);

	        List<Map<String, Object>> result =  prepaymentMgmtCustomerManager.getChargeHistory(conditionMap);
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

			Supplier supplier = supplierManager.getSupplier(supplierId);

			sbFileName.append(fmtMessage[8]+"_");

			sbFileName.append(TimeUtil.getCurrentTimeMilli());

			// check download dir
			File downDir = new File(filePath);

			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),
							Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {

						filename = file.getName();
						isDel = false;

						// 파일길이 : 30이상, 확장자 : xls|zip
						if (filename.length() > 30
								&& (filename.endsWith("xls") || filename
										.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(fmtMessage[8]+"_")
									&& filename.substring(17, 25).compareTo(
											deleteDate) < 0) {
								isDel = true;
							}

							if (isDel) {
								file.delete();
							}
						}
						filename = null;
					}
				}
			} else {
				// directory 가 없으면 생성
				downDir.mkdir();
			}

			// create excel file
			PrepaymentBalanceHistoryMakeExcel wExcel = new PrepaymentBalanceHistoryMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath,
						sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum)
								.append(").xls");
						list = result.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, isLast, filePath,
								sbSplFileName.toString(), supplier);
						fileNameList.add(sbSplFileName.toString());
						list = null;
						splCnt = cnt;
						cnt = 0;
						idx = (i + 1);
					}
					cnt++;
				}
			}

			// create zip file
			StringBuilder sbZipFile = new StringBuilder();
			sbZipFile.append(sbFileName).append(".zip");

			ZipUtils zutils = new ZipUtils();
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

			// return object
			mav.addObject("filePath", filePath);
			mav.addObject("fileName", fileNameList.get(0));
			mav.addObject("zipFileName", sbZipFile.toString());
			mav.addObject("fileNames", fileNameList);
		} catch (ParseException pe) {
			logger.debug(pe,pe);
		} catch (Exception e) {
			logger.debug(e,e);
		}
		
		return mav;
	}

    
    @RequestMapping(value = "gadget/prepaymentMgmt/chargeAndBalanceHistoryExcelMake")
    public ModelAndView chargeAndBalanceHistoryExcelMake(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("filePath") String filePath) {
    	
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();


		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
		
		try {
			Integer supplierId = Integer.parseInt(condition[4]);
			
			Contract contract = contractManager.getContractByContractNumber(condition[0]);
	        Integer contractId = contract.getId();
		        
	        conditionMap.put("contractNumber", StringUtil.nullToBlank(condition[0]));
	        conditionMap.put("searchType", StringUtil.nullToBlank(condition[1]));
	        conditionMap.put("searchStartDate", StringUtil.nullToBlank(condition[2]));
	        conditionMap.put("searchEndDate", StringUtil.nullToBlank(condition[3]));
	        conditionMap.put("mdsId", StringUtil.nullToBlank(condition[5]));
	        conditionMap.put("contractId", contractId);
	        conditionMap.put("page", 1);
	        conditionMap.put("limit", 1000000);
	        conditionMap.put("start", 0);
	        conditionMap.put("supplierId", supplierId);
	        conditionMap.put("SPN", StringUtil.nullToBlank(condition[6]));

	        
	        List<Map<String, Object>> result = prepaymentMgmtCustomerManager.getChargeAndBalanceHistory(conditionMap);
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav; 
			}
			
	        /**
	         *  Excel Title 생성
	         */
	        msgMap.put("type", fmtMessage[0]);
	        msgMap.put("contractNumber", fmtMessage[1]);
	        msgMap.put("accountNo", fmtMessage[2]);
	        msgMap.put("date", fmtMessage[3]);
	        msgMap.put("beforebalance", fmtMessage[4]);
	        msgMap.put("balance", fmtMessage[5]);
	        msgMap.put("cost", fmtMessage[6]);
	        msgMap.put("usage", fmtMessage[7]);
	        msgMap.put("chargeAmount", fmtMessage[8]);
	        msgMap.put("token", fmtMessage[9]);
	        msgMap.put("canceledDate", fmtMessage[10]);
	        msgMap.put("canceledToken", fmtMessage[11]);
	        msgMap.put("paymenttype", fmtMessage[12]);
	        msgMap.put("monthlyUsage", fmtMessage[13]);
	        msgMap.put("monthlyCost", fmtMessage[14]);
	        msgMap.put("vat", fmtMessage[15]);
	        msgMap.put("levy", fmtMessage[16]);
	        msgMap.put("subsidy", fmtMessage[17]);
	        msgMap.put("serviceCharge", fmtMessage[18]);
	        msgMap.put("description", fmtMessage[19]);
	        msgMap.put("title", fmtMessage[20]);
	        
			Supplier supplier = supplierManager.getSupplier(supplierId);
			sbFileName.append(fmtMessage[20]+"_");
			sbFileName.append(TimeUtil.getCurrentTimeMilli());
			
			
			List<Map<String, Object>> listForDebt =  prepaymentMgmtCustomerManager.getDebtBySPN(conditionMap);
			msgMap.put("amountDebt", (String) listForDebt.get(0).get("AMOUNT_DEBT"));
			msgMap.put("countDebt", listForDebt.get(0).get("COUNT_DEBT").toString());

			/**
			 * 파일 삭제
			 */
			File downDir = new File(filePath);
			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),	Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {
						filename = file.getName();
						isDel = false;

						// 파일길이 : 30이상, 확장자 : xls|zip
						if (filename.length() > 30 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(fmtMessage[20]+"_") && filename.substring(17, 25).compareTo(deleteDate) < 0) {
								isDel = true;
							}

							if (isDel) {
								file.delete();
							}
						}
						filename = null;
					}
				}
			} else {
				// directory 가 없으면 생성
				downDir.mkdir();
			}

			/**
			 * 파일 생성
			 */
			ChargeAndBalanceHistoryMakeExcel wExcel = new ChargeAndBalanceHistoryMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = result.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, isLast, filePath,	sbSplFileName.toString(), supplier);
						fileNameList.add(sbSplFileName.toString());
						list = null;
						splCnt = cnt;
						cnt = 0;
						idx = (i + 1);
					}
					cnt++;
				}
			}

			// create zip file
			StringBuilder sbZipFile = new StringBuilder();
			sbZipFile.append(sbFileName).append(".zip");

			ZipUtils zutils = new ZipUtils();
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

			// return object
			mav.addObject("filePath", filePath);
			mav.addObject("fileName", fileNameList.get(0));
			mav.addObject("zipFileName", sbZipFile.toString());
			mav.addObject("fileNames", fileNameList);
		} catch (ParseException pe) {
			logger.debug(pe,pe);
		} catch (Exception e) {
			logger.debug(e,e);
		}

		return mav;
    }
    
}