package com.aimir.bo.mvm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateTabOther;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultDate;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MbusSlaveIOModuleManager;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.MvmMeterValueManager;
import com.aimir.service.mvm.NetStationMonitoringManager;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplierMgmtManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.SortUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;


@Controller
public class NetStationMonitoringController {
	private static Log logger = LogFactory.getLog(NetStationMonitoringController.class);

    @Autowired
    SearchMeteringDataManager searchMeteringDataManager;
    
    @Autowired
    NetStationMonitoringManager netStationMonitoringManager;
    
    
    @Autowired
    MvmMeterValueManager mvmMeterValueManager;

    @Autowired
    SupplyTypeManager supplyTypeManager;

    @Autowired
    SupplierMgmtManager supplierMgmtManager;

    @Autowired
    OperatorManager operatorManager;
    
    @Autowired
    MvmDetailViewManager mvmDetailViewManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    MessageSource messageSource;

    @Autowired
    MbusSlaveIOModuleManager mbusSlaveIOModuleManater;

    /**
     * @param supplierId
     * @param contractNumber
     * @param customerName
     * @param meteringSF
     * @param searchDateType
     * @param searchStartDate
     * @param searchStartHour
     * @param searchEndDate
     * @param searchEndHour
     * @param searchWeek
     * @param locationId
     * @param permitLocationId
     * @param tariffType
     * @param mcuId
     * @param deviceType
     * @param mdevId
     * @param contractGroup
     * @param sicIds
     * @param mvmMiniType
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getNetStationMonitaringDataList")
    public ModelAndView getNetStationMonitaringDataList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("customerName") String customerName,
            @RequestParam("meteringSF") String meteringSF,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("searchWeek") String searchWeek,
            @RequestParam("locationId") Integer locationId,
            @RequestParam(value="permitLocationId", required=false) Integer permitLocationId,
            @RequestParam("tariffType") Integer tariffType,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("mdevId") String mdevId,
            @RequestParam("contractGroup") String contractGroup,
            @RequestParam("sicIds") String sicIds,
            @RequestParam("mvmMiniType") String mvmMiniType) {

        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("customerName", customerName);
        conditionMap.put("meteringSF", meteringSF);

        if(searchStartDate.equals("0")) {
            List<Map<String, Object>> result = null;
            Integer totalCount = null;

            mav.addObject("result", result);
            mav.addObject("totalCount", totalCount);

            return mav;
        }

        if ( StringUtil.nullToBlank(searchStartDate).isEmpty() || StringUtil.nullToBlank(searchEndDate).isEmpty()) {
            try {
                searchStartDate = TimeUtil.getCurrentDay();
                searchStartHour = "00";
                searchEndDate = TimeUtil.getCurrentDay();
                searchEndHour = "23";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if ( StringUtil.nullToBlank(searchStartHour).isEmpty() ){
                searchStartHour = "00";
        }
        if ( StringUtil.nullToBlank(searchEndHour).isEmpty()) {
            searchEndHour = "23";
        }
        
        if ( StringUtil.nullToBlank(searchDateType).isEmpty() ){
            searchDateType = DateType.HOURLY.getCode(); 
        }
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchStartHour", searchStartHour);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchEndHour", searchEndHour);
        conditionMap.put("searchWeek", searchWeek);
        conditionMap.put("locationId", locationId);

        if (permitLocationId != null) {
            conditionMap.put("permitLocationId", permitLocationId);
        }
        conditionMap.put("tariffType", tariffType);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("deviceType", deviceType);
        conditionMap.put("mdevId", mdevId);
        conditionMap.put("contractGroup", contractGroup);


        conditionMap.put("dst", 0);
        List<Map<String, Object>> result = null;
        Integer totalCount = null;
        DateType dateType = null;

        for (DateType obj : DateType.values()) {
            if (obj.getCode().equals(searchDateType)) {
                dateType = obj;
                break;
            }
        }

        switch(dateType) {
        case HOURLY:
        case DAILY:
            result = netStationMonitoringManager.getMeteringDataHourlyData(conditionMap);
            totalCount = netStationMonitoringManager.getMeteringDataHourlyDataTotalCount(conditionMap);
            break;
        case WEEKLY:
        case MONTHLY:
        case WEEKDAILY:
        case SEASONAL:
        case YEARLY:
            break;
        }

        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }

    
    /**
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/gadget/mvm/mvmMaxGadgetNM.do")
    public final ModelAndView executeMaxGadgetNM(HttpServletRequest request, HttpServletResponse response) {
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();
        Integer supplierId = supplier.getId();
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        ModelAndView mav = new ModelAndView();

        Operator operator = null;
        if(user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(user.getOperator(new Operator()).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String uriParam = request.getRequestURI();
        mav.setViewName("/gadget/mvm/mvmMaxGadgetNM");
        
        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", "EM");
        Map<String, String> locationList = searchMeteringDataManager.getLocationList();
        Map<String, String> tariffType = searchMeteringDataManager.getTariffTypeList();

        DeviceType[] types = DeviceType.values();

        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

        for (DeviceType type : types) {
            resultMap.put(type.name(), type.name());
        }

       Map<String, Object> resultMap1 = new LinkedHashMap<String, Object>();
       resultMap1.put("", "All");
       resultMap1.putAll(SortUtil.getCompareMap(resultMap));
        
        try {
            String currentDate = TimeUtil.getCurrentDay();
            String formatDate = TimeLocaleUtil.getLocaleDate(currentDate.substring(0, 8), lang, country);
            mav.addObject("currentDate", currentDate);
            mav.addObject("formatDate", formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mav.addObject("deviceType", resultMap1);
        mav.addObject("locationList", locationList);
        mav.addObject("tariffType", tariffType);
        return mav;
    }

    /**
     * SP-929
     * @param searchDateType
     * @param searchStartDate
     * @param searchEndDate
     * @param searchStartHour
     * @param searchEndHour
     * @param chartType
     * @param mvmMiniType
     * @param meterNo
     * @param channel
     * @param msgAvg
     * @param msgMax
     * @param msgMin
     * @param msgSum
     * @param viewAll
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getDetailNetStationMonitaringData.do")
    public final ModelAndView getDetailNetStationMonitaringData(@RequestParam("searchDateType") String searchDateType,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("chartType") String chartType,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("meterNo") String meterNo,
            @RequestParam("channel") String channel,
            @RequestParam("msgAvg") String msgAvg,
            @RequestParam("msgMax") String msgMax,
            @RequestParam("msgMin") String msgMin,
            @RequestParam("msgSum") String msgSum,
            @RequestParam("viewAll") String viewAll,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("chartType", chartType);
        conditionMap.put("mvmMiniType", mvmMiniType);
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);
        conditionMap.put("msgAvg", msgAvg);
        conditionMap.put("msgMax", msgMax);
        conditionMap.put("msgMin", msgMin);
        conditionMap.put("msgSum", msgSum);
        conditionMap.put("viewAll", viewAll);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        DateTabOther other = null;
        DateType dateType = null;

        try {
            if (StringUtil.nullToBlank(searchStartDate).isEmpty() && StringUtil.nullToBlank(searchEndDate).isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        for (DateTabOther obj : DateTabOther.values()) {
            if (obj.getCode().equals(searchDateType)) {
                other = obj;
                break;
            }
        }

        if (other != null) {
            switch(other) {
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = netStationMonitoringManager.getMeteringDataDetail(conditionMap);
                    break;
            }
        } else {
            for (DateType obj : DateType.values()) {
                if (obj.getCode().equals(searchDateType)) {
                    dateType = obj;
                    break;
                }
            }

            switch(dateType) {
                case HOURLY:
                    result = netStationMonitoringManager.getMeteringDataDetail(conditionMap);
                    break;
                case DAILY:
                case WEEKLY:
                case MONTHLY:
                case WEEKDAILY:
                case SEASONAL:
                    break;
            }
        }

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * SP-929
     * @param searchStartDate
     * @param searchEndDate
     * @param searchStartHour
     * @param searchEndHour
     * @param meterNo
     * @param channel
     * @param type
     * @param searchType
     * @param supplierId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/mvm/getDetailNetStationMonitaringDataChart.do")
    public final ModelAndView getDetailNetStationMonitaringDataChart(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("meterNo") String meterNo,
            @RequestParam("channel") String channel,
            @RequestParam("type") String type,
            @RequestParam("searchType") String searchType,
            @RequestParam("supplierId") String supplierId) {
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        int supId = user.getRoleData().getSupplier().getId();
        supplierId = Integer.toString(supId);

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", new Integer(supplierId));
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);
        conditionMap.put("searchType", searchType);

        Map<String, Object> result = null;
        DateTabOther other = null;
        DateType dateType = null;

        try {
            if (StringUtil.nullToBlank(searchStartDate).isEmpty() && StringUtil.nullToBlank(searchEndDate).isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        for (DateTabOther obj : DateTabOther.values()) {
            if (obj.getCode().equals(searchType)) {
                other = obj;
                break;
            }
        }

        if (other != null) {
            switch(other) {
                case RATE:
                    break;
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = netStationMonitoringManager.getMeteringDataDetailChartData(conditionMap );
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
            }
        } else {
            for (DateType obj : DateType.values()) {
                if (obj.getCode().equals(searchType)) {
                    dateType = obj;
                    break;
                }
            }

            switch(dateType) {
                case HOURLY:
                    result = netStationMonitoringManager.getMeteringDataDetailChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case DAILY:
                 case WEEKLY:
                 case MONTHLY:
                case WEEKDAILY:
                case SEASONAL:
                     break;
            }
        }

        return mav;
    }
    
    /**
     * @return
     */
    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetNM.do")
    public final ModelAndView executeMiniGadgetNM() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(((Operator)user.getOperator(new Operator())).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mav.setViewName("/gadget/mvm/mvmMiniGadgetNM");
        mav.addObject("mvmMiniType", "EM");
        return mav;
    }
    

    /**
     * SP-929
     * @param searchStartDate
     * @param searchEndDate
     * @param meterType
     * @param searchType
     * @param searchEndHour
     * @param searchStartHour
     * @param locationId
     * @param isParent
     * @param supplierId
     * @param permitLocationId
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getMbusSlaveIoCountListPerLocation")
    public ModelAndView getMbusSlaveIoCountListPerLocation(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("meterType") String meterType,
            @RequestParam("searchDateType") String searchType,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("isParent") Boolean isParent,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="permitLocationId", required=false) Integer permitLocationId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            logger.error(e, e);
        }


        conditionMap.put("meterType", meterType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("isParent", isParent);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("permitLocationId", permitLocationId);
        DateType dateType = null;
        
        for (DateType obj : DateType.values()) {
            if (obj.getCode().equals(searchType)) {
                dateType = obj;
                break;
            }
        }

        switch(dateType) {
            case HOURLY:
                if (searchStartHour.isEmpty()) {
                    searchStartHour = "00";
                }
                if (searchEndHour.isEmpty()) {
                    searchEndHour = "23";
                }
                searchStartDate = searchStartDate + searchStartHour + "0000";
                searchEndDate = searchEndDate + searchEndHour + "5959";
                conditionMap.put("searchStartDate", searchStartDate);
                conditionMap.put("searchEndDate", searchEndDate);
                mav.addObject("result", ReflectionUtils.getDefineListToMapList(netStationMonitoringManager.getMbusSlaveIoModuleCountListPerLocation(conditionMap)));
                break;
            case DAILY:
                conditionMap.put("searchStartDate", searchStartDate + "000000");
                conditionMap.put("searchEndDate", searchEndDate + DefaultDate.LAST_HHMMSS.getValue());
                mav.addObject("result", ReflectionUtils.getDefineListToMapList(netStationMonitoringManager.getMbusSlaveIoModuleCountListPerLocation(conditionMap)));
                break;
        }
        return mav;
    }
    
    /**
     * SP-929
     * @param searchStartDate
     * @param searchEndDate
     * @param searchType
     * @param searchEndHour
     * @param searchStartHour
     * @param meterType
     * @param searchDateType
     * @param permitLocationId
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getMeteringNMSuccessRateListWithChildren")
    public ModelAndView getMeteringNMSuccessRateListWithChildren(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchType,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("meterType") String meterType,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("permitLocationId") Integer permitLocationId,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("permitLocationId", permitLocationId);
        DateType dateType = null;
        for (DateType obj : DateType.values()) {
            if (obj.getCode().equals(searchType)) {
                dateType = obj;
                break;
            }
        }
    
        switch(dateType) {
        case HOURLY:
            if (searchStartHour.isEmpty()) {
                searchStartHour = "00";
            }
            if (searchEndHour.isEmpty()) {
                searchEndHour = "23";
            }
            searchStartDate = searchStartDate + searchStartHour + "0000";
            searchEndDate = searchEndDate + searchEndHour + "5959";
            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
             break;
        case DAILY:
            conditionMap.put("searchStartDate", searchStartDate + "000000");
            conditionMap.put("searchEndDate", searchEndDate + DefaultDate.LAST_HHMMSS.getValue());
            break;
        }
        
        List<MeteringFailureData> resultList = netStationMonitoringManager.getMeteringSuccessRateListWithChildren(conditionMap);
        int listlength = resultList.size();
        mav.addObject("result", ReflectionUtils.getDefineListToMapList(resultList.subList(0, listlength-1)));
        mav.addObject("total",ReflectionUtils.getDefineListToMapList(resultList.subList(listlength-1, listlength)));
        return mav;
    }
}