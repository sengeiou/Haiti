package com.aimir.bo.device;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.NameOfModel;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MbusSlaveIOModuleDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.command.conf.SM110Meta;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.ModemConfig;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.vo.DeviceModelConfigVo;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.device.MbusSlaveIOModuleManager;
import com.aimir.service.device.MeterInstallImgManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.MeterMaxMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

import net.sf.json.JSONArray;

@Controller
public class MeterController { 

    private static Log log = LogFactory.getLog(MeterController.class);

    @Autowired
    MeterManager meterManager;

	@Autowired
	ModemManager modemManager;
	
    @Autowired
    MeterInstallImgManager meterInstallImgManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    DeviceModelManager deviceModelManager;

    @Autowired
    DeviceVendorManager deviceVendorManager;

    @Autowired
    AimirFilePath aimirFilePath;

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    AsyncCommandLogManager asyncCommandLogManager;

    @Autowired 
	LocationDao locationDao;
    
    @Autowired
    SearchMeteringDataManager searchMeteringDataManager;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    EventAlertLogDao eventAlertLogDao;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    MbusSlaveIOModuleManager mbusSlaveIOModuleManager;
    
    @Autowired
    MbusSlaveIOModuleDao mbusSlaveIOModuleDao;
    
    @RequestMapping(value="/gadget/device/meterMiniGadget")
    public ModelAndView getMiniChart(@RequestParam(value="meterChart" ,required=false) String meterChart) {
        ModelAndView mav = new ModelAndView("gadget/device/meterMiniGadget");
        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(user.getOperator(new Operator()).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                //e.printStackTrace();
                log.error("user is not operator type");
            }
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/meterMaxGadgetInfo")
    public ModelAndView getMaxGadgetInfo() {
        return new ModelAndView("gadget/device/meterMaxGadgetInfo");
    }

    @RequestMapping(value="/gadget/device/meterMaxGadgetEmptyMeter")
    public ModelAndView getMiniChart() {
        return new ModelAndView("gadget/device/meterMaxGadgetEmptyMeter");
    }

    // Sub-Page Schedule Tab
    @RequestMapping(value = "/gadget/device/meterMaxGadgetScheduleTab")
    public ModelAndView getMaxGadgetScheduleTab() {
        return new ModelAndView("gadget/device/meterMaxGadgetScheduleTab");
    }



    // MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/gadget/device/getMeterMiniChart")
    public ModelAndView getMeterMiniChart(@RequestParam(value="meterChart" ,required=false) String meterChart,
            @RequestParam(value="supplierId", required=false) String supplierId,
            @RequestParam(value="fmtmessagecommalert", required=false) String fmtmessagecommalert,
            @RequestParam(value="gridType", required=false) String gridType,
            @RequestParam(value="permitLocationId", required=false) String permitLocationId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        if (meterChart == null)
            meterChart = "mc";

        condition.put("meterChart", meterChart);
        condition.put("supplierId", supplierId);
        condition.put("fmtmessagecommalert", fmtmessagecommalert);
        condition.put("gridType", gridType);
        condition.put("permitLocationId", permitLocationId);

        List<Object> miniChart = meterManager.getMiniChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<HashMap> chartData= (List) miniChart.get(0);
        List<Object> chartSeries= (List<Object>) miniChart.get(1);

        int totalCnt = chartData.size();
        
        //그리드 데이타
        mav.addObject("chartData",chartData);

        //data tot cnt
        mav.addObject("totalCnt",totalCnt);
        mav.addObject("chartSeries",chartSeries);
        mav.addObject("cell",JSONArray.fromObject(miniChart.get(0)));

        return mav;
    }

    // 제조사 목록
    @SuppressWarnings("unused")
    @RequestMapping(value = "/gadget/device/meterStatus")
    public ModelAndView getmeterStatus(String meterType) {
        // reference Data 인자 ;
    	 ModelAndView mav = new ModelAndView("jsonView");

        List<Code> meterStatus = codeManager.getChildCodes(Code.METER_STATUS);
        String str = CommonConstants.ChangeMeterTypeName.WM.getCode();
        if (CommonConstants.ChangeMeterTypeName.WM.getCode().equals(meterType)) {
        	Code meterwaterStatus =  codeManager.getCodeByName("WaterMeterStatus");
        	meterStatus.addAll(codeManager.getChildCodes(meterwaterStatus.getCode()));
        } else if (CommonConstants.ChangeMeterTypeName.GM.getCode().equals(meterType)) {
        	Code metergasStatus =  codeManager.getCodeByName("GasMeterStatus");
        	meterStatus.addAll(codeManager.getChildCodes(metergasStatus.getCode()));
        }

        mav.addObject("meterStatus", meterStatus);

        return mav;
    }
    
/*    // 제조사 목록
    @RequestMapping(value = "/gadget/device/alarmStatus")
    public ModelAndView getalarmStatus(String meterType) {
        // reference Data 인자 ;
    	 ModelAndView mav = new ModelAndView("jsonView");
    	
    	List<Code> alarmStatus = new ArrayList<Code>();
    	 if(CommonConstants.ChangeMeterTypeName.WM.getCode().equals(meterType)){
    		 alarmStatus = codeManager.getChildCodes("1.3.1.2.2");
    	 }else if(CommonConstants.ChangeMeterTypeName.GM.getCode().equals(meterType)){
    		 alarmStatus = codeManager.getChildCodes("1.3.1.3.2");
    	}

        mav.addObject("alarmStatus", alarmStatus);

        return mav;
    }*/
    
    // MeterMaxGadget 초기 검색조건 조회
    @RequestMapping(value = "/gadget/device/meterMaxGadget")
    public ModelAndView getMeterMaxGadgetCondition() {
        ModelAndView mav = new ModelAndView("gadget/device/meterMaxGadget");
        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(user.getOperator(new Operator()).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.toString(), e);
            }
        }

        List<Code> meterType = codeManager.getChildCodes(Code.METER_TYPE);
        List<Code> meterStatus = codeManager.getChildCodes(Code.METER_STATUS);
        List<Code> meterHwVer = codeManager.getChildCodes(Code.METER_HW_VERSION);
        List<Code> meterSwVer = codeManager.getChildCodes(Code.METER_SW_VERSION);
        Properties prop = new Properties();
        try {
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

        Map<String, Object> serchDate = meterManager.getMeterSearchCondition();

        mav.addObject("meterType", meterType);
        mav.addObject("meterStatus", meterStatus);
        mav.addObject("meterHwVer", meterHwVer);
        mav.addObject("meterSwVer", meterSwVer);

        mav.addObject("installMinDate", serchDate.get("installMinDate"));
        mav.addObject("installMaxDate", serchDate.get("installMaxDate"));
        mav.addObject("yesterday", serchDate.get("yesterday"));
        mav.addObject("today", serchDate.get("today"));

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // 수정권한(command = true)
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("cmdAuth", authMap.get("command"));  // 수정권한(command = true)

        mav.addObject("dcuHandshakingTimeout"	, prop.getProperty("dcu.timeout.handshaking")); 
        mav.addObject("dcuDayTimeout"			, prop.getProperty("dcu.timeout.day"));
        mav.addObject("modemHandshakingTimeout"	, prop.getProperty("modem.timeout.handshaking")); 
        mav.addObject("modemDayTimeout"			, prop.getProperty("modem.timeout.day"));
        mav.addObject("meterHandshakingTimeout"	, prop.getProperty("meter.timeout.handshaking"));
        mav.addObject("meterDayTimeout"			, prop.getProperty("meter.timeout.day"));

        return mav;
    }

    @RequestMapping(value = "gadget/device/getMeterSearchChart")
    public ModelAndView getMeterSearchChart(@RequestParam("sMeterType") String sMeterType,
            @RequestParam("sMdsId") String sMdsId,
            @RequestParam("sStatus") String sStatus,
            @RequestParam("sMeterGroup") String sMeterGroup,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sLocationId") String sLocationId,
            @RequestParam("sConsumLocationId") String sConsumLocationId,
            @RequestParam(value = "sPermitLocationId", required = false) String sPermitLocationId,
            @RequestParam("sVendor") String sVendor,
            @RequestParam("sModel") String sModel,
            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,
            @RequestParam("sModemYN") String sModemYN,
            @RequestParam("sCustomerYN") String sCustomerYN,
            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            @RequestParam("sCommState") String sCommState,
            @RequestParam(value = "sCustomerId", required = false) String sCustomerId,
            @RequestParam(value = "sCustomerName", required = false) String sCustomerName,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("sMeterAddress") String sMeterAddress,
            @RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="limit", required=false) Integer limit,
            @RequestParam("sGs1") String sGs1,
            @RequestParam("sDeviceSerial") String sDeviceSerial){

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

        condition.put("sCommState", sCommState);
        condition.put("sCustomerId", sCustomerId);
        condition.put("sCustomerName", sCustomerName);

        condition.put("supplierId", supplierId);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("sGs1", sGs1);
        condition.put("sDeviceSerial", sDeviceSerial);

        List<Object> meterSearchChart = meterManager.getMeterSearchChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        if (page != null) {
            mav.addObject("gridData", meterSearchChart.get(0));
            mav.addObject("totalCnt", meterSearchChart.get(1));
        } else {
            mav.addObject("chartData", meterSearchChart.get(0));
        }

        return mav;
    }

    @RequestMapping(value = "gadget/device/getMeterSearchGrid")
    public ModelAndView getMeterSearchGrid(@RequestParam("sMeterType") String sMeterType,
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
            @RequestParam(value = "curPage", required = false) String curPage,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            @RequestParam("sGroupOndemandYN") String sGroupOndemandYN,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value = "sCustomerId", required = false) String customerId,
            @RequestParam(value = "sCustomerName", required = false) String customerName,
            @RequestParam(value = "sPermitLocationId", required = false) String sPermitLocationId,
            @RequestParam("sMeterAddress") String sMeterAddress,
            @RequestParam("sHwVersion") String sHwVersion,
            @RequestParam("sFwVersion") String sFwVersion,
            @RequestParam("sGs1") String sGs1,
            @RequestParam("sType") String sType,
            @RequestParam("fwGadget") String fwGadget,
            @RequestParam(value = "sMbusSMYN", required = false) String sMbusSMYN,
            @RequestParam(value = "sDeviceSerial", required = false) String sDeviceSerial,
            @RequestParam(value = "sNotDeleted", required = false) String sNotDeleted
            ) {
        String limit = null;
        if (curPage == null) {
            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
            curPage = request.getParameter("page");
            limit = request.getParameter("limit");
        }

        /*// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
		if (sLocationId.equals("")) {
			List<Location> locationRoot = locationDao.getRootLocationList();
			sLocationId = Integer.toString(locationRoot.get(0).getId());
		}*/
        
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

        condition.put("curPage", curPage);
        condition.put("limit", limit);
        condition.put("sOrder", sOrder);
        condition.put("sCommState", sCommState);
        condition.put("sGroupOndemandYN", sGroupOndemandYN);

        condition.put("supplierId", supplierId);

        condition.put("sCustomerId", customerId);
        condition.put("sCustomerName", customerName);
        
        condition.put("sHwVersion", sHwVersion);
        condition.put("sFwVersion", sFwVersion);
        condition.put("sGs1", sGs1);
        condition.put("sType", sType);
        condition.put("fwGadget", fwGadget);
        condition.put("sMbusSMYN", sMbusSMYN);
        condition.put("sDeviceSerial", sDeviceSerial);
        condition.put("sNotDeleted", sNotDeleted);
        
        List<Object> meterSearchGrid = meterManager.getMeterSearchGrid(condition);
        
        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String mdPattern = supplier.getMd().getPattern();
        Integer dot = mdPattern.indexOf(".");

        if(dot != -1)
        	mdPattern = mdPattern.substring(0,dot);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt", meterSearchGrid.get(0));
        mav.addObject("gridData", meterSearchGrid.get(1));
        mav.addObject("allGridData", meterSearchGrid.get(2));
        mav.addObject("mdNumberPattern", mdPattern.replace("#","0"));

        return mav;
    }

    @RequestMapping(value = "gadget/device/getSimpleMeterSearchGrid")
    public ModelAndView getSimpleMeterSearchGrid(@RequestParam("sMeterType") String sMeterType,
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
                                           @RequestParam(value = "curPage", required = false) String curPage,
                                           @RequestParam("sOrder") String sOrder,
                                           @RequestParam("sCommState") String sCommState,
                                           @RequestParam("sGroupOndemandYN") String sGroupOndemandYN,
                                           @RequestParam("supplierId") String supplierId,
                                           @RequestParam(value = "sCustomerId", required = false) String customerId,
                                           @RequestParam(value = "sCustomerName", required = false) String customerName,
                                           @RequestParam(value = "sPermitLocationId", required = false) String sPermitLocationId,
                                           @RequestParam("sMeterAddress") String sMeterAddress,
                                           @RequestParam("sHwVersion") String sHwVersion,
                                           @RequestParam("sFwVersion") String sFwVersion) {
        String limit = null;
        if (curPage == null) {
            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
            curPage = request.getParameter("page");
            limit = request.getParameter("limit");
        }

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

        condition.put("curPage", curPage);
        condition.put("limit", limit);
        condition.put("sOrder", sOrder);
        condition.put("sCommState", sCommState);
        condition.put("sGroupOndemandYN", sGroupOndemandYN);

        condition.put("supplierId", supplierId);

        condition.put("sCustomerId", customerId);
        condition.put("sCustomerName", customerName);

        condition.put("sHwVersion", sHwVersion);
        condition.put("sFwVersion", sFwVersion);

        List<Object> meterSearchGrid = meterManager.getSimpleMeterSearchGrid(condition);

        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String mdPattern = supplier.getMd().getPattern();
        Integer dot = mdPattern.indexOf(".");

        if(dot != -1)
            mdPattern = mdPattern.substring(0,dot);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt", meterSearchGrid.get(0));
        mav.addObject("gridData", meterSearchGrid.get(1));
        mav.addObject("mdNumberPattern", mdPattern.replace("#","0"));

        return mav;
    }

    @RequestMapping(value="gadget/device/getMeterLogChart")
    public ModelAndView getMeterLogChart(@RequestParam("meterMds")   String meterMds
            , @RequestParam("startDate")  String startDate
            , @RequestParam("endDate")    String endDate
            , @RequestParam("supplierId") String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("meterMds",       meterMds);
        condition.put("startDate",      startDate);
        condition.put("endDate",        endDate);
        condition.put("supplierId",     supplierId);

        List<Object> meterLogChart = meterManager.getMeterLogChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("chartData", meterLogChart.get(0));

        return mav;
    }


    @RequestMapping(value="gadget/device/getMeterLogGrid")
    public ModelAndView getMeterLogGrid(@RequestParam("meterMds")   String meterMds
            , @RequestParam("startDate")  String startDate
            , @RequestParam("endDate")    String endDate
            , @RequestParam("logType")    String logType
            , @RequestParam("curPage")    String curPage) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("meterMds",       meterMds);
        condition.put("startDate",      startDate);
        condition.put("endDate",        endDate);
        condition.put("logType",        logType);
        condition.put("curPage",        curPage);

        List<Object> meterLogGrid = meterManager.getMeterLogGrid(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt", meterLogGrid.get(0));
        mav.addObject("gridData", meterLogGrid.get(1));

        return mav;
    }


    @RequestMapping(value="/gadget/device/getMeter")
    public ModelAndView getMeter(@RequestParam("meterId") Integer meterId) {

        Meter meter = meterManager.getMeter(meterId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("meter", meter);

        return mav;
    }


    @RequestMapping(value="/gadget/device/getMeterInfo")
    public ModelAndView getMeterInfo(@RequestParam("meterId") Integer meterId) {

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();   
        
        String country = user.getSupplier().getCountry().getCode_2letter();
        String lang    = user.getSupplier().getLang().getCode_2letter();
        
        ModelAndView mav = new ModelAndView("/gadget/device/meterMaxGadgetInfo");
        
        if (meterId != null) {

	        Meter meter = meterManager.getMeter(meterId);
	
	        meter.setInstallDate(meter.getInstallDate());
	        // 로케일 정보에 맞게 날짜 포멧을 변경한다.
	        meter.setInstallDateHidden(TimeLocaleUtil.getLocaleDate(meter.getInstallDate(),lang,country));
	
	        // 개행문자 변환
	        if (meter.getAddress() != null) {
	            meter.setAddress(meter.getAddress().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "\\\\n"));
	        }

	        mav.addObject("meter", meter);
	        
	        if (meter != null) {
	            
				if (meter.getMeterType() != null)
					mav.addObject("meterType", meter.getMeterType().getName());
	
				if (meter.getModel() != null) {
					mav.addObject("model", meter.getModel().getName());
				}
			}
        }
        return mav;  
    }


    // ModemMax > Modem에 등록된 Meter목록 조회
    @SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/device/getMeterListByModem")
    public ModelAndView getMeterListByModem(@RequestParam("modemId") Integer modemId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("modemId",       modemId);

        List<Object> meterList = meterManager.getMeterListByModem(condition);
        
        List<Object> gridData2 = (List<Object>) meterList.get(0);
        
        //tot Cnt
        int totalCnt =  gridData2.size();

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData2", gridData2);
        mav.addObject("gridData", meterList.get(0));
        
        mav.addObject("totalCnt", totalCnt);

        return mav;
    }

    // ModemMax > Modem에 등록된 Meter삭제
    @RequestMapping(value="/gadget/device/unsetModemId")
    public ModelAndView unsetModemId(@RequestParam("mdsId") String[] mdsId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdsId",         mdsId);

        Boolean result = meterManager.unsetModemId(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", result);

        return mav;
    }

    //
    @RequestMapping(value="/gadget/device/getMeterListForContract")
    public ModelAndView getMeterListForContract(@RequestParam("mdsId") String mdsId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdsId",       mdsId);

        List<Object> meterList = meterManager.getMeterListForContract(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData", meterList);

        return mav;
    }
    
    //KYHGH CustomerMax Meter조회 
	@RequestMapping(value = "/gadget/device/getMeterListForContractExtJs")
	public ModelAndView getMeterListContractExtJs(
			@RequestParam("mdsId") String mdsId,
			@RequestParam("query") String query)
	{

		Map<String, Object> condition = new HashMap<String, Object>();
		if (query == null || query.length() == 0)
		{
			condition.put("mdsId", mdsId);
		} else
		{
			condition.put("mdsId", query);
		}

		/*
		 * if(query == null || query.equals("")){ condition.put("mdsId", mdsId);
		 * }else{ if(!field1.equals("")){ if(field1.equals("MDSID")){
		 * condition.put("mdsId", query); } if(field1.equals("ADDRESS")){
		 * condition.put("address", query); } } if(!field2.equals("")){
		 * if(field2.equals("MDSID")){ condition.put("mdsId", query); }
		 * if(field2.equals("ADDRESS")){ condition.put("address", query); } } }
		 */
		List<Object> meterList = null; // meterManager.getMeterListContractExtJs(condition);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("gridData", meterList);

		return mav;
	}

    // ModemMax > Modem에  미터등록
    @RequestMapping(value="/gadget/device/setModemId")
    public ModelAndView setModemId(
    		@RequestParam("mdsId") String[] mdsId
            , @RequestParam("modemId") Integer modemId
            
    		)
    {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdsId",         mdsId);
        condition.put("modemId",       modemId);

        Boolean result = meterManager.setModemId(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", result);

        return mav;
    }

    // ModemMax > Modem에 등록되지 않은 Meter목록 조회
    @SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/device/getMeterListByNotModem")
    public ModelAndView getMeterListByNotModem(@RequestParam("supplierId") Integer supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId",       supplierId);

        List<Object> meterList = meterManager.getMeterListByNotModem(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> gridData = (List<Object>) meterList.get(0);

        int totalCnt = gridData.size();

        mav.addObject("gridData", gridData);
        mav.addObject("totalCnt", totalCnt);

        return mav;
    }

    // 장비등록 > 개별등록 > 미터  - 미터번호 중복확인
    @RequestMapping(value="/gadget/device/isMeterDuplicateByMdsId")
    public ModelAndView isMeterDuplicate(@RequestParam("mdsId") String mdsId) {

        Meter meter = meterManager.getMeter(mdsId);
        String meterStatus = null;
        if(meter != null) {
        	meterStatus = meter.getMeterStatus() == null ? null : meter.getMeterStatus().getName();
        }
        
        ModelAndView mav = new ModelAndView("jsonView");
        if (meter == null) {
            mav.addObject("result", "false");
        } else if(MeterStatus.Delete.name().equals(meterStatus)) {
        	mav.addObject("result", "delete");
        } else
            mav.addObject("result", "true");

        return mav;
    }

    @RequestMapping(value="gadget/device/insertMeter")
    public ModelAndView insertMeter(@RequestParam("meterType"  )  String meterType
            , @RequestParam("mdsId"      )  String mdsId
            , @RequestParam("modemSerial")  String modemSerial

            , @RequestParam("vendor"     )  String vendor
            , @RequestParam("model"      )  String model
            , @RequestParam("port"       )  String port

            , @RequestParam("loc"        )  String loc
            , @RequestParam("locDetail"  )  String locDetail

            , @RequestParam("supplierId ")  String supplierId){

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("meterType",       meterType);
        condition.put("mdsId",           mdsId);
        condition.put("modemSerial",     modemSerial);

        condition.put("vendor",          vendor);
        condition.put("model",           model);
        condition.put("port",            port);

        condition.put("loc",             loc);
        condition.put("locDetail",       locDetail);

        condition.put("supplierId",       supplierId);

        meterManager.insertMeterByMap(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        return mav;
    }

    @RequestMapping(value="gadget/device/insertEnergyMeter")
    public ModelAndView insertEnergyMeter(@ModelAttribute("meterInfoFormEdit") EnergyMeter energyMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        energyMeter.setSupplier(supplier);
        result = meterManager.insertEnergyMeter(energyMeter);
        
        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }

    @RequestMapping(value="gadget/device/insertWaterMeter")
    public ModelAndView insertWaterMeter(@ModelAttribute("meterInfoFormEdit") WaterMeter waterMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        waterMeter.setSupplier(supplier);
        result = meterManager.insertWaterMeter(waterMeter);

        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }

    @RequestMapping(value="gadget/device/insertGasMeter")
    public ModelAndView insertGasMeter(@ModelAttribute("meterInfoFormEdit") GasMeter gasMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        gasMeter.setSupplier(supplier);
        result = meterManager.insertGasMeter(gasMeter);
        
        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }

    @RequestMapping(value="gadget/device/insertHeatMeter")
    public ModelAndView insertHeatMeter(@ModelAttribute("meterInfoFormEdit") HeatMeter heatMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        heatMeter.setSupplier(supplier);
        result = meterManager.insertHeatMeter(heatMeter);
        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }

    @RequestMapping(value="gadget/device/insertSolarPowerMeter")
    public ModelAndView insertSolarPowerMeter(@ModelAttribute("meterInfoFormEdit") SolarPowerMeter solarPowerMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        solarPowerMeter.setSupplier(supplier);
        result = meterManager.insertSolarPowerMeter(solarPowerMeter);
        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }

    @RequestMapping(value="gadget/device/insertVolumeCorrector")
    public ModelAndView insertVolumeCorrector(@ModelAttribute("meterInfoFormEdit") VolumeCorrector volumeCorrector){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        volumeCorrector.setSupplier(supplier);
        result = meterManager.insertVolumeCorrector(volumeCorrector);
        String status = "failed";
        
        if (result != null && result.get("id") != null) {
        	mav.addObject("id", result.get("id"));
        	status = "success";
        }
        
        mav.addObject("status", status);
        
        return mav;
    }
    
    // 장비등록 > 개별등록 > 미터  - 미터번호 중복확인
    @SuppressWarnings("unused")
    @RequestMapping(value="/gadget/device/updateMeterThreshold")
    public ModelAndView updateMeterThreshold(@RequestParam("meterId") Integer meterId, @RequestParam("usageThreshold") Double usageThreshold) {

        Map<String, Object> result = new HashMap<String, Object>();
        Meter meter = meterManager.getMeter(meterId);
        meter.setUsageThreshold(usageThreshold);

        ModelAndView mav = new ModelAndView("jsonView");
        if (meter == null)
            mav.addObject("result", "false");
        else
            mav.addObject("result", "true");
        
        result = meterManager.updateMeter(meter);

        mav.addObject("id", result.get("id"));
        mav.addObject("status", "success");

        return mav;
    }
    
    @RequestMapping(value="gadget/device/updateMeter")
    public ModelAndView updateMeter(@ModelAttribute("meterInfoFormEdit") Meter meter){
    	
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
 
        result = meterManager.updateMeter(meter);

        mav.addObject("id", result.get("id"));

        return mav;
    }
    
    @RequestMapping(value="gadget/device/updateWaterMeterInfo")
    public ModelAndView updateWaterMeterInfo(@ModelAttribute("meterInfoFormEdit") WaterMeter watermeter){
    	
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
 
        result = meterManager.updateWaterMeterInfo(watermeter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    @RequestMapping(value="gadget/device/updateMeterLoc")
    public ModelAndView updateMeterLoc(@ModelAttribute("meterLocForm") Meter meter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateMeterLoc(meter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    @RequestMapping(value="gadget/device/updateMeterAddress")
    public ModelAndView updateMeterAddress(@ModelAttribute("meterLocForm") Meter meter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateMeterAddress(meter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    // EnergyMeter Update
    @RequestMapping(value="gadget/device/updateEnergyMeter")
    public ModelAndView updateEnergyMeter(@ModelAttribute("meterInstallFormEdit") EnergyMeter energyMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        result = meterManager.updateEnergyMeter(energyMeter);
        
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // WaterMeter Update
    @RequestMapping(value="gadget/device/updateWaterMeter")
    public ModelAndView updateWaterMeter(@ModelAttribute("meterInstallFormEdit") WaterMeter waterMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
 
        result = meterManager.updateWaterMeter(waterMeter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    // GasMeter Update
    @RequestMapping(value="gadget/device/updateGasMeter")
    public ModelAndView updateGasMeter(@ModelAttribute("meterInstallFormEdit") GasMeter gasMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateGasMeter(gasMeter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    // HeatMeter Update
    @RequestMapping(value="gadget/device/updateHeatMeter")
    public ModelAndView updateHeatMeter(@ModelAttribute("meterInstallFormEdit") HeatMeter heatMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateHeatMeter(heatMeter);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    // SolarPowerMeter Update
    @RequestMapping(value="gadget/device/updateSolarPowerMeter")
    public ModelAndView updateSolarPowerMeter(@ModelAttribute("meterInstallFormEdit") SolarPowerMeter solarPowerMeter){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateSolarPowerMeter(solarPowerMeter);

        mav.addObject("id", result.get("id"));

        return mav;
    }


    // VolumeCorrector Update
    @RequestMapping(value="gadget/device/updateVolumeCorrector")
    public ModelAndView updateVolumeCorrector(@ModelAttribute("meterInstallFormEdit") VolumeCorrector volumeCorrector){

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterManager.updateVolumeCorrector(volumeCorrector);

        mav.addObject("id", result.get("id"));

        return mav;
    }

    // 미터 삭제
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="gadget/device/delteMeter")
    public ModelAndView deleteMeter(@ModelAttribute("meterInfoFormEdit") Meter meter
            , HttpServletRequest request) throws IOException {

        ModelAndView mav = new ModelAndView("jsonView");

        // 설치 이미지 삭제
        List <Object> rtnList = meterInstallImgManager.deleteMeterInstallAllImg(meter.getId());
        int rtnListSize = rtnList.size();
        String contextRoot  = new HttpServletRequestWrapper(request).getRealPath("/");

        for (int i = 0; i < rtnListSize; i++) {
            HashMap saveFile = (HashMap) rtnList.get(i);
            String saveFileName = saveFile.get("saveFileName").toString();
            FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(contextRoot, saveFileName));
        }
       
        int id = meterManager.deleteMeterStatus(meter);
        mav.addObject("id", id);
       
//        mav.addObject("id", meterManager.deleteMeter(meter));

        return mav;
    }

    // 미터 유형별 조회
    @SuppressWarnings("unused")
    @RequestMapping(value="/gadget/device/getMeterByType")
    public ModelAndView getMeterByType(@RequestParam("meterId")     Integer meterId
            , @RequestParam("meterType")   String meterType) {

    	ModelAndView mav = null;
        Map<String, Object> condition = new HashMap<String, Object>();

        if (meterId != null) {
	        meterType = meterManager.getMeter(meterId).getMeterType().getName().toString();
	        condition.put("meterId",        meterId);
	        condition.put("meterType",      meterType);

	        Object obj = meterManager.getMeterByType(condition);

	        Meter meter = null;

	        if (meterType.equals(MeterType.EnergyMeter.toString())){
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetEnergyMeter");
	            EnergyMeter em = (EnergyMeter) obj;
	            DecimalFormat df = DecimalUtil.getIntegerDecimalFormat(em.getSupplier().getMd());
	            if(em.getCt() != null)
	            	mav.addObject("CT",df.format(em.getCt()));
	            if(em.getPulseConstant() != null){
	            	mav.addObject("KE",df.format(em.getPulseConstant()));
	            }
	        }

	        if (meterType.equals(MeterType.WaterMeter.toString()))
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetWaterMeter");

	        if (meterType.equals(MeterType.GasMeter.toString()))
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetGasMeter");

	        if (meterType.equals(MeterType.HeatMeter.toString()))
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetHeatMeter");

	        if (meterType.equals(MeterType.SolarPowerMeter.toString()))
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetSolarPowerMeter");

	        if (meterType.equals(MeterType.VolumeCorrector.toString()))
	            mav = new ModelAndView("/gadget/device/meterMaxGadgetVolumeCorrector");

	        mav.addObject("meter", obj);
	       	meter = (Meter)obj;
	       	
	        if (meter != null) {

				if (meter.getModel() != null) {
					mav.addObject("model", meter.getModel().getName());

					if (CommonConstants.NameOfModel.SENSUS_220C.equalsOfName(meter
							.getModel().getName())) {

						WaterMeter SENSUS_220C = (WaterMeter) meter;

						if (SENSUS_220C.getAlarmStatus() != null) {

							Integer alarmStatus = SENSUS_220C.getAlarmStatus();
							Code code = codeManager.getCode(alarmStatus);
							String  alarmStatusName = code != null ? code.getName() : null;

							// int 값을 code값으로 변환한다.
							String nameOfaStatus = CommonConstants
									.getWaterMeterAlarmStatusCodesNames(alarmStatus);

							mav.addObject("alarmStatus", nameOfaStatus);
						}

					} else if (NameOfModel.SM110.equalsOfName(meter.getModel()
							.getName())) {

						EnergyMeter energyMeter = (EnergyMeter) meter;

						if (energyMeter.getSwitchStatus() != null)
							mav.addObject("switchStatus", SM110Meta.getSwitchStatus(String.valueOf(energyMeter.getSwitchStatus().getCode())));
						else
							mav.addObject("switchStatus", "-");
					}
				}
			}
        }
        return mav;
    }

    @RequestMapping(value="/gadget/device/saveMeterInstallImgFile")
    public ModelAndView saveMeterInstallImgFile(HttpServletRequest request, HttpServletResponse response)throws ServletRequestBindingException, IOException{

        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");

        String filename = multipartFile.getOriginalFilename();
        if (filename == null || "".equals(filename))
            return null;

        String extension = filename.substring(filename.indexOf("."));

        String saveBasePathName = contextRoot+aimirFilePath.getPhotoBasePath();
        String saveTempPathName = contextRoot+aimirFilePath.getPhotoTempPath();
        String savePathName     = contextRoot+aimirFilePath.getMeterPath();

        if (!FileUploadHelper.exists(saveBasePathName)) {
            File savedir = new File(saveBasePathName);
            savedir.mkdir();
        }

        if (!FileUploadHelper.exists(saveTempPathName)) {
            File savedir = new File(saveTempPathName);
            savedir.mkdir();
        }

        if (!FileUploadHelper.exists(savePathName)) {
            File savedir = new File(savePathName);
            savedir.mkdir();
        }

        File uFile =new File(FileUploadHelper.makePath(saveTempPathName, filename));

        multipartFile.transferTo(uFile);

        Date date = new Date();
        String savefilename = date.getTime()+extension;

        // File Copy & Temp File Delete
        FileUploadHelper.copy(FileUploadHelper.makePath(saveTempPathName, filename), FileUploadHelper.makePath(savePathName, savefilename));
        FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(saveTempPathName, filename));

        String viewfilename = aimirFilePath.getMeterPath() + "/" +savefilename;

        ModelAndView mav = new ModelAndView("gadget/device/meterMaxInstallImg");
        mav.addObject("savefilename", viewfilename);

        return mav;
    }

    @RequestMapping(value="/gadget/device/insertMeterInstallImg")
    public ModelAndView insertMeterInstallImg ( @RequestParam("meterId")        Integer meterId
            , @RequestParam("orgFileName")    String orgFileName
            , @RequestParam("saveFileName") String saveFileName) throws IOException {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("meterId",        meterId);
        condition.put("orgFileName",    orgFileName);
        condition.put("saveFileName",   saveFileName);

        meterInstallImgManager.insertMeterInstallImg(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterInstallImgManager.getMeterInstallImgList(meterId);
        mav.addObject("installImgList", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/getInsertInstallImg")
    public ModelAndView getInstallImgList( @RequestParam("meterId")         Integer meterId) throws IOException {

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterInstallImgManager.getMeterInstallImgList(meterId);

        mav.addObject("installImgList", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteMeterInstallImg")
    public ModelAndView deleteMeterInstallImg ( @RequestParam("meterId")           Integer meterId
            ,@RequestParam("meterInstallImgId") Integer meterInstallImgId
            , HttpServletRequest request) throws IOException {

        // DB삭제
        String currentTimeMillisName = meterInstallImgManager.deleteMeterInstallImg(meterInstallImgId);

        // 파일 삭제
        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
        FileUploadHelper.removeExistingFile(contextRoot + "/" + currentTimeMillisName);

        // 삭제후 목록 조회
        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterInstallImgManager.getMeterInstallImgList(meterId);
        mav.addObject("installImgList", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeteringDataByMeterChart")
    public ModelAndView getMeteringDataByMeterChart(@RequestParam("meterId")        String meterId
            , @RequestParam("meterType")      String meterType
            , @RequestParam("searchStartDate")String searchStartDate
            , @RequestParam("searchEndDate")  String searchEndDate
            , @RequestParam("searchStartHour")String searchStartHour
            , @RequestParam("searchEndHour")  String searchEndHour
            , @RequestParam("searchDateType") String searchDateType
            , @RequestParam("supplierId") String supplierId) throws IOException {

        Meter meter = meterManager.getMeter(Integer.parseInt(meterId));
        meterType = meter.getMeterType().getName();
        Map<String, Object> condition = new HashMap<String, Object>();
        
        condition.put("meterId"         , meterId        );
        condition.put("meterType"       , meterType      );

        condition.put("searchStartDate" , searchStartDate);
        condition.put("searchEndDate"   , searchEndDate  );
        condition.put("searchStartHour" , searchStartHour);
        condition.put("searchEndHour"   , searchEndHour  );
        condition.put("searchDateType"  , searchDateType );
        condition.put("supplierId",     supplierId);

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterManager.getMeteringDataByMeterChart(condition);

        mav.addObject("chartData", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeteringDataByMeterGrid")
    public ModelAndView getMeteringDataByMeterGrid(@RequestParam("meterId") String meterId
            , @RequestParam("meterType") String meterType
            , @RequestParam("searchStartDate") String searchStartDate
            , @RequestParam("searchEndDate") String searchEndDate
            , @RequestParam("searchStartHour") String searchStartHour
            , @RequestParam("searchEndHour") String searchEndHour
            , @RequestParam("searchDateType") String searchDateType
            , @RequestParam("supplierId") String supplierId
            , @RequestParam(value="curPage", required=false) String curPage) throws IOException {
    	
        if (curPage == null) {
	    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	    	curPage = request.getParameter("page");
    	}
        Map<String, Object> condition = new HashMap<String, Object>();

        Meter meter = meterManager.getMeter(Integer.parseInt(meterId));
        meterType = meter.getMeterType().getName().toString();
        condition.put("meterId"         , meterId        );
        condition.put("meterType"       , meterType      );

        condition.put("searchStartDate" , searchStartDate);
        condition.put("searchEndDate"   , searchEndDate  );
        condition.put("searchStartHour" , searchStartHour);
        condition.put("searchEndHour"   , searchEndHour  );
        condition.put("searchDateType"  , searchDateType );
        condition.put("supplierId"  , supplierId );
        condition.put("curPage"         , curPage );

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterManager.getMeteringDataByMeterGrid(condition);

        mav.addObject("totalCnt", result.get(0));
        mav.addObject("gridData", result.get(1));

        return mav;
    }

    /**
     * method name : viewPhasorDiagram<b/>
     * method Desc : onDemand Result 화면에서 Phasor Diagram 을 보여준다.
     *
     * @param volAng_a
     * @param volAng_b
     * @param volAng_c
     * @param curAng_a
     * @param curAng_b
     * @param curAng_c
     * @return
     */
    @RequestMapping(value="gadget/device/viewPhasorDiagram")
    public ModelAndView viewPhasorDiagram(@RequestParam("volAng_a") String volAng_a,
            @RequestParam("volAng_b") String volAng_b,
            @RequestParam("volAng_c") String volAng_c,
            @RequestParam("curAng_a") String curAng_a,
            @RequestParam("curAng_b") String curAng_b,
            @RequestParam("curAng_c") String curAng_c) {
        ModelAndView mav = new ModelAndView("gadget/device/viewPhasorDiagramSvg");

        mav.addObject("volAng_a", volAng_a);
        mav.addObject("volAng_b", volAng_b);
        mav.addObject("volAng_c", volAng_c);
        mav.addObject("curAng_a", curAng_a);
        mav.addObject("curAng_b", curAng_b);
        mav.addObject("curAng_c", curAng_c);

        return mav;
    }
    
    // 장비모델 정보조회
    @RequestMapping(value = "/gadget/system/getModelinfo.do")
    public ModelAndView getDeviceModelInfo(@RequestParam("devicemodelId") int devicemodelId) {
        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(devicemodelId);

        // 추가
        DeviceModelConfigVo deviceModelConfig = new DeviceModelConfigVo();
        deviceModelConfig.setDeviceModel(deviceModel);

        if (deviceModel.getDeviceType() != null) {
            Code mainDeviceType = deviceModel.getDeviceType().getParent().getParent();
            
            if (mainDeviceType != null) {
                deviceModelConfig.setMainDeviceType(mainDeviceType);
                deviceModelConfig.setMainDeviceTypeName(mainDeviceType.getName());

                if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Modem")) {
                    ModemConfig modemConfig = (ModemConfig)deviceModel.getDeviceConfig();

                    if (modemConfig != null) {
                        deviceModelConfig.setModemConfig(modemConfig);
                    }
                } else if (StringUtil.nullToBlank(mainDeviceType.getName()).equals("Meter")) {
                    MeterConfig meterConfig = (MeterConfig)deviceModel.getDeviceConfig();

                    if (meterConfig != null) {
                        deviceModelConfig.setMeterConfig(meterConfig);

                        mav.addObject("channels", meterConfig.getChannels());
                    }
                }
            }
        }
   
        mav.addObject("deviceModelConfig", deviceModelConfig);
 
        String[] namesOfContain = CommonConstants.EnableCommandModel.getNameOfContain(deviceModelConfig.getModelName());
        
        mav.addObject("namesOfContain",namesOfContain);
        
        return mav;
    }
    
    //미터리스트를 엑셀로 저장하는 페이지
	@RequestMapping(value = "/gadget/device/meterMaxExcelDownloadPopup")
	public ModelAndView meterMaxExcelDownloadPopup() {
		ModelAndView mav = new ModelAndView("/gadget/device/meterMaxExcelDownloadPopup");
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/meterCommInfoExcelDownloadPopup")
	public ModelAndView meterCommInfoExcelDownloadPopup() {
		ModelAndView mav = new ModelAndView("/gadget/device/meterCommInfoExcelDownloadPopup");
		return mav;
	}
	
	@RequestMapping(value="/gadget/device/meterShipmentFileExcelDownloadPopup")
	public ModelAndView meterShipmentFileExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/meterShipmentFileExcelDownloadPopup");
        return mav;
    }
	
	//미터리스트를 엑셀로 저장
	@RequestMapping(value = "/gadget/device/meterMaxExcelMake")
	public ModelAndView meterMaxExcelMake(
			@RequestParam("condition[]") 	String[] condition,
			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
			@RequestParam("filePath") 		String filePath) {

		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		List<Object> list = new ArrayList<Object>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

		final String logPrefix = "meterList";//9

		ModelAndView mav = new ModelAndView("jsonView");

		List<Object> result = null;
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			
            conditionMap.put("sMeterType", condition[0]);
            conditionMap.put("sMdsId", condition[1]);
            conditionMap.put("sStatus", condition[2]);

            conditionMap.put("sMcuName", condition[3]);
            conditionMap.put("sLocationId", condition[4]);
            conditionMap.put("sConsumLocationId", condition[5]);

            conditionMap.put("sVendor", condition[6]);
            conditionMap.put("sModel", condition[7]);
            conditionMap.put("sInstallStartDate", condition[8]);
            conditionMap.put("sInstallEndDate", condition[9]);

            conditionMap.put("sModemYN", condition[10]);
            conditionMap.put("sCustomerYN", condition[11]);
            conditionMap.put("sLastcommStartDate", condition[12]);
            conditionMap.put("sLastcommEndDate", condition[13]);
            conditionMap.put("sOrder", condition[14]);
            conditionMap.put("sCommState", condition[15]);

            conditionMap.put("supplierId", condition[16]);
            conditionMap.put("sMeterGroup", condition[17]);

            conditionMap.put("sCustomerId", condition[18]);
            conditionMap.put("sCustomerName", condition[19]);
            
            conditionMap.put("sPermitLocationId", condition[20]);
            conditionMap.put("sMeterAddress", condition[21]);
            
            conditionMap.put("curPage", "1");
			result = (List<Object>) meterManager.getMeterListExcel(conditionMap);

			total = new Integer(result.size()).longValue();

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

			sbFileName.append(logPrefix);

			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

			// message 생성
			msgMap.put("no", 		       fmtMessage[0]);
			msgMap.put("meterid", 	       fmtMessage[1]);
			msgMap.put("metertype",        fmtMessage[2]);
			msgMap.put("mcuid", 	       fmtMessage[3]);			
			msgMap.put("vendor",	       fmtMessage[4]);
			msgMap.put("model", 	       fmtMessage[5]);
			msgMap.put("modemModel",       fmtMessage[6]);
			msgMap.put("contractNumber",   fmtMessage[7]);
			msgMap.put("lastcomm", 	       fmtMessage[8]);	
			msgMap.put("location", 	       fmtMessage[9]);	
			msgMap.put("status", 	       fmtMessage[10]);
			// 통신상태 메시지
			msgMap.put("normal", 		   fmtMessage[11]);	
			msgMap.put("commstateYellow",  fmtMessage[12]);	
			msgMap.put("commstateRed", 	   fmtMessage[13]);
			msgMap.put("title",            fmtMessage[14]);
			// 고객, 계약 정보
			msgMap.put("customerId",       fmtMessage[15]);
			msgMap.put("customerName",     fmtMessage[16]);
			msgMap.put("installId",        fmtMessage[17]);
			msgMap.put("ct",               fmtMessage[18]);
			msgMap.put("installProperty",  fmtMessage[19]);
			msgMap.put("customerAddress",  fmtMessage[20]);
//			msgMap.put("address",          fmtMessage[20]); // address를 customerAddr로 변경
            msgMap.put("transformerRatio", fmtMessage[21]);
            msgMap.put("address1",         fmtMessage[22]);
            msgMap.put("address2",         fmtMessage[23]);
            msgMap.put("address3",         fmtMessage[24]);
            msgMap.put("meterAddress",     fmtMessage[25]);
            msgMap.put("ver",              fmtMessage[26]);
            // 모뎀 (미터터미널) 아이디
            msgMap.put("modemid",          fmtMessage[27]);
            
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

						// 파일길이 : 26이상, 확장자 : xls|zip
						if (filename.length() > 29
								&& (filename.endsWith("xls") || filename
										.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(logPrefix)
									&& filename.substring(9, 17).compareTo(
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
			MeterMaxMakeExcel wExcel = new MeterMaxMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath,
						sbSplFileName.toString());
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
								sbSplFileName.toString());
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

		} catch (ParseException e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		}

		return mav;
	}
	
    @RequestMapping(value = "/gadget/device/meterCommInfoExcelMake")
    public ModelAndView meterCommInfoExcelMake(
    		@RequestParam("condition[]") 	String[] condition,
    		@RequestParam("fmtMessage[]") String[] fmtMessage,
    		@RequestParam("filePath") String filePath) {

		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		List<Object> list = new ArrayList<Object>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; 			// 데이터 조회건수
		Long maxRows = 5000L; 		// excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

		final String logPrefix = "meterList";

    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result = null;
    	
    	try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			
            conditionMap.put("sMeterType", condition[0]);
            conditionMap.put("sMdsId", condition[1]);
            conditionMap.put("sStatus", condition[2]);
            conditionMap.put("sMcuName", condition[3]);
            conditionMap.put("sLocationId", condition[4]);
            conditionMap.put("sConsumLocationId", condition[5]);
            conditionMap.put("sVendor", condition[6]);
            conditionMap.put("sModel", condition[7]);
            conditionMap.put("sInstallStartDate", condition[8]);
            conditionMap.put("sInstallEndDate", condition[9]);
            conditionMap.put("sModemYN", condition[10]);
            conditionMap.put("sCustomerYN", condition[11]);
            conditionMap.put("sLastcommStartDate", condition[12]);
            conditionMap.put("sLastcommEndDate", condition[13]);
            conditionMap.put("sOrder", condition[14]);
            conditionMap.put("sCommState", condition[15]);
            conditionMap.put("supplierId", condition[16]);
            conditionMap.put("sMeterGroup", condition[17]);
            conditionMap.put("sCustomerId", condition[18]);
            conditionMap.put("sCustomerName", condition[19]);
            conditionMap.put("sPermitLocationId", condition[20]);
            conditionMap.put("sMeterAddress", condition[21]);
            
            result = (List<Object>) meterManager.getMeterCommInfoListExcel(conditionMap);
			total = new Integer(result.size()).longValue();

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

			sbFileName.append(logPrefix);
			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

			// message setting 영역 (S)
    		msgMap.put("title", fmtMessage[0]);
    		msgMap.put("no", fmtMessage[1]);
    		msgMap.put("mcuid", fmtMessage[2]);
    		msgMap.put("activity24", fmtMessage[3]);
    		msgMap.put("noActivity24", fmtMessage[4]);
    		msgMap.put("noActivity48", fmtMessage[5]);
    		msgMap.put("unknown", fmtMessage[6]);
    		msgMap.put("commError", fmtMessage[7]);
    		msgMap.put("securityError", fmtMessage[8]);
    		msgMap.put("powerDown", fmtMessage[9]);
    		// message setting 영역 (E)
            
			// Check Download Directory Logic (S)
			File downDir = new File(filePath);

			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {
						filename = file.getName();
						isDel = false;

						// 파일길이 : 26이상, 확장자 : xls|zip
						if (filename.length() > 29 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(logPrefix) && filename.substring(9, 17).compareTo(deleteDate) < 0) {
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
				downDir.mkdir();
			}
			// Check Download Directory Logic (E)
			
			// create excel file
			MeterMaxMakeExcel wExcel = new MeterMaxMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");

				wExcel.writeCommInfoReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = result.subList(idx, (i + 1));
						
						wExcel.writeCommInfoReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
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

		} catch (ParseException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
    	
		return mav;
    }
	
    @RequestMapping(value = "/gadget/device/meterShipmentExcelMake")
    public ModelAndView meterShipmentExcelMake(
    		@RequestParam("supplierId") Integer supplierId,
    		@RequestParam("filePath") String filePath,
    		@RequestParam("detailType") String detailType,
            @RequestParam("model") String model,
            @RequestParam("msg_title") String msg_title,
            @RequestParam("msg_number") String msg_number,
            @RequestParam("msg_type") String msg_type,
            @RequestParam("msg_euiId") String msg_euiId,
            @RequestParam("msg_gs1") String msg_gs1,
            @RequestParam("msg_model") String msg_model,
            @RequestParam("msg_hwVer") String msg_hwVer,
            @RequestParam("msg_swVer") String msg_swVer,
            @RequestParam("msg_productionDate") String msg_productionDate) {
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        Map<String, String> msgMap = new HashMap<String, String>();
    	List<String> fileNameList = new ArrayList<String>();
    	List<Object> list = new ArrayList<Object>();

    	StringBuilder sbFileName = new StringBuilder();
    	StringBuilder sbSplFileName = new StringBuilder();

    	boolean isLast = false;
    	Long total = 0L; 		// 데이터 조회건수
    	Long maxRows = 5000L; 	// excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

    	final String logPrefix = detailType + "_Shipment";
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("sMeterType", detailType);
    	conditionMap.put("model", model);
    	conditionMap.put("curPage", "1");

    	List<Object> result = null;
    	
    	
    	try {
    		result = (List<Object>) meterManager.getMeterListExcel(conditionMap);
    		total = new Integer(result.size()).longValue();

    		mav.addObject("total", total);
    		if (total <= 0) {
    			return mav;
    		}

    		sbFileName.append(logPrefix);
    		sbFileName.append(TimeUtil.getCurrentTimeMilli());

    		// message setting 영역 (S)
    		msgMap.put("msg_title", msg_title);
    		msgMap.put("msg_number", msg_number);
    		msgMap.put("msg_type", msg_type);
    		msgMap.put("msg_euiId", msg_euiId);
    		msgMap.put("msg_gs1", msg_gs1);
    		msgMap.put("msg_model", msg_model);
    		msgMap.put("msg_hwVer", msg_hwVer);
    		msgMap.put("msg_swVer", msg_swVer);
    		msgMap.put("msg_productionDate", msg_productionDate);
    		// message setting 영역 (E)
    		
    		// Check Download Directory Logic (S)
    		File downDir = new File(filePath);

    		if (downDir.exists()) {
    			File[] files = downDir.listFiles();

    			if (files != null) {
    				String filename = null;
    				String deleteDate;
    				deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
    				boolean isDel = false;

    				for (File file : files) {
    					filename = file.getName();
    					isDel = false;

    					// 파일길이 : 26이상, 확장자 : xls | zip
    					if (filename.length() > 29 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
    						// 10일 지난 파일들 삭제
    						if (filename.startsWith(logPrefix) && filename.substring(9, 17).compareTo(deleteDate) < 0) {
    							isDel = true;
    						}
							if (isDel) {
								file.delete();
							}
						}
						filename = null;
    				}
    			}
    		} else { // directory 가 없으면 생성
    			downDir.mkdir();
    		}
    		// Check Download Directory Logic (E)
    		
    		// create excel file
    		MeterMaxMakeExcel wExcel = new MeterMaxMakeExcel();
    		int cnt = 1;
    		int idx = 0;
    		int fnum = 0;
    		int splCnt = 0;

    		if (total <= maxRows) {
    			sbSplFileName = new StringBuilder();
    			sbSplFileName.append(sbFileName);
    			sbSplFileName.append(".xls");
    			wExcel.writeShipmentReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
    			fileNameList.add(sbSplFileName.toString());
    		} else {
    			for (int i = 0; i < total; i++) {
    				if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
    					sbSplFileName = new StringBuilder();
    					sbSplFileName.append(sbFileName);
    					sbSplFileName.append('(').append(++fnum)
    							.append(").xls");

    					list = result.subList(idx, (i + 1));

    					wExcel.writeShipmentReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
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

    	} catch (ParseException e) {
    		e.printStackTrace();
    		log.error(e.toString(), e);
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error(e.toString(), e);
    	}

    	return mav;
    }
    
	@RequestMapping(value = "/gadget/device/getModemByMeter")
	public ModelAndView getModemByMeter(@RequestParam("meterId") Integer meterId) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Meter meter = meterManager.getMeter(meterId);
		if(meter != null) {
			Modem modem = modemManager.getModem(meter.getModemId());
			
			if(modem != null) {
				mav.addObject("protocolType", modem.getProtocolType());
				mav.addObject("modemType", modem.getModemType());				
				return mav;
			}
		}
		
		mav.addObject("protocolType", "");
		mav.addObject("modemType", "");
		return mav;
	}

    // Load groupCommand External Page
    @RequestMapping(value = "/gadget/device/meterMaxGroupCommandPopup")
    public ModelAndView meterMaxGroupCommandPopup() {
        ModelAndView mav = new ModelAndView(
                "/gadget/device/meterMaxGadgetGroupCommand");
        mav.addObject("systemMaxLimit", 10);
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/getAsyncLogListForMeter")
    public ModelAndView getAsyncLogListForMeter(
    		@RequestParam(value = "meterId", required = false) String meterId,
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate,
    		@RequestParam(value = "loginId", required = false) String loginId
    		) throws Exception {

    	ModelAndView mav = new ModelAndView("jsonView");
		List<Object> result	= new ArrayList<Object>();
		
		Meter meter = meterManager.getMeter(Integer.parseInt(meterId));
		Modem modem = modemManager.getModem(meter.getModemId());
		
		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        int idx = 1;
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("deviceSerial", modem.getDeviceSerial());
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        
        
        Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// Date Format을 위한 설정
		log.debug("date format(s)");
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		log.debug("date format(e)");
		
		List<Object> resultList = asyncCommandLogManager.getCommandLogList(conditionMap);
		int dataListLen = 0;
		
		if (resultList != null) {
			dataListLen = resultList.size();
		}
		
		for (int i = 0; i < dataListLen; i++) {
			HashMap returnResult = new HashMap();
			Object[] resultData = (Object[]) resultList.get(i);
			returnResult.put("rowNo", (((page-1) * limit) + idx));
			returnResult.put("command", resultData[0]);
			returnResult.put("requestTime", TimeLocaleUtil.getLocaleDate(resultData[1].toString(), lang, country));
			returnResult.put("state", resultData[2]);
			returnResult.put("deviceSerial", resultData[3]);
			returnResult.put("trid", resultData[4]);
			
			result.add(returnResult);
			idx++;
		}
		
		mav.addObject("rtnStr", result);
		mav.addObject("totalCount", asyncCommandLogManager.getCommandLogListTotalCount(conditionMap));
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/getAsyncResultForMeter")
    public ModelAndView getAsyncResultForMeter(
    		@RequestParam(value = "deviceSerial", required = false) String deviceSerial,
    		@RequestParam(value = "trid", required = false) String trid,
    		@RequestParam(value = "command", required = false) String command)throws Exception {
		
    	ModelAndView mav = new ModelAndView("jsonView");
		String noResponse = "Didn't receive a reply from the modem.";
		String resultStr="";
		String returnStr="";
		
		List<AsyncCommandResult> result = asyncCommandLogManager.getCmdResults(deviceSerial, Long.parseLong(trid), command);
		Map<String, Object> resultMapForOnlySMS = new HashMap<String, Object>();
		
		/** ResultType이 SMS_Response인 경우, json으로 변경하지 않고 ResponseFrame을 사용하여 데이터 파싱 후 반환 **/ 
		if (result.get(0).getResultType().equals("SMS_Response")) {
			/** CASE 1) SMS를 보낸 후 SMS로 응답을 받는 경우 **/
			
			resultStr = result.get(0).getResultValue();
			
			if (resultStr == null || resultStr == "") {
				returnStr = noResponse;
			} else {
				ResponseFrame responseFrame = new ResponseFrame();
				resultMapForOnlySMS = responseFrame.decode(resultStr);
				
				String response_messageType = resultMapForOnlySMS.get("messageType").toString();
				String response_commandNumber = resultMapForOnlySMS.get("command").toString();
				
				if (response_messageType.equals("F")) {
					returnStr = noResponse;
				} else if (response_messageType.equals("3")) {
					returnStr = resultMapForOnlySMS.get("errorCode").toString();
				} else if (response_messageType.equals("2")) {
					/** SUCCESS일 경우 **/
					// command 종류에 따라(GET/SET) 결과 반환 로직 분류 (S)
					if (response_commandNumber.equals(COMMAND_TYPE.ACCESS_TECHNOLOGY)) {
						List<String> response_paramList = (List<String>) resultMapForOnlySMS.get("paramList");
						String rtnData = response_paramList.get(0);

						if (rtnData.equals("0")) {
							rtnData = "GSM";
						} else if (rtnData.equals("1")) {
							rtnData = "GSM Compact";
						} else if (rtnData.equals("2")) {
							rtnData = "UTRAN";
						} else if (rtnData.equals("3")) {
							rtnData = "GSM w/EGPRS";
						} else if (rtnData.equals("4")) {
							rtnData = "UTRAN w/HSDPA";
						} else if (rtnData.equals("5")) {
							rtnData = "UTRAN w/HSUPA";
						} else if (rtnData.equals("6")) {
							rtnData = "UTRAN w/HSDPA and HSUPA";
						} else if (rtnData.equals("7")) {
							rtnData = "E-UTRAN";
						}
						
						returnStr = "Access Technology: " + rtnData;
					} else if (response_commandNumber.equals(COMMAND_TYPE.RSSI)) {
						List<String> response_paramList = (List<String>) resultMapForOnlySMS.get("paramList");
						String rtnData = response_paramList.get(0);
						String rssiSign = rtnData.substring(0, 1);
						String rssiValue = rtnData.substring(1, rtnData.length());
						
						rssiValue = rssiSign + rssiValue;
						returnStr = "RSSI: " + rssiValue;
					} else if (response_commandNumber.equals(COMMAND_TYPE.APN_USED)) {
						List<String> response_paramList = (List<String>) resultMapForOnlySMS.get("paramList");
						String apnName = null;
						String apnId = null;
						String apnPw = null;
						
						try {
							apnName = response_paramList.get(0);					
						} catch (Exception e) {
							apnName = "";
						}
						
						try {
							apnId = response_paramList.get(1);					
						} catch (Exception e) {
							apnId = "";
						}
						
						try {
							apnPw = response_paramList.get(2);					
						} catch (Exception e) {
							apnPw = "";
						}
						
						returnStr = "APN Name : " + apnName + ", APN ID : " + apnId + ", APN PW : " + apnPw;
					} else if (response_commandNumber.equals(COMMAND_TYPE.IP_ADDRESS)) {
						List<String> response_paramList = (List<String>) resultMapForOnlySMS.get("paramList");
						returnStr = "IP Address: " + response_paramList.get(0);
					} else {
						returnStr = "SUCCESS";
					}
					// command 종류에 따라(GET/SET) 결과 반환 로직 분류 (E)
				}
			}
		} else {
			/** CASE 2) SMS를 보낸 후 NI 또는 CoAP Command Server로 통신하는 경우 **/
			
			for (int i = 0; i < result.size(); i++) {
				resultStr += result.get(i).getResultValue();
			}

			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> resultMap = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
			});
			Iterator<String> iterator = resultMap.keySet().iterator();
			while (iterator.hasNext()) {
		        String key = (String) iterator.next();
		        returnStr += (key + " : ");
		        returnStr += (resultMap.get(key) + "\n");
			}
		}
		
		mav.addObject("result", returnStr);
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/getRealTimeMeterValues")
	public ModelAndView getMeteringDataList(
			@RequestParam("supplierId") Integer supplierId,
			@RequestParam("deviceType") String deviceType,
			@RequestParam("meterId") Integer meterId,
			@RequestParam("meterMds") String mdevId,
			@RequestParam("searchStartDate") String startDate) {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

		Integer page = Integer.parseInt(request.getParameter("page"));
		Integer limit = Integer.parseInt(request.getParameter("limit"));
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("page", page);
		conditionMap.put("limit", limit);
		conditionMap.put("supplierId", supplierId);
		conditionMap.put("deviceType", deviceType);	// MDEV_TYPE
		conditionMap.put("meterId", meterId);
		conditionMap.put("mdevId", mdevId);
		conditionMap.put("startDate", startDate);
		
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
        Integer totalCount = null;

        list = searchMeteringDataManager.getRealTimeMeterValues(conditionMap);
		totalCount = searchMeteringDataManager.getRealTimeMeterValuesTotalCount(conditionMap);

		if (totalCount <= 0) {
			return mav;
		}
		
		int num = 0;
		if (page != null && limit != null) {
			num = ((page - 1) * limit) + 1;
		} else {
			num = 1;
		}
		
		for (Map<String, Object> obj : list) {
			map = new HashMap<String, Object>();

			map.put("num", num++);
			map.put("meteringTime", TimeLocaleUtil.getLocaleDate((String)obj.get("YYYYMMDDHHMMSS"), lang, country));
			
			if(StringUtil.nullToBlank(obj.get("CH1")).isEmpty()){
                map.put("ch1", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch1", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH1"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH2")).isEmpty()){
                map.put("ch2", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch2", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH2"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH3")).isEmpty()){
                map.put("ch3", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch3", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH3"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH4")).isEmpty()){
                map.put("ch4", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch4", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH4"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH5")).isEmpty()){
                map.put("ch5", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch5", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH5"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH6")).isEmpty()){
                map.put("ch6", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch6", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH6"))));
            }
			
			if(StringUtil.nullToBlank(obj.get("CH7")).isEmpty()){
                map.put("ch7", mdf.format(DecimalUtil.ConvertNumberToDouble(0)));
			} else {
				map.put("ch7", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CH7"))));
            }
			
			result.add(map);
		}

		mav.addObject("result", result);
		mav.addObject("totalCount", totalCount);

		return mav;
	}
    
    @RequestMapping(value="/gadget/device/getFirmwareVersionList_meter")
    public ModelAndView getFirmwareVersionList(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
    		@RequestParam(value="sType") String sType,
    		@RequestParam(value="installStartDate") String installStartDate,
    		@RequestParam(value="installEndtDate") String installEndtDate,
    		@RequestParam(value="lastCommStartDate") String lastCommStartDate,
    		@RequestParam(value="lastCommEndDate") String lastCommEndDate,
    		@RequestParam(value="hwVer") String hwVer
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
        condition.put("deviceId", deviceId);
        condition.put("fwVersion", fwVersion);
        condition.put("locationId", locationId);
        condition.put("dcuName", dcuName);
        condition.put("sType", sType);
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);

		List<String> versionList = meterManager.getFirmwareVersionList(condition);
		
		mav.addObject("result", versionList);
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/getDeviceList_meter")
    public ModelAndView getDeviceList_meter(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="versions") String versions,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
    		@RequestParam(value="sType") String sType,
    		@RequestParam(value="installStartDate") String installStartDate,
    		@RequestParam(value="installEndtDate") String installEndtDate,
    		@RequestParam(value="lastCommStartDate") String lastCommStartDate,
    		@RequestParam(value="lastCommEndDate") String lastCommEndDate,
    		@RequestParam(value="hwVer") String hwVer
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
        condition.put("versions", versions);
        condition.put("deviceId", deviceId);
        condition.put("fwVersion", fwVersion);
        condition.put("locationId", locationId);
        condition.put("dcuName", dcuName);
        condition.put("sType", sType);
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = meterManager.getDeviceList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	
	@RequestMapping(value="/gadget/device/getTargetList_meter")
    public ModelAndView getTargetListBy_meter(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
    		@RequestParam(value="sType") String sType,
    		@RequestParam(value="installStartDate") String installStartDate,
    		@RequestParam(value="installEndtDate") String installEndtDate,
    		@RequestParam(value="lastCommStartDate") String lastCommStartDate,
    		@RequestParam(value="lastCommEndDate") String lastCommEndDate,
    		@RequestParam(value="hwVer") String hwVer
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
        condition.put("deviceId", deviceId);
        condition.put("fwVersion", fwVersion);
        condition.put("locationId", locationId);
        condition.put("dcuName", dcuName);
        condition.put("sType", sType);
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = meterManager.getTargetList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	/* 체크된 대상으로 Clone on,off 실행 */
	@RequestMapping(value="/gadget/device/getTargetList_meter_cloneonoff")
    public ModelAndView getTargetListBy_meter_cloneonoff(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
    		@RequestParam(value="locationName") String locationName,
    		@RequestParam(value="installStartDate") String installStartDate,
    		@RequestParam(value="installEndtDate") String installEndtDate,
    		@RequestParam(value="lastCommStartDate") String lastCommStartDate,
    		@RequestParam(value="lastCommEndDate") String lastCommEndDate,
    		@RequestParam(value="hwVer") String hwVer,
    		@RequestParam(value="chkParent") Boolean chkParent,
    		@RequestParam(value="dcuall") Boolean dcuall
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
        condition.put("deviceId", deviceId);
        condition.put("fwVersion", fwVersion);
        condition.put("locationId", locationId);
        condition.put("dcuName", dcuName);
        condition.put("locationName", locationName);
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        condition.put("chkParent", chkParent);
        condition.put("dcuall", dcuall);
        
		List<String> deviceList = meterManager.getTargetListMeter(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/checkDeviceType_meter")
    public ModelAndView checkDeviceType(
    		@RequestParam(value="deviceId") String deviceId
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
        Meter me = meterManager.getMeter(deviceId);
        
        Modem mo= null;
        ModemType mt = null;
        Protocol pt = null;
		String type = "";
        
        if(me != null && me.getModemId() != null)
        	mo = modemManager.getModem(me.getModemId());
        else{
        	mav.addObject("result", null);
            return mav;
        }
        
        if(mo != null) {
            mt = mo.getModemType();
    		pt = mo.getProtocolType();
        }
		
		if(mt == null || pt == null){
			mav.addObject("result", "");
	        return mav;
		}
		
		if( (mt.name()).equals(ModemType.SubGiga.name()) ){
			type = ModemIFType.RF.name();
		}else{
			if( (pt.name()).equals(Protocol.SMS.name()))
				type = ModemIFType.MBB.name();
			else
				type = ModemIFType.Ethernet.name();
		}
		
		mav.addObject("result", type);
        return mav;
    }
	
	/* Meter Clone on,off 그리드 조회 */
	@RequestMapping(value="/gadget/device/getDeviceList_meter_cloneonoff")
	public ModelAndView getDeviceList_Meter(
			@RequestParam(value="modelId") String modelId,
			@RequestParam(value="versions") String versions,
			@RequestParam(value="deviceId") String deviceId,
			@RequestParam(value="fwVersion") String fwVersion,
			@RequestParam(value="locationId") String locationId,
			@RequestParam(value="dcuName") String dcuName,
			@RequestParam(value="installStartDate") String installStartDate,
			@RequestParam(value="installEndtDate") String installEndtDate,
			@RequestParam(value="lastCommStartDate") String lastCommStartDate,
			@RequestParam(value="lastCommEndDate") String lastCommEndDate,
			@RequestParam(value="hwVer") String hwVer,
			@RequestParam(value="chkParent") Boolean chkParent
			) throws Exception {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
		condition.put("versions", versions);
		condition.put("deviceId", deviceId);
		condition.put("fwVersion", fwVersion);
		condition.put("locationId", locationId);
		condition.put("dcuName", dcuName);
		condition.put("installStartDate", installStartDate);
		condition.put("installEndtDate", installEndtDate);
		condition.put("lastCommStartDate", lastCommStartDate);
		condition.put("lastCommEndDate", lastCommEndDate);
		condition.put("hwVer", hwVer);
		condition.put("chkParent", chkParent);
		
		List<String> deviceList = meterManager.getDeviceListMeter(condition);
		
		mav.addObject("result", deviceList);
		return mav;
	}
	
	/* Meter Clone on,off 팝업 리스트 조회 - metersearchgrid 와 분기 */
	 @RequestMapping(value = "gadget/device/getMeterListCloneonoff")
	    public ModelAndView getMeterListCloneonoff(@RequestParam("sMeterType") String sMeterType,
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
	            @RequestParam(value = "curPage", required = false) String curPage,
	            @RequestParam("sOrder") String sOrder,
	            @RequestParam("sCommState") String sCommState,
	            @RequestParam("sGroupOndemandYN") String sGroupOndemandYN,
	            @RequestParam("supplierId") String supplierId,
	            @RequestParam(value = "sCustomerId", required = false) String customerId,
	            @RequestParam(value = "sCustomerName", required = false) String customerName,
	            @RequestParam(value = "sPermitLocationId", required = false) String sPermitLocationId,
	            @RequestParam("sMeterAddress") String sMeterAddress,
	            @RequestParam("sHwVersion") String sHwVersion,
	            @RequestParam("sFwVersion") String sFwVersion,
	            @RequestParam("sGs1") String sGs1,
	            @RequestParam("sType") String sType,
	            @RequestParam("fwGadget") String fwGadget,
	            @RequestParam(value="chkParent") Boolean chkParent
	            ) {
	        String limit = null;
	        if (curPage == null) {
	            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	            curPage = request.getParameter("page");
	            limit = request.getParameter("limit");
	        }

	        /*// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
			if (sLocationId.equals("")) {
				List<Location> locationRoot = locationDao.getRootLocationList();
				sLocationId = Integer.toString(locationRoot.get(0).getId());
			}*/
	        
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

	        condition.put("curPage", curPage);
	        condition.put("limit", limit);
	        condition.put("sOrder", sOrder);
	        condition.put("sCommState", sCommState);
	        condition.put("sGroupOndemandYN", sGroupOndemandYN);

	        condition.put("supplierId", supplierId);

	        condition.put("sCustomerId", customerId);
	        condition.put("sCustomerName", customerName);
	        
	        condition.put("sHwVersion", sHwVersion);
	        condition.put("sFwVersion", sFwVersion);
	        condition.put("sGs1", sGs1);
	        condition.put("sType", sType);
	        condition.put("fwGadget", fwGadget);
	        condition.put("chkParent", chkParent);

	        List<Object> meterSearchGrid = meterManager.getMeterListCloneonoff(condition);
	        
	        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
	        String mdPattern = supplier.getMd().getPattern();
	        Integer dot = mdPattern.indexOf(".");

	        if(dot != -1)
	        	mdPattern = mdPattern.substring(0,dot);

	        ModelAndView mav = new ModelAndView("jsonView");
	        mav.addObject("gridData", meterSearchGrid);

	        return mav;
	    }
	 /**
	 * SP-929
	 * Netstation Monitoring Tab in meter Max gadget 
	 * @param meterId
	 * @param supplierId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value="/gadget/device/getMbusSlaveIOInfo")
	    public ModelAndView getMbusSlaveIOInfo(
	            @RequestParam("meterId") Integer meterId,
	            @RequestParam("supplierId") Integer supplierId,
	            @RequestParam(value = "loginId", required = false) String loginId) {

	        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
	        AimirUser user = (AimirUser)instance.getUserFromSession();   
	        
	        
	        ModelAndView mav = new ModelAndView("/gadget/device/meterMaxGadgetMbusSlaveIOInfo");
	        Meter meter = meterManager.getMeter(meterId);


	        if (meter != null) {
	            Map<String, Object> map = mbusSlaveIOModuleManager.getMbusSlaveIOModuleInfo(meterId,supplierId);
	            if ( map != null ){
	                map.put("modelName", meter.getModel() != null ? meter.getModel().getName() : null );
	                mav.addObject("result", map);
	            }
	        }
	        if ( loginId != null){
	            mav.addObject("loginId", loginId);
	        }
	        return mav;  
	    }
	
	/* SP-1004 Clone on탭 Excel search시 parent 정보 출력 */
	@RequestMapping(value="/gadget/device/getParentDeviceList_meter")
    public ModelAndView getParentDevice(
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="supplierId") String supplierId,
    		@RequestParam(value="modelId") String modelId
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modelId", modelId);
		condition.put("fwVersion", fwVersion);
		condition.put("deviceId", deviceId);
        condition.put("supplierId", supplierId);
        
        List<Map<String, Object>> deviceList = meterManager.getParentDevice(condition);
		
		mav.addObject("result", deviceList);
		
        return mav;
    }
	
}
