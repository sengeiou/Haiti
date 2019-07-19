package com.aimir.bo.mvm;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

import com.aimir.bo.common.CommonConstantsProperty;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateTabOther;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.MvmMeterValueManager;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplierMgmtManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.MeteringDataMakeExcel;
import com.aimir.util.SortUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;


@Controller
public class MvmMaxController {
	private static Log logger = LogFactory.getLog(MvmMaxController.class);

    @Autowired
    SearchMeteringDataManager searchMeteringDataManager;
    
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

    @RequestMapping(value={"/gadget/mvm/mvmMaxGadgetEM.do","/gadget/mvm/mvmMeterValueMaxGadgetEM.do"})
    public final ModelAndView executeEM(HttpServletRequest request, HttpServletResponse response) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
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
        if(uriParam.contains("/gadget/mvm/mvmMaxGadgetEM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMaxGadget");
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueMaxGadgetEM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMeterValueMaxGadget");
        }
//        if (operator != null && operator.getLocationId() != null) {
//            mav.setViewName("/gadget/mvm/mvmMaxLocationGadget");
//        } else {
//            mav.setViewName("/gadget/mvm/mvmMaxGadget");
//        }

        
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

    @RequestMapping(value={"/gadget/mvm/mvmMaxGadgetGM.do","/gadget/mvm/mvmMeterValueMaxGadgetGM.do"})
    public final ModelAndView executeGM(HttpServletRequest request, HttpServletResponse response) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
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

//        if (operator != null && operator.getLocationId() != null) {
//            mav.setViewName("/gadget/mvm/mvmMaxLocationGadget");
//        } else {
//            mav.setViewName("/gadget/mvm/mvmMaxGadget");
//        }

        String uriParam = request.getRequestURI();
        if(uriParam.contains("/gadget/mvm/mvmMaxGadgetGM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMaxGadget");
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueMaxGadgetGM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMeterValueMaxGadget");
        }
        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", "GM");
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

    @RequestMapping(value={"/gadget/mvm/mvmMaxGadgetWM.do","/gadget/mvm/mvmMeterValueMaxGadgetWM.do"})
    public final ModelAndView executeWM(HttpServletRequest request, HttpServletResponse response) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
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

//        if (operator != null && operator.getLocationId() != null) {
//            mav.setViewName("/gadget/mvm/mvmMaxLocationGadget");
//        } else {
//            mav.setViewName("/gadget/mvm/mvmMaxGadget");
//        }

        String uriParam = request.getRequestURI();
        if(uriParam.contains("/gadget/mvm/mvmMaxGadgetWM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMaxGadget");
        } else if(uriParam.contains("/gadget/mvm/mvmMeterValueMaxGadgetWM.do")) {
        	mav.setViewName("/gadget/mvm/mvmMeterValueMaxGadget");
        }
        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", "WM");
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

    @RequestMapping(value = "/gadget/mvm/mvmMaxGadgetHM.do")
    public final ModelAndView executeHM(HttpServletRequest request, HttpServletResponse response) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
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

//        if (operator != null && operator.getLocationId() != null) {
//            mav.setViewName("/gadget/mvm/mvmMaxLocationGadget");
//        } else {
//            mav.setViewName("/gadget/mvm/mvmMaxGadget");
//        }

        mav.setViewName("/gadget/mvm/mvmMaxGadget");
        mav.addObject("supplierId", supplierId);
        mav.addObject("mvmMiniType", "HM");
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

    @RequestMapping(value="/gadget/mvm/mvmMaxExcelDownloadPopup")
    public ModelAndView mvmMaxExcelDownloadPopup() {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        ModelAndView mav = new ModelAndView("/gadget/mvm/mvmMaxExcelDownloadPopup");
        mav.addObject("supplierId", supplierId);

        return mav;
    }

	@RequestMapping(value = "/gadget/mvm/mvmDetailExcelDownloadPopup")
    public ModelAndView mvmDetailExcelDownloadPopup() {
		ModelAndView mav = new ModelAndView("/gadget/mvm/mvmDetailExcelDownloadPopup");
		return mav;
	}

	@RequestMapping(value="/gadget/mvm/mvmDetailGadgetExcelMake")
	public ModelAndView mvmDetailGadgetExcelMake(
			@RequestParam("supplierId") Integer supplierId,
			@RequestParam(value = "searchType", required = false) String searchType,
			@RequestParam(value = "searchStartDate", required = false) String searchStartDate,
			@RequestParam(value = "searchEndDate", required = false) String searchEndDate,
			@RequestParam(value = "searchStartHour", required = false) String searchStartHour,
			@RequestParam(value = "searchEndHour", required = false) String searchEndHour,
			@RequestParam(value = "meterNo", required = false) String meterNo,
			@RequestParam(value = "channel", required = false) String channel,
			@RequestParam(value = "type", required = false) String type) {
        ModelAndView mav = new ModelAndView("jsonView");
        
        try {
            StringBuilder sbFileName = new StringBuilder();
        	StringBuilder sbSplFileName = new StringBuilder();

        	Long total = 0L;        // 데이터 조회건수
        	Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
            
        	final String ratePrefix     = "meteringDetailDataRate";
        	final String intervalPrefix	= "meteringDetailDataInterval";
            final String hourPrefix     = "meteringDetailDataHour";
        	final String dayPrefix      = "meteringDetailDataDay";
        	final String dayWeekPrefix  = "meteringDetailDataDayWeek";
        	final String weekPrefix     = "meteringDetailDataWeek";
        	final String monthPrefix    = "meteringDetailDataMonth";
        	final String seasonPrefix   = "meteringDetailDataSeason";
        	final String yearPrefix     = "meteringDetailDataYear";
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
			Map<String, String> msgMap = new HashMap<String, String>();
			List<String> fileNameList = new ArrayList<String>();
			
        	conditionMap.put("supplierId", supplierId);
        	conditionMap.put("meterNo", meterNo);
        	conditionMap.put("channel", channel);
        	
        	String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        	conditionMap.put("meterType", meterType);
        	
        	String tlbType = MeterType.valueOf(meterType).getLpClassName();
        	conditionMap.put("tlbType", tlbType);
        	
        	Map<String, Object> result = null;
			List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
        	DateTabOther other = null;
        	DateType dateType = null;
        	String searchTypeName = null;

        	if (StringUtil.nullToBlank(searchType).isEmpty()) {
        		searchType = DateTabOther.RATE.name();
        	}

			if (StringUtil.nullToBlank(searchStartDate).isEmpty() && StringUtil.nullToBlank(searchEndDate).isEmpty()) {
				searchStartDate = TimeUtil.getCurrentDay();
				searchEndDate = TimeUtil.getCurrentDay();
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
				switch (other) {
        			case RATE:
        				conditionMap.put("rate1", CommonConstantsProperty.getProperty("customer.usage.rate1"));
        				conditionMap.put("rate2", CommonConstantsProperty.getProperty("customer.usage.rate2"));
        				conditionMap.put("rate3", CommonConstantsProperty.getProperty("customer.usage.rate3"));
        				conditionMap.put("rateChannel", ElectricityChannel.Usage.getChannel());
        				result = mvmDetailViewManager.getMeteringDataDetailRatelyChartData(conditionMap);
        				sbFileName.append(ratePrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = other.RATE.name();
        				
        				logger.info("searchData: " + searchData);
        				break;
        			case INTERVAL:
        				conditionMap.put("searchStartHour", searchStartHour);
        				conditionMap.put("searchEndHour", searchEndHour);
        				result = mvmDetailViewManager.getMeteringDataDetailIntervalChartData(conditionMap);
        				sbFileName.append(intervalPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = other.INTERVAL.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
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
        				sbFileName.append(hourPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.HOURLY.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        			case DAILY:
        				result = mvmDetailViewManager.getMeteringDataDetailDailyChartData(conditionMap);
        				sbFileName.append(dayPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.DAILY.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        			case WEEKLY:
        				result = mvmDetailViewManager.getMeteringDataDetailWeeklyChartData(conditionMap);
        				sbFileName.append(weekPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.WEEKLY.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        			case MONTHLY:
        				result = mvmDetailViewManager.getMeteringDataDetailMonthlyChartData(conditionMap);
        				sbFileName.append(monthPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.MONTHLY.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        			case WEEKDAILY:
        				result = mvmDetailViewManager.getMeteringDataDetailWeekDailyChartData(conditionMap);
        				sbFileName.append(dayWeekPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.WEEKDAILY.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        			case SEASONAL:
        				result = mvmDetailViewManager.getMeteringDataDetailSeasonalChartData(conditionMap);
        				sbFileName.append(seasonPrefix);
        				
        				searchData = (List<Map<String, Object>>)result.get("searchData");
        				searchTypeName = dateType.SEASONAL.name();
        				
        				logger.info("searchAddData: " + (List<Map<String, Object>>)result.get("searchAddData"));
        				logger.info("searchData: " + searchData);
        				break;
        		}
        	}
        	
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}
			
			sbFileName.append(TimeUtil.getCurrentTimeMilli());
			
			// message setting 영역 (S)
			Supplier supplier = supplierManager.getSupplier(supplierId);
			String unit = null;
			String lang = supplier.getLang().getCode_2letter();
	        String country = supplier.getCountry().getCode_2letter();
	        Locale locale = new Locale(lang, country);

			if (type.equals("EM")) {
				unit = messageSource.getMessage("aimir.unit.kwh", new Object[0], locale);
			} else if (type.equals("GM") || type.equals("WM") || type.equals("HM")) {
				unit = messageSource.getMessage("aimir.unit.m3", new Object[0], locale);
			}

	        msgMap.put("msg_title", "Metering Detail Data");
			msgMap.put("msg_meterTime", messageSource.getMessage("aimir.meteringtime", new Object[0], locale));
			
			if (searchTypeName.equals("RATE")) {
				msgMap.put("msg_01", "Rate 1");
				msgMap.put("msg_02", "Rate 2");
				msgMap.put("msg_03", "Rate 3");
				msgMap.put("msg_04", "");
	        } else {
	        	msgMap.put("msg_01", messageSource.getMessage("aimir.comm.active.import", new Object[0], locale) + "[" + unit + "]");
				msgMap.put("msg_02", messageSource.getMessage("aimir.comm.active.export", new Object[0], locale) + "[" + unit + "]");
				msgMap.put("msg_03", messageSource.getMessage("aimir.comm.reactive.import", new Object[0], locale) + "[" + unit + "]");
				msgMap.put("msg_04", messageSource.getMessage("aimir.comm.reactive.export", new Object[0], locale) + "[" + unit + "]");
	        }
			// message setting 영역 (E)
			
			// Check Download Directory Logic (S)
			String filePath = messageSource.getMessage("aimir.report.fileDownloadDir", new Object[0], locale);
			File downDir = new File(filePath);

			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate = null;
					boolean isDel = false;

					try {
						// 10일 이전 일자
						deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					for (File file : files) {
						filename = file.getName();
						isDel = false;

						// 파일길이 : 22이상, 확장자 : xls|zip
						if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(hourPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(dayPrefix) && filename.substring(15, 23).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(dayWeekPrefix) && filename.substring(19, 27).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(weekPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(monthPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(seasonPrefix) && filename.substring(18, 26).compareTo(deleteDate) < 0) {
								isDel = true;
							} else if (filename.startsWith(yearPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							}

							if (isDel) {
								file.delete();
							}
						}
						filename = null;
					}
				}
			} else { // directory가 없으면 생성
				downDir.mkdir();
			}
			// Check Download Directory Logic (E)
			
			// create excel file
			MeteringDataMakeExcel wExcel = new MeteringDataMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;
			
			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");

				wExcel.writeDetailReportExcel(searchData, msgMap, filePath, sbSplFileName.toString(), searchTypeName);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = searchData.subList(idx, (i + 1));
						
						wExcel.writeDetailReportExcel(list, msgMap, filePath, sbSplFileName.toString(), searchTypeName);
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
			try {
				zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mav.addObject("filePath", filePath);
			mav.addObject("fileName", fileNameList.get(0));
			mav.addObject("zipFileName", sbZipFile.toString());
			mav.addObject("fileNames", fileNameList);
		} catch (Exception e) {
			logger.error(e, e);
		}
        
        return mav;
    }
    
    @RequestMapping(value="/gadget/mvm/mvmMaxGadgetExcelMake")
    public ModelAndView mvmMaxGadgetExcelMake (@RequestParam("supplierId") Integer supplierId,
            @RequestParam("contractNumber") String contractNumber,           @RequestParam("customerName") String customerName,
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
            @RequestParam("sicId") Integer sicId,
            @RequestParam("mvmMiniType") String mvmMiniType,
            @RequestParam("msgNumber") String msgNumber,
            @RequestParam("msgContractNumber") String msgContractNumber,
            @RequestParam("msgCustomerName") String msgCustomerName,
            @RequestParam("msgMeteringtime") String msgMeteringtime,
            @RequestParam("msgUsage") String msgUsage,
            @RequestParam("msgPrevious") String msgPrevious,
            @RequestParam("msgMeterValue2") String msgMeterValue2,
            @RequestParam("msaPrevMeterValue") String msaPrevMeterValue,
            @RequestParam("msaPrevUsage") String msaPrevUsage,
            @RequestParam("msgMeterId") String msgMeterId,
            @RequestParam("msgModemId") String msgModemId,
            @RequestParam(value="accumulate", required=false) String accumulate,
            @RequestParam(value="msgMeterValue", required=false) String msgMeterValue,
            @RequestParam("filePath") String filePath,
            @RequestParam("title") String title,
            @RequestParam(value="meterValue", required=false) Boolean meterValue) {
        ModelAndView mav = new ModelAndView("jsonView");        
        List<Map<String, Object>> result = null;
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
//        List<MeteringListData> list = new ArrayList<MeteringListData>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L;        // 데이터 조회건수
        Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String hourPrefix    = "meteringDataHour";       //16글자
        final String dayPrefix     = "meteringDataDay";        //15글자
        final String dayWeekPrefix = "meteringDataDayWeek";    //19글자
        final String weekPrefix    = "meteringDataWeek";       //16글자
        final String monthPrefix   = "meteringDataMonth";      //17글자
        final String seasonPrefix  = "meteringDataSeason";     //18글자
        final String yearPrefix    = "meteringDataYear";       //16글자

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("customerName", customerName);
        conditionMap.put("meteringSF", meteringSF);
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
        conditionMap.put("sicId", sicId);

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

        DateType dateType = null;

        for (DateType obj : DateType.values()) {
            if (obj.getCode().equals(searchDateType)) {
                dateType = obj;
                break;
            }
        }
        
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        String supplierName = user.getRoleData().getSupplier().getName();

        if(meterValue) {
            switch(dateType) {
	            case HOURLY:
	        		result = searchMeteringDataManager.getMeteringValueDataHourlyData(conditionMap);
	        		sbFileName.append(hourPrefix);
	                break;
	            case DAILY:
	                result = mvmMeterValueManager.getMeteringValueDataDailyData(conditionMap);
	                sbFileName.append(dayPrefix);
	                break;
	            case WEEKLY:
	                result = mvmMeterValueManager.getMeteringValueDataWeeklyData(conditionMap);
	                sbFileName.append(monthPrefix);
	                break;
	            case MONTHLY:
					result = searchMeteringDataManager.getMeteringValueMonthlyData(conditionMap);
					sbFileName.append(monthPrefix);
	                break;
	            case WEEKDAILY:
	                result = mvmMeterValueManager.getMeteringValueDataWeekDailyData(conditionMap);
	                sbFileName.append(dayWeekPrefix);
	                break;
	            case SEASONAL:
	                result = mvmMeterValueManager.getMeteringValueDataSeasonalData(conditionMap);
	                sbFileName.append(seasonPrefix);
	                break;
	            case YEARLY:
	                result = mvmMeterValueManager.getMeteringValueDataYearlyData(conditionMap);
	                sbFileName.append(yearPrefix);
	                break;
            }
        } else {
            switch(dateType) {
	            case HOURLY:
	            	if("WM".equals(mvmMiniType) || "GM".equals(mvmMiniType)) {
	            		result = searchMeteringDataManager.getMeteringValueDataHourlyData(conditionMap);
	            		sbFileName.append(hourPrefix);
	            	} else {
	            		result = searchMeteringDataManager.getMeteringDataHourlyData(conditionMap);
	                    sbFileName.append(hourPrefix);
	            	}
	                break;
	            case DAILY:
	                result = searchMeteringDataManager.getMeteringDataDailyData(conditionMap);
	                sbFileName.append(dayPrefix);
	                break;
	            case WEEKLY:
	                result = searchMeteringDataManager.getMeteringDataWeeklyData(conditionMap);
	                sbFileName.append(weekPrefix);
	                break;
	            case MONTHLY:
	            	if("WM".equals(mvmMiniType) || "GM".equals(mvmMiniType)) {
	            		result = searchMeteringDataManager.getMeteringValueMonthlyData(conditionMap);
	            		sbFileName.append(monthPrefix);
	            	} else {
	            		result = searchMeteringDataManager.getMeteringDataMonthlyData(conditionMap);
	                    sbFileName.append(monthPrefix);
	            	}
	                break;
	            case WEEKDAILY:
	                result = searchMeteringDataManager.getMeteringDataWeekDailyData(conditionMap);
	                sbFileName.append(dayWeekPrefix);
	                break;
	            case SEASONAL:
	                result = searchMeteringDataManager.getMeteringDataSeasonalData(conditionMap);
	                sbFileName.append(seasonPrefix);
	                break;
	            case YEARLY:
	                result = searchMeteringDataManager.getMeteringDataYearlyData(conditionMap);
	                sbFileName.append(yearPrefix);
	                break;
	        }
        }


        total = new Integer(result.size()).longValue();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        sbFileName.append(TimeUtil.getCurrentTimeMilli());

        // message 생성
        msgMap.put("number", msgNumber);
        msgMap.put("contractNumber", msgContractNumber);
        msgMap.put("customerName", msgCustomerName);
        msgMap.put("meteringTime", msgMeteringtime);
        msgMap.put("usage", msgUsage);
        msgMap.put("previous", msgPrevious);
        msgMap.put("meterId", msgMeterId);
        msgMap.put("modemId", msgModemId);
        msgMap.put("title",title);
        msgMap.put("accumulate",accumulate);
        msgMap.put("meterValue",msgMeterValue);
        msgMap.put("meterValue2",msgMeterValue2);
        msgMap.put("prevMeterValue",msaPrevMeterValue);
        msgMap.put("prevUsage",msaPrevUsage);
        
        // check download dir
        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                String filename = null;
                String deleteDate = null;

                try {
                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);    // 10일 이전 일자
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean isDel = false;

                for (File file : files) {
                    filename = file.getName();
                    isDel = false;

                    // 파일길이 : 22이상, 확장자 : xls|zip
                    if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                        // 10일 지난 파일들 삭제
                        if (filename.startsWith(hourPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayPrefix) && filename.substring(15, 23).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayWeekPrefix) && filename.substring(19, 27).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(weekPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(monthPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(seasonPrefix) && filename.substring(18, 26).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(yearPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
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

        //////////// 수정해야되는 부분
        // create excel file
        MeteringDataMakeExcel wExcel = new MeteringDataMakeExcel();
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) {
            sbSplFileName = new StringBuilder();
            sbSplFileName.append(sbFileName);
            sbSplFileName.append(".xls");
            if((("WM".equals(mvmMiniType) || "GM".equals(mvmMiniType)) && (DateType.MONTHLY == dateType || DateType.HOURLY == dateType))) {
            	wExcel.writeReportExcelForMeterValue(result, msgMap, isLast, filePath, sbSplFileName.toString(), dateType, mvmMiniType, supplierName);
            }  else {
            	wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString(), dateType, mvmMiniType, supplierName);
            }
            
            fileNameList.add(sbSplFileName.toString());
        } else {
            for (int i = 0; i < total; i++) {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                    sbSplFileName = new StringBuilder();
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(++fnum).append(").xls");

                    list = result.subList(idx, (i + 1));

                    wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString(), dateType, mvmMiniType, supplierName);
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
        try {
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return object
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("zipFileName", sbZipFile.toString());
        mav.addObject("fileNames", fileNameList);
        
        return mav;
    }

    /**
     * method name : getMeteringDataList<b/>
     * method Desc : Metering Data 맥스가젯의 Metering Data List 를 조회한다.
     *
     * @param supplierId
     * @param contractNumber
     * @param customerName
     * @param meteringSF
     * @param searchDateType
     * @param searchStartDate
     * @param searchStartHour
     * @param searchEndDate
     * @param searchEndHour
     * @param locationId
     * @param tariffType
     * @param mcuId
     * @param deviceType
     * @param mdevId
     * @param contractGroup
     * @param sicId
     * @param mvmMiniType
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getMeteringDataList")
    public ModelAndView getMeteringDataList(@RequestParam("supplierId") Integer supplierId,
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
            
            if (StringUtil.nullToBlank(searchDateType).isEmpty() || StringUtil.nullToBlank(searchStartDate).isEmpty() || StringUtil.nullToBlank(searchEndDate).isEmpty()) {
                try {
                	searchDateType = DateType.DAILY.getCode();         // default 일자조건탭
                    searchStartDate = TimeUtil.getCurrentDay();
                    searchStartHour = "00";
                    searchEndDate = TimeUtil.getCurrentDay();
                    searchEndHour = "23";
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

            if (!StringUtil.nullToBlank(sicIds).isEmpty()) {
                String[] sicIdArray = sicIds.split(",");
                List<Integer> sicIdList = new ArrayList<Integer>();
                
                for (String id : sicIdArray) {
                    sicIdList.add(Integer.valueOf(id));
                }
                
                conditionMap.put("sicIdList", sicIdList);
            }

            String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
            conditionMap.put("meterType", meterType);
            String tlbType = MeterType.valueOf(meterType).getLpClassName();
            conditionMap.put("tlbType", tlbType);

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
                	if("WM".equals(mvmMiniType) || "GM".equals(mvmMiniType)) {
                		result = searchMeteringDataManager.getMeteringValueDataHourlyData(conditionMap);
                		totalCount = searchMeteringDataManager.getMeteringValueDataHourlyDataTotalCount(conditionMap);
                	} else {
                		result = searchMeteringDataManager.getMeteringDataHourlyData(conditionMap);
                		totalCount = searchMeteringDataManager.getMeteringDataHourlyDataTotalCount(conditionMap);
                	}
                    break;
                case DAILY:
                    result = searchMeteringDataManager.getMeteringDataDailyData2(conditionMap);
                    totalCount = searchMeteringDataManager.getMeteringDataDailyDataTotalCount2(conditionMap);
                    break;
                case WEEKLY:
                    result = searchMeteringDataManager.getMeteringDataWeeklyData(conditionMap);
                    totalCount = searchMeteringDataManager.getMeteringDataWeeklyDataTotalCount(conditionMap);
                    break;
                case MONTHLY:
                	if("WM".equals(mvmMiniType) || "GM".equals(mvmMiniType)) {
    					result = searchMeteringDataManager.getMeteringValueMonthlyData(conditionMap);
                    	totalCount = searchMeteringDataManager.getMeteringValueMonthlyDataTotalCount(conditionMap);
                	} else {
                    	result = searchMeteringDataManager.getMeteringDataMonthlyData(conditionMap);
                    	totalCount = searchMeteringDataManager.getMeteringDataMonthlyDataTotalCount(conditionMap);
                    }
                    break;
                case WEEKDAILY:
                    result = searchMeteringDataManager.getMeteringDataWeekDailyData(conditionMap);
                    totalCount = searchMeteringDataManager.getMeteringDataWeekDailyDataTotalCount(conditionMap);
                    break;
                case SEASONAL:
                    result = searchMeteringDataManager.getMeteringDataSeasonalData(conditionMap);
                    totalCount = searchMeteringDataManager.getMeteringDataSeasonalDataTotalCount(conditionMap);
                    break;
                case YEARLY:
                    result = searchMeteringDataManager.getMeteringDataYearlyData(conditionMap);
                    totalCount = searchMeteringDataManager.getMeteringDataYearlyDataTotalCount(conditionMap);
                    break;
            }
            
            mav.addObject("result", result);
            mav.addObject("totalCount", totalCount);

            return mav;
    	}
    //}
    
    /**
     * method name : getMeteringValueDataList<b/>
     * method Desc : Metering Data 맥스가젯의 Metering Value List 를 조회한다.
     * 				 mvmMeterValueMaxGadget 용
     *
     * @param supplierId
     * @param contractNumber
     * @param customerName
     * @param meteringSF
     * @param searchDateType
     * @param searchStartDate
     * @param searchStartHour
     * @param searchEndDate
     * @param searchEndHour
     * @param locationId
     * @param tariffType
     * @param mcuId
     * @param deviceType
     * @param mdevId
     * @param contractGroup
     * @param sicId
     * @param mvmMiniType
     * @return
     */
    @RequestMapping(value = "/gadget/mvm/getMeteringValueDataList")
    public ModelAndView getMeteringValueDataList(@RequestParam("supplierId") Integer supplierId,
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
        
        if (StringUtil.nullToBlank(searchDateType).isEmpty() || StringUtil.nullToBlank(searchStartDate).isEmpty() || StringUtil.nullToBlank(searchEndDate).isEmpty()) {
            try {
                searchDateType = DateType.DAILY.getCode();         // default 일자조건탭
                searchStartDate = TimeUtil.getCurrentDay();
                searchStartHour = "00";
                searchEndDate = TimeUtil.getCurrentDay();
                searchEndHour = "23";
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

        if (!StringUtil.nullToBlank(sicIds).isEmpty()) {
            String[] sicIdArray = sicIds.split(",");
            List<Integer> sicIdList = new ArrayList<Integer>();
            
            for (String id : sicIdArray) {
                sicIdList.add(Integer.valueOf(id));
            }
            
            conditionMap.put("sicIdList", sicIdList);
        }

        String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        conditionMap.put("meterType", meterType);
        String tlbType = MeterType.valueOf(meterType).getLpClassName();
        conditionMap.put("tlbType", tlbType);

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
        		result = searchMeteringDataManager.getMeteringValueDataHourlyData(conditionMap);
        		totalCount = searchMeteringDataManager.getMeteringValueDataHourlyDataTotalCount(conditionMap);
                break;
            case DAILY:
                result = mvmMeterValueManager.getMeteringValueDataDailyData(conditionMap);
                totalCount = mvmMeterValueManager.getMeteringValueDataDailyDataTotalCount(conditionMap);
                break;
            case WEEKLY:
                result = mvmMeterValueManager.getMeteringValueDataWeeklyData(conditionMap);
                totalCount = mvmMeterValueManager.getMeteringValueDataWeeklyDataTotalCount(conditionMap);
                break;
            case MONTHLY:
				result = searchMeteringDataManager.getMeteringValueMonthlyData(conditionMap);
            	totalCount = searchMeteringDataManager.getMeteringValueMonthlyDataTotalCount(conditionMap);
                break;
            case WEEKDAILY:
                result = mvmMeterValueManager.getMeteringValueDataWeekDailyData(conditionMap);
                totalCount = mvmMeterValueManager.getMeteringValueDataWeekDailyDataTotalCount(conditionMap);
                break;
            case SEASONAL:
                result = mvmMeterValueManager.getMeteringValueDataSeasonalData(conditionMap);
                totalCount = mvmMeterValueManager.getMeteringValueDataSeasonalDataTotalCount(conditionMap);
                break;
            case YEARLY:
                result = mvmMeterValueManager.getMeteringValueDataYearlyData(conditionMap);
                totalCount = mvmMeterValueManager.getMeteringValueDataYearlyDataTotalCount(conditionMap);
                break;
        }

        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }
}