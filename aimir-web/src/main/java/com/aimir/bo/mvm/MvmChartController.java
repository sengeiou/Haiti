package com.aimir.bo.mvm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.ComparisonChartManager;
import com.aimir.service.mvm.MvmChartViewManager;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmChartViewData;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeUtil;

@Controller
public class MvmChartController {

    @Autowired
    MvmChartViewManager mvmChartViewManager;

    @Autowired
    ComparisonChartManager comparisonChartManager;

    @Autowired
    SupplyTypeManager supplyTypeManager;

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MvmChartController.class);

//    @RequestMapping(value = "/gadget/mvm/mvmChartView.do", method = RequestMethod.GET)
//    public final ModelAndView executeEM(@RequestParam("contractNo") String contractNo,
//            @RequestParam("mvmMiniType") String mvmMiniType, 
//            @RequestParam("tabType") String tabType,
//            @RequestParam("stdDate") String startDate, 
//            @RequestParam("endDate") String endDate,
//            @RequestParam("comboValue") String comboValue) {
//
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("/gadget/mvm/mvmChartView");
//        mav.addObject("mvmMiniType", mvmMiniType);
//        mav.addObject("tabType", tabType);
//
//        String startMonth = changeDateToMonth(startDate);
//        String endMonth = changeDateToMonth(endDate);
//        String startYear = changDateToYear(startDate);
//        String endYear = changDateToYear(endDate);
//
//        mav.addObject("comboValue", comboValue);
//        mav.addObject("startDate", startDate);
//        mav.addObject("endDate", endDate);
//        mav.addObject("startMonth", startMonth);
//        mav.addObject("endMonth", endMonth);
//        mav.addObject("startYear", startYear);
//        mav.addObject("endYear", endYear);
//
//        List<CustomerInfo> customerInfo = mvmChartViewManager.getCustomerInfo(contractNo);
//        mav.addObject("channelList", mvmChartViewManager.getChannelInfo(mvmMiniType));
//        mav.addObject("customerInfo", customerInfo);
//
//        return mav;
//    }

    @RequestMapping(value = "/gadget/mvm/mvmChartView.do")
    public final ModelAndView executeEM(@RequestParam("contractNos") String contractNos,
            @RequestParam("meterList") String meterList,
            @RequestParam("meterNos") String[] meterNos,
            @RequestParam("mvmMiniType") String mvmMiniType, 
            @RequestParam("tabType") String tabType,
            @RequestParam("stdDate") String startDate, 
            @RequestParam("endDate") String endDate,
            @RequestParam("comboValue") String comboValue) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/mvmChartView");
        mav.addObject("mvmMiniType", mvmMiniType);
        mav.addObject("tabType", tabType);

        String startMonth = changeDateToMonth(startDate);
        String endMonth = changeDateToMonth(endDate);
        String startYear = changDateToYear(startDate);
        String endYear = changDateToYear(endDate);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("comboValue", comboValue);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("startMonth", startMonth);
        mav.addObject("endMonth", endMonth);
        mav.addObject("startYear", startYear);
        mav.addObject("endYear", endYear);
        mav.addObject("supplierId", Integer.toString(supplierId));

        List<CustomerInfo> customerInfo = mvmChartViewManager.getCustomerInfo(contractNos, meterList);
        mav.addObject("channelList", mvmChartViewManager.getChannelInfo(mvmMiniType, meterNos));
        mav.addObject("customerInfo", customerInfo);

        String currentDate = null;
        try {
            currentDate = TimeUtil.getCurrentDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mav.addObject("currentDate", currentDate);
        return mav;
    }

    @RequestMapping(value = "/gadget/mvm/getMvmChartMeteringData.do")
    public final ModelAndView getMvmChartMeteringData(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("contractNumbers") String contractNumbers,
            @RequestParam("channel") String channel,
            @RequestParam("type") String type,
            @RequestParam("meterNos") String meterNos,
            @RequestParam("supplierId") String supplierId) {
        List<MvmChartViewData> result = null;
        String[] condition = {searchStartDate, searchEndDate, "", "", contractNumbers, channel, supplierId, meterNos};

        ModelAndView mav = new ModelAndView("jsonView");

        if (DateType.HOURLY.getCode().equals(searchDateType)) {                 // TabName : Hourly
            result = mvmChartViewManager.getSearchDataHour(condition, type);
        } else if (DateType.PERIOD.getCode().equals(searchDateType)) {          // TabName : Daily
            result = mvmChartViewManager.getSearchDataDay(condition, type);
        } else if (DateType.WEEKLY.getCode().equals(searchDateType)) {          // TabName : Daily(Week)
            result = mvmChartViewManager.getSearchDataDayWeek(condition, type);
        } else if (DateType.MONTHLY.getCode().equals(searchDateType)) {         // TabName : Weekly
            result = mvmChartViewManager.getSearchDataWeek(condition, type);
        } else if (DateType.MONTHLYPERIOD.getCode().equals(searchDateType)) {   // TabName : Monthly
            result = mvmChartViewManager.getSearchDataMonth(condition, type);
        } else if (DateType.YEARLY.getCode().equals(searchDateType)) {          // TabName : Seasonal
            result = mvmChartViewManager.getSearchDataSeason(condition, type);
        }
        
        if (result != null && result.size() > 0) {
            result.remove(result.size()-1);
            result.remove(result.size()-1);
            mav.addObject("searchData", ReflectionUtils.getDefineListToMapList(result));
        }

        return mav;
    }

    @RequestMapping(value = "/gadget/mvm/getMvmChartMeteringDataOverChart.do")
    public final ModelAndView getMvmChartMeteringDataOverChart(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("contractNumbers") String contractNumbers,
            @RequestParam("channel") String channel,
            @RequestParam("type") String type,
            @RequestParam("supplierId") String supplierId) {
        Map<String, Object> result = null;

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("beginDate", searchStartDate);
        condition.put("endDate", searchEndDate);
        condition.put("contractNumbers", contractNumbers);
        condition.put("channel", channel);
        condition.put("type", type);
        condition.put("supplierId", supplierId);
        
        List<String> categoriesData = new ArrayList<String>();
        ModelAndView mav = new ModelAndView("jsonView");

//        if (DateType.HOURLY.getCode().equals(searchDateType)) {                 // TabName : Hourly
//            result = mvmChartViewManager.getSearchDataHour(condition, type);
//        } else 
        if (DateType.PERIOD.getCode().equals(searchDateType)) {          // TabName : Daily
            result = comparisonChartManager.getOverlayChartDailyData(condition);
            categoriesData = comparisonChartManager.getLocaleAllHours(supplierId);
        } else if (DateType.WEEKLY.getCode().equals(searchDateType)) {          // TabName : Daily(Week)
            result = comparisonChartManager.getOverlayChartDailyWeekData(condition);
//            categoriesData = comparisonChartManager.getLocaleAllWeekDays(supplierId);
            categoriesData = comparisonChartManager.getLocaleAllHours(supplierId);
        } else if (DateType.MONTHLY.getCode().equals(searchDateType)) {         // TabName : Weekly
            result = comparisonChartManager.getOverlayChartWeeklyData(condition);
            categoriesData = comparisonChartManager.getLocaleAllWeekDays(supplierId);
        } else if (DateType.MONTHLYPERIOD.getCode().equals(searchDateType)) {   // TabName : Monthly
            result = comparisonChartManager.getOverlayChartMonthlyData(condition);
            
            for (int i = 1 ; i < 32 ; i++) {
                categoriesData.add(Integer.toString(i));
            }
        }
//        else if (DateType.YEARLY.getCode().equals(searchDateType)) {          // TabName : Seasonal
//            result = mvmChartViewManager.getSearchDataSeason(condition, type);
//        }
        
        mav.addObject("overlayData", result);
        mav.addObject("categoriesData", categoriesData);

        return mav;
    }

    public String changeDateToMonth (String value) {
    	String month="";
    	if(value != null && value.length() > 6) {
    		month = value.substring(4, 6);
    	}
    	return month;
    }
    
    public String changeDateToHour (String value) {
    	String hour="";
    	if(value != null && value.length() > 8) {
    		hour = value.substring(6, 8);
    	}
    	return hour;
    }
    
    public String changDateToYear (String value) {
    	String year = "";
    	if(value != null && value.length() > 4) {
    		year = value.substring(0, 4);
    	}
    	return year;
    }
}
