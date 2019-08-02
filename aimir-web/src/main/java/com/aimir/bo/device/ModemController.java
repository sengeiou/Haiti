package com.aimir.bo.device;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemSleepMode;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.model.device.ACD;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.Converter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.IHD;
import com.aimir.model.device.LTE;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ModemMaxMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class ModemController {
	
	private static Log log = LogFactory.getLog(ModemController.class);

	@Autowired
	ModemDao modemDao;
	
	@Autowired
	MeterDao meterDao;
	
    @Autowired
    ModemManager modemManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    AsyncCommandLogManager asyncCommandLogManager;
    
    @Autowired
    OperatorManager operatorManager;
    
    @Autowired 
	LocationDao locationDao;

    @RequestMapping(value = "/gadget/device/modemMiniGadget")
    public ModelAndView getMiniChart(@RequestParam(value = "modemChart", required = false) String modemChart) {
        ModelAndView mav = new ModelAndView("gadget/device/modemMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());        

        return mav;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/gadget/device/getModemMiniChart")
    public ModelAndView getModemMiniChart(@RequestParam(value = "modemChart", required = false) String modemChart,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(value = "fmtmessagecommalert", required = false) String fmtmessagecommalert,
            @RequestParam(value = "chartType", required = false) String chartType) {

        Map<String, Object> condition = new HashMap<String, Object>();

        if (modemChart == null)
            modemChart = "ml";

        condition.put("modemChart", modemChart);
        condition.put("supplierId", supplierId);
        condition.put("chartType", chartType);

        // fmtMsg
        condition.put("fmtmessagecommalert", fmtmessagecommalert);

        List<Object> miniChart = modemManager.getMiniChart2(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<HashMap> gridList = (List) miniChart.get(0);
        List<HashMap> chartSeriesList = (List) miniChart.get(1);

        int totalCnt = gridList.size();
        
        // grid data
        mav.addObject("gridData", gridList);

        // totCnt
        mav.addObject("totalCnt", totalCnt);

        // 해더값 model header value
        mav.addObject("chartSeries", chartSeriesList);

        mav.addObject("modemChartType", modemChart);

        return mav;
    }

    /**
     * @desc extjs action
     * @param modemChart
     * @param supplierId
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/gadget/device/getModemMiniChart2")
    public ModelAndView getModemMiniChart2(@RequestParam(value = "modemChart", required = false) String modemChart,
            @RequestParam(value = "supplierId", required = false) String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        if (modemChart == null)
            modemChart = "ml";

        condition.put("modemChart", modemChart);
        condition.put("supplierId", supplierId);

        List<Object> miniChart = modemManager.getMiniChart2(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<HashMap> gridList = (List) miniChart.get(0);
        List<HashMap> chartSeriesList = (List) miniChart.get(1);

        int totalCnt = gridList.size();

        // grid data
        mav.addObject("chartData", gridList);

        // totCnt
        mav.addObject("totalCnt", totalCnt);

        // 해더값 model header value
        mav.addObject("chartSeries", chartSeriesList);

        mav.addObject("modemChartType", modemChart);

        return mav;
    }

    @RequestMapping(value = "/gadget/device/modemMaxGadget")
    public ModelAndView loadModemMaxGadgetForm() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemMaxGadget");

        List<Code> modemType = codeManager.getChildCodes(Code.MODEM_TYPE);
        List<Code> modemSwRev = codeManager.getChildCodes(Code.MODEM_SW_REVISION);
        List<Code> modemHwVer = codeManager.getChildCodes(Code.MODEM_HW_VERSION);
        List<Code> modemSwVer = codeManager.getChildCodes(Code.MODEM_SW_VERSION);
        List<Code> modemStatus = codeManager.getChildCodes(Code.MODEM_SLEEP_MODE);

        List<Code> mcuType = codeManager.getChildCodes(Code.MCU_TYPE);

        Map<String, Object> serchDate = modemManager.getModemSearchCondition();

        mav.addObject("modemType", modemType);
        mav.addObject("modemSwRev", modemSwRev);
        mav.addObject("modemHwVer", modemHwVer);
        mav.addObject("modemSwVer", modemSwVer);
        mav.addObject("modemStatus", modemStatus);
        mav.addObject("mcuType", mcuType);

        mav.addObject("installMinDate", serchDate.get("installMinDate"));
        mav.addObject("installMaxDate", serchDate.get("installMaxDate"));
        mav.addObject("yesterday", serchDate.get("yesterday"));
        mav.addObject("today", serchDate.get("today"));

//        mav.setViewName("/gadget/device/modemMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("cmdAuth", authMap.get("command"));  // Command 권한(command = true)
        mav.addObject("supplierName", user.getRoleData().getSupplier().getName()); //공급사 이름

        return mav;
    }

    @RequestMapping(value = "/gadget/device/modemMaxGadgetInfo")
    public ModelAndView getMaxGadgetInfo() {
        return new ModelAndView("gadget/device/modemMaxGadgetInfo");
    }

    @RequestMapping(value = "/gadget/device/modemMaxGadgetScheduleTab")
    public ModelAndView getMaxGadgetScheduleTab() {
        return new ModelAndView("gadget/device/modemMaxGadgetScheduleTab");
    }

    @RequestMapping(value = "gadget/device/getModemSearchGrid")
    public ModelAndView getModemSearchGrid(@RequestParam("sModemType") String sModemType,
            @RequestParam("sModemId") String sModemId,
            @RequestParam("sState") String sState,
            @RequestParam("sInstallState") String sInstallState,

            @RequestParam("sMcuType") String sMcuType,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sModemSwVer") String sModemSwVer,
            @RequestParam("sModemSwRev") String sModemSwRev,
            @RequestParam("sModemHwVer") String sModemHwVer,

            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,

            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            @RequestParam("sLocationId") String sLocationId,

            @RequestParam("curPage") String curPage,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("sModemType", sModemType);
        condition.put("sModemId", sModemId);
        condition.put("sState", sState);
        condition.put("sInstallState", sInstallState);

        condition.put("sMcuType", sMcuType);
        condition.put("sMcuName", sMcuName);
        condition.put("sModemSwVer", sModemSwVer);
        condition.put("sModemSwRev", sModemSwRev);
        condition.put("sModemHwVer", sModemHwVer);

        condition.put("sInstallStartDate", sInstallStartDate);
        condition.put("sInstallEndDate", sInstallEndDate);

        condition.put("sLastcommStartDate", sLastcommStartDate);
        condition.put("sLastcommEndDate", sLastcommEndDate);
        condition.put("sLocationId", sLocationId);

        condition.put("curPage", curPage);
        condition.put("sOrder", sOrder);
        condition.put("sCommState", sCommState);

        condition.put("supplierId", supplierId);
        
        Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
        condition.put("deleteCodeId", deleteCode.getId());
        
        List<Object> modemSearchGrid = modemManager.getModemSearchGrid(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt", modemSearchGrid.get(0));
        mav.addObject("gridData", modemSearchGrid.get(1));

        return mav;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "gadget/device/getModemSearchGrid2")
    public ModelAndView getModemSearchGrid2(@RequestParam("sModemType") String sModemType,
            @RequestParam("sModemId") String sModemId,
            @RequestParam("sMcuType") String sMcuType,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sModemFwVer") String sModemFwVer,
            @RequestParam("sModemSwRev") String sModemSwRev,
            @RequestParam("sModemHwVer") String sModemHwVer,
            @RequestParam(value="sModomStatus", required=false) String sModemStatus,

            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,

            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            @RequestParam("sLocationId") String sLocationId,

            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("gridType") String gridType,
            @RequestParam("modelId") String modelId,
            @RequestParam("sMeterSerial") String sMeterSerial,
            @RequestParam("sModuleBuild") String sModuleBuild,
            @RequestParam("fwGadget") String fwGadget,
            String sState,
            String sInstallState) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("sModemType", sModemType);
        if(sMeterSerial =="" || sMeterSerial == null)
        	condition.put("sModemId", sModemId);
        else{
        	if(sModemId =="" || sModemId == null)
        		condition.put("sModemId", (meterDao.get(sMeterSerial).getModem()).getDeviceSerial());
        	else if(sModemId.equals((meterDao.get(sMeterSerial).getModem()).getDeviceSerial()))
        		condition.put("sModemId", sModemId);
        	else
        		condition.put("sModemId", "no search");
        }
        
        /*// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
 		if (sLocationId.equals("")) {
 			List<Location> locationRoot = locationDao.getRootLocationList();
 			sLocationId = Integer.toString(locationRoot.get(0).getId());
 		}*/
        
        condition.put("sState", sState);
        condition.put("sInstallState", sInstallState);
        condition.put("sMcuType", sMcuType);
        condition.put("sMcuName", sMcuName);
        condition.put("sModemFwVer", sModemFwVer);
        condition.put("sModemSwRev", sModemSwRev);
        condition.put("sModemHwVer", sModemHwVer);
        condition.put("sModemStatus", sModemStatus);
        condition.put("sInstallStartDate", sInstallStartDate);
        condition.put("sInstallEndDate", sInstallEndDate);
        condition.put("sLastcommStartDate", sLastcommStartDate);
        condition.put("sLastcommEndDate", sLastcommEndDate);
        condition.put("sLocationId", sLocationId);
        condition.put("page", page);
        condition.put("pageSize", pageSize);
        condition.put("sOrder", sOrder);
        condition.put("sCommState", sCommState);
        condition.put("supplierId", supplierId);
        condition.put("modelId", modelId);
        condition.put("gridType", gridType);
        condition.put("fwGadget", fwGadget);
        condition.put("sModuleBuild", sModuleBuild);
        
        Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
        condition.put("deleteCodeId", deleteCode.getId());
        Code BreakDownCode = codeManager.getCodeByCode(ModemSleepMode.BreakDown.getCode());
        condition.put("breakDownCodeId", BreakDownCode.getId());
        Code NormalCode = codeManager.getCodeByCode(ModemSleepMode.Normal.getCode());
        condition.put("normalCodeId", NormalCode.getId());
        Code RepairCode = codeManager.getCodeByCode(ModemSleepMode.Repair.getCode());
        condition.put("repairCodeId", RepairCode.getId());
        Code SecurityErrorCode = codeManager.getCodeByCode(ModemSleepMode.SecurityError.getCode());
        condition.put("securityErrorCodeId", SecurityErrorCode.getId());
        Code CommErrorCode = codeManager.getCodeByCode(ModemSleepMode.CommError.getCode());
        condition.put("commErrorCodeId", CommErrorCode.getId());
        List<Object> modemSearchGrid = modemManager.getModemSearchGrid2(condition, gridType);
        ModelAndView mav = new ModelAndView("jsonView");

        String totalCnt = (String) modemSearchGrid.get(0);
        List gridDataList = (List) modemSearchGrid.get(1);

        mav.addObject("totalCnt", totalCnt);
        mav.addObject("gridData", gridDataList);

        return mav;
    }

    /**
     * @desc : topLeft modem search chart Grid
     * @param sModemType
     * @param sModemId
     * @param sState
     * @param sInstallState
     * @param sMcuType
     * @param sMcuName
     * @param sModemSwVer
     * @param sModemSwRev
     * @param sModemHwVer
     * @param sInstallStartDate
     * @param sInstallEndDate
     * @param sLastcommStartDate
     * @param sLastcommEndDate
     * @param sLocationId
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "gadget/device/getModemSearchChart")
    public ModelAndView getModemSearchChart(
            @RequestParam("sModemType") String sModemType,
            @RequestParam("sModemId") String sModemId,
            @RequestParam("sMcuType") String sMcuType,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sModemFwVer") String sModemFwVer,
            @RequestParam("sModemSwRev") String sModemSwRev,
            @RequestParam("sModemHwVer") String sModemHwVer,

            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,

            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
            @RequestParam("sLocationId") String sLocationId,
            @RequestParam("supplierId") String supplierId,
            @RequestParam(value="sModemStatus", required=false) Integer sModemStatus,
            String sState,
            String sInstallState,
            @RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="limit", required=false) Integer limit,
            @RequestParam(value="sMeterSerial", required=false) String sMeterSerial,
            @RequestParam(value="sModuleBuild", required=false) String sModuleBuild
    		) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("sModemType", sModemType);
        if(sMeterSerial =="" || sMeterSerial == null)
        	condition.put("sModemId", sModemId);
        else{
        	if(sModemId =="" || sModemId == null)
        		condition.put("sModemId", (meterDao.get(sMeterSerial).getModem()).getDeviceSerial());
        	else if(sModemId.equals((meterDao.get(sMeterSerial).getModem()).getDeviceSerial()))
        		condition.put("sModemId", sModemId);
        	else
        		condition.put("sModemId", "no search");
        }
        condition.put("sState", sState);
        condition.put("sInstallState", sInstallState);

        condition.put("sMcuType", sMcuType);
        condition.put("sMcuName", sMcuName);
        condition.put("sModemFwVer", sModemFwVer);
        condition.put("sModemSwRev", sModemSwRev);
        condition.put("sModemHwVer", sModemHwVer);
        condition.put("sModemStatus", sModemStatus);

        condition.put("sInstallStartDate", sInstallStartDate);
        condition.put("sInstallEndDate", sInstallEndDate);

        condition.put("sLastcommStartDate", sLastcommStartDate);
        condition.put("sLastcommEndDate", sLastcommEndDate);
        condition.put("sLocationId", sLocationId);

        condition.put("supplierId", supplierId);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("sModuleBuild", sModuleBuild);
        
        Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
        condition.put("deleteCodeId", deleteCode.getId());
        Code BreakDownCode = codeManager.getCodeByCode(ModemSleepMode.BreakDown.getCode());
        condition.put("breakDownCodeId", BreakDownCode.getId());
        Code NormalCode = codeManager.getCodeByCode(ModemSleepMode.Normal.getCode());
        condition.put("normalCodeId", NormalCode.getId());
        Code RepairCode = codeManager.getCodeByCode(ModemSleepMode.Repair.getCode());
        condition.put("repairCodeId", RepairCode.getId());
        Code SecurityErrorCode = codeManager.getCodeByCode(ModemSleepMode.SecurityError.getCode());
        condition.put("securityErrorCodeId", SecurityErrorCode.getId());
        Code CommErrorCode = codeManager.getCodeByCode(ModemSleepMode.CommError.getCode());
        condition.put("commErrorCodeId", CommErrorCode.getId());

        List<Object> modemSearchGrid = modemManager.getModemSearchChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        if (page != null) {
            mav.addObject("gridData", modemSearchGrid.get(0));
            mav.addObject("totalCnt", modemSearchGrid.get(1));
        } else {
            mav.addObject("chartData", modemSearchGrid.get(0));
        }

        return mav;
    }

    @RequestMapping(value = "gadget/device/getModemLogChart")
    public ModelAndView getModemLogChart(@RequestParam("modemId") String modemId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("modemId", modemId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("supplierId", supplierId);

        List<Object> modemModemLogChart = modemManager.getModemLogChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("chartData", modemModemLogChart.get(0));

        return mav;
    }

/*  모뎀가젯의 history탭 : 구현이 안되어 있는 부분이고 필요없는 기능이라 삭제
    @RequestMapping(value = "gadget/device/getModemLogGrid")
    public ModelAndView getModemLogGrid(
            @RequestParam("modemId") String modemId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("logType") String logType,
            @RequestParam("curPage") String curPage)
    {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("modemId", modemId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("logType", logType);
        condition.put("curPage", curPage);

        List<Object> modemLogGrid = modemManager.getModemLogGrid(condition);

        ModelAndView mav = new ModelAndView("jsonView");


        mav.addObject("totalCnt", modemLogGrid.get(0));
        mav.addObject("gridData", modemLogGrid.get(1));

        return mav;

    }
*/

    /*
     * 2019.07.25 | Ambiguous mapping Error
    @RequestMapping(value = "/gadget/device/updateModem")
    public ModelAndView updateModem(@RequestParam("modemId") Integer modemId,
            @ModelAttribute("modemInfoForm") Modem inModem) {
        ModelAndView mav = new ModelAndView("jsonView");
        return mav;
    }
    */

    @RequestMapping(value = "/gadget/device/getModem")
    public ModelAndView getModem(@RequestParam("modemId") String modemId) {
        Modem modem = modemManager.getModem(Integer.parseInt(modemId));
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("modem", modem);

        return mav;
    }

    @RequestMapping(value = "/gadget/device/getModemByType")
    public ModelAndView getModemByType(@RequestParam("modemId") Integer modemId,
            @RequestParam("modemType") String modemType,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("modemId", modemId);
        condition.put("modemType", modemType);
        
        List<Object> rtnObj = modemManager.getModemByType(condition, supplierId);

        ModelAndView mav = null;

        if (modemType.equals(ModemType.ZRU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoZRU");
        else if (modemType.equals(ModemType.ZEU_PLS.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoZEUPLS");
        else if (modemType.equals(ModemType.MMIU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoMMIU");
        else if (modemType.equals(ModemType.IEIU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoIEIU");
        else if (modemType.equals(ModemType.ZMU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoZMU");
        else if (modemType.equals(ModemType.IHD.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoIHD");
        else if (modemType.equals(ModemType.ACD.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoACD");
        else if (modemType.equals(ModemType.HMU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoHMU");
        else if (modemType.equals(ModemType.PLC_G3.name()) || modemType.equals(ModemType.PLC_PRIME.name()) || modemType.equals(ModemType.PLC_HD.name()) || modemType.equals(ModemType.PLCIU.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoPLCIU");
        else if (modemType.equals(ModemType.ZEU_MBus.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoZEUMBus");
        else if (modemType.equals(ModemType.Repeater.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoZBRepeater");
        else if (modemType.equals(ModemType.Converter_Ethernet.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoConverter");
        else if (modemType.equals(ModemType.SubGiga.name())){
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoSubGiga");
        }
        else if (modemType.equals(ModemType.LTE.name()))
            mav = new ModelAndView("/gadget/device/modemMaxGadgetInfoLTE");
        
        Modem modem = (Modem) rtnObj.get(0);
        Modem pModem;
        if(modem.getParentModemId()!=null){
        	pModem = modemManager.getModem(modem.getParentModemId());
        	mav.addObject("parentDeviceSerial",  pModem.getDeviceSerial());
        }
        DecimalFormat df = DecimalUtil.getIntegerDecimalFormat(modem.getSupplier().getMd());
        
        // 개행문자 변환
        if (modem != null && modem.getAddress() != null) {
            modem.setAddress(modem.getAddress().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "\\\\n"));
        }
        
        // txSize Format적용
        if(modem != null && modem.getRfPower() != null){
	        mav.addObject("txSize",df.format(modem.getRfPower()));
        }
        
        // hopCount Format적용
        if(modemType.equals(ModemType.SubGiga)){
            SubGiga subGigaModem = (SubGiga)modem;
            if(subGigaModem != null && subGigaModem.getHopsToBaseStation() != null){
    	        mav.addObject("hops",df.format(subGigaModem.getHopsToBaseStation()));
            }
         }

        mav.addObject("modem", modem);
        mav.addObject("installDate", rtnObj.get(1));
        mav.addObject("lastLinkTime", rtnObj.get(2));
        
        return mav;
    }

    @RequestMapping(value = "/gadget/device/deleteModem")
    public ModelAndView deleteModem(@RequestParam("modemId") String modemId) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        int resultId;
        try {
        	Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
            resultId = modemManager.deleteModemStatus(Integer.parseInt(modemId), deleteCode);
        } catch (Exception e) {
            resultId = 0;
        }
        mav.addObject("result", resultId);
        return mav;
    }

    // ModemSerial의 자동완성기능
    @RequestMapping(value = "/gadget/device/getModemSerialList")
    public ModelAndView getModemSerialList(@RequestParam("modemSerial") String modemSerial,
            @RequestParam("supplierId") String supplierId) {
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("modemSerial", modemSerial);
        condition.put("supplierId", supplierId);

        List<Object> modemLogGrid = modemManager.getModemSerialList(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("rtnModemSerials", modemLogGrid.get(0));

        return mav;
    }

    // 장비등록 > 개별등록 > 미터 - ModemId의 유효성 확인
    @RequestMapping(value = "/gadget/device/isModemValidateCkByDeviceSerial")
    public ModelAndView isModemValidateCkByDeviceSerial(@RequestParam("deviceSerial") String deviceSerial) {
        Modem modem = modemManager.getModem(deviceSerial);
        int meterCount = 0;
        String modemStatus = null;
        if(modem != null) {
            meterCount = modem.getMeter().size();
            modemStatus = modem.getModemStatus() == null ? null : modem.getModemStatus().getCode();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        if (modem == null)
            mav.addObject("result", "false");
        else if (ModemSleepMode.Delete.getCode().equals(modemStatus))
        	mav.addObject("result", "delete");
        else if (meterCount == 0 || modem.getModemType() == CommonConstants.getModemTypeName("ZEUMBus"))
            mav.addObject("result", "true");
        else if (meterCount != 0) {
        	mav.addObject("result", "false");
        }

        return mav;
    }

    // 장비등록 > 개별등록 > 미터 - ModemId의 중복확인
    @RequestMapping(value = "/gadget/device/isModemDuplicateByDeviceSerial")
    public ModelAndView isModemValidateByDeviceSerial(@RequestParam("deviceSerial") String deviceSerial) {
        Modem modem = modemManager.getModem(deviceSerial);
        String modemStatus = null;
        if(modem != null) {
        	modemStatus = modem.getModemStatus() == null ? null : modem.getModemStatus().getCode();
        }
        
        ModelAndView mav = new ModelAndView("jsonView");
        if (modem == null) {
            mav.addObject("result", "false");
        } else if (ModemSleepMode.Delete.getCode().equals(modemStatus)){
        	mav.addObject("result", "delete");
        } else {
        	mav.addObject("result", "true");
        }

        return mav;
    }

    // Modem Insert ///////////////////////////////////////////////////////
    // ZRU 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemZRU")
    public ModelAndView insertModemZRU(@ModelAttribute("modemInfoFormEdit") ZRU zru,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zru.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zru.setProtocolType(protocolTypeName);

        result = modemManager.insertModemZRU(zru);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZEUPLS 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemZEUPLS")
    public ModelAndView insertModemZEUPLS(@ModelAttribute("modemInfoFormEdit") ZEUPLS zeupls,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zeupls.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zeupls.setProtocolType(protocolTypeName);

        result = modemManager.insertModemZEUPLS(zeupls);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // MMIU 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemMMIU")
    public ModelAndView insertModemMMIU(@ModelAttribute("modemInfoFormEdit") MMIU mmiu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        mmiu.setSupplier(supplier);
        mmiu.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            mmiu.setProtocolType(protocolTypeName);

        result = modemManager.insertModemMMIU(mmiu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // IEIU 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemIEIU")
    public ModelAndView insertModemIEIU(@ModelAttribute("modemInfoFormEdit") IEIU ieiu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        ieiu.setSupplier(supplier);
        ieiu.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            ieiu.setProtocolType(protocolTypeName);

        result = modemManager.insertModemIEIU(ieiu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZMU 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemZMU")
    public ModelAndView insertModemZMU(@ModelAttribute("modemInfoFormEdit") ZMU zmu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        zmu.setSupplier(supplier);
        zmu.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zmu.setProtocolType(protocolTypeName);

        result = modemManager.insertModemZMU(zmu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // IHD 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemIHD")
    public ModelAndView insertModemIHD(@ModelAttribute("modemInfoFormEdit") IHD ihd,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        ihd.setSupplier(supplier);
        ihd.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            ihd.setProtocolType(protocolTypeName);

        result = modemManager.insertModemIHD(ihd);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ACD 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemACD")
    public ModelAndView insertModemACD(@ModelAttribute("modemInfoFormEdit") ACD acd,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        acd.setSupplier(supplier);
        acd.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            acd.setProtocolType(protocolTypeName);

        result = modemManager.insertModemACD(acd);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // HMU 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemHMU")
    public ModelAndView insertModemHMU(@ModelAttribute("modemInfoFormEdit") HMU hmu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        hmu.setSupplier(supplier);
        hmu.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            hmu.setProtocolType(protocolTypeName);

        result = modemManager.insertModemHMU(hmu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // PLC 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemPLCIU")
    public ModelAndView insertModemPLCIU(@ModelAttribute("modemInfoFormEdit") PLCIU plc,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Role role = user.getRoleData();
        plc.setSupplier(supplierManager.getSupplier(role.getSupplier().getId()));

        plc.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            plc.setProtocolType(protocolTypeName);

        result = modemManager.insertModemPLCIU(plc);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZEUMBus
    @RequestMapping(value = "gadget/device/insertModemZEUMBus")
    public ModelAndView insertModemZEUMBus(@ModelAttribute("modemInfoFormEdit") ZEUMBus zeumBus,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        zeumBus.setSupplier(supplier);
        zeumBus.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zeumBus.setProtocolType(protocolTypeName);

        result = modemManager.insertModemZEUMBus(zeumBus);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZBRepeater
    @RequestMapping(value = "gadget/device/insertModemZBRepeater")
    public ModelAndView insertModemZBRepeater(@ModelAttribute("modemInfoFormEdit") ZBRepeater zbRepeater,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        zbRepeater.setSupplier(supplier);
        zbRepeater.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zbRepeater.setProtocolType(protocolTypeName);

        result = modemManager.insertModemZBRepeater(zbRepeater);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // Converter
    @RequestMapping(value = "gadget/device/insertModemConverter")
    public ModelAndView insertModemConverter(@ModelAttribute("modemInfoFormEdit") Converter converter,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        converter.setSupplier(supplier);
        converter.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            converter.setProtocolType(protocolTypeName);

        result = modemManager.insertModemConverter(converter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // SubGiga 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemSubGiga")
    public ModelAndView insertModemSubGiga(@ModelAttribute("modemInfoFormEdit") SubGiga subGiga,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        subGiga.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            subGiga.setProtocolType(protocolTypeName);

        result = modemManager.insertModemSubGiga(subGiga);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // LTE 모뎀 등록
    @RequestMapping(value = "gadget/device/insertModemLTE")
    public ModelAndView insertModemLTE(@ModelAttribute("modemInfoFormEdit") LTE lte,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        lte.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
        	lte.setProtocolType(protocolTypeName);

        result = modemManager.insertModemLTE(lte);
        mav.addObject("id", result.get("id"));

        return mav;
    }
    
    // Modem Update
    // 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModem")
    public ModelAndView updateModem(@ModelAttribute("modemInfoFormEdit") Modem modem,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        modem.setModemType(modemTypeName);
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            modem.setProtocolType(protocolTypeName);

        if(modem.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(modem.getModemStatusCodeId());
        	modem.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModem(modem);
        mav.addObject("id", result.get("id"));

        return mav;
    }
    
    // ZRU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemZRU")
    public ModelAndView updateModemZRU(@ModelAttribute("modemInfoFormEdit") ZRU zru,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zru.setModemType(modemTypeName);
        
        String deviceSerial = zru.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zru.setProtocolType(protocolTypeName);

        if(zru.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(zru.getModemStatusCodeId());
        	zru.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemZRU(zru);
        mav.addObject("id", result.get("id"));

        return mav;
    }
    
    // ZEUPLS 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemZEUPLS")
    public ModelAndView updateModemZEUPLS(@ModelAttribute("modemInfoFormEdit") ZEUPLS zeupls,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zeupls.setModemType(modemTypeName);
        
        String deviceSerial = zeupls.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zeupls.setProtocolType(protocolTypeName);

        if(zeupls.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(zeupls.getModemStatusCodeId());
        	zeupls.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemZEUPLS(zeupls);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // MMIU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemMMIU")
    public ModelAndView updateModemMMIU(@ModelAttribute("modemInfoFormEdit") MMIU mmiu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        
        mmiu.setModemType(modemTypeName);
        
        String deviceSerial = mmiu.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            mmiu.setProtocolType(protocolTypeName);
        
        if(mmiu.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(mmiu.getModemStatusCodeId());
        	mmiu.setModemStatus(modemStatus);
        }

        result = modemManager.updateModemMMIU(mmiu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // IEIU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemIEIU")
    public ModelAndView updateModemIEIU(@ModelAttribute("modemInfoFormEdit") IEIU ieiu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        ieiu.setModemType(modemTypeName);
        
        String deviceSerial = ieiu.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            ieiu.setProtocolType(protocolTypeName);

        if(ieiu.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(ieiu.getModemStatusCodeId());
        	ieiu.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemIEIU(ieiu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZMU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemZMU")
    public ModelAndView updateModemZMU(@ModelAttribute("modemInfoFormEdit") ZMU zmu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zmu.setModemType(modemTypeName);
        
        String deviceSerial = zmu.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zmu.setProtocolType(protocolTypeName);

        if(zmu.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(zmu.getModemStatusCodeId());
        	zmu.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemZMU(zmu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // IHD 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemIHD")
    public ModelAndView updateModemIHD(@ModelAttribute("modemInfoFormEdit") IHD ihd,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        ihd.setModemType(modemTypeName);
        
        String deviceSerial = ihd.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            ihd.setProtocolType(protocolTypeName);

        if(ihd.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(ihd.getModemStatusCodeId());
        	ihd.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemIHD(ihd);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ACD 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemACD")
    public ModelAndView updateModemACD(@ModelAttribute("modemInfoFormEdit") ACD acd,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        acd.setModemType(modemTypeName);
        
        String deviceSerial = acd.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            acd.setProtocolType(protocolTypeName);

        if(acd.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(acd.getModemStatusCodeId());
        	acd.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemACD(acd);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // HMU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemHMU")
    public ModelAndView updateModemHMU(@ModelAttribute("modemInfoFormEdit") HMU hmu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        hmu.setModemType(modemTypeName);
        
        String deviceSerial = hmu.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            hmu.setProtocolType(protocolTypeName);

        if(hmu.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(hmu.getModemStatusCodeId());
        	hmu.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemHMU(hmu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // PLCIU 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemPLCIU")
    public ModelAndView updateModemPLCIU(@ModelAttribute("modemInfoFormEdit") PLCIU plciu,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        plciu.setModemType(modemTypeName);
        
        String deviceSerial = plciu.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            plciu.setProtocolType(protocolTypeName);

        if(plciu.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(plciu.getModemStatusCodeId());
        	plciu.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemPLCIU(plciu);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZEUMBus 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemZEUMBus")
    public ModelAndView updateModemZEUMBus(@ModelAttribute("modemInfoFormEdit") ZEUMBus zeumBus,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zeumBus.setModemType(modemTypeName);
        
        String deviceSerial = zeumBus.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zeumBus.setProtocolType(protocolTypeName);

        if(zeumBus.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(zeumBus.getModemStatusCodeId());
        	zeumBus.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemZEUMBus(zeumBus);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // ZBRepeater 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemZBRepeater")
    public ModelAndView updateModemZBRepeater(@ModelAttribute("modemInfoFormEdit") ZBRepeater zbRepeater,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        zbRepeater.setModemType(modemTypeName);
        
        String deviceSerial = zbRepeater.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            zbRepeater.setProtocolType(protocolTypeName);

        if(zbRepeater.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(zbRepeater.getModemStatusCodeId());
        	zbRepeater.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemZBRepeater(zbRepeater);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // SubGiga 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemSubGiga")
    public ModelAndView updateModemSubGiga(@ModelAttribute("modemInfoFormEdit") SubGiga subGiga,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        subGiga.setModemType(modemTypeName);
        
        String deviceSerial = subGiga.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            subGiga.setProtocolType(protocolTypeName);

        if(subGiga.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(subGiga.getModemStatusCodeId());
        	subGiga.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemSubGiga(subGiga);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // LTE 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemLTE")
    public ModelAndView updateModemLTE(@ModelAttribute("modemInfoFormEdit") LTE lte,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        lte.setModemType(modemTypeName);
        
        String deviceSerial = lte.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
        	lte.setProtocolType(protocolTypeName);

        if(lte.getModemStatusCodeId() != null) {
        	Code modemStatus = codeManager.getCode(lte.getModemStatusCodeId());
        	lte.setModemStatus(modemStatus);
        }
        
        result = modemManager.updateModemLTE(lte);
        mav.addObject("id", result.get("id"));

        return mav;
    }
    
    @RequestMapping(value = "gadget/device/updateModemScheduleZEUPLS")
    public ModelAndView updateModemScheduleZEUPLS(@ModelAttribute("modemScheduleForm") ZEUPLS zeupls) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        ZEUPLS oriZeupls = (ZEUPLS) modemManager.getModem(zeupls.getId());
        MCU mcu = oriZeupls.getMcu();
        String mcuId = mcu.getSysID();
        int revisionNumber = StringUtil.getDigitOnly(mcu.getSysSwRevision());

        int fw = Integer.parseInt(StringUtil.nullCheck(oriZeupls.getFwVer(), "").replace(".", ""));
        int buildNumber = Integer.parseInt(StringUtil.nullCheck(oriZeupls.getFwRevision(), ""));

        boolean cmdResult = false;
        try {
            if (!StringUtil.nullCheck(oriZeupls.getMeteringDay(), "").equals(StringUtil.nullCheck(zeupls.getMeteringDay(), ""))) {
                cmdResult = cmdOperationUtil.doZEUPLSScheduleUpdate(mcuId, revisionNumber, zeupls, "meteringDay", fw,
                        buildNumber);
            }

            if (!StringUtil.nullCheck(oriZeupls.getMeteringHour(), "").equals(
                    StringUtil.nullCheck(zeupls.getMeteringHour(), ""))) {
                cmdResult = cmdOperationUtil.doZEUPLSScheduleUpdate(mcuId, revisionNumber, zeupls, "meteringHour", fw,
                        buildNumber);
            }

            if (oriZeupls.getLpPeriod() != zeupls.getLpPeriod()) {
                cmdResult = cmdOperationUtil.doZEUPLSScheduleUpdate(mcuId, revisionNumber, zeupls, "lpPeriod", fw, buildNumber);
            }

            if (oriZeupls.getLpChoice() != zeupls.getLpChoice()) {
                cmdResult = cmdOperationUtil.doZEUPLSScheduleUpdate(mcuId, revisionNumber, zeupls, "lpChoice", fw, buildNumber);
            }

            if (oriZeupls.getAlarmFlag() != zeupls.getAlarmFlag()) {
                cmdResult = cmdOperationUtil
                        .doZEUPLSScheduleUpdate(mcuId, revisionNumber, zeupls, "alarmFlag", fw, buildNumber);
            }

        } catch (Exception e) {
            cmdResult = false;
            e.printStackTrace();
        }
        if (cmdResult) {
            result = modemManager.updateModemScheduleZEUPLS(zeupls);
        }

        mav.addObject("id", result.get("id"));

        return mav;
    }

    @RequestMapping(value = "gadget/device/updateModemScheduleZBRepeater")
    public ModelAndView updateModemScheduleZBRepeater(@ModelAttribute("modemScheduleForm") ZBRepeater zbrepeater) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        ZEUPLS orizbrepeater = (ZEUPLS) modemManager.getModem(zbrepeater.getId());
        MCU mcu = orizbrepeater.getMcu();
        String mcuId = mcu.getSysID();
        int revisionNumber = StringUtil.getDigitOnly(mcu.getSysSwRevision());

        int fw = Integer.parseInt(StringUtil.nullCheck(orizbrepeater.getFwVer(), "").replace(".", ""));
        int buildNumber = Integer.parseInt(StringUtil.nullCheck(orizbrepeater.getFwRevision(), ""));
        Map<String, Object> param = new HashMap<String, Object>();

        param.put("mcuId", mcuId);
        param.put("modemId", orizbrepeater.getDeviceSerial());
        param.put("revisionNumber", revisionNumber);
        param.put("fw", fw);
        param.put("buildNumber", buildNumber);

        boolean cmdResult = false;
        try {
            if (!StringUtil.nullCheck(orizbrepeater.getMeteringDay(), "").equals(
                    StringUtil.nullCheck(zbrepeater.getMeteringDay(), ""))) {
                param.put("meteringDay", zbrepeater.getMeteringDay());
            }

            if (!StringUtil.nullCheck(orizbrepeater.getMeteringDay(), "").equals(
                    StringUtil.nullCheck(zbrepeater.getMeteringDay(), ""))) {
                param.put("meteringHour", zbrepeater.getMeteringHour());
            }

            if (orizbrepeater.getLpChoice() != zbrepeater.getLpChoice()) {
                param.put("lpChoice", zbrepeater.getLpChoice());
            }

            cmdResult = cmdOperationUtil.doModemScheduleUpdate(param);
        } catch (Exception e) {
            cmdResult = false;
            e.printStackTrace();
        }
        if (cmdResult) {
            result = modemManager.updateModemZBRepeater(zbrepeater);
        }

        mav.addObject("id", result.get("id"));

        return mav;
    }

    @RequestMapping(value = "gadget/device/updateModemScheduleZEUMBus")
    public ModelAndView updateModemScheduleZEUMBus(@ModelAttribute("modemScheduleForm") ZEUMBus zeumbus) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        ZEUPLS oriZeumbus = (ZEUPLS) modemManager.getModem(zeumbus.getId());
        MCU mcu = oriZeumbus.getMcu();
        String mcuId = mcu.getSysID();
        int revisionNumber = StringUtil.getDigitOnly(mcu.getSysSwRevision());

        int fw = Integer.parseInt(StringUtil.nullCheck(oriZeumbus.getFwVer(), "").replace(".", ""));
        int buildNumber = Integer.parseInt(StringUtil.nullCheck(oriZeumbus.getFwRevision(), ""));
        Map<String, Object> param = new HashMap<String, Object>();

        param.put("mcuId", mcuId);
        param.put("modemId", oriZeumbus.getDeviceSerial());
        param.put("revisionNumber", revisionNumber);
        param.put("fw", fw);
        param.put("buildNumber", buildNumber);

        boolean cmdResult = false;
        try {
            if (!StringUtil.nullCheck(oriZeumbus.getMeteringDay(), "").equals(
                    StringUtil.nullCheck(zeumbus.getMeteringDay(), ""))) {
                param.put("meteringDay", zeumbus.getMeteringDay());
            }

            if (!StringUtil.nullCheck(oriZeumbus.getMeteringDay(), "").equals(
                    StringUtil.nullCheck(zeumbus.getMeteringDay(), ""))) {
                param.put("meteringHour", zeumbus.getMeteringHour());
            }

            if (oriZeumbus.getLpChoice() != zeumbus.getLpChoice()) {
                param.put("lpChoice", zeumbus.getLpChoice());
            }

            cmdResult = cmdOperationUtil.doModemScheduleUpdate(param);
        } catch (Exception e) {
            cmdResult = false;
            e.printStackTrace();
        }
        if (cmdResult) {
            result = modemManager.updateModemZEUMBus(zeumbus);
        }

        mav.addObject("id", result.get("id"));

        return mav;
    }

    /**
     * Converter 기본정보 update
     * @return
     */
    // Converter 모뎀 변경
    @RequestMapping(value = "gadget/device/updateModemConverter")
    public ModelAndView updateModemConverter(@ModelAttribute("modemInfoFormEdit") Converter converter,
            @RequestParam("modemTypeName") String modemTypeName,
            @RequestParam(value = "protocolTypeName", required = false) String protocolTypeName) {

        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        converter.setModemType(modemTypeName);
        
        String deviceSerial = converter.getDeviceSerial();
        modemDao.updateModemColumn(modemTypeName, deviceSerial);
        
        if (protocolTypeName != null && protocolTypeName.length() != 0)
            converter.setProtocolType(protocolTypeName);

        result = modemManager.updateModemConverter(converter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "gadget/device/updateModemSchedule")
    public ModelAndView updateModemSchedule(@RequestParam("modemId") String modemId,
            @RequestParam("modemType") String modemType,
            @RequestParam("meteringDay") String meteringDay,
            @RequestParam("meteringHour") String meteringHour,
            @RequestParam("lpChoice") Integer lpChoice) {

        ModelAndView mav = new ModelAndView("jsonView");
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            ZRU oriZru = (ZRU) modemManager.getModem(Integer.parseInt(modemId));

            MCU mcu = oriZru.getMcu();

            String mcuId = mcu.getSysID();
            int revisionNumber = StringUtil.getDigitOnly(mcu.getSysSwRevision());

            int fw = Integer.parseInt(StringUtil.nullCheck(oriZru.getFwVer(), "").replace(".", ""));
            int buildNumber = Integer.parseInt(StringUtil.nullCheck(oriZru.getFwRevision(), ""));
            Map<String, Object> param = new HashMap<String, Object>();

            param.put("mcuId", mcuId);
            param.put("modemId", oriZru.getDeviceSerial());
            param.put("revisionNumber", revisionNumber);
            param.put("fw", fw);
            param.put("buildNumber", buildNumber);

            boolean cmdResult = false;
            try {
                if (!StringUtil.nullCheck(oriZru.getMeteringDay(), "").equals(StringUtil.nullCheck(meteringDay, ""))) {
                    param.put("meteringDay", meteringDay);
                }
                if (!StringUtil.nullCheck(oriZru.getMeteringHour(), "").equals(StringUtil.nullCheck(meteringHour, ""))) {
                    param.put("meteringHour", meteringHour);
                }
                if (oriZru.getLpChoice() != lpChoice) {
                    param.put("lpChoice", lpChoice);
                }
                cmdResult = cmdOperationUtil.doModemScheduleUpdate(param);
            } catch (Exception e) {
                cmdResult = false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (cmdResult) {
                oriZru.setMeteringDay(meteringDay);
                oriZru.setMeteringHour(meteringHour);
                oriZru.setLpChoice(lpChoice);
                result = modemManager.updateModemZRU(oriZru);
            }

            mav.addObject("id", "success");

            return mav;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    //모뎀리스트를 엑셀로 저장하는 페이지
    @RequestMapping(value = "/gadget/device/modemMaxExcelDownloadPopup")
    public ModelAndView modemMaxExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemMaxExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/modemCommInfoExcelDownloadPopup")
    public ModelAndView modemCommInfoExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemCommInfoExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/modemShipmentFileExcelDownloadPopup")
    public ModelAndView mcuShipmentFileExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemShipmentFileExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/modemGetLogExcelDownloadPopup")
    public ModelAndView modemGetLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemGetLogExcelDownloadPopup");
        return mav;
    }

    // 모뎀리스트를 엑셀로 저장
    @RequestMapping(value = "/gadget/device/modemMaxExcelMake")
    public ModelAndView modemMaxExcelMake(
    		@RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("filePath") String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "modemList";// 9

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            conditionMap.put("sModemType", condition[0]);
            conditionMap.put("sModemId", condition[1]);
            conditionMap.put("sState", condition[2]);
            conditionMap.put("sInstallState", condition[3]);
            conditionMap.put("sMcuType", condition[4]);
            conditionMap.put("sMcuName", condition[5]);
            conditionMap.put("sModemFwVer", condition[6]);
            conditionMap.put("sModemSwRev", condition[7]);
            conditionMap.put("sModemHwVer", condition[8]);
            conditionMap.put("sInstallStartDate", condition[9]);
            conditionMap.put("sInstallEndDate", condition[10]);
            conditionMap.put("sLastcommStartDate", condition[11]);
            conditionMap.put("sLastcommEndDate", condition[12]);
            conditionMap.put("sLocationId", condition[13]);
            conditionMap.put("sOrder", condition[14]);
            conditionMap.put("sCommState", condition[15]);
            conditionMap.put("supplierId", condition[16]);
            conditionMap.put("curPage", "1");
            
            Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
            conditionMap.put("deleteCodeId", deleteCode.getId());

            result = (List<Object>) modemManager.getModemListExcel(conditionMap);

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());// 14

            // message 생성
            msgMap.put("no", fmtMessage[0]);
            msgMap.put("id", fmtMessage[1]);
            msgMap.put("type", fmtMessage[2]);
            msgMap.put("mcuid", fmtMessage[3]);
            msgMap.put("vendor", fmtMessage[4]);
            msgMap.put("model", fmtMessage[5]);
            msgMap.put("ver", fmtMessage[6]);
            msgMap.put("lastcomm", fmtMessage[7]);
            msgMap.put("title", fmtMessage[8]);
            msgMap.put("protocolType", fmtMessage[9]);
            msgMap.put("phone", fmtMessage[10]);
            msgMap.put("macAddr", fmtMessage[11]);
            
            // check download dir
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
                // directory 가 없으면 생성
                downDir.mkdir();
            }

            // create excel file
            ModemMaxMakeExcel wExcel = new ModemMaxMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/modemCommInfoExcelMake")
    public ModelAndView modemCommInfoExcelMake(
    		@RequestParam("condition[]") String[] condition,
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

    	final String logPrefix = "ModemCommInfo";
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result = null;
    	
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			
			conditionMap.put("sModemType", condition[0]);
            conditionMap.put("sModemId", condition[1]);
            conditionMap.put("sState", condition[2]);
            conditionMap.put("sInstallState", condition[3]);
            conditionMap.put("sMcuType", condition[4]);
            conditionMap.put("sMcuName", condition[5]);
            conditionMap.put("sModemSwVer", condition[6]);
            conditionMap.put("sModemSwRev", condition[7]);
            conditionMap.put("sModemHwVer", condition[8]);
            conditionMap.put("sInstallStartDate", condition[9]);
            conditionMap.put("sInstallEndDate", condition[10]);
            conditionMap.put("sLastcommStartDate", condition[11]);
            conditionMap.put("sLastcommEndDate", condition[12]);
            conditionMap.put("sLocationId", condition[13]);
            conditionMap.put("sOrder", condition[14]);
            conditionMap.put("sCommState", condition[15]);
            conditionMap.put("supplierId", condition[16]);

			Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
			Code BreakDownCode = codeManager.getCodeByCode(ModemSleepMode.BreakDown.getCode());
			Code NormalCode = codeManager.getCodeByCode(ModemSleepMode.Normal.getCode());
			Code RepairCode = codeManager.getCodeByCode(ModemSleepMode.Repair.getCode());
			Code SecurityErrorCode = codeManager.getCodeByCode(ModemSleepMode.SecurityError.getCode());
			Code CommErrorCode = codeManager.getCodeByCode(ModemSleepMode.CommError.getCode());

			conditionMap.put("deleteCodeId", deleteCode.getId());
			conditionMap.put("breakDownCodeId", BreakDownCode.getId());
			conditionMap.put("normalCodeId", NormalCode.getId());
			conditionMap.put("repairCodeId", RepairCode.getId());
			conditionMap.put("securityErrorCodeId", SecurityErrorCode.getId());
			conditionMap.put("commErrorCodeId", CommErrorCode.getId());
    		
			result = (List<Object>) modemManager.getModemCommInfoListExcel(conditionMap);
    		total = new Integer(result.size()).longValue();

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

    		sbFileName.append(logPrefix);
    		sbFileName.append(TimeUtil.getCurrentTimeMilli());

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
    						// 10일 지난 파일 삭제
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
    		ModemMaxMakeExcel wExcel = new ModemMaxMakeExcel();
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
    
    @RequestMapping(value = "/gadget/device/modemShipmentExcelMake")
    public ModelAndView modemShipmentExcelMake(
    		@RequestParam("supplierId") Integer supplierId,
    		@RequestParam("filePath") String filePath,
    		@RequestParam("detailType") String detailType,
            @RequestParam("model") String model,
            @RequestParam("purchaseOrder") String purchaseOrder,
            @RequestParam("msg_title") String msg_title,
            @RequestParam("msg_number") String msg_number,
            @RequestParam("msg_po") String msg_po,
            @RequestParam("msg_type") String msg_type,
            @RequestParam("msg_euiId") String msg_euiId,
            @RequestParam("msg_gs1") String msg_gs1,
            @RequestParam("msg_model") String msg_model,
            @RequestParam("msg_hwVer") String msg_hwVer,
            @RequestParam("msg_swVer") String msg_swVer,
            @RequestParam("msg_imei") String msg_imei,
            @RequestParam("msg_imsi") String msg_imsi,
            @RequestParam("msg_iccId") String msg_iccId,
            @RequestParam("msg_msisdn") String msg_msisdn,
            @RequestParam("msg_productionDate") String msg_productionDate) {
    	
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        final String logPrefix = detailType + "_Shipment";
        boolean isLast = false;
        Long total = 0L; 		// 데이터 조회건수
        Long maxRows = 5000L; 	// excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
        String protocolType = null;
        
        if (detailType.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName())) {
			detailType = ModemType.MMIU.name();
			protocolType = Protocol.IP.name();
        } else if (detailType.equals(CommonConstants.ShipmentTargetType.EthernetConverter.getName())) {
        	detailType = ModemType.Converter_Ethernet.name(); 
        } else if (detailType.equals(CommonConstants.ShipmentTargetType.MBBModem.getName())) {
			detailType = ModemType.MMIU.name();
			protocolType = Protocol.SMS.name();
        } else if (detailType.equals(CommonConstants.ShipmentTargetType.RFModem.getName())) {
			detailType = ModemType.SubGiga.name();
			protocolType = Protocol.IP.name();
		}

    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("sModemType", detailType);
    	conditionMap.put("protocolType", protocolType);
    	conditionMap.put("modelId", model);						
    	conditionMap.put("purchaseOrder", purchaseOrder);
    	conditionMap.put("curPage", "1");
    	
    	Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode()); // **
        conditionMap.put("deleteCodeId", deleteCode.getId());
    	
    	List<Object> result = null;
    	
    	try {
            result = (List<Object>) modemManager.getModemListExcel(conditionMap);
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
    		msgMap.put("msg_po", msg_po);
    		msgMap.put("msg_type", msg_type);
    		msgMap.put("msg_euiId", msg_euiId);
    		msgMap.put("msg_gs1", msg_gs1);
    		msgMap.put("msg_model", msg_model);
    		msgMap.put("msg_hwVer", msg_hwVer);
    		msgMap.put("msg_swVer", msg_swVer);
    		msgMap.put("msg_imei", msg_imei);
    		msgMap.put("msg_imsi", msg_imsi);
    		msgMap.put("msg_iccId", msg_iccId);
    		msgMap.put("msg_msisdn", msg_msisdn);
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
    		ModemMaxMakeExcel wExcel = new ModemMaxMakeExcel();
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
                        sbSplFileName.append('(').append(++fnum).append(").xls");

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
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return mav;
    }
    
    @RequestMapping(value = "/gadget/device/modemEventLogExcelMake")
    public ModelAndView modemEventLogExcelMake(
    		@RequestParam("supplierId") Integer supplierId,
            @RequestParam("result") String result,
            @RequestParam("filePath") String filePath
            ) {

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        final String mcuListData = "EventLog";

        boolean isLast = false;
        
        try {
            sbFileName.append(mcuListData);
            sbFileName.append(TimeUtil.getCurrentTimeMilli());
            
            // check download dir
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

                        // 파일길이 : 22이상, 확장자 : xls|zip
                        if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(mcuListData) && filename.substring(3, 11).compareTo(deleteDate) < 0) {
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
            ModemMaxMakeExcel wExcel = new ModemMaxMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

         
            sbSplFileName = new StringBuilder();
            sbSplFileName.append(sbFileName);
            sbSplFileName.append(".xls");
            wExcel.writeLogReportExcel(result, filePath, sbSplFileName.toString());
            fileNameList.add(sbSplFileName.toString());
            

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/modemCMDLine")
    public ModelAndView modemCMDLine() {
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        String supplierName = user.getRoleData().getSupplier().getName();
        
        ModelAndView mav = new ModelAndView("/gadget/device/modemCMDLine");
        mav.addObject("supplierName", supplierName);
        
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/getModemProperties")
    public ModelAndView getModemProperties(@RequestParam(value = "modemId", required = false) String modemId) {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));

		mav.addObject("protocolType", modem.getProtocolType());
		mav.addObject("modemType", modem.getModemType());
        
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/getAsyncLogList")
    public ModelAndView getAsyncLogList(
    		@RequestParam(value = "modemId", required = false) String modemId,
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate,
    		@RequestParam(value = "loginId", required = false) String loginId
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		List<Object> result	= new ArrayList<Object>();
		
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
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
    
    @RequestMapping(value = "/gadget/device/getAsyncResult")
    public ModelAndView getAsyncResult(
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
    
    @RequestMapping(value="/gadget/device/asyncResultPopup")
    public ModelAndView AsyncResultPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/asyncResultPopup");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }
    
    // INSERT START SP-681
	@RequestMapping(value="/gadget/device/modemNIHelpPopup")
    public ModelAndView modemNiHelpPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/modemNIHelpPopup");
    	return mav;
    }
    // INSERT END SP-681

	@RequestMapping(value="/gadget/device/codiNIHelpPopup")
    public ModelAndView codiNiHelpPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/codiNIHelpPopup");
    	return mav;
    }
	
	@RequestMapping(value="/gadget/device/getFirmwareVersionList_modem")
    public ModelAndView getFirmwareVersionList(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);

		List<String> versionList = modemManager.getFirmwareVersionList(condition);
		
		mav.addObject("result", versionList);
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/getDeviceList_modem")
    public ModelAndView getDeviceList_dcu(
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = modemManager.getDeviceList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	/* Modem Clone on,off */
	@RequestMapping(value="/gadget/device/getDeviceList_modem_cloneonoff")
    public ModelAndView getDeviceList_Modem(
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
        
		List<String> deviceList = modemManager.getDeviceListModem(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	
	
	@RequestMapping(value="/gadget/device/getTargetList_modem")
    public ModelAndView getTargetListBy_modem(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
    		@RequestParam(value="dcuName") String dcuName,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = modemManager.getTargetList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/getTargetList_modem_cloneonoff")
    public ModelAndView getTargetListBy_modem_cloneonoff(
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
        
		List<String> deviceList = modemManager.getTargetListModem(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
	 @RequestMapping(value = "gadget/device/getModemList")
	    public ModelAndView getModemList(@RequestParam("sModemType") String sModemType,
	            @RequestParam("sModemId") String sModemId,
	            @RequestParam("sMcuType") String sMcuType,
	            @RequestParam("sMcuName") String sMcuName,
	            @RequestParam("sModemFwVer") String sModemFwVer,
	            @RequestParam("sModemSwRev") String sModemSwRev,
	            @RequestParam("sModemHwVer") String sModemHwVer,
	            @RequestParam(value="sModomStatus", required=false) String sModemStatus,

	            @RequestParam("sInstallStartDate") String sInstallStartDate,
	            @RequestParam("sInstallEndDate") String sInstallEndDate,

	            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
	            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
	            @RequestParam("sLocationId") String sLocationId,

	            @RequestParam("page") String page,
	            @RequestParam("pageSize") String pageSize,
	            @RequestParam("sOrder") String sOrder,
	            @RequestParam("sCommState") String sCommState,
	            @RequestParam("supplierId") String supplierId,
	            @RequestParam("gridType") String gridType,
	            @RequestParam("modelId") String modelId,
	            @RequestParam("sMeterSerial") String sMeterSerial,
	            @RequestParam("fwGadget") String fwGadget,
	    		@RequestParam(value="chkParent") Boolean chkParent,
	            String sState,
	            String sInstallState) {

	        Map<String, Object> condition = new HashMap<String, Object>();
	        condition.put("sModemType", sModemType);
	        if(sMeterSerial =="" || sMeterSerial == null)
	        	condition.put("sModemId", sModemId);
	        else{
	        	if(sModemId =="" || sModemId == null)
	        		condition.put("sModemId", (meterDao.get(sMeterSerial).getModem()).getDeviceSerial());
	        	else if(sModemId.equals((meterDao.get(sMeterSerial).getModem()).getDeviceSerial()))
	        		condition.put("sModemId", sModemId);
	        	else
	        		condition.put("sModemId", "no search");
	        }
	        
	        /*// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
	 		if (sLocationId.equals("")) {
	 			List<Location> locationRoot = locationDao.getRootLocationList();
	 			sLocationId = Integer.toString(locationRoot.get(0).getId());
	 		}*/
	        
	        condition.put("sState", sState);
	        condition.put("sInstallState", sInstallState);
	        condition.put("sMcuType", sMcuType);
	        condition.put("sMcuName", sMcuName);
	        condition.put("sModemFwVer", sModemFwVer);
	        condition.put("sModemSwRev", sModemSwRev);
	        condition.put("sModemHwVer", sModemHwVer);
	        condition.put("sModemStatus", sModemStatus);
	        condition.put("sInstallStartDate", sInstallStartDate);
	        condition.put("sInstallEndDate", sInstallEndDate);
	        condition.put("sLastcommStartDate", sLastcommStartDate);
	        condition.put("sLastcommEndDate", sLastcommEndDate);
	        condition.put("sLocationId", sLocationId);
	        condition.put("page", page);
	        condition.put("pageSize", pageSize);
	        condition.put("sOrder", sOrder);
	        condition.put("sCommState", sCommState);
	        condition.put("supplierId", supplierId);
	        condition.put("modelId", modelId);
	        condition.put("gridType", gridType);
	        condition.put("fwGadget", fwGadget);
	        condition.put("chkParent", chkParent);
	        
	        Code deleteCode = codeManager.getCodeByCode(ModemSleepMode.Delete.getCode());
	        condition.put("deleteCodeId", deleteCode.getId());
	        Code BreakDownCode = codeManager.getCodeByCode(ModemSleepMode.BreakDown.getCode());
	        condition.put("breakDownCodeId", BreakDownCode.getId());
	        Code NormalCode = codeManager.getCodeByCode(ModemSleepMode.Normal.getCode());
	        condition.put("normalCodeId", NormalCode.getId());
	        Code RepairCode = codeManager.getCodeByCode(ModemSleepMode.Repair.getCode());
	        condition.put("repairCodeId", RepairCode.getId());
	        Code SecurityErrorCode = codeManager.getCodeByCode(ModemSleepMode.SecurityError.getCode());
	        condition.put("securityErrorCodeId", SecurityErrorCode.getId());
	        Code CommErrorCode = codeManager.getCodeByCode(ModemSleepMode.CommError.getCode());
	        condition.put("commErrorCodeId", CommErrorCode.getId());
	        List<Object> gridDataList = modemManager.getModemList(condition);
	        ModelAndView mav = new ModelAndView("jsonView");

	        mav.addObject("gridData", gridDataList);

	        return mav;
	    }
	 
	@RequestMapping(value="/gadget/device/checkDeviceType")
    public ModelAndView checkDeviceType(
    		@RequestParam(value="deviceId") String deviceId
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
        
		Modem mo = modemDao.get(deviceId);
		ModemType mt = mo.getModemType();
		Protocol pt = mo.getProtocolType();
		String type = "";
		
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
    @RequestMapping(value="/gadget/device/modemMaxGadgetPopup")
    public final ModelAndView modemConnectedMeterPopup(@RequestParam("modemId") String modemId
            , @RequestParam("supplierId")String supplierId) {
            ModelAndView mav = new ModelAndView("/gadget/device/modemMaxGadgetPopup");
            
            List<Code> meterType = codeManager.getChildCodes(Code.METER_TYPE);
            List<Code> meterStatus = codeManager.getChildCodes(Code.METER_STATUS);
           
            mav.addObject("meterType", meterType);
            mav.addObject("meterStatus", meterStatus);
            
            mav.addObject("modemId", modemId);
            mav.addObject("supplierId", supplierId);

            return mav;
    }
    
	/* SP-1004 Clone on탭 Excel search시 parent 정보 출력 */
	@RequestMapping(value="/gadget/device/getParentDeviceList_modem")
    public ModelAndView getParentDevice(
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="supplierId") String supplierId,
    		@RequestParam(value="modelId") String modelId
    		) throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("deviceId", deviceId);
		condition.put("fwVersion", fwVersion);
		condition.put("supplierId", supplierId);
		condition.put("modelId", modelId);
        
		List<Map<String, Object>> deviceList = modemManager.getParentDevice(condition);
		if(deviceList != null) {
			Integer total =deviceList.size(); 
			mav.addObject("total", total);
		}
		
		mav.addObject("result", deviceList);
        return mav;
    }
	
}
