package com.aimir.bo.device;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

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
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MdisMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MdisMeterManager;
import com.aimir.service.device.MeterInstallImgManager;
import com.aimir.service.device.MeterMdisManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ExportExcelGrid;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * MeterMdisController.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 21. v1.0        문동규   MDIS 관련 method 를 기존 Controller(MeterController) 에서 분리
 * </pre>
 */
@Controller
public class MeterMdisController {

    @Autowired
    MeterMdisManager meterMdisManager;

    @Autowired
    MeterInstallImgManager meterInstallImgManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    AimirFilePath aimirFilePath;

    @Autowired
    MdisMeterManager mdisMeterManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    SupplierManager supplierManager;

    @RequestMapping(value="/gadget/device/meterMdisMiniGadget")
    public ModelAndView getMiniChart(@RequestParam(value="meterChart" ,required=false) String meterChart) {
        return new ModelAndView("gadget/device/meterMdisMiniGadget");
    }

    @RequestMapping(value="/gadget/device/meterMdisMaxGadgetInfo")
    public ModelAndView getMaxGadgetInfo() {
        return new ModelAndView("gadget/device/meterMdisMaxGadgetInfo");
    }

    @RequestMapping(value="/gadget/device/meterMdisMaxGadgetEmptyMeter")
    public ModelAndView getMiniChart() {
        return new ModelAndView("gadget/device/meterMdisMaxGadgetEmptyMeter");
    }

    @RequestMapping(value="/gadget/device/meterMdisExcelDownloadPopup")
    public ModelAndView mvmMaxExcelDownloadPopup() {      
        ModelAndView mav = new ModelAndView("/gadget/device/meterMdisExcelDownloadPopup");
        return mav;
    }

    // MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
    @RequestMapping(value="/gadget/device/getMeterMiniChartMdis")
    public ModelAndView getMeterMiniChartMdis(@RequestParam(value="meterChart" ,required=false) String meterChart
                                        , @RequestParam(value="supplierId" ,required=false) String supplierId) {
        Map<String, Object> condition = new HashMap<String, Object>();

        if(meterChart == null)
            meterChart = "ml";

        condition.put("meterChart", meterChart);
        condition.put("supplierId", supplierId);

        List<Object> miniChart = meterMdisManager.getMiniChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("chartData",miniChart.get(0));
        mav.addObject("chartSeries",miniChart.get(1));
        mav.addObject("cell",JSONArray.fromObject(miniChart.get(0)));

        return mav;
    }

    // MeterMaxGadget 초기 검색조건 조회
    @RequestMapping(value="/gadget/device/meterMdisMaxGadget")
    public ModelAndView getMeterMaxGadgetCondition() {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Code> meterType             = codeManager.getChildCodes(Code.METER_TYPE);
        List<Code> meterStatus           = codeManager.getChildCodes(Code.METER_STATUS);
        List<Code> meterHwVer            = codeManager.getChildCodes(Code.METER_HW_VERSION);
        List<Code> meterSwVer            = codeManager.getChildCodes(Code.METER_SW_VERSION);

        Map<String, Object> serchDate    = meterMdisManager.getMeterSearchCondition();
        List<String> lcdDispContents = new ArrayList<String>();

        for (CommonConstants.MdisLcdDisplayContent obj : CommonConstants.MdisLcdDisplayContent.values()) {
            lcdDispContents.add(obj.getMessage());
        }

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("supplierId", supplierId);
        mav.addObject("loginId", user.getLoginId());
        
        Supplier supplier = user.getRoleData().getSupplier();
        String md = supplier.getMd().getPattern();
        int decimalPos = 0;     // 소수점 자릿수

        if (md.indexOf(".") != -1) {
            decimalPos = md.length() - (md.indexOf(".") + 1);
        }
        mav.addObject("decimalPos", decimalPos);
        
        mav.addObject("meterType"    , meterType);
        mav.addObject("meterStatus"  , meterStatus);
        mav.addObject("meterHwVer"   , meterHwVer);
        mav.addObject("meterSwVer"   , meterSwVer);
        mav.addObject("installMinDate"   , serchDate.get("installMinDate"));
        mav.addObject("installMaxDate"   , serchDate.get("installMaxDate"));
        mav.addObject("yesterday"        , serchDate.get("yesterday"));
        mav.addObject("today"            , serchDate.get("today"));
        mav.addObject("lcdDispContents", lcdDispContents);

        // meter control id
        mav.addObject("onDemandCtrlId", MeterCommand.ON_DEMAND_METERING.getId());
        mav.addObject("relayStatusCtrlId", MeterCommand.RELAY_STATUS.getId());
        mav.addObject("relayOnCtrlId", MeterCommand.RELAY_ON.getId());
        mav.addObject("relayOffCtrlId", MeterCommand.RELAY_OFF.getId());
        mav.addObject("timeSyncCtrlId", MeterCommand.TIME_SYNC.getId());
        mav.addObject("swVerCtrlId", MeterCommand.GET_SW_VER.getId());
        mav.addObject("getTamperingCtrlId", MeterCommand.GET_TAMPERING.getId());
        mav.addObject("clsTamperingCtrlId", MeterCommand.CLEAR_TAMPERING.getId());
        mav.addObject("addPrepaidDepositCtrlId", MeterCommand.ADD_PREPAID_DEPOSIT.getId());
        mav.addObject("prepaidRateCtrlId", MeterCommand.SET_PREPAID_RATE.getId());
        mav.addObject("getPrepaidDepositCtrlId", MeterCommand.GET_PREPAID_DEPOSIT.getId());
        mav.addObject("lp1TimingCtrlId", MeterCommand.SET_LP1_TIMING.getId());
        mav.addObject("lp2TimingCtrlId", MeterCommand.SET_LP2_TIMING.getId());
        mav.addObject("meterDirectionCtrlId", MeterCommand.SET_METER_DIRECTION.getId());
        mav.addObject("meterKindCtrlId", MeterCommand.SET_METER_KIND.getId());
        mav.addObject("prepaidAlertCtrlId", MeterCommand.SET_PREPAID_ALERT.getId());
        mav.addObject("displayItemsCtrlId", MeterCommand.SET_METER_DISPLAY_ITEMS.getId());
        mav.addObject("meterResetCtrlId", MeterCommand.SET_METER_RESET.getId());
        mav.addObject("lcdDispContents", lcdDispContents);
        
        mav.setViewName("/gadget/device/meterMdisMaxGadget");

        return mav;
    }

    /**
     * method name : getMeterCommandInitData<b/>
     * method Desc : MDIS - Meter Management 화면에서 Meter Command Popup 창의 기초데이터를 조회한다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/device/getMeterCommandInitData")
    public ModelAndView getMeterCommandInitData() {
        List<List<String>> meterKind = new ArrayList<List<String>>();
        List<List<String>> lcdDispScroll = new ArrayList<List<String>>();
        List<List<String>> lp1Timing = new ArrayList<List<String>>();
        List<List<String>> lp2Pattern = new ArrayList<List<String>>();
        List<List<String>> lp2Timing = new ArrayList<List<String>>();
        List<List<String>> meterDirection = new ArrayList<List<String>>();
        List<String> array = null;
        
        for (CommonConstants.MdisMeterKind obj : CommonConstants.MdisMeterKind.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            meterKind.add(array);
        }

        for (CommonConstants.MdisLcdDisplayScroll obj : CommonConstants.MdisLcdDisplayScroll.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            lcdDispScroll.add(array);
        }

        for (CommonConstants.MdisLp1Timing obj : CommonConstants.MdisLp1Timing.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            lp1Timing.add(array);
        }

        for (CommonConstants.MdisLp2Pattern obj : CommonConstants.MdisLp2Pattern.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            lp2Pattern.add(array);
        }

        for (CommonConstants.MdisLp2Timing obj : CommonConstants.MdisLp2Timing.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            lp2Timing.add(array);
        }

        for (CommonConstants.MdisMeterDirection obj : CommonConstants.MdisMeterDirection.values()) {
            array = new ArrayList<String>();
            array.add(obj.getCode());
            array.add(obj.getMessage());
            meterDirection.add(array);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("meterKindArray", meterKind);
        mav.addObject("lcdDispScrollArray", lcdDispScroll);
        mav.addObject("lp1TimingArray", lp1Timing);
        mav.addObject("lp2PatternArray", lp2Pattern);
        mav.addObject("lp2TimingArray", lp2Timing);
        mav.addObject("meterDirectionArray", meterDirection);

        return mav;
    }

    /**
     * method name : getMeterSearchChartMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 chart 데이터를 조회한다.
     *
     * @param sMeterType
     * @param sMdsId
     * @param sStatus
     * @param sCmdStatus
     * @param sOperators
     * @param sPrepaidDeposit
     * @param sMcuName
     * @param sLocationId
     * @param sConsumLocationId
     * @param sVendor
     * @param sModel
     * @param sInstallStartDate
     * @param sInstallEndDate
     * @param sModemYN
     * @param sCustomerYN
     * @param sLastcommStartDate
     * @param sLastcommEndDate
     * @param supplierId
     * @return
     */
    @RequestMapping(value="gadget/device/getMeterSearchChartMdis")
    public ModelAndView getMeterSearchChartMdis(@RequestParam("sMeterType")         String sMeterType
            , @RequestParam("sMdsId")             String sMdsId
            , @RequestParam("sStatus")            String sStatus

            , @RequestParam("sCmdStatus")         String sCmdStatus
            , @RequestParam("sOperators")         String sOperators
            , @RequestParam("sPrepaidDeposit")    Integer sPrepaidDeposit

            , @RequestParam("sMcuName")           String sMcuName
            , @RequestParam("sLocationId")        String sLocationId
            , @RequestParam("sConsumLocationId")  String sConsumLocationId

            , @RequestParam("sVendor")            String sVendor
            , @RequestParam("sModel")             String sModel
            , @RequestParam("sInstallStartDate")  String sInstallStartDate
            , @RequestParam("sInstallEndDate")    String sInstallEndDate

            , @RequestParam("sModemYN")           String sModemYN
            , @RequestParam("sCustomerYN")        String sCustomerYN
            , @RequestParam("sLastcommStartDate") String sLastcommStartDate
            , @RequestParam("sLastcommEndDate")   String sLastcommEndDate

            , @RequestParam("supplierId")         String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("sMeterType",         sMeterType         );
        condition.put("sMdsId",             sMdsId             );
        condition.put("sStatus",            sStatus            );

        condition.put("sCmdStatus",         sCmdStatus         );
        condition.put("sOperators",         sOperators         );
        condition.put("sPrepaidDeposit",    sPrepaidDeposit    );

        condition.put("sMcuName",           sMcuName           );
        condition.put("sLocationId",        sLocationId        );
        condition.put("sConsumLocationId",  sConsumLocationId  );

        condition.put("sVendor",            sVendor            );
        condition.put("sModel",             sModel             );
        condition.put("sInstallStartDate",  sInstallStartDate  );
        condition.put("sInstallEndDate",    sInstallEndDate    );

        condition.put("sModemYN",           sModemYN           );
        condition.put("sCustomerYN",        sCustomerYN        );
        condition.put("sLastcommStartDate", sLastcommStartDate );
        condition.put("sLastcommEndDate",   sLastcommEndDate   );

        condition.put("supplierId",         supplierId   );

        List<Object> meterSearchChart = meterMdisManager.getMeterSearchChartMdis(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridData"  , meterSearchChart.get(0));
        mav.addObject("chartData" , meterSearchChart.get(1));

        return mav;
    }

    /**
     * method name : getMeterSearchGridMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 미터기 정보 리스트를 조회한다.
     *
     * @param sMeterType
     * @param sMdsId
     * @param sStatus
     * @param sCmdStatus
     * @param sOperators
     * @param sPrepaidDeposit
     * @param sMcuName
     * @param sLocationId
     * @param sConsumLocationId
     * @param sVendor
     * @param sModel
     * @param sInstallStartDate
     * @param sInstallEndDate
     * @param sModemYN
     * @param sCustomerYN
     * @param sLastcommStartDate
     * @param sLastcommEndDate
     * @param curPage
     * @param sOrder
     * @param sCommState
     * @param supplierId
     * @return
     */
    @RequestMapping(value="gadget/device/getMeterSearchGridMdis")
    public ModelAndView getMeterSearchGridMdis(@RequestParam("sMeterType")          String sMeterType
            , @RequestParam("sMdsId")              String sMdsId
            , @RequestParam("sStatus")             String sStatus

            , @RequestParam("sCmdStatus")          String sCmdStatus
            , @RequestParam("sOperators")          String sOperators
            , @RequestParam("sPrepaidDeposit")     Integer sPrepaidDeposit

            , @RequestParam("sMcuName")            String sMcuName
            , @RequestParam("sLocationId")         String sLocationId
            , @RequestParam("sConsumLocationId")   String sConsumLocationId

            , @RequestParam("sVendor")             String sVendor
            , @RequestParam("sModel")              String sModel
            , @RequestParam("sInstallStartDate")   String sInstallStartDate
            , @RequestParam("sInstallEndDate")     String sInstallEndDate

            , @RequestParam("sModemYN")            String sModemYN
            , @RequestParam("sCustomerYN")         String sCustomerYN
            , @RequestParam("sLastcommStartDate")  String sLastcommStartDate
            , @RequestParam("sLastcommEndDate")    String sLastcommEndDate

            , @RequestParam("curPage")             String curPage
            , @RequestParam("sOrder")              String sOrder
            , @RequestParam("sCommState")          String sCommState

            , @RequestParam("supplierId")          String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("sMeterType",         sMeterType         );
        condition.put("sMdsId",             sMdsId             );
        condition.put("sStatus",            sStatus            );

        condition.put("sCmdStatus",         sCmdStatus         );
        condition.put("sOperators",         sOperators          );
        condition.put("sPrepaidDeposit",    sPrepaidDeposit    );

        condition.put("sMcuName",           sMcuName           );
        condition.put("sLocationId",        sLocationId        );
        condition.put("sConsumLocationId",  sConsumLocationId  );

        condition.put("sVendor",            sVendor            );
        condition.put("sModel",             sModel             );
        condition.put("sInstallStartDate",  sInstallStartDate  );
        condition.put("sInstallEndDate",    sInstallEndDate    );

        condition.put("sModemYN",           sModemYN           );
        condition.put("sCustomerYN",        sCustomerYN        );
        condition.put("sLastcommStartDate", sLastcommStartDate );
        condition.put("sLastcommEndDate",   sLastcommEndDate   );

        condition.put("curPage",            curPage   );
        condition.put("sOrder",             sOrder );
        condition.put("sCommState",         sCommState   );

        condition.put("supplierId",         supplierId   );

        List<Object> meterSearchGrid = meterMdisManager.getMeterSearchGridMdis(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt" , meterSearchGrid.get(0));
        mav.addObject("gridData" , meterSearchGrid.get(1));

        return mav;
    }

    @RequestMapping(value="gadget/device/getMeterLogChartMdis")
    public ModelAndView getMeterLogChartMdis(@RequestParam("meterMds")   String meterMds
            , @RequestParam("startDate")  String startDate
            , @RequestParam("endDate")    String endDate
            , @RequestParam("supplierId") String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("meterMds",       meterMds);
        condition.put("startDate",      startDate);
        condition.put("endDate",        endDate);
        condition.put("supplierId",     supplierId);

        List<Object> meterLogChart = meterMdisManager.getMeterLogChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("chartData", meterLogChart.get(0));

        return mav;
    }

    @RequestMapping(value="gadget/device/getMeterLogGridMdis")
    public ModelAndView getMeterLogGridMdis(@RequestParam("meterMds")   String meterMds
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

        List<Object> meterLogGrid = meterMdisManager.getMeterLogGrid(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("totalCnt", meterLogGrid.get(0));
        mav.addObject("gridData", meterLogGrid.get(1));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterInfoMdis")
    public ModelAndView getMeterInfoMdis(@RequestParam("meterId") Integer meterId) {
        Meter meter = meterMdisManager.getMeter(meterId);
        meter.setInstallDate( TimeLocaleUtil.getLocaleDate(meter.getInstallDate()));

        // 개행문자 변환
        if (meter.getAddress() != null) {
            meter.setAddress(meter.getAddress().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "\\\\n"));
        }

        ModelAndView mav = new ModelAndView("/gadget/device/meterMdisMaxGadgetInfo");
        mav.addObject("meter", meter);

        return mav;
    }

    @RequestMapping(value="gadget/device/insertEnergyMeterMdis")
    public ModelAndView insertEnergyMeterMdis(@ModelAttribute("meterInfoFormEdit") EnergyMeter energyMeter){
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Supplier supplier = user.getRoleData().getSupplier();

        energyMeter.setSupplier(supplier);
        result = meterMdisManager.insertEnergyMeter(energyMeter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    @RequestMapping(value="gadget/device/updateMeterMdis")
    public ModelAndView updateMeterMdis(@ModelAttribute("meterInfoFormEdit") Meter meter){
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterMdisManager.updateMeter(meter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // EnergyMeter Update
    @RequestMapping(value="gadget/device/updateEnergyMeterMdis")
    public ModelAndView updateEnergyMeterMdis(@ModelAttribute("meterInstallFormEdit") EnergyMeter energyMeter){
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        result = meterMdisManager.updateEnergyMeter(energyMeter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // 미터 삭제
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="gadget/device/delteMeterMdis")
    public ModelAndView deleteMeterMdis(@ModelAttribute("meterInfoFormEdit") Meter meter
            , HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        ModelAndView mav = new ModelAndView("jsonView");

        // 설치 이미지 삭제
        List <Object> rtnList = meterInstallImgManager.deleteMeterInstallAllImg(meter.getId());
        int rtnListSize = rtnList.size();
        String contextRoot  = new HttpServletRequestWrapper(request).getRealPath("/");

        for(int i=0 ; i < rtnListSize ; i++){
            HashMap saveFile = (HashMap) rtnList.get(i);
            String saveFileName = saveFile.get("saveFileName").toString();
            FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(contextRoot, saveFileName));
        }

        result = meterMdisManager.deleteMeter(meter);
        mav.addObject("id", result.get("id"));

        return mav;
    }

    // 미터 유형별 조회
    @RequestMapping(value="/gadget/device/getMeterByTypeMdis")
    public ModelAndView getMeterByTypeMdis(@RequestParam("meterId")     Integer meterId
            , @RequestParam("meterType")   String meterType) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("meterId",        meterId);
        condition.put("meterType",      meterType);

        Object obj = meterMdisManager.getMeterByType(condition);

        ModelAndView mav = null;

        if(meterType.equals(MeterType.EnergyMeter.toString()))
            mav = new ModelAndView("/gadget/device/meterMdisMaxGadgetEnergyMeter");
        if(meterType.equals(MeterType.WaterMeter.toString()))
            mav = new ModelAndView("/gadget/device/meterMaxGadgetWaterMeter");
        if(meterType.equals(MeterType.GasMeter.toString()))
            mav = new ModelAndView("/gadget/device/meterMaxGadgetGasMeter");
        if(meterType.equals(MeterType.HeatMeter.toString()))
            mav = new ModelAndView("/gadget/device/meterMaxGadgetHeatMeter");
        if(meterType.equals(MeterType.VolumeCorrector.toString()))
            mav = new ModelAndView("/gadget/device/meterMaxGadgetVolumeCorrector");

        mav.addObject("meter", obj);

        return mav;
    }

    @RequestMapping(value="/gadget/device/saveMeterInstallImgFileMdis")
    public ModelAndView saveMeterInstallImgFileMdis(HttpServletRequest request, HttpServletResponse response)throws ServletRequestBindingException, IOException{
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

        ModelAndView mav = new ModelAndView("gadget/device/meterMdisMaxInstallImg");
        mav.addObject("savefilename", viewfilename);

        return mav;
    }

    @RequestMapping(value="/gadget/device/insertMeterInstallImgMdis")
    public ModelAndView insertMeterInstallImgMdis ( @RequestParam("meterId")        Integer meterId
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

    @RequestMapping(value="/gadget/device/getInsertInstallImgMdis")
    public ModelAndView getInstallImgListMdis( @RequestParam("meterId") Integer meterId) throws IOException {
        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = meterInstallImgManager.getMeterInstallImgList(meterId);

        mav.addObject("installImgList", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteMeterInstallImgMdis")
    public ModelAndView deleteMeterInstallImgMdis ( @RequestParam("meterId") Integer meterId
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

    @RequestMapping(value="/gadget/device/getMeteringDataByMeterChartMdis")
    public ModelAndView getMeteringDataByMeterChartMdis(@RequestParam("meterId")        String meterId
            , @RequestParam("meterType")      String meterType
            , @RequestParam("searchStartDate")String searchStartDate
            , @RequestParam("searchEndDate")  String searchEndDate
            , @RequestParam("searchStartHour")String searchStartHour
            , @RequestParam("searchEndHour")  String searchEndHour
            , @RequestParam("searchDateType") String searchDateType
            , @RequestParam("supplierId") String supplierId) throws IOException {
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

        List<Object> result = meterMdisManager.getMeteringDataByMeterChart(condition);
        mav.addObject("chartData", result);

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeteringDataByMeterGridMdis")
    public ModelAndView getMeteringDataByMeterGridMdis(@RequestParam("meterId") String meterId
            , @RequestParam("meterType") String meterType
            , @RequestParam("searchStartDate") String searchStartDate
            , @RequestParam("searchEndDate") String searchEndDate
            , @RequestParam("searchStartHour") String searchStartHour
            , @RequestParam("searchEndHour") String searchEndHour
            , @RequestParam("searchDateType") String searchDateType
            , @RequestParam("supplierId") String supplierId
            , @RequestParam("curPage") String curPage) throws IOException {
        Map<String, Object> condition = new HashMap<String, Object>();

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

        List<Object> result = meterMdisManager.getMeteringDataByMeterGrid(condition);

        mav.addObject("totalCnt", result.get(0));
        mav.addObject("gridData", result.get(1));

        return mav;
    }

    /**
     * method name : getMeterDetailInfoMdis<b/>
     * method Desc :
     *
     * @param meterId
     * @param meterType
     * @param supplierId
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/gadget/device/getMeterDetailInfoMdis")
    public ModelAndView getMeterDetailInfoMdis(@RequestParam("meterId") Integer meterId
            , @RequestParam("meterType") String meterType
            , @RequestParam("supplierId") String supplierId) throws IOException {
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("meterId", meterId);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> result = meterMdisManager.getMeterDetailInfo(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getMdisMeterByMeterId<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 선택된 Meter 의 MdisMeter 정보를 조회한다.
     *
     * @param meterId
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/gadget/device/getMdisMeterByMeterId")
    public ModelAndView getMdisMeterByMeterId(@RequestParam("meterId") Integer meterId) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (meterId == null) {
            mav.addObject("result", null);
            return mav;
        }

        MdisMeter mdisMeter = mdisMeterManager.getMdisMeterByMeterId(meterId);
        mav.addObject("mdisMeter", mdisMeter);
        Contract contract = contractManager.getContractByMeterId(meterId);
        mav.addObject("threshold", (contract != null) ? contract.getPrepaymentThreshold() : null);
        Meter meter = meterMdisManager.getMeter(meterId);
        mav.addObject("conditions", (meter != null && meter.getConditions() != null) ? meter.getConditions() : "");
        EnergyMeter emeter = null;

        Code meterType = meter.getMeterType();
        if (meterType != null && "1.3.1.1".equals(meterType.getCode())) {
            emeter = meterMdisManager.getEnergyMeter(meterId);
            mav.addObject("switchStatus", (emeter.getSwitchStatus() != null) ? emeter.getSwitchStatus().getCode() : "");
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMDFormatNumber")
    public ModelAndView getMDFormatNumber(@RequestParam("supplierId") Integer supplierId, 
            @RequestParam("number") String number) {
        ModelAndView mav = new ModelAndView("jsonView");
        String formattedNumber = null;

        if (StringUtil.nullToBlank(number).isEmpty()) {
            return mav;
        }

        Supplier supplier = supplierManager.getSupplier(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        formattedNumber = mdf.format(Double.valueOf(number));
        mav.addObject("result", formattedNumber);

        return mav;
    }

    @RequestMapping(value="/gadget/device/getTDFormatNumber")
    public ModelAndView getTDFormatNumber(@RequestParam("number") String number) {
        ModelAndView mav = new ModelAndView("jsonView");
        String formattedNumber = null;

        if (StringUtil.nullToBlank(number).isEmpty()) {
            return mav;
        }

        DecimalFormat tdf = new DecimalFormat("##,###");

        formattedNumber = tdf.format(Double.valueOf(number));
        mav.addObject("result", formattedNumber);

        return mav;
    }

    /**
     * method name : getMdisMeterByMeterIdBulkCommand<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 Bulk Meter Command 에서 선택된 Meter 의 MdisMeter 정보를 조회한다.
     *
     * @param meterId
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/gadget/device/getMdisMeterByMeterIdBulkCommand")
    public ModelAndView getMdisMeterByMeterIdBulkCommand(@RequestParam("meterIds[]") String[] meterIds) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (meterIds == null || meterIds.length <= 0) {
            mav.addObject("result", null);
            return mav;
        }

        int len = meterIds.length;
        List<Integer> meterIdList = new ArrayList<Integer>();

        for (int i = 0; i < len; i++) {
            meterIdList.add(Integer.parseInt(meterIds[i]));
        }

        Map<String, Object> result = mdisMeterManager.getMdisMeterByMeterIdBulkCommand(meterIdList);
        mav.addAllObjects(result);
        return mav;
    }

    /**
     * method name : getMeterMdisExportExcel<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 Excel file 을 생성한다.
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
    @RequestMapping(value = "/gadget/device/getMeterMdisExportExcel")
    public ModelAndView getMeterMdisExportExcel(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("sMeterType") String sMeterType,
            @RequestParam("sMdsId") String sMdsId,
            @RequestParam("sStatus") Integer sStatus,
            @RequestParam("sCmdStatus") String sCmdStatus,
            @RequestParam("sOperators") String sOperators,
            @RequestParam("sPrepaidDeposit") Integer sPrepaidDeposit,
            @RequestParam("sMcuName") String sMcuName,
            @RequestParam("sLocationId") Integer sLocationId,
            @RequestParam("sConsumLocationId") String sConsumLocationId,
            @RequestParam("sVendor") Integer sVendor,
            @RequestParam("sModel") Integer sModel,
            @RequestParam("sInstallStartDate") String sInstallStartDate,
            @RequestParam("sInstallEndDate") String sInstallEndDate,
            @RequestParam("sModemYN") String sModemYN,
            @RequestParam("sCustomerYN") String sCustomerYN,
            @RequestParam("sLastcommStartDate") String sLastcommStartDate,
            @RequestParam("sLastcommEndDate") String sLastcommEndDate,
//            @RequestParam("curPage") String curPage,
            @RequestParam("sOrder") String sOrder,
            @RequestParam("sCommState") String sCommState,
            @RequestParam("commStatusMsg[]") String[] commStatusMsg,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("widths[]") String[] widths,
            @RequestParam("aligns[]") String[] aligns,
            @RequestParam("filePath") String filePath,
            @RequestParam(value="title", required=false) String title) {
        String meterPrefix = "meterReport";     // excel file prefix
        int maxRows = 5000;     // excel 파일 당 최대 행수

        ModelAndView mav = new ModelAndView("jsonView");
        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("sMeterType", sMeterType);
        conditionMap.put("sMdsId", sMdsId);
        conditionMap.put("sStatus", sStatus);
        conditionMap.put("sCmdStatus", sCmdStatus);
        conditionMap.put("sOperators", sOperators);
        conditionMap.put("sPrepaidDeposit", sPrepaidDeposit);
        conditionMap.put("sMcuName", sMcuName);
        conditionMap.put("sLocationId", sLocationId);
        conditionMap.put("sConsumLocationId", sConsumLocationId);
        conditionMap.put("sVendor", sVendor);
        conditionMap.put("sModel", sModel);
        conditionMap.put("sInstallStartDate", sInstallStartDate);
        conditionMap.put("sInstallEndDate", sInstallEndDate);
        conditionMap.put("sModemYN", sModemYN);
        conditionMap.put("sCustomerYN", sCustomerYN);
        conditionMap.put("sLastcommStartDate", sLastcommStartDate);
        conditionMap.put("sLastcommEndDate", sLastcommEndDate);
//        conditionMap.put("curPage", curPage   );
        conditionMap.put("sOrder", sOrder);
        conditionMap.put("sCommState", sCommState);
        
        if (commStatusMsg.length == 3) {
            Map<String, String> commMsgMap = new HashMap<String, String>();
            commMsgMap.put("fmtMessage00", commStatusMsg[0]);
            commMsgMap.put("fmtMessage24", commStatusMsg[1]);
            commMsgMap.put("fmtMessage48", commStatusMsg[2]);
            conditionMap.put("commStatusMsg", commMsgMap);
        }

        List<String> headerList = Arrays.asList(headerMsg);
        List<String> alignList = Arrays.asList(aligns);
        List<Integer> widthList = new ArrayList<Integer>();

        for (String obj : widths) {
            if (!StringUtil.nullToBlank(obj).isEmpty()) {
                widthList.add(Integer.parseInt(obj));
            } else {
                widthList.add(null);
            }
        }

        List<List<Object>> result = meterMdisManager.getMeterMdisExportExcelData(conditionMap);

        int total = result.size();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        try {
            sbFileName.append(meterPrefix).append(TimeUtil.getCurrentTimeMilli());

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -1) + "235959"; // 10일 이전 일자

                    for (File file : files) {
                        filename = file.getName();

                        if (filename.startsWith(meterPrefix)
                                && (filename.endsWith("xls") || filename.endsWith("zip"))
                                && (((filename.indexOf("(") != -1) ? filename.indexOf("(") : (filename.length() - 4)) == (meterPrefix
                                        .length() + deleteDate.length()))
                                && filename.substring(meterPrefix.length(),
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
            ExportExcelGrid wExcel = new ExportExcelGrid();
            wExcel.setFilePath(filePath);
            wExcel.setHeaderList(headerList);
            wExcel.setWidthList(widthList);
            wExcel.setAlignList(alignList);
            wExcel.setTitle(title);
//            wExcel.setSearchStartDate(startDateFormat);
//            wExcel.setSearchEndDate(endDateFormat);
//            wExcel.setSearchDateType(searchDateType);

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.setFileName(sbSplFileName.toString());
                wExcel.setDataList(result);
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
                    wExcel.setDataList(sublist);
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