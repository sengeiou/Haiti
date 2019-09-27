package com.aimir.bo.mvm;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommonConstantsProperty;
import com.aimir.bo.common.CommonController;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateTabOther;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MbusSlaveIOModuleManager;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.MvmMeterValueManager;
import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmDetailViewData;
import com.aimir.service.mvm.impl.MvmDetailViewManagerImpl.Week;
import com.aimir.service.mvm.impl.MvmDetailViewManagerImpl.WeeklyData;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

import net.sf.json.JSONObject;

@Controller
public class MvmDetailController {

    @Autowired
    MeterDao meterDao;

    @Autowired
    MvmDetailViewManager mvmDetailViewManager;
    
    @Autowired
    MvmMeterValueManager mvmMeterValueManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    RoleManager roleManager;

    @Autowired
    MbusSlaveIOModuleManager mbusSlaveIOModuleManager;
    
    @RequestMapping(value={"/gadget/mvm/mvmDetailView.do", "/gadget/mvm/mvmMeterValueDetailView.do"})
    public final ModelAndView executeEM(@RequestParam("meterNo") String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("tabType") String tabType,
            @RequestParam("stdDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("comboValue") String comboValue,
            @RequestParam("dayComboValue") String dayComboValue,
            @RequestParam("contractId") String contractId,
            @RequestParam("supplierId") String supplierId,
            HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String uriParam = request.getRequestURI();
        List<ChannelInfo> channelInfo = null;
        
        if(uriParam.contains("/gadget/mvm/mvmDetailView.do")) {
        	channelInfo = mvmDetailViewManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmDetailView");
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueDetailView.do")) {
        	channelInfo = mvmMeterValueManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmMeterValueDetailView");
        }

        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", mvmMiniType);
        mav.addObject("tabType", tabType);

        String startMonth = changeDateToMonth(startDate);
        String endMonth   = changeDateToMonth(endDate);
        String startYear  = changDateToYear(startDate);
        String endYear    = changDateToYear(endDate);

        Integer meterId = 0;
        if(meterNo != null && !meterNo.isEmpty()){
            Meter meter = meterDao.get(meterNo);
            meterId = meter != null ? meter.getId() : 0;
        }

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String formatStartDate = TimeLocaleUtil.getLocaleDate(startDate.substring(0, 8), lang, country);
        String formatEndDate = TimeLocaleUtil.getLocaleDate(endDate.substring(0, 8), lang, country);

        String md = supplier.getMd().getPattern();
        int decimalPos = 0;     // 소수점 자릿수

        if (md.indexOf(".") != -1) {
            decimalPos = md.length() - (md.indexOf(".") + 1);
        }
        mav.addObject("decimalPos", decimalPos);
        mav.addObject("comboValue", comboValue);
        mav.addObject("dayComboValue", dayComboValue);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("formatStartDate", formatStartDate);
        mav.addObject("formatEndDate", formatEndDate);
        mav.addObject("startMonth", startMonth);
        mav.addObject("endMonth", endMonth);
        mav.addObject("startYear", startYear);
        mav.addObject("endYear", endYear);
        mav.addObject("contractId", contractId);
        mav.addObject("meterId",meterId);

        mav.addObject("customerInfo", mvmDetailViewManager.getCustomerInfo(meterNo, supplierId));
        //mav.addObject("channelList", mvmDetailViewManager.getChannelInfo(meterNo, mvmMiniType));
        mav.addObject("channelList", channelInfo);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // 수정권한(command = true)
        return mav;
    }
    @RequestMapping(value={"/gadget/mvm/mvmDetailView3.do", "/gadget/mvm/mvmMeterValueDetailView.do"})
    public final ModelAndView executeEM3(@RequestParam("meterNo") String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("tabType") String tabType,
            @RequestParam("stdDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("comboValue") String comboValue,
            @RequestParam("dayComboValue") String dayComboValue,
            @RequestParam("contractId") String contractId,
            @RequestParam("supplierId") String supplierId,
            HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String uriParam = request.getRequestURI();
        List<ChannelInfo> channelInfo = null;
        
        if(uriParam.contains("/gadget/mvm/mvmDetailView3.do")) {
        	channelInfo = mvmDetailViewManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmDetailView3");
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueDetailView.do")) {
        	channelInfo = mvmMeterValueManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmMeterValueDetailView");
        }

        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", mvmMiniType);
        mav.addObject("tabType", tabType);

        String startMonth = changeDateToMonth(startDate);
        String endMonth   = changeDateToMonth(endDate);
        String startYear  = changDateToYear(startDate);
        String endYear    = changDateToYear(endDate);

        Integer meterId = 0;
        if(meterNo != null && !meterNo.isEmpty()){
            Meter meter = meterDao.get(meterNo);
            meterId = meter != null ? meter.getId() : 0;
        }

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String formatStartDate = TimeLocaleUtil.getLocaleDate(startDate.substring(0, 8), lang, country);
        String formatEndDate = TimeLocaleUtil.getLocaleDate(endDate.substring(0, 8), lang, country);

        String md = supplier.getMd().getPattern();
        int decimalPos = 0;     // 소수점 자릿수

        if (md.indexOf(".") != -1) {
            decimalPos = md.length() - (md.indexOf(".") + 1);
        }
        mav.addObject("decimalPos", decimalPos);
        mav.addObject("comboValue", comboValue);
        mav.addObject("dayComboValue", dayComboValue);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("formatStartDate", formatStartDate);
        mav.addObject("formatEndDate", formatEndDate);
        mav.addObject("startMonth", startMonth);
        mav.addObject("endMonth", endMonth);
        mav.addObject("startYear", startYear);
        mav.addObject("endYear", endYear);
        mav.addObject("contractId", contractId);
        mav.addObject("meterId",meterId);

        mav.addObject("customerInfo", mvmDetailViewManager.getCustomerInfo(meterNo, supplierId));
        //mav.addObject("channelList", mvmDetailViewManager.getChannelInfo(meterNo, mvmMiniType));
        mav.addObject("channelList", channelInfo);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // 수정권한(command = true)
        return mav;
    }
    
    @RequestMapping(value={"/gadget/mvm/mvmDetailView2.do", "/gadget/mvm/mvmMeterValueDetailView.do"})
    public final ModelAndView executeEM2(@RequestParam("meterNo") String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("tabType") String tabType,
            @RequestParam("stdDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("comboValue") String comboValue,
            @RequestParam("dayComboValue") String dayComboValue,
            @RequestParam("contractId") String contractId,
            @RequestParam("supplierId") String supplierId,
            HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String uriParam = request.getRequestURI();
        List<ChannelInfo> channelInfo = null;
        
        if(uriParam.contains("/gadget/mvm/mvmDetailView2.do")) {
        	channelInfo = mvmDetailViewManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmDetailView2"); 
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueDetailView.do")) {
        	channelInfo = mvmMeterValueManager.getChannelInfoAll(meterNo, mvmMiniType);
        	mav.setViewName("/gadget/mvm/mvmMeterValueDetailView");
        }

        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", mvmMiniType);
        mav.addObject("tabType", tabType);

        String startMonth = changeDateToMonth(startDate);
        String endMonth   = changeDateToMonth(endDate);
        String startYear  = changDateToYear(startDate);
        String endYear    = changDateToYear(endDate);

        Integer meterId = 0;
        if(meterNo != null && !meterNo.isEmpty()){
            Meter meter = meterDao.get(meterNo);
            meterId = meter != null ? meter.getId() : 0;
        }

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String formatStartDate = TimeLocaleUtil.getLocaleDate(startDate.substring(0, 8), lang, country);
        String formatEndDate = TimeLocaleUtil.getLocaleDate(endDate.substring(0, 8), lang, country);

        String md = supplier.getMd().getPattern();
        int decimalPos = 0;     // 소수점 자릿수

        if (md.indexOf(".") != -1) {
            decimalPos = md.length() - (md.indexOf(".") + 1);
        }
        mav.addObject("decimalPos", decimalPos);
        mav.addObject("comboValue", comboValue);
        mav.addObject("dayComboValue", dayComboValue);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("formatStartDate", formatStartDate);
        mav.addObject("formatEndDate", formatEndDate);
        mav.addObject("startMonth", startMonth);
        mav.addObject("endMonth", endMonth);
        mav.addObject("startYear", startYear);
        mav.addObject("endYear", endYear);
        mav.addObject("contractId", contractId);
        mav.addObject("meterId",meterId);

        mav.addObject("customerInfo", mvmDetailViewManager.getCustomerInfo(meterNo, supplierId));
        //mav.addObject("channelList", mvmDetailViewManager.getChannelInfo(meterNo, mvmMiniType));
        mav.addObject("channelList", channelInfo);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // 수정권한(command = true)
        return mav;
    }
    
    @RequestMapping("/gadget/mvm/contractMvmViewMax.do")
    public ModelAndView contractMvmDetailMeteringData(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mav = new ModelAndView("/gadget/mvm/contractMvmDetailMeteringDataMaxGadget");
		AimirUser user = CommonController.getAimirUser(response, request);
		Supplier supplier = user.getSupplier();
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		String mdsId = meterDao.findByCondition("supplierId", supplier.getId()).getMdsId();
		List<ChannelInfo>channelInfo = mvmDetailViewManager.getChannelInfo(mdsId, "EM");
		mav.addObject("supplierId", supplier.getId());
		mav.addObject("channelList", channelInfo);
		mav.addObject("datePattern", TimeLocaleUtil.getDateFormat(12, lang, country));
    	return mav;
    }
    
    /*
     * mvm Report (Rate) 용
     *
     *
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/report/mvmReportRate.do")
    public ModelAndView getReportRate(@RequestParam("supplierId") String supplierId,
            @RequestParam("meterNo")String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("startHour") String startHour,
            @RequestParam("endHour") String endHour,
            @RequestParam("periodType") String periodType,
            @RequestParam("channel") String channel) {
        ModelAndView mav = new ModelAndView("/report/reportData");
        String xmlString = "";

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", new Integer(supplierId));
//        conditionMap.put("searchStartDate", startDate);
//        conditionMap.put("searchEndDate", endDate);
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        List<Map<String,Object>> chartMap = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> gridMap = new ArrayList<Map<String,Object>>();

        try {
            Element element = new Element("mvmReport");

            CustomerInfo ci = mvmDetailViewManager.getCustomerInfo(meterNo, supplierId);

            Element customerInfo = genCustomerInfo(ci);
            element.addContent(customerInfo);

                String rate1 = CommonConstantsProperty.getProperty("customer.usage.rate1");
                String rate2 = CommonConstantsProperty.getProperty("customer.usage.rate2");
                String rate3 = CommonConstantsProperty.getProperty("customer.usage.rate3");
                conditionMap.put("rate1", rate1);
                conditionMap.put("rate2", rate2);
                conditionMap.put("rate3", rate3);
                conditionMap.put("rateChannel", 1);

                try {
                    if (StringUtil.nullToBlank(startDate).isEmpty() && StringUtil.nullToBlank(endDate).isEmpty()) {
                        startDate = TimeUtil.getCurrentDay();
                        endDate = TimeUtil.getCurrentDay();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                conditionMap.put("searchStartDate", startDate);
                conditionMap.put("searchEndDate", endDate);

                Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailRatelyChartData(conditionMap);

                conditionMap.put("searchDateType", DateTabOther.INTERVAL.getCode());
                conditionMap.put("msgAvg", "AVG");
                conditionMap.put("msgMax", "MAX");
                conditionMap.put("msgMin", "MIN");
                conditionMap.put("msgSum", "SUM");
                conditionMap.put("viewAll", "NO");
                conditionMap.put("chartType", "line");

                gridMap = mvmDetailViewManager.getMeteringDataDetailRatelyData(conditionMap);
                chartMap = (List<Map<String, Object>>) result.get("searchData");

                Element chart = genReportChartMapInfoRate(chartMap, gridMap, 3, supplierId);
                element.addContent(chart);
                xmlString = getXmlString(element);

            } catch (Exception e) {
                e.printStackTrace();
            }
            mav.addObject("data",xmlString);
            return mav;

    }

    /*
     * Rate를 제외한 나머지 mvm Report 생성 logic.
     *
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/report/mvmReport.do")
    public ModelAndView getReport(@RequestParam("supplierId") String supplierId,
            @RequestParam("meterNo")String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("startHour") String startHour,
            @RequestParam("endHour") String endHour,
            @RequestParam("periodType") String periodType,
            @RequestParam("channel") String channel) {

        ModelAndView mav = new ModelAndView("/report/reportData");
        String xmlString = "";

        String[] condition = { startDate, endDate, "", "", meterNo, channel, supplierId };

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", new Integer(supplierId));
        conditionMap.put("searchStartDate", startDate);
        conditionMap.put("searchEndDate", endDate);
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        List<ChannelInfo> channelInfo = mvmDetailViewManager.getChannelInfo(meterNo, mvmMiniType);
        List<Map<String,Object>> chartMap = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> gridMap = new ArrayList<Map<String,Object>>();

        try {
            Element element = new Element("mvmReport");
            CustomerInfo ci = mvmDetailViewManager.getCustomerInfo(meterNo, supplierId);

            Element customerInfo = genCustomerInfo(ci);
            element.addContent(customerInfo);
            int dateCode = 0;

            if (DateTabOther.INTERVAL.getCode().equals(periodType)) {
                conditionMap.put("searchDateType", DateTabOther.INTERVAL.getCode());
                conditionMap.put("msgAvg", "AVG");
                conditionMap.put("msgMax", "MAX");
                conditionMap.put("msgMin", "MIN");
                conditionMap.put("msgSum", "SUM");
                conditionMap.put("viewAll", "NO");
                conditionMap.put("chartType", "line");

                Map<String, Object> conditionLpMap = new HashMap<String, Object>();

                conditionLpMap.putAll(conditionMap);
//                conditionLpMap.put("searchStartDate", startDate+startHour);
//                conditionLpMap.put("searchEndDate", endDate+endHour);
                conditionLpMap.put("searchStartHour", startHour);
                conditionLpMap.put("searchEndHour", endHour);

                Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailIntervalChartData(conditionLpMap);
                List<Map<String,Object>> gridIntervalMap = new ArrayList<Map<String,Object>>();

//                conditionMap.put("searchDateType", DateTabOther.INTERVAL.getCode());
//                conditionMap.put("msgAvg", "AVG");
//                conditionMap.put("msgMax", "MAX");
//                conditionMap.put("msgMin", "MIN");
//                conditionMap.put("msgSum", "SUM");
//                conditionMap.put("viewAll", "NO");
//                conditionMap.put("chartType", "line");
//                conditionMap.put("searchStartDate", startDate);
//                conditionMap.put("searchEndDate", endDate);
                conditionMap.put("searchStartHour", startHour);
                conditionMap.put("searchEndHour", endHour);
                gridMap = mvmDetailViewManager.getMeteringDataDetailHourlyData(conditionMap, true);
                chartMap = (List<Map<String, Object>>)result.get("searchData");
                gridIntervalMap = mvmDetailViewManager.getMeteringDataDetailLpData(conditionLpMap);

                dateCode = Integer.parseInt(DateTabOther.INTERVAL.getCode());

                Element chart = genReportChartInterval(gridIntervalMap,chartMap,gridMap,channel,supplierId,dateCode,channelInfo);
                element.addContent(chart);

            } else {

                if (DateType.HOURLY.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailHourlyChartData(conditionMap);
                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgAvg", "AVG");
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    gridMap = mvmDetailViewManager.getMeteringDataDetailHourlyData(conditionMap);
                    chartMap= (List<Map<String, Object>>) result.get("searchData");
                    dateCode = Integer.parseInt(DateType.HOURLY.getCode());
                }

                else if (DateType.DAILY.getCode().equals(periodType)) {
                    Map<String, Object> result =mvmDetailViewManager.getMeteringDataDetailDailyChartData(conditionMap);

                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgAvg", "AVG");
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    gridMap = mvmDetailViewManager.getMeteringDataDetailDailyData(conditionMap);
                    chartMap = (List<Map<String, Object>>) result.get("searchData");
                    dateCode = Integer.parseInt(DateType.DAILY.getCode());
                }

                else if (DateType.WEEKLY.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailWeeklyChartData(conditionMap);

                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    chartMap = (List<Map<String, Object>>) result.get("searchData");
                    gridMap = mvmDetailViewManager.getMeteringDataDetailWeeklyData(conditionMap);
                    dateCode = Integer.parseInt(DateType.WEEKLY.getCode());
                }

                else if (DateType.MONTHLY.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailMonthlyChartData(conditionMap);

                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgAvg", "AVG");
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    gridMap = mvmDetailViewManager.getMeteringDataDetailMonthlyData(conditionMap);
                    chartMap = (List<Map<String, Object>>) result.get("searchData");
                    dateCode = Integer.parseInt(DateType.MONTHLY.getCode());
                }

                else if (DateType.WEEKDAILY.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailWeekDailyChartData(conditionMap);

                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgAvg", "AVG");
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    gridMap = mvmDetailViewManager.getMeteringDataDetailWeekDailyData(conditionMap);
                    chartMap = (List<Map<String, Object>>) result.get("searchData");
                    dateCode = Integer.parseInt(DateType.WEEKDAILY.getCode());
                }

                else if (DateType.SEASONAL.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getMeteringDataDetailSeasonalChartData(conditionMap);

                    conditionMap.put("searchDateType", periodType);
                    conditionMap.put("msgAvg", "AVG");
                    conditionMap.put("msgMax", "MAX");
                    conditionMap.put("msgMin", "MIN");
                    conditionMap.put("msgSum", "SUM");
                    conditionMap.put("viewAll", "NO");
                    conditionMap.put("chartType", "line");

                    gridMap = mvmDetailViewManager.getMeteringDataDetailSeasonalData(conditionMap);
                    chartMap = (List<Map<String, Object>>) result.get("searchData");
                    dateCode = Integer.parseInt(DateType.SEASONAL.getCode());
                }

                else if (DateType.YEARLY.getCode().equals(periodType)) {
                    Map<String, Object> result = mvmDetailViewManager.getDetailDayData4fc(condition, mvmMiniType, supplierId);

                    chartMap = (List<Map<String, Object>>) result.get("searchData");

                    dateCode = Integer.parseInt(DateType.YEARLY.getCode());
                }

                Element chart = genReportChartMapInfo(chartMap,gridMap,channel,dateCode,supplierId,channelInfo);
                element.addContent(chart);

            }

            xmlString = getXmlString(element);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("data",xmlString);
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/veeCalendar.do" , method=RequestMethod.GET)
    public final ModelAndView executeCalendar(@RequestParam("year") String year
            , @RequestParam("month") String month
            , @RequestParam("supplierId")String supplierId) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/gadget/mvm/veeCalendar");

            mav.addObject("year", year);
            mav.addObject("month", month);
            mav.addObject("supplierId", supplierId);

            return mav;
    }


    @RequestMapping(value="/gadget/mvm/getCalData.do")
    public final ModelAndView getCalData(@RequestParam("beginDate") String beginDate,
                                          @RequestParam("endDate") String endDate
                                        , @RequestParam("mdsId")String mdsId
                                        , @RequestParam("channel")String channel
                                        , @RequestParam("type")String type
                                        , @RequestParam("contractId")String contractId
                                        , @RequestParam("supplierId") String supplierId){

        String[] values = new String[8];

        values[0] = beginDate;
        values[1] = endDate;
        values[2] = "";
        values[3] = "";
        values[4] = mdsId;
        values[5] = channel;
        values[6] = contractId;
        values[7] = supplierId;

        ModelAndView mav = new ModelAndView("jsonView");
//      mav.setViewName("/gadget/mvm/veeCalendar");

        HashMap<String, Object> result = mvmDetailViewManager.getCalendarDetailMonthData(values, type);

        //System.out.println("result : " + result);

        mav.addObject("result", result);

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getCalChart.do")
    public final ModelAndView getCalChart(@RequestParam("beginDate") String beginDate,
                                          @RequestParam("endDate") String endDate
                                        , @RequestParam("mdsId")String mdsId
                                        , @RequestParam("channel")String channel
                                        , @RequestParam("type")String type
                                        , @RequestParam("contractId")String contractId){

        String[] values = new String[7];

        values[0] = beginDate;
        values[1] = endDate;
        values[2] = "";
        values[3] = "";
        values[4] = mdsId;
        values[5] = channel;
        values[6] = contractId;

        ModelAndView mav = new ModelAndView("jsonView");
//      mav.setViewName("/gadget/mvm/veeCalendar");

        HashMap<String, Object> result = mvmDetailViewManager.getCalendarDetailMonthChart(values, type);

        //System.out.println("result : " + result);

        mav.addObject("result", result);

        return mav;
    }


    @RequestMapping(value="/gadget/mvm/CalChart.do")
    public final ModelAndView showCalChart(@RequestParam("param") String param){

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/veeCalChart");
        mav.addObject("param", param);
        return mav;
    }

    @RequestMapping(value = "/gadget/mvm/getWeeklyComparisonChartData.do")
    public final ModelAndView getWeeklyComparisonChartData(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("type") String type,
            @RequestParam("meterNo") String meterNo,
            @RequestParam("channel") String channel,
            @RequestParam("supplierId") String supplierId){
        Integer nsupplierId = Integer.parseInt(supplierId);
        Supplier supplier = supplierDao.get(nsupplierId);

        ModelAndView mav = new ModelAndView("jsonView");

        // data
        String[] condition = { searchStartDate, searchEndDate, "", "", meterNo, channel, supplierId };
        WeeklyData[] weeklyDatas = mvmDetailViewManager.getDetailWeeklyUnitData(condition, type, supplierId);

        //categories(labels)
        Map<String,Object> categories = new HashMap<String,Object>();
        List<Map<String,Object>> category = new ArrayList<Map<String,Object>>();
        category.add(newMap("label", "SUN"));
        category.add(newMap("label", "MON"));
        category.add(newMap("label", "TUE"));
        category.add(newMap("label", "WED"));
        category.add(newMap("label", "THU"));
        category.add(newMap("label", "FRI"));
        category.add(newMap("label", "SAT"));
        categories.put("category",category);


        // dataset
        List<Map<String,Object>> dataset = new ArrayList<Map<String,Object>>();

        for(WeeklyData weeklyData:weeklyDatas)
        for(int i = 0; i< weeklyData.getCount() ; i++){

            Map<String,Object> dataset_attr = new HashMap<String,Object>();

            //주 데이터를 읽어온다.
            Week week = weeklyData.getWeekOfIndex(i);

            //주별로 화면에 표시 하기 위해 series를 나눈다.
            // 1주 월요일 데이터와 2주 월요일 데이터는 같은 X축에 표시된다.
            dataset_attr.put("seriesname", String.format("Week%d(%s)",(i+1),weeklyData.getChannel()));

            List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

            MvmDetailViewData[] weekList = week.getWeekList();

            for(MvmDetailViewData md : weekList){
                Map<String,Object> data_attr = new HashMap<String,Object>();


                //value
                if(md.getValue()!=null){

                    String date = md.getDate();
                    if (date != null) {
                        int yyyy = Integer.parseInt(date.substring(0, 4));
                        int mm = Integer.parseInt(date.substring(4, 6));
                        int dd = Integer.parseInt(date.substring(6, 8));
                        Calendar cal = Calendar.getInstance();
                        cal.set(yyyy, mm - 1, dd);

                        //mouse over tooltip
                        String toolText = TimeLocaleUtil.getLocaleDate(supplier, cal);
                        data_attr.put("toolText", String.format("%s{br}%s", toolText,md.getValue()));
                    }
                    //value
                    data_attr.put("value", md.getValue());
                }

                data.add(data_attr);
            }
            dataset_attr.put("data",data);
            dataset.add(dataset_attr);
        }

        //fusionchart Json데이터 설정.
        Map<String,Object> fcJson = new HashMap<String,Object>();


        Map<String,Object> chart = new HashMap<String,Object>();
        chart.put("chartLeftMargin", "5");
        chart.put("chartRightMargin", "10");
        chart.put("chartTopMargin", "10");
        chart.put("chartBottomMargin", "10");
        chart.put("showValues", "0");
        chart.put("showLabels", "1");
        chart.put("showLegend", "1");
        chart.put("useRoundEdges", "0");
        chart.put("legendPosition", "RIGHT");
        chart.put("labelDisplay", "5");
        chart.put("decimals", "3");
        chart.put("forceDecimals", "1");
        chart.put("toolTipSepChar", "{br}");
        chart.put("formatNumberScale", "0");
        chart.put("thousandSeparator", ",");
        chart.put("decimalSeparator", ".");
        chart.put("width", "100%");
        chart.put("height", "100%");
        chart.put("baseFont", "dotum");
        chart.put("baseFontSize", "12");
        chart.put("baseFontColor", "#434343");
        chart.put("showBorder", "0");
        chart.put("showShadow", "1");
        chart.put("canvasBgColor", "d7d7d7,ffffff");
        chart.put("bgColor", "ffffff");
        chart.put("maxColWidth", "40");
        chart.put("divLineColor", "aaaaaa");
        chart.put("divLineAlpha", "20");
        chart.put("legendShadow", "0");
        chart.put("showAlternateHGridColor", "0");
        chart.put("divLineIsDashed", "0");
        chart.put("divLineDashLen", "0");
        chart.put("divLineDashGap", "0");
        chart.put("legendBgColor", "ffffff");
        chart.put("legendBorderAlpha", "0");
        chart.put("lineThickness", "4");
        chart.put("anchorBorderThickness", "2");
        chart.put("labelStep", "1");

        fcJson.put("chart", chart);
        fcJson.put("categories", categories);
        fcJson.put("dataset", dataset);

        JSONObject jsonObject = JSONObject.fromObject(fcJson);

        mav.addObject("fcJson", jsonObject.toString());
        return mav;

    }

    /**
     * key, value를 입력받아 Map<String, String> Object를 만들어준다.
     * @param key
     * @param value
     * @return
     */
    private Map<String, Object> newMap(String key, Object value) {
        if(key==null || value==null)
            return null;

        Map<String, Object> map = new HashMap<String,Object>();
        map.put(key, value);
        return map;
    }

    /**
     * method name : getMvmDetailMeteringDataChart<b/>
     * method Desc :
     *
     * @param searchStartDate
     * @param searchEndDate
     * @param meterNo
     * @param channel
     * @param type
     * @param searchType
     * @param supplierId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringDataChart.do")
    public final ModelAndView getMvmDetailMeteringDataChart(
    		@RequestParam("searchStartDate") String searchStartDate,
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

        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        Map<String, Object> result = null;
        DateTabOther other = null;
        DateType dateType = null;

        if (StringUtil.nullToBlank(searchType).isEmpty()) {
            searchType = DateTabOther.RATE.name();
        }

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
                    conditionMap.put("rate1", CommonConstantsProperty.getProperty("customer.usage.rate1"));
                    conditionMap.put("rate2", CommonConstantsProperty.getProperty("customer.usage.rate2"));
                    conditionMap.put("rate3", CommonConstantsProperty.getProperty("customer.usage.rate3"));
                    conditionMap.put("rateChannel", ElectricityChannel.Usage.getChannel());

                    result = mvmDetailViewManager.getMeteringDataDetailRatelyChartData(conditionMap);
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    mav.addObject("searchTotalData", (Map<String, Object>)result.get("searchTotalData"));
                    break;
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = mvmDetailViewManager.getMeteringDataDetailIntervalChartData(conditionMap);
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
                    result = mvmDetailViewManager.getMeteringDataDetailHourlyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case DAILY:
                    result = mvmDetailViewManager.getMeteringDataDetailDailyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case WEEKLY:
                    result = mvmDetailViewManager.getMeteringDataDetailWeeklyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case MONTHLY:
                    result = mvmDetailViewManager.getMeteringDataDetailMonthlyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case WEEKDAILY:
                    result = mvmDetailViewManager.getMeteringDataDetailWeekDailyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case SEASONAL:
                    result = mvmDetailViewManager.getMeteringDataDetailSeasonalChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
            }
        }

        return mav;
    }
    
    /**
     * method name : getMvmDetailMeteringValueChart<b/>
     * method Desc :
     *
     * @param searchStartDate
     * @param searchEndDate
     * @param meterNo
     * @param channel
     * @param type
     * @param searchType
     * @param supplierId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringValueChart.do")
    public final ModelAndView getMvmDetailMeteringValueChart(@RequestParam("searchStartDate") String searchStartDate,
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

        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        Map<String, Object> result = null;
        DateTabOther other = null;
        DateType dateType = null;

        if (StringUtil.nullToBlank(searchType).isEmpty()) {
            searchType = DateTabOther.RATE.name();
        }

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
                    conditionMap.put("rate1", CommonConstantsProperty.getProperty("customer.usage.rate1"));
                    conditionMap.put("rate2", CommonConstantsProperty.getProperty("customer.usage.rate2"));
                    conditionMap.put("rate3", CommonConstantsProperty.getProperty("customer.usage.rate3"));
                    List<Integer> channelIdList = new ArrayList<Integer>();
                    channelIdList.add(ElectricityChannel.Usage.getChannel());
                    conditionMap.put("channelIdList", channelIdList);
                    conditionMap.put("rateChannel", ElectricityChannel.Usage.getChannel());

                    result = mvmMeterValueManager.getMeteringValueDetailRatelyChartData(conditionMap);
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    mav.addObject("searchTotalData", (Map<String, Object>)result.get("searchTotalData"));
                    break;
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = mvmMeterValueManager.getMeteringValueDetailIntervalChartData(conditionMap);
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
                    result = mvmMeterValueManager.getMeteringValueDetailHourlyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case DAILY:
                    result = mvmMeterValueManager.getMeteringValueDetailDailyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case WEEKLY:
                    result = mvmMeterValueManager.getMeteringValueDetailWeeklyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case MONTHLY:
                    result = mvmMeterValueManager.getMeteringValueDetailMonthlyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case WEEKDAILY:
                    result = mvmMeterValueManager.getMeteringValueDetailWeekDailyChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
                case SEASONAL:
                    result = mvmMeterValueManager.getMeteringValueDetailSeasonalChartData(conditionMap);
                    mav.addObject("searchAddData", (List<Map<String, Object>>)result.get("searchAddData"));
                    mav.addObject("searchData", (List<Map<String, Object>>)result.get("searchData"));
                    break;
            }
        }

        return mav;
    }

    /**
     * method name : getMvmDetailMeteringData<b/>
     * method Desc :
     *
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
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringData.do")
    public final ModelAndView getMvmDetailMeteringData(@RequestParam("searchDateType") String searchDateType,
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

        if (StringUtil.nullToBlank(searchDateType).isEmpty()) {
            searchDateType = DateTabOther.RATE.name();
        }

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
                case RATE:
                    conditionMap.put("rate1", CommonConstantsProperty.getProperty("customer.usage.rate1"));
                    conditionMap.put("rate2", CommonConstantsProperty.getProperty("customer.usage.rate2"));
                    conditionMap.put("rate3", CommonConstantsProperty.getProperty("customer.usage.rate3"));
                    conditionMap.put("rateChannel", ElectricityChannel.Usage.getChannel());

                    result = mvmDetailViewManager.getMeteringDataDetailRatelyData(conditionMap);
                    break;
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = mvmDetailViewManager.getMeteringDataDetailIntervalData(conditionMap);
                    break;
                default:
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
                    result = mvmDetailViewManager.getMeteringDataDetailHourlyData(conditionMap);
                    break;
                case DAILY:
                    result = mvmDetailViewManager.getMeteringDataDetailDailyData(conditionMap);
                    break;
                case WEEKLY:
                    result = mvmDetailViewManager.getMeteringDataDetailWeeklyData(conditionMap);
                    break;
                case MONTHLY:
                    result = mvmDetailViewManager.getMeteringDataDetailMonthlyData(conditionMap);
                    break;
                case WEEKDAILY:
                    result = mvmDetailViewManager.getMeteringDataDetailWeekDailyData(conditionMap);
                    break;
                case SEASONAL:
                    result = mvmDetailViewManager.getMeteringDataDetailSeasonalData(conditionMap);
                    break;
                default:
                	break;
            }
        }

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : getMvmDetailMeteringValue<b/>
     * method Desc :
     *
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
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringValue.do")
    public final ModelAndView getMvmDetailMeteringValue(@RequestParam("searchDateType") String searchDateType,
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

        if (StringUtil.nullToBlank(searchDateType).isEmpty()) {
            searchDateType = DateTabOther.RATE.name();
        }

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
                case RATE:
                    conditionMap.put("rate1", CommonConstantsProperty.getProperty("customer.usage.rate1"));
                    conditionMap.put("rate2", CommonConstantsProperty.getProperty("customer.usage.rate2"));
                    conditionMap.put("rate3", CommonConstantsProperty.getProperty("customer.usage.rate3"));
                    List<Integer> channelIdList = new ArrayList<Integer>();
                    channelIdList.add(ElectricityChannel.Usage.getChannel());
                    conditionMap.put("channelIdList", channelIdList);
                    conditionMap.put("rateChannel", ElectricityChannel.Usage.getChannel());

                    result = mvmMeterValueManager.getMeteringValueDetailRatelyData(conditionMap);
                    break;
                case INTERVAL:
                    conditionMap.put("searchStartHour", searchStartHour);
                    conditionMap.put("searchEndHour", searchEndHour);
                    result = mvmMeterValueManager.getMeteringValueDetailHourlyData(conditionMap, true);
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
                    result = mvmMeterValueManager.getMeteringValueDetailHourlyData(conditionMap);
                    break;
                case DAILY:
                    result = mvmMeterValueManager.getMeteringValueDetailDailyData(conditionMap);
                    break;
                case WEEKLY:
                    result = mvmMeterValueManager.getMeteringValueDetailWeeklyData(conditionMap);
                    break;
                case MONTHLY:
                    result = mvmMeterValueManager.getMeteringValueDetailMonthlyData(conditionMap);
                    break;
                case WEEKDAILY:
                    result = mvmMeterValueManager.getMeteringValueDetailWeekDailyData(conditionMap);
                    break;
                case SEASONAL:
                    result = mvmMeterValueManager.getMeteringValueDetailSeasonalData(conditionMap);
                    break;
            }
        }

        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getMvmDetailMeteringDataLpData<b/>
     * method Desc :
     *
     * @param supplierId
     * @param mvmMiniType
     * @param meterNo
     * @param channel
     * @param node
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringDataLpData.do")
    public final ModelAndView getMvmDetailMeteringDataLpData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("meterNo") String meterNo,
            @RequestParam("channel") String channel,
            @RequestParam("node") String node) {
        ModelAndView mav = new ModelAndView("treeJsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", node.substring(0, 8));
        conditionMap.put("searchEndDate", node.substring(0, 8));

        conditionMap.put("searchStartHour", node.substring(8, 10));
        conditionMap.put("searchEndHour", node.substring(8, 10));

        if (node.length() > 10) {
            conditionMap.put("dst", Integer.valueOf(node.substring(11)));
        }
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);

        result = mvmDetailViewManager.getMeteringDataDetailLpData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * method name : getMvmDetailMeteringValueLpData<b/>
     * method Desc :
     *
     * @param supplierId
     * @param mvmMiniType
     * @param meterNo
     * @param channel
     * @param node
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getMvmDetailMeteringValueLpData.do")
    public final ModelAndView getMvmDetailMeteringValueLpData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("meterNo") String meterNo,
            @RequestParam("channel") String channel,
            @RequestParam("node") String node) {
        ModelAndView mav = new ModelAndView("treeJsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", node.substring(0, 8));
        conditionMap.put("searchEndDate", node.substring(0, 8));

        conditionMap.put("searchStartHour", node.substring(8, 10));
        conditionMap.put("searchEndHour", node.substring(8, 10));

        if (node.length() > 10) {
            conditionMap.put("dst", Integer.valueOf(node.substring(11)));
        }
        conditionMap.put("meterNo", meterNo);
        conditionMap.put("channel", channel);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);

        result = mvmMeterValueManager.getMeteringValueDetailLpData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    @RequestMapping("/gadget/mvm/getContractDetailMeteringDataLpData.do")
    public ModelAndView getContractDetailMeteringDataLpData(
    		@RequestParam Integer supplierId,
    		@RequestParam String type,
    		@RequestParam String startGcode,
    		@RequestParam String endGcode,
    		@RequestParam String node,
    		@RequestParam String channel) {
    	ModelAndView mav = new ModelAndView("treeJsonView");
    	List<List<Map<String, Object>>> tmpList= new ArrayList<List<Map<String, Object>>>();
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	int lpDataSize = 0;
    	String searchDate = node.substring(0, 8);
    	String searchHour = node.substring(8, 10);
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("startContract", startGcode);
    	conditionMap.put("endContract", endGcode);
    	List<Map<String, Object>> list = mvmDetailViewManager.getMdsIdFromContract(conditionMap);
    	
    	conditionMap.put("searchStartDate", searchDate);
    	conditionMap.put("searchEndDate",searchDate);
    	conditionMap.put("searchStartHour", searchHour);
    	conditionMap.put("searchEndHour", searchHour);
    	conditionMap.put("channel", channel);
    	
    	Supplier supplier = supplierDao.get(supplierId);
    	DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
    	
    	type = ChangeMeterTypeName.valueOf(type).getCode();
    	conditionMap.put("meterType", type);
    	
    	for (int i = 0; i < list.size(); i++) {
    		Map<String, Object> mdsIdNode = list.get(i);
    		conditionMap.put("meterNo",mdsIdNode.get("MDS_ID"));
    		List<Map<String, Object>> lpData = mvmDetailViewManager.getMeteringDataDetailLpData(conditionMap);
    		lpDataSize = lpData.size();
    		tmpList.add(lpData);
    	}
    	
    	for (int i = 0 ; i < lpDataSize; i++) {
    		Map<String, Object>map = tmpList.get(0).get(i);
    		Set<String> mapKey = map.keySet();
    		
    		for(String key: mapKey) {
    			if(key.startsWith("channel")) {
    				map.put(key, "0.0");
    			}
    		}
    		    		
    		for (int j = 0 ; j < tmpList.size(); j++) {
    			List<Map<String, Object>>tmp = tmpList.get(j);
    			Map<String, Object> tmpMap = tmp.get(i);
    			Set<String> keySet = tmpMap.keySet();
    			
    			for (String key : keySet) {
    				Double value = 0.0d;
    				
    				if(key.startsWith("channel")) {
    					String valStr = tmpMap.get(key).toString().trim();
    					
    					if(!valStr.startsWith("-")) {
    						value = Double.parseDouble(valStr);
    					}
    					Double total = Double.parseDouble(map.get(key).toString());
    					map.put(key, df.format(total + value));
    				}
    			}
    		}
    		
    		result.add(map);
    	}
    	mav.addObject("result", result);
    	return mav;
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping("/gadget/mvm/getContractMvmDetailMeteringDataChart.do")
    public ModelAndView getContractMvmDetailMeteringDataChart(
    		@RequestParam String searchStartDate,
    		@RequestParam String searchEndDate,
    		String searchStartHour,
    		String searchEndHour,
    		@RequestParam String startGcode,
    		@RequestParam String endGcode,
    		@RequestParam String channel,
    		@RequestParam String type,
    		@RequestParam String searchType) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
    	AimirUser user = (AimirUser) instance.getUserFromSession();
    	DateType dateType = null;
    	
    	
    	for ( DateType dType : DateType.values() ) {
    		if( dType.getCode().equals(searchType) ) {
    			dateType = dType;
    			break;
    		}
    	}
    	
    	Integer supplierId = user.getSupplier().getId();
    	Supplier supplier = supplierDao.get(supplierId);
    	DecimalFormat md = DecimalUtil.getDecimalFormat(supplier.getMd());
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("startContract", startGcode);
    	conditionMap.put("endContract", endGcode);
    	List<Map<String, Object>> list = mvmDetailViewManager.getMdsIdFromContract(conditionMap);

    	conditionMap.put("channel", channel);
    	conditionMap.put("searchStartDate", searchStartDate);
    	conditionMap.put("searchEndDate", searchEndDate);
    	
    	String meterType = ChangeMeterTypeName.valueOf(type).getCode();
    	conditionMap.put("meterType", meterType);
    	String tlbType = MeterType.valueOf(meterType).getLpClassName();
    	conditionMap.put("tlbType", tlbType);
    	
    	
    	List<List<Map<String, Object>>> searchDataList = new ArrayList<List<Map<String, Object>>>();
    	
    	int searchDataSize = 0;
    	
    	for (Map<String, Object> map : list) {
    		conditionMap.put("meterNo", StringUtil.nullToBlank(map.get("MDS_ID")));
    		Map<String, Object> chartResult = new HashMap<String, Object>();
    		
    		switch(dateType) {
	            case HOURLY:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailHourlyChartData(conditionMap);
	                break;
	            case DAILY:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailDailyChartData(conditionMap);
	                break;
	            case WEEKLY:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailWeeklyChartData(conditionMap);
	                break;
	            case MONTHLY:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailMonthlyChartData(conditionMap);
	                break;
	            case WEEKDAILY:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailWeekDailyChartData(conditionMap);
	                break;
	            case SEASONAL:
	                chartResult = mvmDetailViewManager.getMeteringDataDetailSeasonalChartData(conditionMap);
	                break;
	        }
            searchDataList.add((List<Map<String, Object>>) chartResult.get("searchData"));
            searchDataSize = ((List<Map<String, Object>>) chartResult.get("searchData")).size(); 
    	}    	
    	
    	List<Map<String, Object>> resultSearchData = new ArrayList<Map<String, Object>>();
    	
    	for (int i =0 ; i < searchDataSize ; i++) {
    		Double value = 0.0d;
    		List<Map<String, Object>> mapList = searchDataList.get(0);
    		
			Map<String, Object> map = (Map<String, Object>) mapList.get(i);
    		
    		for (int j=0;  j < searchDataList.size(); j++) {
    			List<Map<String, Object>> searchData = searchDataList.get(j);
    			Map<String, Object> meteringData = searchData.get(i);
    			value += meteringData.get("value") != null ? (Double)meteringData.get("value"): 0d;
    		}
    		map.put("value", value);
    		map.put("decimalValue", md.format(StringUtil.nullToDoubleZero(value)));
			resultSearchData.add(map);
    	}
    	
    	mav.addObject("searchData", resultSearchData);
    	return mav;
    }
    
    @RequestMapping("/gadget/mvm/getContractMvmDetailMeteringData.do")
    public ModelAndView getContractMvmDetailMeteringData(
    		@RequestParam String searchStartDate,
    		@RequestParam String searchEndDate,
    		String searchStartHour,
    		String searchEndHour,
    		@RequestParam String startGcode,
    		@RequestParam String endGcode,
    		@RequestParam String channel,
    		@RequestParam String type,
    		@RequestParam String searchType) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
    	AimirUser user = (AimirUser) instance.getUserFromSession();
    	DateType dateType = null;
    	
    	
    	for ( DateType dType : DateType.values() ) {
    		if( dType.getCode().equals(searchType) ) {
    			dateType = dType;
    			break;
    		}
    	}
    	
    	String[] channelNumbers = channel.split(",");
    	List<String> channelList = new ArrayList<String>();
    	
    	for (int i=0 ; i < channelNumbers.length ; i++) {
    		String num = channelNumbers[i];
    		channelList.add("channel_" +  num);
    	}
    	
    	Integer supplierId = user.getSupplier().getId();
    	Supplier supplier = supplierDao.get(supplierId);
    	DecimalFormat md = DecimalUtil.getDecimalFormat(supplier.getMd());
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("startContract", startGcode);
    	conditionMap.put("endContract", endGcode);
    	List<Map<String, Object>> list = mvmDetailViewManager.getMdsIdFromContract(conditionMap);
    	
    	conditionMap.put("channel", channel);
    	conditionMap.put("searchStartDate", searchStartDate);
    	conditionMap.put("searchEndDate", searchEndDate);
    
    	String meterType = ChangeMeterTypeName.valueOf(type).getCode();
    	conditionMap.put("meterType", meterType);
    	String tlbType = MeterType.valueOf(meterType).getLpClassName();
    	conditionMap.put("tlbType", tlbType);
    	
    	List<List<Map<String, Object>>> searchGridDataList = new ArrayList<List<Map<String, Object>>>();
    	int searchGridDataSize = 0;
    	
    	for (Map<String, Object> map : list) {
    		conditionMap.put("meterNo", StringUtil.nullToBlank(map.get("MDS_ID")));
    		List<Map<String, Object>> gridResult = new ArrayList<Map<String, Object>>();
    		
    		switch(dateType) {
	            case HOURLY:
	                gridResult =  mvmDetailViewManager.getMeteringDataDetailHourlyData(conditionMap);
	                break;
	            case DAILY:
	                gridResult =  mvmDetailViewManager.getMeteringDataDetailDailyData(conditionMap);
	                break;
	            case WEEKLY:
	                gridResult =  mvmDetailViewManager.getMeteringDataDetailWeeklyData(conditionMap);
	                break;
	            case MONTHLY:
	                gridResult =  mvmDetailViewManager.getMeteringDataDetailMonthlyData(conditionMap);
	                break;
	            case WEEKDAILY:
	                gridResult =  mvmDetailViewManager.getMeteringDataDetailWeekDailyData(conditionMap);
	                break;
	            case SEASONAL:
	                gridResult = mvmDetailViewManager.getMeteringDataDetailSeasonalData(conditionMap);
	                break;
	        }
            searchGridDataList.add(gridResult);
            searchGridDataSize = gridResult.size() - 1; // sum & avg(max/min) 제외
    	}
    	
    	List<Map<String, Object>> resultGridData = new ArrayList<Map<String, Object>>();
    	
    	for (int i = 0; i < searchGridDataSize; i++) {
    		List<Map<String, Object>> mapList = searchGridDataList.get(0);
    		
    		Map<String, Object> channelMap = (Map<String, Object>) mapList.get(i);
    		if ( channelMap.get("meteringTime") == null) {
    			channelMap.put("meteringTime", "Sum");
    		}
    		Set<String> mapKey = channelMap.keySet();
    		
    		for( Object mapKeyVal : mapKey ) {
    			String mapKeyStr = mapKeyVal.toString();
    			
    			if( mapKeyStr.startsWith("channel") ) {
    				channelMap.put(mapKeyStr, "0.0");
    			}
    		}
    		
    		for (int j=0; j < searchGridDataList.size(); j++) {
    			List<Map<String, Object>> gridData = searchGridDataList.get(j);
    			Map<String, Object> meteringData = gridData.get(i);
    			Set<String> key = meteringData.keySet();
    			
    			for( Object keyVal : key ) {
    				String keyStr = keyVal.toString();
    				
    				if( keyStr.startsWith("channel") ) {
    					Double value = 0d;
    					String valStr = meteringData.get(keyStr).toString().trim();
    					
    					if (valStr != null && !valStr.equals("-")) {
    						value = Double.parseDouble(valStr);
    					} 
    					
    					Double totalValue = Double.parseDouble(channelMap.get(keyStr).toString());
    					totalValue += value;
    					channelMap.put(keyStr, md.format(StringUtil.nullToDoubleZero(totalValue)));
    				}
    			}
    		}
    		resultGridData.add(channelMap);
    	}            
  	
    	mav.addObject("result", resultGridData);
    	return mav;
    }
    
    private Element genCustomerInfo(CustomerInfo ci) {
        Element customerInfo = new Element("customerInfo");

        customerInfo.setAttribute("customerName", StringUtil.nullToBlank(ci.getCustomerName()));
        customerInfo.setAttribute("contractNumber", StringUtil.nullToBlank(ci.getContractNo()));
        customerInfo.setAttribute("tarrifType", StringUtil.nullToBlank(ci.getTariffType()));
        customerInfo.setAttribute("address", StringUtil.nullToBlank(ci.getAdress()));
        customerInfo.setAttribute("location", StringUtil.nullToBlank(ci.getLocation()));
        customerInfo.setAttribute("telephoneNumber", StringUtil.nullToBlank(ci.getTelephoneNo()));
        customerInfo.setAttribute("mobileNumber", StringUtil.nullToBlank(ci.getMobileNo()));
        customerInfo.setAttribute("meterType", StringUtil.nullToBlank(ci.getMeterType()));
        customerInfo.setAttribute("meterId", StringUtil.nullToBlank(ci.getMeterNo()));
        customerInfo.setAttribute("mcuId", StringUtil.nullToBlank(ci.getMcuNo()));
        customerInfo.setAttribute("meteringTime", StringUtil.nullToBlank(ci.getLastTime()));
        customerInfo.setAttribute("meteringData", StringUtil.nullToBlank(ci.getLastMeteringData()));

        return customerInfo;
    }

    /**
     * 보고서에서는 dataSource로 xml을 이용하고 있으니
     * 보고서에서 차트에 사용되는 Map을 재조립하여 Element를 반환한다.
     * @param Map :차트 데이터 Map
     * @return Element :차트 데이터 Element
     */
    private Element genReportChartMapInfo(List<Map<String,Object>> result,List<Map<String,Object>> gridMap,
                            String channel, int dateCode, String supplierId, List<ChannelInfo> channelInfo) {
        Element e =null;
        e = genReportChart(result, gridMap, channel, supplierId, dateCode, channelInfo);

        return e;
    }

    private Element genReportChart(List<Map<String,Object>> result, List<Map<String,Object>> gridMap,
                            String channel, String supplierId, int dateCode, List<ChannelInfo> channelInfo){

        Element master = new Element("Master");
        Element row1 = null;
        Element row2 = null;
        Element RowInfo = null;

        String[] channels = channel.split(",");

        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Locale locale = new Locale(lang, country);
        String title = messageSource.getMessage("aimir.report.metering", new Object[0], locale);
        String customerInfo = messageSource.getMessage("aimir.customerview", new Object[0], locale);
        String meaNo = messageSource.getMessage("aimir.buildingMgmt.contractNumber", new Object[0], locale);
        String telePhone = messageSource.getMessage("aimir.telephoneno", new Object[0], locale);
        String tariffType = messageSource.getMessage("aimir.contract.tariff.type", new Object[0], locale);
        String address = messageSource.getMessage("aimir.address", new Object[0], locale);
        String cellPhone = messageSource.getMessage("aimir.cpno", new Object[0], locale);
        String location = messageSource.getMessage("aimir.location", new Object[0], locale);
        String meterType = messageSource.getMessage("aimir.metertype", new Object[0], locale);
        String meteringTime = messageSource.getMessage("aimir.meteringtime", new Object[0], locale);
        String meterId = messageSource.getMessage("aimir.meterid", new Object[0], locale);
        String meterValue = messageSource.getMessage("aimir.meteringdata", new Object[0], locale);
        String commEquipId = messageSource.getMessage("aimir.mcuid2", new Object[0], locale);

        int index = 1;

        Element Chartinfo = new Element("Chartinfo");

        RowInfo = new Element("RowInfo");

        //interval title 구분.
        if      (dateCode==0)           RowInfo.setAttribute("title", title+" ( HOURLY ) ");
        else if (dateCode==1)           RowInfo.setAttribute("title", title+" ( DAILY ) ");
        else if (dateCode==3)           RowInfo.setAttribute("title", title+" ( WEEKLY ) ");
        else if (dateCode==4)           RowInfo.setAttribute("title", title+" ( MONTHLY ) ");
        else if (dateCode==6)           RowInfo.setAttribute("title", title+" ( WEEKLYDAILY ) ");
        else if (dateCode==7)           RowInfo.setAttribute("title", title+" ( SEASONAL ) ");
        else if (dateCode==21)          RowInfo.setAttribute("title", title+" ( INTERVAL ) ");

        RowInfo.setAttribute("customerInfo", customerInfo);
        RowInfo.setAttribute("meaNo", meaNo);
        RowInfo.setAttribute("telePhone", telePhone);
        RowInfo.setAttribute("tariffType", tariffType);
        RowInfo.setAttribute("address", address);
        RowInfo.setAttribute("cellPhone", cellPhone);
        RowInfo.setAttribute("location", location);
        RowInfo.setAttribute("meterType", meterType);
        RowInfo.setAttribute("meteringTime", meteringTime);
        RowInfo.setAttribute("meterId", meterId);
        RowInfo.setAttribute("meterValue", meterValue);
        RowInfo.setAttribute("commEquipInfo", commEquipId);

        Chartinfo.addContent(RowInfo);

        row1 = new Element("Row");
        Element element = new Element("ChartData");

        for (Map<String, Object> map : result) {
            if(index<channels.length){
                if(index==1){
                    row1.setAttribute("localeDate",String.valueOf(map.get("reportDate")));
                }
                String ch = String.valueOf(map.get("channel"));
                row1.setAttribute("channel"+ch,String.valueOf(map.get("decimalValue")));
            }else{
                index = 0;
                String ch = String.valueOf(map.get("channel"));
                row1.setAttribute("channel"+ch,String.valueOf(map.get("decimalValue")));
                element.addContent(row1);
                row1 = new Element("Row");
            }
            index++;
        }


        Element gridData = new Element("GridData");
        row1 = new Element("Row1");
        row2 = new Element("Row2");
        Element row3 = new Element("Row3");
        Element row4 = new Element("Row4");

        for (Map<String, Object> map : gridMap) {
            if("sum".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for(String key : keys) {
                    row1.setAttribute(key,map.get(key).toString());
                }
            }
            else if("avg".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    row2.setAttribute(key,map.get(key).toString());
                }
            }else{
                for(index=0;index<15;index++){
                    if(index==0){
                        row3.setAttribute("meteringTime",String.valueOf(map.get("meteringTime")));
                    }
                    row3.setAttribute("channel_"+index,String.valueOf(map.get("channel_"+index)));
                }
                element.addContent(row3);
                row3 = new Element("Row3");

            }
        }

        for(int j=0; j<channelInfo.size(); j++){
            row4.setAttribute("channelName"+j, channelInfo.get(j).getCodeName());
        }

        gridData.addContent(row1);
        gridData.addContent(row2);
        gridData.addContent(row3);
        gridData.addContent(row4);

        master.addContent(element);
        master.addContent(Chartinfo);
        master.addContent(gridData);

        return master;
    }

    private Element genReportChartInterval(List<Map<String,Object>> resultInterval, List<Map<String,Object>> result, List<Map<String,Object>> gridMap,
            String channel, String supplierId, int dateCode, List<ChannelInfo> channelInfo){

        Element master = new Element("Master");
        Element row1 = null;
        Element row2 = null;
        Element RowInfo = null;

        String[] channels = channel.split(",");

        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Locale locale = new Locale(lang, country);
        String title = messageSource.getMessage("aimir.report.metering", new Object[0], locale);
        String customerInfo = messageSource.getMessage("aimir.customerview", new Object[0], locale);
        String meaNo = messageSource.getMessage("aimir.buildingMgmt.contractNumber", new Object[0], locale);
        String telePhone = messageSource.getMessage("aimir.telephoneno", new Object[0], locale);
        String tariffType = messageSource.getMessage("aimir.contract.tariff.type", new Object[0], locale);
        String address = messageSource.getMessage("aimir.address", new Object[0], locale);
        String cellPhone = messageSource.getMessage("aimir.cpno", new Object[0], locale);
        String location = messageSource.getMessage("aimir.location", new Object[0], locale);
        String meterType = messageSource.getMessage("aimir.metertype", new Object[0], locale);
        String meteringTime = messageSource.getMessage("aimir.meteringtime", new Object[0], locale);
        String meterId = messageSource.getMessage("aimir.meterid", new Object[0], locale);
        String meterValue = messageSource.getMessage("aimir.meteringdata", new Object[0], locale);
        String commEquipId = messageSource.getMessage("aimir.mcuid2", new Object[0], locale);

        int index = 1;

        Element Chartinfo = new Element("Chartinfo");

        RowInfo = new Element("RowInfo");

        RowInfo.setAttribute("title", title+" ( INTERVAL ) ");
        RowInfo.setAttribute("customerInfo", customerInfo);
        RowInfo.setAttribute("meaNo", meaNo);
        RowInfo.setAttribute("telePhone", telePhone);
        RowInfo.setAttribute("tariffType", tariffType);
        RowInfo.setAttribute("address", address);
        RowInfo.setAttribute("cellPhone", cellPhone);
        RowInfo.setAttribute("location", location);
        RowInfo.setAttribute("meterType", meterType);
        RowInfo.setAttribute("meteringTime", meteringTime);
        RowInfo.setAttribute("meterId", meterId);
        RowInfo.setAttribute("meterValue", meterValue);
        RowInfo.setAttribute("commEquipInfo", commEquipId);

        Chartinfo.addContent(RowInfo);

        row1 = new Element("Row");
        Element element = new Element("ChartData");

        for (Map<String, Object> map : result) {
            if(index<channels.length){
                if(index==1){
                    row1.setAttribute("localeDate",String.valueOf(map.get("reportDate")));
                }
                String ch = String.valueOf(map.get("channel"));
                row1.setAttribute("channel"+ch,String.valueOf(map.get("decimalValue")));
            }else{
                index = 0;
                String ch = String.valueOf(map.get("channel"));
                row1.setAttribute("channel"+ch,String.valueOf(map.get("decimalValue")));
                element.addContent(row1);
                row1 = new Element("Row");
            }
            index++;
        }


        Element gridData = new Element("GridData");
        row1 = new Element("Row1");
        row2 = new Element("Row2");
        Element row3 = new Element("Row3");
        Element row4 = new Element("Row4");

        for (Map<String, Object> map : gridMap) {
            if("sum".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for(String key : keys) {
                    row1.setAttribute(key,map.get(key).toString());
                }
            }
            else if("avg".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    row2.setAttribute(key,map.get(key).toString());
                }
            }
        }


        for(Map<String, Object> map : resultInterval){

            for(index=0;index<15;index++){
                if(index==0){
                    row3.setAttribute("meteringTime",String.valueOf(map.get("meteringTime")));
                }
                row3.setAttribute("channel_"+index,String.valueOf(map.get("channel_"+index)));
            }
            element.addContent(row3);
            row3 = new Element("Row3");

        }


        for(int j=0; j<channelInfo.size(); j++){
            row4.setAttribute("channelName"+j, channelInfo.get(j).getCodeName());
        }

        gridData.addContent(row1);
        gridData.addContent(row2);
        gridData.addContent(row3);
        gridData.addContent(row4);

        master.addContent(element);
        master.addContent(Chartinfo);
        master.addContent(gridData);

        return master;
    }

    private Element genReportChartMapInfoRate(List<Map<String,Object>> result,List<Map<String,Object>> result2, int rate, String supplierId) {
        Element master  = new Element("Master");
        Element element = new Element("ChartRateData");

        Element row = null;
        Element row1 = null;
        Element row2 = null;
        Element RowInfo = null;

        int index = 1;
        row = new Element("Row");
        for (Map<String, Object> map : result) {
            if(index<rate){
                if(index==1){
                    row.setAttribute("localeDate",String.valueOf(map.get("localeDate")));
                    //row.setAttribute("reportDate", String.valueOf(map.get("reportDate")));
                }
                row.setAttribute("Rate"+index,String.valueOf(map.get("decimalValue")));
            }else{
                row.setAttribute("Rate"+index,String.valueOf(map.get("decimalValue")));
                element.addContent(row);
                row = new Element("Row");
                index = 0;
            }
            index++;
        }

        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Locale locale = new Locale(lang, country);
        String title = messageSource.getMessage("aimir.report.metering", new Object[0], locale);
        String customerInfo = messageSource.getMessage("aimir.customerview", new Object[0], locale);
        String meaNo = messageSource.getMessage("aimir.buildingMgmt.contractNumber", new Object[0], locale);
        String telePhone = messageSource.getMessage("aimir.telephoneno", new Object[0], locale);
        String tariffType = messageSource.getMessage("aimir.contract.tariff.type", new Object[0], locale);
        String address = messageSource.getMessage("aimir.address", new Object[0], locale);
        String cellPhone = messageSource.getMessage("aimir.cpno", new Object[0], locale);
        String location = messageSource.getMessage("aimir.location", new Object[0], locale);
        String meterType = messageSource.getMessage("aimir.metertype", new Object[0], locale);
        String meteringTime = messageSource.getMessage("aimir.meteringtime", new Object[0], locale);
        String meterId = messageSource.getMessage("aimir.meterid", new Object[0], locale);
        String meterValue = messageSource.getMessage("aimir.meteringdata", new Object[0], locale);
        String commEquipId = messageSource.getMessage("aimir.mcuid2", new Object[0], locale);


        Element Chartinfo = new Element("Chartinfo");

        RowInfo = new Element("RowInfo");

        RowInfo.setAttribute("title", title+" ( RATE ) ");
        RowInfo.setAttribute("customerInfo", customerInfo);
        RowInfo.setAttribute("meaNo", meaNo);
        RowInfo.setAttribute("telePhone", telePhone);
        RowInfo.setAttribute("tariffType", tariffType);
        RowInfo.setAttribute("address", address);
        RowInfo.setAttribute("cellPhone", cellPhone);
        RowInfo.setAttribute("location", location);
        RowInfo.setAttribute("meterType", meterType);
        RowInfo.setAttribute("meteringTime", meteringTime);
        RowInfo.setAttribute("meterId", meterId);
        RowInfo.setAttribute("meterValue", meterValue);
        RowInfo.setAttribute("commEquipInfo", commEquipId);

        Chartinfo.addContent(RowInfo);

        Element gridData = new Element("GridData");
        row1 = new Element("Row1");
        row2 = new Element("Row2");
        Element row3 = new Element("Row3");

        for (Map<String, Object> map : result2) {
            if("sum".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for(String key : keys) {
                    row1.setAttribute(key,map.get(key).toString());
                }
            }
            else if("avg".equals(map.get("id"))){
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    row2.setAttribute(key,map.get(key).toString());
                }
            }else{
                for(index=1;index<4;index++){
                    if(index==1){
                        row3.setAttribute("meteringTime",String.valueOf(map.get("meteringTime")));
                        row3.setAttribute("meteringTimeDis",String.valueOf(map.get("meteringTimeDis")));
                    }
                    row3.setAttribute("Rate"+index,String.valueOf(map.get("rate_"+index)));
                }
                element.addContent(row3);
                row3 = new Element("Row3");

            }
        }

        gridData.addContent(row1);
        gridData.addContent(row2);

        master.addContent(element);
        master.addContent(gridData);
        master.addContent(Chartinfo);

        return master;
    }

    private String getXmlString(Element element) {

        XMLOutputter xmlOut = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        xmlOut.setFormat(format);

        String xmlStr = xmlOut.outputString(element);

        return xmlStr;
    }

    /*
     * 일자에서 월 추출
     */
    public String changeDateToMonth (String value) {
        String month="";
        if(value != null && value.length() > 6) {
            month = value.substring(4, 6);
        }
        return month;
    }

    /*
     * 일자에서 시간 추출
     */
    public String changeDateToHour (String value) {
        String hour="";
        if(value != null && value.length() > 8) {
            hour = value.substring(6, 8);
        }
        return hour;
    }

    /*
     * 일자에서 년도 추출
     */
    public String changDateToYear (String value) {
        String year = "";
        if(value != null && value.length() > 4) {
            year = value.substring(0, 4);
        }
        return year;
    }
    
    /**
     * NetStation Monitoring Detail View
     * SP-929
     * @param meterNo
     * @param mvmMiniType
     * @param tabType
     * @param startDate
     * @param endDate
     * @param comboValue
     * @param dayComboValue
     * @param contractId
     * @param supplierId
     * @param channels
     * @param request
     * @return
     */
    @RequestMapping(value="/gadget/mvm/mvmDetailViewNM.do")
    public final ModelAndView executeDetailViewNM(@RequestParam("meterNo") String meterNo,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("tabType") String tabType,
            @RequestParam("stdDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("comboValue") String comboValue,
            @RequestParam("dayComboValue") String dayComboValue,
            @RequestParam("contractId") String contractId,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("channels")   String channels,
            HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String uriParam = request.getRequestURI();
        List<ChannelInfo> channelInfo = new ArrayList<ChannelInfo>();
        String[] channel = channels.split(",");
        for ( int i = 0; i < channel.length; i++ ){
            ChannelInfo ch = new ChannelInfo();
            ch.setCodeId(String.valueOf(i+1));
            ch.setCodeName(channel[i]);
            channelInfo.add(ch);
        }
        
        mav.setViewName("/gadget/mvm/mvmDetailViewNM");


        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", mvmMiniType);
        mav.addObject("tabType", tabType);

        String startMonth = changeDateToMonth(startDate);
        String endMonth   = changeDateToMonth(endDate);
        String startYear  = changDateToYear(startDate);
        String endYear    = changDateToYear(endDate);
        
        Integer meterId = 0;
        String sysId = "";
        String meterType = "";
        if(meterNo != null && !meterNo.isEmpty()){
            Meter meter = meterDao.get(meterNo);
            if ( meter != null ){
                meterId = meter.getId();
                meterType = meter.getMeterType().getName();
                if (  meter.getModem() != null && meter.getModem().getMcu() != null ){
                    sysId = meter.getModem().getMcu().getSysID();
                }
            }
        }

        Supplier supplier = supplierManager.getSupplier((Integer.parseInt(supplierId)));
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        String formatStartDate = TimeLocaleUtil.getLocaleDate(startDate.substring(0, 8), lang, country);
        String formatEndDate = TimeLocaleUtil.getLocaleDate(endDate.substring(0, 8), lang, country);

        String md = supplier.getMd().getPattern();
        int decimalPos = 0;     // 소수점 자릿수

        if (md.indexOf(".") != -1) {
            decimalPos = md.length() - (md.indexOf(".") + 1);
        }
        mav.addObject("decimalPos", decimalPos);
        mav.addObject("comboValue", comboValue);
        mav.addObject("dayComboValue", dayComboValue);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("formatStartDate", formatStartDate);
        mav.addObject("formatEndDate", formatEndDate);
        mav.addObject("startMonth", startMonth);
        mav.addObject("endMonth", endMonth);
        mav.addObject("startYear", startYear);
        mav.addObject("endYear", endYear);
        mav.addObject("contractId", contractId);
        mav.addObject("meterId",meterId);
        mav.addObject("meterNo", meterNo);
        mav.addObject("mcuId", sysId );
        mav.addObject("meterType", meterType );
        mav.addObject("customerInfo", mvmDetailViewManager.getCustomerInfo(meterNo, supplierId));
        //mav.addObject("channelList", mvmDetailViewManager.getChannelInfo(meterNo, mvmMiniType));
        mav.addObject("channelList", channelInfo);

        Map<String,Object> slaveIOInfo =  mbusSlaveIOModuleManager.getMbusSlaveIOModuleInfo(meterNo, Integer.parseInt(supplierId));
        mav.addObject("slaveInfo", slaveIOInfo);

        
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // 수정권한(command = true)
        return mav;
    }
}
