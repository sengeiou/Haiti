package com.aimir.bo.mvm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.service.device.MeterManager;
import com.aimir.service.mvm.CustomerUsageManager;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.PrepaymentLogManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;


@Controller
public class customerUsageController {

	@Autowired
	CustomerUsageManager customerUsageManager;

	@Autowired
	OperatorManager operatorManager;

    @Autowired
    MeterManager meterManager;

	@Autowired
	CustomerManager customerManager;

	@Autowired
    ContractManager contractManager;

	@Autowired
    PrepaymentLogManager prepaymentLogManager;

    @Autowired
    MvmDetailViewManager mvmDetailViewManager;

	@RequestMapping(value = "/gadget/customer/customerUsageEmMiniGadget")
	public ModelAndView getMiniChart() {
	    ModelAndView mav = new ModelAndView("gadget/customer/customerUsageEmMiniGadget");

	    AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        String meterTypeCode = "3.1"; //EnergyMeter

        List<Map<String, Object>> contractInfo = contractManager.getContractByloginId(user.getLoginId(), meterTypeCode);
        Double currentCredit = contractInfo.size() > 0 ? StringUtil.nullToDoubleZero((Double)contractInfo.get(0).get("CURRENTCREDIT")) : 0;
        Integer contractId = contractInfo.size() > 0 ? (Integer)contractInfo.get(0).get("CONTRACTID") : -1;
        //Integer meterId = contractInfo.size() > 0 ? (Integer)contractInfo.get(0).get("METERID") : -1;

        Meter meter = null;
        if(0 < contractInfo.size() && contractInfo.get(0).get("METERID") != null){
        	meter = meterManager.getMeter((Integer)contractInfo.get(0).get("METERID"));
        }

        List<Map<String, Object>> contractList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> map : contractInfo) {
            if(map.get("CONTRACTID") != null && !"".equals(map.get("CONTRACTID"))) {
                Map<String, Object> contractMap = new HashMap<String, Object>();
                contractMap.put("id", map.get("CONTRACTID"));
                contractMap.put("name", map.get("CONTRACTNUMBER"));
                contractList.add(contractMap);
            }
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfYyyy = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfmm = new SimpleDateFormat("MM");

        Map<String,String> monthlyStartEndDate = CalendarUtil.getDateMonth(sdfYyyy.format(cal.getTime()), sdfmm.format(cal.getTime()));
        String startDate = monthlyStartEndDate.get("startDate");
        String endDate = monthlyStartEndDate.get("endDate");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("contractId", contractId);
        condition.put("startDate", startDate);
        condition.put("endDate",endDate);
        Double monthlyCredit = prepaymentLogManager.getMonthlyCredit(condition);
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(user.getSupplier().getCd());

        startDate = TimeLocaleUtil.getLocaleDate(
                startDate , user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());
        endDate = TimeLocaleUtil.getLocaleDate(
                endDate , user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());

	    mav.addObject("currentCredit",cdf.format(currentCredit));
	    mav.addObject("monthlyCredit",cdf.format(monthlyCredit));
	    mav.addObject("currentDate","("+startDate+"~"+endDate+")");
	    mav.addObject("contractList",contractList.size() < 1 ? null : contractList);
	    mav.addObject("meterId", meter != null && meter.getMdsId() != null ? meter.getMdsId() : "-");
	    mav.addObject("address", meter != null && meter.getAddress() != null ? meter.getAddress() : "-");

        Customer customer = customerManager.getCustomersByLoginId(user.getLoginId());
        StringBuilder customerAddress = new StringBuilder();
        if(customer != null && customer.getAddress() != null && !customer.getAddress().equals("")){
        	customerAddress.append(customer.getAddress());
        }

        if(customer != null && customer.getAddress1() != null && !customer.getAddress1().equals("")) {
        	customerAddress.append("\n" + customer.getAddress1());
        }

        if(customer != null && customer.getAddress2() != null && !customer.getAddress2().equals("")) {
        	customerAddress.append("\n" + customer.getAddress2());
        }

        if(customer != null && customer.getAddress3() != null && !customer.getAddress3().equals("")) {
        	customerAddress.append("\n" + customer.getAddress3());
        }

        customerAddress.trimToSize();
	    mav.addObject("customerAddress", user != null &&  0 < customerAddress.length() ? customerAddress.toString() : "-");

	    if(meter != null && !meter.getMdsId().equals("")){
	    	CustomerInfo cInfo = mvmDetailViewManager.getCustomerInfo(meter.getMdsId(), String.valueOf(user.getRoleData().getSupplier().getId()));
	        mav.addObject("lastMeteringData", cInfo.getLastMeteringData());
	    }else{
	    	mav.addObject("lastMeteringData", "-");
	    }

		return mav;
	}

	@RequestMapping(value = "/gadget/customer/customerUsageWmMiniGadget")
	public ModelAndView getMiniChart2() {
       ModelAndView mav = new ModelAndView("gadget/customer/customerUsageWmMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        String meterTypeCode = "3.2"; //WaterMeter

        List<Map<String, Object>> contractInfo = contractManager.getContractByloginId(user.getLoginId(), meterTypeCode);
        Double currentCredit = contractInfo.size() > 0 ? StringUtil.nullToDoubleZero((Double)contractInfo.get(0).get("CURRENTCREDIT")) : 0;
        Integer contractId = contractInfo.size() > 0 ? (Integer)contractInfo.get(0).get("CONTRACTID") : -1;
        Integer meterId = contractInfo.size() > 0 ? (Integer)contractInfo.get(0).get("METERID") : -1;
        Meter meter = meterManager.getMeter(meterId);

        List<Map<String, Object>> contractList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> map : contractInfo) {
            if(map.get("CONTRACTID") != null && !"".equals(map.get("CONTRACTID"))) {
                Map<String, Object> contractMap = new HashMap<String, Object>();
                contractMap.put("id", map.get("CONTRACTID"));
                contractMap.put("name", map.get("CONTRACTNUMBER"));
                contractList.add(contractMap);
            }
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfYyyy = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfmm = new SimpleDateFormat("MM");

        Map<String,String> monthlyStartEndDate = CalendarUtil.getDateMonth(sdfYyyy.format(cal.getTime()), sdfmm.format(cal.getTime()));
        String startDate = monthlyStartEndDate.get("startDate");
        String endDate = monthlyStartEndDate.get("endDate");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("contractId", contractId);
        condition.put("startDate", monthlyStartEndDate.get("startDate"));
        condition.put("endDate",monthlyStartEndDate.get("endDate"));
        Double monthlyCredit = prepaymentLogManager.getMonthlyCredit(condition);

        DecimalFormat cdf = DecimalUtil.getDecimalFormat(user.getSupplier().getCd());

        startDate = TimeLocaleUtil.getLocaleDate(
                startDate, user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());
        endDate = TimeLocaleUtil.getLocaleDate(
                endDate, user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());

        mav.addObject("currentCredit",cdf.format(currentCredit));
        mav.addObject("monthlyCredit",cdf.format(monthlyCredit));
        mav.addObject("currentDate","("+startDate+"~"+endDate+")");
        mav.addObject("contractList",contractList.size() < 1 ? "-" : contractList);
        mav.addObject("meterId", meter != null && meter.getMdsId() != null ? meter.getMdsId() : "-");
        mav.addObject("address", meter != null && meter.getAddress() != null ? meter.getAddress() : "-");

        Customer customer = customerManager.getCustomersByLoginId(user.getLoginId());
        StringBuilder customerAddress = new StringBuilder();
        if(customer != null && customer.getAddress() != null && !customer.getAddress().equals("")){
        	customerAddress.append(customer.getAddress());
        }

        if(customer != null && customer.getAddress1() != null && !customer.getAddress1().equals("")) {
        	customerAddress.append("\n" + customer.getAddress1());
        }

        if(customer != null && customer.getAddress2() != null && !customer.getAddress2().equals("")) {
        	customerAddress.append("\n" + customer.getAddress2());
        }

        if(customer != null && customer.getAddress3() != null && !customer.getAddress3().equals("")) {
        	customerAddress.append("\n" + customer.getAddress3());
        }

        customerAddress.trimToSize();
	    mav.addObject("customerAddress", user != null &&  0 < customerAddress.length() ? customerAddress.toString() : "-");

	    if(meter != null && !meter.getMdsId().equals("")){
	    	CustomerInfo cInfo = mvmDetailViewManager.getCustomerInfo(meter.getMdsId(), String.valueOf(user.getRoleData().getSupplier().getId()));
	        mav.addObject("lastMeteringData", cInfo.getLastMeteringData());
	    }else{
	    	mav.addObject("lastMeteringData", "-");
	    }

        return mav;
	}

	@RequestMapping(value = "/gadget/customer/customerUsageGmMiniGadget")
	public ModelAndView getMiniChart3() {
		return new ModelAndView("gadget/customer/customerUsageGmMiniGadget");
	}

	@RequestMapping(value = "/gadget/customer/customerUsageGmTariffMiniGadget")
	public ModelAndView getUsageFeeGm() {
		return new ModelAndView("gadget/customer/customerUsageGmTariffMiniGadget");
	}

	@RequestMapping(value = "/gadget/customer/customerUsageWmTariffMiniGadget")
	public ModelAndView getUsageFeeWm() {
		return new ModelAndView("gadget/customer/customerUsageWmTariffMiniGadget");
	}

	@RequestMapping(value = "/gadget/customer/customerUsageTariffMiniGadget")
	public ModelAndView getUsageFeeEm() {
		return new ModelAndView("gadget/customer/customerUsageTariffMiniGadget");
	}

	@RequestMapping(value = "/gadget/customer/customerMainGadget")
	public ModelAndView getMainGadget() {
		return new ModelAndView("gadget/customer/customerMainGadget");
	}

	@RequestMapping(value = "/gadget/customer/getCustomerUsageMiniGrid")
	public ModelAndView getCustomerUsageMiniGrid(
			@RequestParam(value = "sViewType", required = true) String sViewType
			 , @RequestParam(value = "sUserId" ,required=false) String sUserId
			 , @RequestParam(value = "iMdev_type" ,required=true) String iMdev_type
			 , @RequestParam(value = "METER_TYPE" ,required=true) String METER_TYPE
			 , @RequestParam(value = "iStand" ,required=true) String iStand) {

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("sViewType", StringUtil.nullToBlank(sViewType));

		if("".equals(sUserId) || sUserId == null)
			condition.put("sUserId", StringUtil.nullToBlank(user.getAccountId()));
		else
			condition.put("sUserId", StringUtil.nullToBlank(sUserId));


		condition.put("iMdev_type", StringUtil.nullToBlank(iMdev_type));
		condition.put("METER_TYPE", StringUtil.nullToBlank(METER_TYPE));
		condition.put("iStand", StringUtil.nullToBlank(iStand));

		List<Object> usageData	= customerUsageManager.getCustomerUsageMiniChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("useageData",usageData.get(0));
        mav.addObject("sEndData",usageData.get(1));

       	return mav;
	}

	@RequestMapping(value = "/gadget/customer/getCustomerUsageMiniChartbySearchDate")
	public ModelAndView getCustomerUsageMiniChartbySearchDate(
			@RequestParam(value = "sUserId" ,required=false) String sUserId
			 , @RequestParam(value = "iMdev_type" ,required=true) String iMdev_type
			 , @RequestParam(value = "METER_TYPE" ,required=true) String METER_TYPE
			 , @RequestParam(value = "searchType" ,required=true) String searchType
			 , @RequestParam(value = "startDate" ,required=true) String startDate
			 , @RequestParam(value = "endDate" ,required=true) String endDate
			 , @RequestParam(value = "contractId" ,required=true) String contractId
			 , @RequestParam(value = "supplierId" , required=true) String supplierId) {

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Map<String, Object> condition = new HashMap<String, Object>();

		if("".equals(sUserId) || sUserId == null)
			condition.put("sUserId", StringUtil.nullToBlank(user.getAccountId()));
		else
			condition.put("sUserId", StringUtil.nullToBlank(sUserId));

		Integer contractIdInt = ("-".equals(contractId)) || ("null".equals(contractId)) ? -1 : Integer.parseInt(contractId);

		condition.put("iMdev_type", StringUtil.nullToBlank(iMdev_type));
		condition.put("METER_TYPE", StringUtil.nullToBlank(METER_TYPE));
		condition.put("supplierId", StringUtil.nullToBlank(user.getSupplier().getId()));
		condition.put("contractId", contractIdInt);
		if(startDate.length() == 0 && endDate.length() == 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

			condition.put("searchType", CommonConstants.DateType.valueOf("DAILY").getCode());
			condition.put("startDate", df.format(new Date()));
			condition.put("endDate", df.format(new Date()));
		} else {
			condition.put("searchType", StringUtil.nullToBlank(searchType));
			condition.put("startDate", StringUtil.nullToBlank(startDate));
			condition.put("endDate", StringUtil.nullToBlank(endDate));
		}

		List<Object> usageData	= customerUsageManager.getCustomerUsageMiniChartbySearchDate(condition);


		Contract contract = contractManager.getContract(contractIdInt);
		Double currentCredit = ((contract == null) || contract.getCurrentCredit() == null) ? 0.0 : contract.getCurrentCredit();

		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfYyyy = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfmm = new SimpleDateFormat("MM");

        Map<String,String> monthlyStartEndDate = CalendarUtil.getDateMonth(sdfYyyy.format(cal.getTime()), sdfmm.format(cal.getTime()));
        String monthlyStartDate = monthlyStartEndDate.get("startDate");
        String monthlyEndDate = monthlyStartEndDate.get("endDate");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractId", contractIdInt);
        conditionMap.put("startDate", monthlyStartDate);
        conditionMap.put("endDate",monthlyEndDate);
        Double monthlyCredit = prepaymentLogManager.getMonthlyCredit(conditionMap);

        DecimalFormat cdf = DecimalUtil.getDecimalFormat(user.getSupplier().getCd());

        startDate = TimeLocaleUtil.getLocaleDate(
                startDate , user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());
        endDate = TimeLocaleUtil.getLocaleDate(
                endDate , user.getSupplier().getLang().getCode_2letter(), user.getSupplier().getCountry().getCode_2letter());

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("currentCredit",cdf.format(currentCredit));
        mav.addObject("monthlyCredit",cdf.format(monthlyCredit));
        mav.addObject("currentDate","("+startDate+"~"+endDate+")");
        mav.addObject("useageData",usageData.get(0));
        mav.addObject("sEndData",usageData.get(1));

       	return mav;
	}

	@RequestMapping(value = "/gadget/customer/getCustomerUsageFee")
	public ModelAndView getUsageFee(@RequestParam(value = "sViewType", required = false) String sViewType,
			@RequestParam(value = "sUserId", required = false) String sUserId,
			@RequestParam(value = "iMdev_type", required = false) String iMdev_type){

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Map<String, Object> condition	= new HashMap<String, Object>();

		condition.put("sViewType", StringUtil.nullToBlank(sViewType));

		if("".equals(sUserId) || sUserId == null)
			condition.put("sUserId", StringUtil.nullToBlank(user.getAccountId()));
		else
			condition.put("sUserId", StringUtil.nullToBlank(sUserId));

		condition.put("iMdev_type", StringUtil.nullToBlank(iMdev_type));

		List<Object> usageFee	= customerUsageManager.getCustomerUsageFee(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("usageFee",usageFee.get(0));
        mav.addObject("sEnd", usageFee.get(1));
        mav.addObject("tariff", usageFee.get(2));

       	return mav;
	}


	@RequestMapping(value = "/gadget/customer/getCustomerMainGadgetUsageFee")
	public ModelAndView getUsageMainGadgetFee(
			@RequestParam(value = "sViewType", required = true) String sViewType
			 , @RequestParam(value = "sUserId" ,required=false) String sUserId
			 , @RequestParam(value = "iMdev_type" ,required=true) String iMdev_type
			 , @RequestParam(value = "iStand" ,required=true) String iStand) {

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("sViewType", StringUtil.nullToBlank(sViewType));

		if("".equals(sUserId) || sUserId == null)
			condition.put("sUserId", StringUtil.nullToBlank(user.getAccountId()));
		else
			condition.put("sUserId", StringUtil.nullToBlank(sUserId));


		condition.put("iMdev_type", StringUtil.nullToBlank(iMdev_type));
		condition.put("iStand", StringUtil.nullToBlank(iStand));

		condition.put("sDate", CalendarUtil.getCurrentDate());


		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.EnergyMeter.toString()));
		List<Object> usageEMData	= customerUsageManager.getCustomerUsageMiniChart(condition);

		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.GasMeter.toString()));
		List<Object> usageGMData	= customerUsageManager.getCustomerUsageMiniChart(condition);

		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.WaterMeter.toString()));
		List<Object> usageWMData	= customerUsageManager.getCustomerUsageMiniChart(condition);

		/*
		String METER_TYPE	= StringUtil.nullToBlank(condition.get("METER_TYPE"));
		 */
		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.EnergyMeter.toString()));
		List<Object> EMco2Data	= customerUsageManager.getCustomerCO2Daily(condition);

		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.GasMeter.toString()));
		List<Object> GMco2Data	= customerUsageManager.getCustomerCO2Daily(condition);

		condition.put("METER_TYPE", StringUtil.nullToBlank(CommonConstants.MeterType.WaterMeter.toString()));
		List<Object> WMco2Data = customerUsageManager.getCustomerCO2Daily(condition);

		System.out.println("EM CO2  : " + (EMco2Data.size()==0?"em co2 size 0 ~":EMco2Data.get(0)));
		System.out.println("GM CO2  : " + (GMco2Data.size()==0?"em co2 size 0 ~":GMco2Data.get(0)));
		System.out.println("WM CO2  : " + (WMco2Data.size()==0?"em co2 size 0 ~":WMco2Data.get(0)));

       ModelAndView mav = new ModelAndView("jsonView");

       mav.addObject("usageEMData",usageEMData.get(0));
       mav.addObject("sEndData", usageEMData.get(1));
       mav.addObject("usageGMData",usageGMData.get(0));
       mav.addObject("usageWMData",usageWMData.get(0));

       mav.addObject("emCo2", (EMco2Data.size()==0?"":EMco2Data.get(0)));
       mav.addObject("gmCo2", (GMco2Data.size()==0?"":GMco2Data.get(0)));
       mav.addObject("wmCo2", (WMco2Data.size()==0?"":WMco2Data.get(0)));

       return mav;
	}

	@RequestMapping(value = "/gadget/customer/getCustomerTariff")
	public ModelAndView getTariff(@RequestParam(value = "sViewType", required = true) String sViewType
			, @RequestParam(value = "sUserId", required = false) String sUserId
			, @RequestParam(value = "iMdev_type", required = false) String iMdev_type
			, @RequestParam(value = "METER_TYPE" ,required=true) String METER_TYPE
			, @RequestParam(value = "yyyymmdd", required = false) String yyyymmdd){

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("sViewType", StringUtil.nullToBlank(sViewType));

		if("".equals(sUserId) || sUserId == null)
			condition.put("sUserId", StringUtil.nullToBlank(user.getAccountId()));
		else
			condition.put("sUserId", StringUtil.nullToBlank(sUserId));

		condition.put("iMdev_type", StringUtil.nullToBlank(iMdev_type));
		condition.put("METER_TYPE", StringUtil.nullToBlank(METER_TYPE));
		condition.put("supplierId", user.getSupplier().getId());
		condition.put("yyyymmdd", StringUtil.nullToBlank(yyyymmdd));

		Map<String, Object> tariffData	= customerUsageManager.getCustomerTariff(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("tariffData",tariffData);

       	return mav;
	}

	//로그인한 사용자 정보
	@RequestMapping(value = "/gadget/customer/tariff/getUser")
	public ModelAndView getUser() {
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

		AimirUser user = (AimirUser) instance.getUserFromSession();

		mav.addObject("userId", user.getAccountId());
		mav.addObject("supplierId", user.getSupplier().getId());
		return mav;
	}


	@RequestMapping(value = "/gadget/customer/getGoogleWeather")
	public ModelAndView getGoogleWeather(
			@RequestParam(value = "area", required = true) String area) {

		String userLocation = "";

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

		AimirUser user = (AimirUser) instance.getUserFromSession();

//		System.out.println("@@@@@@@@@@@@@@user.getLoginId() : " + user.getLoginId());
//		System.out.println("@@@@@@@@@@@@@@user.getAccountId() : " + user.getAccountId());

		Customer c = customerManager.getCustomersByLoginId(user.getLoginId());
		String lang = user.getSupplier().getLang().getCode_2letter();
		//System.out.println(" LANG : " + lang);

		Set<Contract> cont = c.getContracts();

		if(cont.toArray().length != 0){
			Object[] arry = cont.toArray();
			userLocation = ((Contract)arry[0]).getLocation().getName();

			area = userLocation;
		}

//		System.out.println(" @@@@@ userLocation : " + userLocation);
//		System.out.println(" @@@@@ area : " + area);


		if("".equals(StringUtil.nullToBlank(area))){
			System.out.println("지역(도시)정보가 없습니다.[City information does not exist]");

			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("ERROR", "지역(도시)정보가 없습니다.[City information does not exist]");

			return mav;
		}

		//[참고] 지역 찾기 : http://i.wund.com/ 검색후 도시이름을 찾는다.
		//[참고] http://www.google.com/ig/api?weather=도시이름&ie=인코딩&oe=인코딩&hl=언어로케이션   예)http://www.google.com/ig/api?weather=seoul&hl=en
		// www.google.com/ig/api?weather=new+york,ny
		String server 	= "http://www.google.com/ig/api?weather=";
		String param = area;
		String strXML 	= "";

		server = server + param+"&ie=utf-8&oe=utf-8&hl="+lang;
		//String encodeUrl = new String(URLCodec.encodeUrl(new BitSet(), server.getBytes()));
		try {
			URL url = new URL(server);

			strXML = FileCopyUtils.copyToString(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"));

		} catch (MalformedURLException mue) {
		    mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		DocumentBuilder _docBuilder = null;
		Document document = null;

//		System.out.println("@@@@@@@@@@@@@  xml @@@@@@@@@@@@@@@@");
//		System.out.println(strXML);
//		System.out.println("@@@@@@@@@@@@@  xml @@@@@@@@@@@@@@@@");

		try {
			_docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = _docBuilder.parse(new InputSource(new java.io.StringReader(strXML)));
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
		}

		document.setDocumentURI(server);

		Element elem	= document.getDocumentElement();
//		System.out.println("=====> " + elem.getNodeName());

		List<Map<String,Map<String, String>>> list = null;

		if(!"xml_api_reply".equals(elem.getNodeName())){
			System.out.println("구글 날씨를 가져오지 못했습니다.[Weather could not be imported]");

			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("ERROR", "구글 날씨를 가져오지 못했습니다.[Google Weather could not be imported]");

			return mav;
		}else{

			NodeList nodeList 	= document.getDocumentElement().getChildNodes(); // weather 하나 가져옴.
			list 				= new ArrayList<Map<String,Map<String, String>>>(); // 결과를 담을 배열 list생성
			Map<String, Map<String,String>> map 	= new HashMap<String, Map<String,String>>();
			Map<String,String> map2 					= null;

			Node row 		= null;
			NodeList child	= null;

			Node row2		= null;
			NodeList child2	= null;

//																										=> xml 노드...
			for (int i = 0; i < nodeList.getLength(); i++) { 	//					  					=> weather
				row 	= nodeList.item(i);
				child 	= row.getChildNodes();

				if(child.getLength() == 1){
					System.out.println("잘못된 지역코드입니다.[Region code is incorrect.]");

					ModelAndView mav = new ModelAndView("jsonView");
					mav.addObject("ERROR", "잘못된 지역코드입니다.[Region code is incorrect.]");

					return mav;
				}

				for(int k=0; k<child.getLength(); k++){			//										=> forecast_information .....etc
					row2 	= child.item(k);
					child2 	= row2.getChildNodes();

//					System.out.println("111 " + row2.getNodeName());

					if("forecast_conditions".equals(row2.getNodeName())) continue;

					map2 	= new HashMap<String,String>();

					for (int a = 0; a < child2.getLength(); a++) {	//									=> city .... etc
						Node nodeList2 = child2.item(a);

						map2.put(nodeList2.getNodeName(), nodeList2.getAttributes().getNamedItem("data").getNodeValue());
					}

					map.put(row2.getNodeName(), map2);
				}

				list.add(map);

				System.out.println("@@@@@@@@@ map @@@@@@@");
				System.out.println(map);
			}
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("xmlData", list);

		return mav;
	}

	@RequestMapping(value = "/gadget/customer/getContractInfo")
    public ModelAndView getContractInfo(
            @RequestParam(value = "contractId", required = true) String contractId) {

	    Integer contractIdInt = ("-".equals(contractId)) || ("null".equals(contractId)) ? -1 : Integer.parseInt(contractId);

	    Contract contract = contractManager.getContract(contractIdInt);

	    Meter meter = new Meter();
	    if(contract.getMeterId() != null) {
	    	meter = meterManager.getMeter(contract.getMeterId());
	    }
	    ModelAndView mav = new ModelAndView("jsonView");
	    mav.addObject("mdsId", meter != null && meter.getMdsId() != null ? meter.getMdsId() : "-");
	    mav.addObject("address", meter != null && meter.getAddress() != null ? meter.getAddress() : "-");
        return mav;
	}
}


/*
$googleWeatherCity['kor'] = array("강릉", "경주", "고양", "광명", "광주", "구미", "군산", "군포", "김해", "대구", "대전", "동해", "목포", "부산", "부천", "서귀포", "서울", "성남", "송탄", "수원", "시흥", "안산", "안양", "용인", "울릉도", "울산", "원주", "의정부", "익산", "인천", "일산", "전주", "제주", "진주", "철원", "청주", "춘천", "파주", "평택", "포항", "흑산도", "충주", "상주", "창원", "천안", "여수", "안동", "양산", "태안", "진해", "오산", "순천", "당진", "마산", "삼척", "거제");
$googleWeatherCity['eng'] = array("gangneung", "gyeongju", "goyang", "gwangmyeong", "gwangju", "gumi", "gunsan", "gunpo", "gimhae", "daegu", "daejeon", "donghae", "mokpo", "busan", "bucheon", "seogwipo", "seoul", "seongnam", "songtan", "suwon", "siheung", "ansan", "anyang", "yongin", "ulleung-do", "ulsan", "wonju", "uijeongbu", "iksan", "incheon", "ilsan", "jeonju", "jeju", "jinju", "cheorwon", "cheongju", "chuncheon", "paju", "pyeongtaek", "pohang", "heuksan-do", "chungju", "sangju", "changwon", "cheonan", "yeosu", "andong", "yangsan", "taean", "jinhae", "osan", "suncheon", "dangjin", "masan", "samchok", "kuje");




[xml_api_reply][weather][problem_cause]
오류 발생, problem_cause의 data attr에 오류 내용이 표시됩니다.


[xml_api_reply][weather][forecast_information][postal_code]

도시 이름입니다. 요청했던 도시 이름이 표시됩니다. 자세한 도시명을 받아오려면 city 를 읽으면 됩니다.


[xml_api_reply][weather][forecast_information][current_date_time]

최근 업데이트된 날짜입니다. GMT+0 기준입니다.




[xml_api_reply][weather][current_conditions][condition]

현재 날씨 상태입니다. 맑음, 비 등등이 한글로 표시됩니다.


[xml_api_reply][weather][current_conditions][temp_c]

현재 기온을 ℃로 표시합니다.


[xml_api_reply][weather][current_conditions][humidity]

현재 습도를 표시합니다. 습도: 10% 형식으로 표시됩니다.


[xml_api_reply][weather][current_conditions][wind_condition]

현재 바람를 표시합니다. 바람: 폭풍, 8 km/h 형식으로 표시됩니다.





[$i일 후의 날씨 정보] (최대 4일까지, 오늘도 포함)

[xml_api_reply][weather][forecast_conditions][$i][day_of_week]

요일을 표시합니다.


[xml_api_reply][weather][forecast_conditions][$i][low]

최저기온을 표시합니다.


[xml_api_reply][weather][forecast_conditions][$i][high]

최고기온을 표시합니다.



[xml_api_reply][weather][forecast_conditions][$i][condition]

날씨 상태를 한글로 표시합니다.
*/