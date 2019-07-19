package com.aimir.bo.device;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.OperationList;
import com.aimir.model.device.OperationLogChartData;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.OperationListManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.device.impl.OperationLogManagerImpl;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.OperationLogMaxMakeExcel;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class OperationLogController<V> {

    @Autowired
    OperationLogManager operationLogManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    LocationManager locationManager;

    @Autowired
    DeviceModelManager deviceModelManager;

    @Autowired
    OperationListManager operationListManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/device/operationLog/operationLogMiniGadget")
    public ModelAndView getOperationLogMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/operationLogMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        String preMonth = null;
        String nowMonth = null;

        try {
            preMonth = TimeUtil.getPreMonth(TimeUtil.getCurrentDay(), 1).substring(0, 8);
            nowMonth = TimeUtil.getCurrentDay().substring(0, 8);
        } catch(ParseException pe) {
            pe.printStackTrace();
        }

        Integer supplierId = user.getRoleData().getSupplier().getId();
        Supplier supplier = supplierManager.getSupplier(supplierId);

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        preMonth = TimeLocaleUtil.getLocaleDate(preMonth,lang,country);
        nowMonth = TimeLocaleUtil.getLocaleDate(nowMonth,lang,country);
        String searchDate = preMonth+"~"+nowMonth;
        mav.addObject("searchDate", searchDate);
        mav.addObject("supplierId", supplierId);
        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationLogMiniChartData")
    public ModelAndView getOperationLogMiniChartData() {
    	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Integer supplierId = user.getRoleData().getSupplier().getId();

        return new ModelAndView("jsonView", new ModelMap("chartDatas", operationLogManager.getOperationLogMiniChartData(supplierId)));
    }

    @RequestMapping(value="/gadget/device/operationLog/operationLogMaxGadget")
    public ModelAndView getOperationLogMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/operationLogMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        //그리드에서 사용하는 코드값과 TargetClass의 코드값이 달라서 
        //cmdController.java에서 등록되는 명령들은 cmdCotroller.java에 맞춰 코드 변경
        //MCU, Meter관련 코드
        List<Code> codeList = codeManager.getChildCodes(Code.TAGET_CLASS);
        for (int i = 0; i < codeList.size(); i++) {
            Code code = codeList.get(i);
            if(CommonConstants.TargetClass.EnergyMeter.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.3.1.1"));
            }
            if(CommonConstants.TargetClass.WaterMeter.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.3.1.2"));
            }
            if(CommonConstants.TargetClass.GasMeter.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.3.1.3"));
            }
            if(CommonConstants.TargetClass.HeatMeter.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.3.1.4"));
            }
            if(CommonConstants.TargetClass.VolumeCorrector.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.3.1.5"));
            }
            
            if(CommonConstants.TargetClass.DCU.getCode().equals(code.getCode()) ){
                codeList.set(i, codeManager.getCodeByCode("1.1.1.7"));  //dcu
                codeList.add(codeManager.getCodeByCode("1.1.1.4"));     //outdoor
                codeList.add(codeManager.getCodeByCode("1.1.1.3"));     //indoor
            }
            
        }
        mav.addObject("targets", codeList);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("cmdAuth", authMap.get("command"));  // Command 권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationLogMaxChartData")
    public ModelAndView getOperationLogMaxChartData(
        @RequestParam("operatorType") String operatorType,
        @RequestParam("userId") String userId,
        @RequestParam("targetType") String targetType,
        @RequestParam("targetName") String targetName,
        @RequestParam("status") String status,
        @RequestParam("description") String description,
        @RequestParam("period") String period,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("operation") String operation,
        @RequestParam("clickControl") String clickControl,     // 클릭된 컨트롤
        @RequestParam("operatorType2") String operatorType2,   // 컬럼차트 클릭시 선택된 그래프의 오퍼레이터타입
        @RequestParam("status2") String status2,               // 컬럼차트 클릭시 선택된 그래프의 실패, 성공 여부
        @RequestParam("date") String date,                     // 컬럼차트 클릭시 선택된 그래프의 해당일자
        @RequestParam("supplierId") String supplierId,
        @RequestParam("page") String page,
        @RequestParam("pageSize") String pageSize) {

        Map<String, String> conditioMap = new HashMap<String, String>();
        conditioMap.put("operatorType", operatorType);
        conditioMap.put("userId", userId);
        conditioMap.put("targetType", targetType);
        conditioMap.put("targetName", targetName);
        conditioMap.put("status", status);
        conditioMap.put("description", description);

        if (period == "") {
            period = CommonConstants.DateType.HOURLY.getCode();
        }

        if (startDate == "" || endDate == "" || "0000".equals(startDate) || "5959".equals(endDate)) {
            try {
                if (period.equals(CommonConstants.DateType.HOURLY.getCode())) {
                    startDate = TimeUtil.getCurrentDay() + "000000";
                    endDate = TimeUtil.getCurrentDay() + "235959";
                } else {
                    startDate = TimeUtil.getCurrentDay();
                    endDate = TimeUtil.getCurrentDay();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        conditioMap.put("period", period);
        conditioMap.put("startDate", startDate);
        conditioMap.put("endDate", endDate);
        conditioMap.put("operation", operation);
        conditioMap.put("date", date);
        conditioMap.put("status2", status2);
        conditioMap.put("page", page);
        conditioMap.put("pageSize", pageSize);
        conditioMap.put("supplierId", supplierId);

        if("columnChart".equals(clickControl))
            conditioMap.put("operatorType", operatorType2);

        ModelAndView mav = new ModelAndView("jsonView");

        // 조회 버튼 클릭시

        mav.addObject("chartDatas", operationLogManager.getColumnChartData(conditioMap));

        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationLogMaxAdvanceData")
    public ModelAndView getOperationLogMaxAdvanceData(
        @RequestParam("operatorType") String operatorType,
        @RequestParam("userId") String userId,
        @RequestParam("targetType") String targetType,
        @RequestParam("targetName") String targetName,
        @RequestParam("status") String status,
        @RequestParam("description") String description,
        @RequestParam("period") String period,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("operation") String operation,
        @RequestParam("clickControl") String clickControl,     // 클릭된 컨트롤
        @RequestParam("operatorType2") String operatorType2,   // 컬럼차트 클릭시 선택된 그래프의 오퍼레이터타입
        @RequestParam("status2") String status2,               // 컬럼차트 클릭시 선택된 그래프의 실패, 성공 여부
        @RequestParam("date") String date,                     // 컬럼차트 클릭시 선택된 그래프의 해당일자
        @RequestParam("supplierId") String supplierId,
        @RequestParam("page") String page,
        @RequestParam("pageSize") String pageSize) {

        Map<String, String> conditioMap = new HashMap<String, String>();
        conditioMap.put("operatorType", operatorType);
        conditioMap.put("userId", userId);
        conditioMap.put("targetType", targetType);
        conditioMap.put("targetName", targetName);
        conditioMap.put("status", status);
        conditioMap.put("description", description);

        if (period == "") {
            period = CommonConstants.DateType.HOURLY.getCode();
        }

        if (startDate == "" || endDate == "" || "0000".equals(startDate) || "5959".equals(endDate)) {
            try {
                if (period.equals(CommonConstants.DateType.HOURLY.getCode())) {
                    startDate = TimeUtil.getCurrentDay() + "000000";
                    endDate = TimeUtil.getCurrentDay() + "235959";
                } else {
                    startDate = TimeUtil.getCurrentDay();
                    endDate = TimeUtil.getCurrentDay();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        conditioMap.put("period", period);
        conditioMap.put("startDate", startDate);
        conditioMap.put("endDate", endDate);
        conditioMap.put("operation", operation);
        conditioMap.put("date", date);
        conditioMap.put("status2", status2);
        conditioMap.put("page", page);
        conditioMap.put("pageSize", pageSize);
        conditioMap.put("supplierId", supplierId);

        if("columnChart".equals(clickControl))
            conditioMap.put("operatorType", operatorType2);

        ModelAndView mav = new ModelAndView("jsonView");

        List<OperationLogChartData>  advanceGriddata =  operationLogManager.getAdvanceGridData(conditioMap);
        mav.addObject("advanceGridDatas",advanceGriddata);
        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationLogMaxGridData")
    public ModelAndView getOperationLogMaxGridData(
        @RequestParam("operatorType") String operatorType,
        @RequestParam("userId") String userId,
        @RequestParam("targetType") String targetType,
        @RequestParam("targetName") String targetName,
        @RequestParam("status") String status,
        @RequestParam("description") String description,
        @RequestParam("period") String period,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("operation") String operation,
        @RequestParam("clickControl") String clickControl,     // 클릭된 컨트롤
        @RequestParam("operatorType2") String operatorType2,   // 컬럼차트 클릭시 선택된 그래프의 오퍼레이터타입
        @RequestParam("status2") String status2,               // 컬럼차트 클릭시 선택된 그래프의 실패, 성공 여부
        @RequestParam("date") String date,                     // 컬럼차트 클릭시 선택된 그래프의 해당일자
        @RequestParam("supplierId") String supplierId,
        @RequestParam("page") String page,
        @RequestParam("pageSize") String pageSize) {
    	
    	Log logger = LogFactory.getLog(OperationLogManagerImpl.class);
        Map<String, String> conditioMap = new HashMap<String, String>();
        conditioMap.put("operatorType", operatorType);
        conditioMap.put("userId", userId);
        conditioMap.put("targetType", targetType);
        conditioMap.put("targetName", targetName);
        conditioMap.put("status", status);
        conditioMap.put("description", description);

        if (period == "") {
            period = CommonConstants.DateType.HOURLY.getCode();
        }

        if (startDate == "" || endDate == "" || "0000".equals(startDate) || "5959".equals(endDate)) {
            try {
            	
            	/*if (period.equals(CommonConstants.DateType.HOURLY.getCode())) {
                    startDate = TimeUtil.getCurrentDay() + "000000";
                    endDate = TimeUtil.getCurrentDay() + "235959";
                } else {
                    startDate = TimeUtil.getCurrentDay();
                    endDate = TimeUtil.getCurrentDay();
                }*/
            	
            	//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
            	startDate = TimeUtil.getCurrentDay() + "000000";
                endDate = TimeUtil.getCurrentDay() + "235959";
                
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        conditioMap.put("period", period);
        conditioMap.put("startDate", startDate);
        conditioMap.put("endDate", endDate);
        conditioMap.put("operation", operation);
        conditioMap.put("date", date);
        conditioMap.put("status2", status2);
        conditioMap.put("page", page);
        conditioMap.put("pageSize", pageSize);
        conditioMap.put("supplierId", supplierId);

        if("columnChart".equals(clickControl))
            conditioMap.put("operatorType", operatorType2);

        ModelAndView mav = new ModelAndView("jsonView");

        
        // 조회 버튼 클릭시
        mav.addObject("gridDatas", operationLogManager.getGridData(conditioMap, supplierId));
        mav.addObject("total", operationLogManager.getOperationLogMaxGridDataCount(conditioMap));
        
        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationLogMaxGridDataCount")
    public ModelAndView getOperationLogMaxGridDataCount(

        @RequestParam("operatorType") String operatorType,
        @RequestParam("userId") String userId,
        @RequestParam("targetType") String targetType,
        @RequestParam("targetName") String targetName,
        @RequestParam("status") String status,
        @RequestParam("description") String description,
        @RequestParam("period") String period,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("operation") String operation,
        @RequestParam("clickControl") String clickControl,     // 클릭된 컨트롤
        @RequestParam("operatorType2") String operatorType2,   // 컬럼차트 클릭시 선택된 그래프의 오퍼레이터타입
        @RequestParam("status2") String status2,               // 컬럼차트 클릭시 선택된 그래프의 실패, 성공 여부
        @RequestParam("date") String date) {                   // 컬럼차트 클릭시 선택된 그래프의 해당일자

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("operatorType", operatorType);
        conditionMap.put("userId", userId);
        conditionMap.put("targetType", targetType);
        conditionMap.put("targetName", targetName);
        conditionMap.put("status", status);
        conditionMap.put("description", description);
        conditionMap.put("period", period);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("operation", operation);
        conditionMap.put("date", date);
        conditionMap.put("status2", status2);

        if("columnChart".equals(clickControl))
            conditionMap.put("operatorType", operatorType2);

        ModelMap model = new ModelMap();
        model.addAttribute("total", operationLogManager.getOperationLogMaxGridDataCount(conditionMap));

        return new ModelAndView("jsonView", model);
    }

    @RequestMapping(value="/gadget/device/operationLog/getEquipments")
    public ModelAndView getEquipments() {


        List<Code> equipmentCodes = codeManager.getChildCodes(Code.EQUIPMENT);

        List<JsonTree> jsonTrees = makeDeviceJsonTree(equipmentCodes);

        return new ModelAndView("jsonView", new ModelMap("jsonTree", jsonTrees));
    }

    // 장비 목록 트리
    private List<JsonTree> makeDeviceJsonTree(List<Code> devices) {

        List<JsonTree> deviceJsonTrees = new ArrayList<JsonTree>();
        JsonTree deviceJsonTree = null;
        Map<String, String> attrbuteMap = null;

        List<Code> deviceTypeCodes = null;
        List<JsonTree> deviceTypeJsonTrees = null;

        for(Code device : devices) {

            deviceTypeCodes = codeManager.getChildCodes(device.getCode() + ".1");
            deviceTypeJsonTrees = makeDeviceTypeJsonTrees(deviceTypeCodes);

            attrbuteMap = new HashMap<String, String>();
            attrbuteMap.put("id", "device_" + device.getId());
            attrbuteMap.put("rel", "device");

            deviceJsonTree = new JsonTree();
            deviceJsonTree.setData(device.getName());
            deviceJsonTree.setState("");
            deviceJsonTree.setAttributes(attrbuteMap);
            deviceJsonTree.setChildren(deviceTypeJsonTrees);

            deviceJsonTrees.add(deviceJsonTree);
        }

        return deviceJsonTrees;
    }

    // 장비 타입 트리
    private List<JsonTree> makeDeviceTypeJsonTrees(List<Code> deviceTypeCodes) {

        List<JsonTree> deviceTypeJsonTrees = new ArrayList<JsonTree>();
        JsonTree deviceTypeJsonTree = null;
        List<JsonTree> deviceModelTrees = null;

        Map<String, String> attrbuteMap = null;

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        for(Code deviceTypeCode : deviceTypeCodes) {

            deviceModelTrees = makeDeviceVendorTrees(supplierId, deviceTypeCode);

            attrbuteMap = new HashMap<String, String>();
            attrbuteMap.put("id", "deviceType_" + deviceTypeCode.getId());
            attrbuteMap.put("rel", "deviceType");

            deviceTypeJsonTree = new JsonTree();
            deviceTypeJsonTree.setData(deviceTypeCode.getName());
            deviceTypeJsonTree.setState("open");
            deviceTypeJsonTree.setAttributes(attrbuteMap);
            deviceTypeJsonTree.setChildren(deviceModelTrees);

            deviceTypeJsonTrees.add(deviceTypeJsonTree);
        }

        return deviceTypeJsonTrees;
    }

    // 디바이스 벤더  트리
    private List<JsonTree> makeDeviceVendorTrees(int supplierId, Code deviceTypeCode) {

        List<JsonTree> deviceVendorTrees = new ArrayList<JsonTree>();
        JsonTree deviceVendorTree = null;
        Map<String, String> attrbuteMap = null;
        DeviceVendor deviceVendor = null;

        // FIXME deviceTypeId 잘못 연결되어 있음.
        List<DeviceModel> deviceModels = deviceModelManager.getDeviceModelByTypeId(supplierId, deviceTypeCode.getId());
        Map<String, DeviceVendor> vendorMaps = new HashMap<String, DeviceVendor>();

        for(DeviceModel deviceModel : deviceModels)
            vendorMaps.put(deviceModel.getDeviceVendor().getId() + "",  deviceModel.getDeviceVendor());

        Set<String> vendorIds = vendorMaps.keySet();

        for(String vendorId : vendorIds) {

            deviceVendor = vendorMaps.get(vendorId);
            List<JsonTree> deviceModelTrees = makeDeviceModelTrees(deviceVendor.getDeviceModels());

            attrbuteMap = new HashMap<String, String>();
            attrbuteMap.put("id", "deviceVendor_" + deviceVendor.getId());
            attrbuteMap.put("rel", "deviceVendor");

            deviceVendorTree = new JsonTree();
            deviceVendorTree.setData(deviceVendor.getName());
            deviceVendorTree.setState("open");
            deviceVendorTree.setAttributes(attrbuteMap);
            deviceVendorTree.setChildren(deviceModelTrees);

            deviceVendorTrees.add(deviceVendorTree);
        }

        return deviceVendorTrees;
    }

    // 디바이스 모델 트리
    private List<JsonTree> makeDeviceModelTrees(List<DeviceModel> deviceModels) {

        List<JsonTree> deviceModelTrees = new ArrayList<JsonTree>();
        JsonTree deviceModelTree = null;
        Map<String, String> attrbuteMap = null;

        for(DeviceModel deviceModel : deviceModels) {

            deviceModelTree = new JsonTree();
            attrbuteMap = new HashMap<String, String>();
            attrbuteMap.put("id", deviceModel.getId() + "");
            attrbuteMap.put("rel", "deviceModel");

            deviceModelTree = new JsonTree();
            deviceModelTree.setData(deviceModel.getName());
            deviceModelTree.setState("open");
            deviceModelTree.setAttributes(attrbuteMap);
            deviceModelTree.setChildren(deviceModelTrees);

            deviceModelTrees.add(deviceModelTree);
        }

        return deviceModelTrees;
    }

    private JsonTree makeJsonTree(Code code, int treeDepth) {

        JsonTree jsonTree = new JsonTree();
        Map<String, String> attrbuteMap = null;
        List<JsonTree> childrens = null;
        String hasChild = "false";

        Set<Code> childCodes = code.getChildren();

        if(childCodes != null && childCodes.size() > 0) {

            childrens = new ArrayList<JsonTree>();
            hasChild = "true";

            for(Code childLocation : childCodes) {

                childrens.add(makeJsonTree(childLocation, 2));
            }
        }

        attrbuteMap = new HashMap<String, String>();
        attrbuteMap.put("operationCodeId", code.getId() + "");
        attrbuteMap.put("hasChild", hasChild);
        attrbuteMap.put("depth", treeDepth + "");

        jsonTree.setData(code.getName());
        jsonTree.setState("open");
        jsonTree.setAttributes(attrbuteMap);
        jsonTree.setChildren(childrens);

        return jsonTree;
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperations")
    public ModelAndView getOperations() {

        List<Code> operationCodes = codeManager.getChildCodes(Code.OPERATION);

        List<JsonTree> jsonTrees = new ArrayList<JsonTree>();

        for(Code operationCode : operationCodes) {
            jsonTrees.add(makeJsonTree(operationCode, 1));
        }

        return new ModelAndView("jsonView", new ModelMap("jsonTree", jsonTrees));
    }

    @RequestMapping(value="/gadget/device/operationLog/getOperationAvailable")
    public ModelAndView getOperationAvailable(

        @RequestParam("deviceModelId") int deviceModelId,
        @RequestParam("operationCode") String operationCode) {

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(deviceModelId);
        Set<OperationList> operationList = deviceModel.getOperationList();

        for(OperationList list : operationList){
            if(list.getOperationCode().getCode().equals(operationCode)){
                mav.addObject("status", "true");
                return mav;
            }
        }
        mav.addObject("status", "false");
        return mav;
    }

    @RequestMapping(value="/gadget/device/operationLog/getEquipmentGridData")
    public ModelAndView getEquipmentGridData(

        @RequestParam("deviceModelId") int deviceModelId) {

        ModelAndView mav = new ModelAndView("jsonView");
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(deviceModelId);
        Set<OperationList> operationList = new HashSet<OperationList>();
        if(deviceModel != null){
            operationList=  deviceModel.getOperationList();
        }
        int i=0;
        List <Map<String,Object>> resultList =  new ArrayList<Map<String,Object>>();
        if(operationList !=null && !operationList.isEmpty() && operationList.size()!=0){

            Map<String,Object> operationMap = null;
            for(OperationList list : operationList){
                operationMap =  new HashMap<String,Object>();
                operationMap.put("id", list.getId());
                operationMap.put("no", i++);
                operationMap.put("level",list.getLevel());
                operationMap.put("modelId",list.getModelId() );
                operationMap.put("operation", list.getOperationCode().getDescr());
                operationMap.put("desc", list.getOperationCode().getDescr());
                resultList.add(operationMap);
            }
        }
        mav.addObject("gridDatas", resultList);
        mav.addObject("total", operationList.size());
        return mav;
    }


    @RequestMapping(value="/gadget/device/operationLog/getOperationGridData")
    public ModelAndView getOperationGridData(

        @RequestParam("operationCodeId") int operationCodeId) {

        ModelAndView mav = new ModelAndView("jsonView");
        List <Map<String,Object>> resultList =  new ArrayList<Map<String,Object>>();
        List<OperationList> operationList = operationLogManager.getOperationGridData(operationCodeId);
        int i=1;
        if(operationList !=null && !operationList.isEmpty() && operationList.size()!=0){

            Map<String,Object> operationMap = null;
            for(OperationList list : operationList){
                operationMap =  new HashMap<String,Object>();
                operationMap.put("id", list.getId());
                operationMap.put("no", i++);
                operationMap.put("level",list.getLevel());
                operationMap.put("modelId",list.getModelId());
                operationMap.put("equipment", list.getEquipment());
                operationMap.put("desc", list.getDesc());
                resultList.add(operationMap);
            }
        }

        mav.addObject("gridDatas", resultList);
        mav.addObject("total", operationList.size());
        return mav;
    }


    @RequestMapping(value="/gadget/device/operationLog/updateOperation")
    public ModelAndView updateOperationList(

            @RequestParam("updateParam") String updateParam) {

        operationLogManager.updateOperation(updateParam);

        return new ModelAndView("jsonView", new ModelMap("result", "success"));
    }

    /**
     * method name : getOperationListAvailable<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 Meter 를 선택할 때 실행 가능한 Meter Command 리스트를 조회한다.
     *
     * @param deviceModelId
     * @param operationCodes
     * @return
     */
    @RequestMapping(value = "/gadget/device/operationLog/getOperationListAvailable")
    public ModelAndView getOperationListAvailable(@RequestParam("modelId") Integer modelId,
            @RequestParam("operationCodes[]") String[] operationCodes) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> omap = new HashMap<String, Object>();
        int len = operationCodes.length;
        List<String> operationCodeList = new ArrayList<String>();

        for (int i = 0; i < len; i++) {
            omap = new HashMap<String, Object>();
            operationCodeList.add(operationCodes[i]);
            omap.put("code", operationCodes[i]);
            omap.put("status", "false");
            result.add(omap);
        }

        List<OperationList> list = operationListManager.getOperationListByModelId(modelId, operationCodeList);

        if (list != null) {
            for (OperationList obj : list) {
                for (Map<String, Object> map : result) {
                    if (obj.getOperationCode().getCode().equals((String)map.get("code"))) {
                        map.put("status", "true");
                        break;
                    }
                }
            }
        }
        mav.addObject("result", result);
        return mav;
    }

    //operationlog리스트를 엑셀로 저장하는 페이지
    @RequestMapping(value = "/gadget/device/operationLogMaxExcelDownloadPopup")
    public ModelAndView operationLogMaxExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView(
                "/gadget/device/operationLogMaxExcelDownloadPopup");
        return mav;
    }
  //operationlog리스트를 엑셀로 저장
    @RequestMapping(value = "/gadget/device/operationLogMaxExcelMake")
    public ModelAndView operationLogMaxExcelMake(
            @RequestParam("condition[]")    String[] condition,
            @RequestParam("fmtMessage[]")   String[] fmtMessage,
            @RequestParam("filePath")       String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "operatorLogList";//9

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = null;
        try {
            Map<String, String> conditionMap = new HashMap<String, String>();

                conditionMap.put("operatorType", condition[0]);
                conditionMap.put("userId"      , condition[1]);
                conditionMap.put("targetType"  , condition[2]);
                conditionMap.put("targetName"  , condition[3]);
                conditionMap.put("status"      , condition[4]);
                conditionMap.put("description" , condition[5]);
                conditionMap.put("period"      , condition[6]);
                conditionMap.put("startDate"   , condition[7]);
                conditionMap.put("endDate"     , condition[8]);
                conditionMap.put("supplierId"  , condition[9]);
                conditionMap.put("operation"   , condition[10]);
                conditionMap.put("clickControl", condition[11]);
                conditionMap.put("operatorType2",condition[12]);
                conditionMap.put("status2"     , condition[13]);
                conditionMap.put("date"        , condition[14]);
                conditionMap.put("page"        , condition[15]);
                conditionMap.put("pageSize"    , condition[16]);
                conditionMap.put("isExcel"       , "true");


            result = (List<Object>) operationLogManager.getOpeartionLogListExcel(conditionMap);

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

            // message 생성
            msgMap.put("no"             ,    fmtMessage[0]);
            msgMap.put("openTime"       ,    fmtMessage[1]);
            msgMap.put("targetType"     ,    fmtMessage[2]);
            msgMap.put("targetName"     ,    fmtMessage[3]);
            msgMap.put("accomplishmentType", fmtMessage[4]);
            msgMap.put("accomplisher"   ,    fmtMessage[5]);
            msgMap.put("operation"      ,    fmtMessage[6]);
            msgMap.put("operationStatus",    fmtMessage[7]);
            msgMap.put("description"    ,    fmtMessage[8]);
            msgMap.put("message"        ,    fmtMessage[9]);
            msgMap.put("title"          ,    fmtMessage[10]);


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
            OperationLogMaxMakeExcel wExcel = new OperationLogMaxMakeExcel();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mav;
    }
}