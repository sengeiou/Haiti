package com.aimir.bo.device;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.CommandType;
import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUInstallImg;
import com.aimir.model.device.MCUVar;
import com.aimir.model.device.Modem;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.MCUInstallImgManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.McuDataMakeExcel;
import com.aimir.util.McuGroupScheduleMakeExcel;
import com.aimir.util.McuLogMakeExcel;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class MCUController {
	private static Log log = LogFactory.getLog(MCUController.class);
	
    @Autowired
    MCUManager mcuManager;

    @Autowired
    MCUInstallImgManager mcuInstallImgManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    DeviceModelManager deviceModelManager;

    @Autowired
    LocationManager locationManager;

    @Autowired
    AimirFilePath aimirFilePath;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    GroupMgmtManager groupMgmtManager;
    
    @Autowired
    OperatorManager operatorManager;

    @Autowired
	CmdOperationUtil cmdOperationUtil;
    
	@Autowired
	OperationLogManager operationLogManager;
    
    @RequestMapping(value="/gadget/device/mcuMiniGadget")
    public ModelAndView loadMcuMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMiniGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);

        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuMaxGadget")
    public ModelAndView loadMcuMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMaxGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        String loginId = user.getLoginId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("loginId", loginId);

        List<Code> mcuTypeMap = codeManager.getChildCodes(Code.MCU_TYPE);
        List<Code> hwVersions = codeManager.getChildCodes(Code.MCU_HW_VERSION);
        List<Code> swVersions = codeManager.getChildCodes(Code.MCU_SW_VERSION);
        List<Code> protocols = codeManager.getChildCodes(Code.PROTOCOL);
        List<Code> mcuStatus = codeManager.getChildCodes(Code.MCU_STATUS);
        Map<String, List<String>> locationMap = mcuManager.getLocationTreeToRows(supplierId);
        List<Map<String, String>> deviceModels = deviceModelManager.getMCUDeviceModel();
        List<Location> locations = locationManager.getLocationsBySupplierId(supplierId);

        mav.addObject("mcuTypeMap", mcuTypeMap);
        mav.addObject("hwVersions", hwVersions);
        mav.addObject("swVersions", swVersions);
        mav.addObject("protocols", protocols);
        mav.addObject("keys", locationMap.get("keys"));
        mav.addObject("locationNames", locationMap.get("locationNames"));
        mav.addObject("deviceModels", deviceModels);
        mav.addObject("locations", locations);
        mav.addObject("mcuStatus", mcuStatus);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("cmdAuth", authMap.get("command"));  // Command권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/device/getGridData")
    @Deprecated
    public ModelAndView getGridData(
        @RequestParam("supplierId") String supplierId,
        @RequestParam("mcuId") String mcuId,
        @RequestParam("mcuType") String mcuType,
        @RequestParam("locationId") String locationId,
        @RequestParam("swVersion") String swVersion,
        @RequestParam("hwVersion") String hwVersion,
        @RequestParam("installDateStart") String installDateStart,
        @RequestParam("installDateEnd") String installDateEnd,
        @RequestParam("page") String page,
        @RequestParam("filter") String filter,
        @RequestParam("order") String order,
        @RequestParam("protocol") String protocol,
        @RequestParam("pageSize") String pageSize) {
        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("hwVersion",hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("page", page);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("protocol", protocol);
        conditionMap.put("pageSize", pageSize);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridDatas", mcuManager.getGridData(conditionMap));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getDcuGridData")
    public ModelAndView getDcuGridData (@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuSerial") String mcuSerial, 
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("swRevison") String swRevison,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("lastcommStartDate") String lastcommStartDate,
            @RequestParam("lastcommEndDate") String lastcommEndDate,
            @RequestParam("filter") String filter,
            @RequestParam("order") String order,
            @RequestParam("protocol") String protocol,
            @RequestParam("mcuStatus") String mcuStatus,
            @RequestParam("fwGadget") String fwGadget,
            @RequestParam("modelId") String modelId) {
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuSerial", mcuSerial);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("swRevison", swRevison);
        conditionMap.put("hwVersion",hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("lastcommStartDate", lastcommStartDate);
        conditionMap.put("lastcommEndDate", lastcommEndDate);
        conditionMap.put("page", page);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("protocol", protocol);
        conditionMap.put("limit", limit);
        conditionMap.put("mcuStatus", mcuStatus);
        conditionMap.put("modelId", modelId);
        conditionMap.put("fwGadget", fwGadget);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", mcuManager.getDcuGridData(conditionMap));
        mav.addObject("totalCount", mcuManager.getDcuGridDataTotalCount(conditionMap));

        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getDcuCodiGridData")
    public ModelAndView getCodiGridData (@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuSerial") String mcuSerial, 
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("swRevison") String swRevison,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("lastcommStartDate") String lastcommStartDate,
            @RequestParam("lastcommEndDate") String lastcommEndDate,
            @RequestParam("filter") String filter,
            @RequestParam("order") String order,
            @RequestParam("protocol") String protocol,
            @RequestParam("mcuStatus") String mcuStatus,
            @RequestParam("fwGadget") String fwGadget,
            @RequestParam("modelId") String modelId) {
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuSerial", mcuSerial);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("swRevison", swRevison);
        conditionMap.put("hwVersion",hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("lastcommStartDate", lastcommStartDate);
        conditionMap.put("lastcommEndDate", lastcommEndDate);
        conditionMap.put("page", page);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("protocol", protocol);
        conditionMap.put("limit", limit);
        conditionMap.put("mcuStatus", mcuStatus);
        conditionMap.put("modelId", modelId);
        conditionMap.put("fwGadget", fwGadget);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", mcuManager.getCodiGridData(conditionMap));
        mav.addObject("totalCount", mcuManager.getCodiGridDataTotalCount(conditionMap));

        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuInfo")
    public ModelAndView getMCU(

            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") Integer mcuId,
            @RequestParam("imgPage") int imgPage) {

        MCU mcu = mcuManager.getMCU(mcuId);
        int imgSize = mcu.getMcuInstallImgs().size();

        // 개행문자 변환
        if (mcu != null && mcu.getSysLocation() != null) {
            mcu.setSysLocation(mcu.getSysLocation().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "\\\n"));
        }

        ModelAndView mav = new ModelAndView("/gadget/device/mcuInfo");
        mav.addObject("mcu", mcu);
        mav.addObject("paging", mcuManager.getPagingInfo(imgPage, imgSize, "IMG"));
        mav.addObject("pagingType", "IMG");
        mav.addObject("mcuTypes", codeManager.getChildCodes(Code.MCU_TYPE));
        mav.addObject("hwVersions", codeManager.getChildCodes(Code.MCU_HW_VERSION));
        mav.addObject("swVersions", codeManager.getChildCodes(Code.MCU_SW_VERSION));
        mav.addObject("protocolCodes", codeManager.getChildCodes(Code.PROTOCOL));
        mav.addObject("mcuStatus", codeManager.getChildCodes(Code.MCU_STATUS));
        mav.addObject("deviceModels", deviceModelManager.getMCUDeviceModel());
        mav.addObject("installDateShow", mcuManager.getInstallDateChage(mcu, supplierId)); //화면 출력용 시간
        mav.addObject("installDate", mcu.getInstallDate());                                 // 히든값
        mav.addObject("lastCommDate", mcuManager.getLastCommDateChage(mcu, supplierId));

        if(imgSize > 0 ) {
            // mcuInstallImgs는 rowId 으로 정렬되어 있으므로 (페이지 번호 - 1)은 순서인덱스와 같음
            MCUInstallImg dii = mcu.getMcuInstallImgs().get(imgPage - 1);
            mav.addObject("mcuInstallImgId", dii.getId());
            mav.addObject("currentTimeMillisName", dii.getCurrentTimeMillisName());
        } else {
            mav.addObject("mcuInstallImgId", "");
            mav.addObject("currentTimeMillisName", "");
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/updateMCUInstallInfo")
    public String updateMCUInstallInfo(@RequestParam("supplierId") String supplierId,
            @RequestParam("installDate") String installDate,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("protocolType") String protocolType,
            @RequestParam("lastswUpdateDate") String lastswUpdateDate,
            @RequestParam("ipAddr") String ipAddr,
            @RequestParam("sysPhoneNumber") String sysPhoneNumber,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("imgPage") String imgPage,
            @RequestParam("locationId") String locationId,
            @RequestParam("sysLocation") String sysLocation,
            @RequestParam("sysLocalPort") String sysLocalPort,
            @RequestParam("sysHwBuild") String sysHwBuild,
            @RequestParam("sysSerialNumber") String sysSerialNumber,
            @RequestParam("sysTlsPort") String sysTlsPort,
            @RequestParam("sysTlsVersion") String sysTlsVersion,
            @RequestParam(value="mcuStatus", required=false) String mcuStatus,
            //sjhan
            @RequestParam(value="ipv6Address", required=false) String ipv6Address,
            @RequestParam(value="amiNetworkAddress", required=false) String amiNetworkAddress,
            @RequestParam(value="amiNetworkAddressV6", required=false) String amiNetworkAddressV6,
            @RequestParam(value="macAddr", required=false) String macAddr) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("installDate", installDate);
        map.put("hwVersion", hwVersion);
        map.put("protocolType", protocolType);
        map.put("lastswUpdateDate", lastswUpdateDate);
        map.put("ipAddr", ipAddr);
        map.put("sysPhoneNumber", sysPhoneNumber);
        map.put("mcuId", mcuId);
        map.put("locationId", locationId);
        map.put("sysLocation", sysLocation);
        map.put("sysLocalPort", sysLocalPort);
        map.put("mcuStatus", mcuStatus);
        map.put("sysHwBuild", sysHwBuild);
        map.put("sysSerialNumber", sysSerialNumber);
        map.put("sysTlsPort", sysTlsPort);
        map.put("sysTlsVersion", sysTlsVersion);
        map.put("amiNetworkAddress", amiNetworkAddress);
        map.put("amiNetworkAddressV6", amiNetworkAddressV6);
        map.put("ipv6Address", ipv6Address);
        map.put("macAddr", macAddr);

        mcuManager.updateMCU(map);

        return "redirect:/gadget/device/mcuInfo.do?mcuId=" + mcuId + "&imgPage=" + imgPage + "&supplierId=" + supplierId;
    }

    @RequestMapping(value="/gadget/device/mcuPagingInfo")
    public ModelAndView getMCUPagingInfo(

            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locId") String locId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("page") int page,
            @RequestParam("filter") String filter,
            @RequestParam("protocol") String protocol) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("mcuId", mcuId);
        map.put("mcuType", mcuType);
        map.put("locId", locId);
        map.put("swVersion", swVersion);
        map.put("hwVersion", hwVersion);
        map.put("installDateStart", installDateStart);
        map.put("installDateEnd", installDateEnd);
        map.put("filter", filter);
        map.put("protocol", protocol);

        ModelAndView mav = new ModelAndView();
        mav.addObject("paging", mcuManager.getPagingInfo(page, mcuManager.getMCUCountByCondition(map), "GRID"));
        mav.addObject("pagingType", "GRID");
        mav.setViewName("/gadget/device/paging");

        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuMaxExcelDownloadPopup")
    public ModelAndView mcuMaxExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMaxExcelDownloadPopup");
        return mav;
    }
    //group Schedule
    @RequestMapping(value="/gadget/device/mcuGroupSchedulePopup")
    public ModelAndView mcuGroupSchedulePopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuGroupSchedulePopup");
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/mcuShipmentFileExcelDownloadPopup")
    public ModelAndView mcuShipmentFileExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuShipmentFileExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/mcuGetLogExcelDownloadPopup")
    public ModelAndView mcuGetLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuGetLogExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/mcuCommLogExcelDownloadPopup")
    public ModelAndView mcuCommLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuCommLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuAlertLogExcelDownloadPopup")
    public ModelAndView mcuAlertLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuAlertLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuOperLogExcelDownloadPopup")
    public ModelAndView mcuOperLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuOperLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value="/gadget/device/addInstallImg")
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
        String savePathName     = contextRoot+aimirFilePath.getMcuPath();

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


        String orginalName = filename;
        MCU mcu = mcuManager.getMCU(Integer.parseInt(request.getParameter("mcuId")));
        mcuInstallImgManager.addMCUInstallImg(mcu, aimirFilePath.getMcuPath() + "/" +savefilename,  orginalName);

        ModelAndView mav = new ModelAndView();
        // 이것은 꽁수 [ajaxSubmit 응답 받을 페이지를 지정해야 하나보다~JSON으로 뱉어주믄 익스플로어 다운된다.버그는 아닐거야~]
        mav.setViewName("redirect:xxx");

        return mav;
    }

    @RequestMapping(value="/gadget/device/updateInstallImg")
    public ModelAndView updateInstallImg (

            @RequestParam("installImgFile") MultipartFile file,
            @RequestParam("mcuInstallImgId") long mcuInstallImgId) throws IOException {


        String currentTimeMillisName = FileUploadHelper.uploadFile(file);
        String orginalName = file.getOriginalFilename();



        mcuInstallImgManager.updateMCUInstallImg(mcuInstallImgId, currentTimeMillisName, orginalName);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:xxx");

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteInstallImg")
    public ModelAndView deleteInstallImg (@RequestParam("mcuInstallImgId") long mcuInstallImgId
                                         , HttpServletRequest request) {

        // 파일 삭제
        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
        MCUInstallImg mcuInstallImg = mcuInstallImgManager.getMCUInstallImg(mcuInstallImgId);
        FileUploadHelper.removeExistingFile(contextRoot + "/" + mcuInstallImg.getCurrentTimeMillisName());

        // DB 삭제
        mcuInstallImgManager.deleteMCUInstallImg(mcuInstallImgId);

        //return new ModelAndView("redirect:xxx");
        return new ModelAndView("jsonView");
    }

    @RequestMapping(value="/gadget/device/mcuInstallImgPagingInfo")
    public ModelAndView getMCUInstallImg(

            @RequestParam("mcuId") Integer mcuId,
            @RequestParam("imgPage") int imgPage,
            @RequestParam("mode") String mode) {

        MCU mcu = mcuManager.getMCU(mcuId);
        int imgSize = mcu.getMcuInstallImgs().size();

        int currentPage = -1;

        if("PAGE".equals(mode))
            currentPage = imgPage;
        else if("INSERT".equals(mode))
            currentPage = imgSize;
        else if("UPDATE".equals(mode))
            currentPage = imgPage;
        else if("DELETE".equals(mode))
            currentPage = 1;

        ModelAndView mav = new ModelAndView();
        mav.addObject("paging", mcuManager.getPagingInfo(currentPage, imgSize, "IMG"));
        mav.addObject("pagingType","IMG");
        mav.setViewName("jsonView");

        if(imgSize > 0) {
            // mcuInstallImgs는 파일명 rowId 으로 정렬되어 있으므로 (페이지 번호 - 1)은 순서인덱스와 같음
            MCUInstallImg dii = mcu.getMcuInstallImgs().get(currentPage - 1);
            mav.addObject("mcuInstallImgId", dii.getId());
            mav.addObject("currentTimeMillisName", dii.getCurrentTimeMillisName());
        } else {
            mav.addObject("mcuInstallImgId", "");
            mav.addObject("currentTimeMillisName", "");
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuLog")
    public ModelAndView getMCULog() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/device/mcuLog");

        return mav;
    }
    
    @RequestMapping(value="/gadget/device/insertMCU")
    public ModelAndView insertMCU(

            @ModelAttribute("mcuInsertForm") MCU mcu,
            @RequestParam("deviceModelId") Integer deviceModelId,
            @RequestParam("mcuTypeId") Integer mcuTypeId,
            @RequestParam("protocolTypeId") Integer protocolTypeId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("tempFile") String tempFile,
            @RequestParam("sysSerialNumber") String sysSerialNumber,
            @RequestParam("sysHwVersion") String sysHwVersion,
            @RequestParam("sysSwVersion") String sysSwVersion,
            @RequestParam("singleRegMCUmcuIpAddr") String singleRegMCUmcuIpAddr,
            @RequestParam("singleRegMCUmcuIpAddr_v6") String singleRegMCUmcuIpAddr_v6,
            @RequestParam("singleRegamiNetworkAddress") String singleRegamiNetworkAddress,
            @RequestParam("singleRegamiNetworkAddressV6") String singleRegamiNetworkAddressV6,
            HttpServletRequest request) throws IOException {

        ModelAndView mav = new ModelAndView("jsonView");

        Location location = locationManager.getLocation(locationId);
        Supplier supplier = location.getSupplier();
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(deviceModelId);
        Code mcuTypeCode = codeManager.getCode(mcuTypeId);
        Code protocolTypeCode = codeManager.getCode(protocolTypeId);

        mcu.setLocation(location);
        mcu.setSupplier(supplier);
        mcu.setDeviceModel(deviceModel);
        mcu.setMcuType(mcuTypeCode);
        mcu.setProtocolType(protocolTypeCode);
        mcu.setSysHwVersion(sysHwVersion);
        mcu.setSysSwVersion(sysSwVersion);
        mcu.setSysSerialNumber(sysSerialNumber);
        if(singleRegMCUmcuIpAddr!= null && singleRegMCUmcuIpAddr!="")
        	mcu.setIpAddr(singleRegMCUmcuIpAddr);
        if(singleRegMCUmcuIpAddr_v6!= null && singleRegMCUmcuIpAddr_v6!="")
        	mcu.setIpv6Addr(singleRegMCUmcuIpAddr_v6);
        if(singleRegamiNetworkAddress!= null && singleRegamiNetworkAddress!="")
        	mcu.setAmiNetworkAddress(singleRegamiNetworkAddress);
        if(singleRegamiNetworkAddressV6!= null && singleRegamiNetworkAddressV6!="")
        	mcu.setAmiNetworkAddressV6(singleRegamiNetworkAddressV6);
        
        mcu = mcuManager.insertMCU(mcu);

        mav.addObject("mcu", mcu);

        if ( StringUtil.nullToBlank(tempFile).length() != 0 ) {

            String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
            String extension = tempFile.substring(tempFile.indexOf("."));
            String savePathName = contextRoot+aimirFilePath.getMcuPath();

            if (!FileUploadHelper.exists(savePathName)) {
                File savedir = new File(savePathName);
                savedir.mkdir();
            }

            Date date = new Date();
            String savefilename = date.getTime()+extension;

            // File Copy & Temp File Delete
            FileUploadHelper.copy(tempFile, FileUploadHelper.makePath(savePathName, savefilename));
            FileUploadHelper.removeExistingFile(tempFile);

            String orginalName = tempFile;

            mcuInstallImgManager.addMCUInstallImg(mcu, aimirFilePath.getMcuPath() + "/" +savefilename, orginalName);
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/updateMCU")
    public ModelAndView updateMCU(

            @RequestParam("mcuId") Integer mcuId,
            @RequestParam("deviceModelId") Integer deviceModelId,
            @RequestParam("mcuTypeId") Integer mcuTypeId,
            @RequestParam("protocolTypeId") Integer protocolTypeId,
            @RequestParam("locationId") Integer locationId,
            @ModelAttribute("mcuInsertForm") MCU inMcu) {


        MCU mcu = mcuManager.getMCU(mcuId);
        mcu.setIpAddr(inMcu.getIpAddr());
        mcu.setServiceAtm(inMcu.getServiceAtm());
        mcu.setGpioX(inMcu.getGpioX());
        mcu.setGpioY(inMcu.getGpioY());
        mcu.setGpioZ(inMcu.getGpioZ());
        mcu.setSysID(inMcu.getSysID());
        mcu.setSysPhoneNumber(inMcu.getSysPhoneNumber());
        mcu.setSysEtherType(inMcu.getSysEtherType());
        mcu.setSysMobileType(inMcu.getSysMobileType());
        mcu.setSysMobileMode(inMcu.getSysMobileMode());
        mcu.setSysMinTemp(inMcu.getSysMinTemp());
        mcu.setSysMaxTemp(inMcu.getSysMaxTemp());
        mcu.setSysServer(inMcu.getSysServer());
        mcu.setSysServerPort(inMcu.getSysServerPort());
        mcu.setSysServerAlarmPort(inMcu.getSysServerAlarmPort());
        mcu.setSysLocalPort(inMcu.getSysLocalPort());
        mcu.setSysMobileAccessPointName(inMcu.getSysMobileAccessPointName());
        mcu.setSysSwRevision(inMcu.getSysSwRevision());
        mcu.setSysSwVersion(inMcu.getSysSwVersion());
        mcu.setSysHwVersion(inMcu.getSysHwVersion());


        Location location = locationManager.getLocation(locationId);
        DeviceModel deviceModel = deviceModelManager.getDeviceModel(deviceModelId);
        Code mcuTypeCode = codeManager.getCode(mcuTypeId);
        Code protocolTypeCode = codeManager.getCode(protocolTypeId);

        mcu.setLocation(location);
        mcu.setDeviceModel(deviceModel);
        mcu.setMcuType(mcuTypeCode);
        mcu.setProtocolType(protocolTypeCode);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("mcu", mcuManager.updateMCU(mcu));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMCU")
    public ModelAndView getMCU(

            @RequestParam("mcuId") Integer mcuId) {

        MCU mcu = mcuManager.getMCU(mcuId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("mcu", mcu);

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteMCU")
    public ModelAndView deleteMCU(

            @RequestParam("mcuId") Integer mcuId) {

        mcuManager.deleteMCU(mcuId);

        ModelAndView mav = new ModelAndView("jsonView");

        return mav;
    }

    @RequestMapping(value="/gadget/device/updateDcuStatus")
    public ModelAndView updateDcuStatus(
            @RequestParam("mcuId") Integer mcuId) {

        int result = mcuManager.updateDcuStatus(mcuId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", result);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getMCUs")
    public ModelAndView getMCUs(

            @RequestParam("locationId") String locationId,
            @RequestParam("MCUType") String mcuType,
            @RequestParam("commState") String commState) {

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("locationId", locationId);
        conditionMap.put("MCUType", mcuType);
        conditionMap.put("commState", commState);
        conditionMap.put("supplierId", supplierId+"");

        return new ModelAndView("jsonView", new ModelMap("gridDatas", mcuManager.getMcusByCondition(conditionMap)));
    }

    @RequestMapping(value="/gadget/device/getMCUTypeByCommStateData")
    public ModelAndView getMCUTypeByCommStateData(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", ReflectionUtils.getDefineListToMapList(mcuManager.getMCUTypeByCommStateData(supplierId)));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMCUTypeByLocationData")
    public ModelAndView getMCUTypeByLocationData(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");
        if(supplierId == null || "".equals(supplierId)){
            // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();
            supplierId = user.getRoleData().getSupplier().getId()+"";
        }
        mav.addObject("result", mcuManager.getMCUTypeListByLocationData(supplierId));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getLocationByMCUTypeData")
    public ModelAndView getLocationByMCUTypeData(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", mcuManager.getLocationListByMCUTypeData(supplierId));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getLocationByCommStateData")
    public ModelAndView getLocationByCommStateData( @RequestParam("supplierId") String supplierId
            ) {

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", ReflectionUtils.getDefineListToMapList(mcuManager.getLocationByCommStateData(supplierId)));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getCommStateByMCUTypeData")
    public ModelAndView getCommStateByMCUTypeData(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", mcuManager.getCommStateListByMCUTypeData(supplierId));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getCommStateByLocationData")
    public ModelAndView getCommStateByLocationData(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result", mcuManager.getCommStateListByLocationData(supplierId));

        return mav;
    }

    // 장비등록 > 개별등록  - 집중기 Name 중복 확인
    @RequestMapping(value="/gadget/device/isMCUDuplicateByMcuId")
    public ModelAndView isMcuDuplicate(@RequestParam("sysId") String sysId) {

        MCU mcu = mcuManager.getMCU(sysId);

        ModelAndView mav = new ModelAndView("jsonView");
        if(mcu == null) {
            mav.addObject("result", "false");
        } else if( sysId == "" || sysId == null) {
            mav.addObject("result", "textNull");
        } else {
        	if(mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode())) {
        		mav.addObject("result", "deleteStatus");
        	} else {
        		mav.addObject("result", "true");
        	}
        }

        return mav;
    }

    // 장비등록 > 개별등록  - 집중기 Name 자동완성
    @RequestMapping(value="/gadget/device/getMCUmcuIdList")
    public ModelAndView getMCUNameList(@RequestParam("sysId")      String sysId
                                     , @RequestParam("supplierId") String supplierId) {

            Map<String, Object> condition = new HashMap<String, Object>();

            condition.put("sysId",          sysId);
            condition.put("supplierId",     supplierId);

            List<Object> mcuNameList = mcuManager.getMCUNameList(condition);


            ModelAndView mav = new ModelAndView("jsonView");
            mav.addObject("mcuNameList", mcuNameList.get(0));

            return mav;

        }


    @RequestMapping(value="/gadget/device/getMCUVar")
    public ModelAndView getMCUVar(@RequestParam("mcuId") int mcuId) {

        MCU mcu = mcuManager.getMCU(mcuId);
        MCUVar mcuVar = mcu.getMcuVar();

        ModelAndView mav = new ModelAndView("jsonView");

        if(mcuVar == null) {
            mav.addObject("isNull", "true");
            return mav;
        } else {
            mav.addObject("isNull", "false");
            String varMeterDayMask = Integer.toBinaryString(mcuVar.getVarMeterDayMask());
            String varEventReadDayMask = Integer.toBinaryString(mcuVar.getVarEventReadDayMask());
            String varMeterTimesyncDayMask = Integer.toBinaryString(mcuVar.getVarMeterTimesyncDayMask());
            String varRecoveryDayMask = Integer.toBinaryString(mcuVar.getVarRecoveryDayMask());

            String varMeterHourMask = Integer.toBinaryString(mcuVar.getVarMeterHourMask());
            String varEventReadHourMask = Integer.toBinaryString(mcuVar.getVarEventReadHourMask ());
            String varMeterTimesyncHourMask = Integer.toBinaryString(mcuVar.getVarMeterTimesyncHourMask());
            String varRecoveryHourMask = Integer.toBinaryString(mcuVar.getVarRecoveryHourMask());

            mav.addObject("mcuVar", mcuVar);
            mav.addObject("varMeterDayMask", makeBinData(varMeterDayMask, "DAY"));
            mav.addObject("varEventReadDayMask", makeBinData(varEventReadDayMask, "DAY"));
            mav.addObject("varMeterTimesyncDayMask", makeBinData(varMeterTimesyncDayMask, "DAY"));
            mav.addObject("varRecoveryDayMask", makeBinData(varRecoveryDayMask, "DAY"));

            mav.addObject("varMeterHourMask", makeBinData(varMeterHourMask, "HOUR"));
            mav.addObject("varEventReadHourMask", makeBinData(varEventReadHourMask, "HOUR"));
            mav.addObject("varMeterTimesyncHourMask", makeBinData(varMeterTimesyncHourMask, "HOUR"));
            mav.addObject("varRecoveryHourMask", makeBinData(varRecoveryHourMask, "HOUR"));

            mav.addObject("varEnableReadMeterEvent", mcuVar.getVarEnableReadMeterEvent());
            mav.addObject("varEnableMeterTimesync", mcuVar.getVarEnableMeterTimesync());
            mav.addObject("varEnableAutoUpload", mcuVar.getVarEnableAutoUpload());
            mav.addObject("varEnableRecovery", mcuVar.getVarEnableRecovery());

            if(mcuVar.getVarMeterUploadCycleType() == 4 || mcuVar.getVarMeterUploadCycleType() == 0) {
                mav.addObject("varMeterUploadCycle", makeBinData(Integer.toBinaryString(mcuVar.getVarMeterUploadCycle()), "HOUR"));
            } else if(mcuVar.getVarMeterUploadCycleType() == 3) {
                mav.addObject("varMeterUploadCycle", mcuVar.getVarMeterUploadCycle());
            } else if(mcuVar.getVarMeterUploadCycleType() == 2) {
                mav.addObject("varMeterUploadCycle", makeBinData(Integer.toBinaryString(mcuVar.getVarMeterUploadCycle()), "DAY"));
            }

            return mav;
        }
    }

    @RequestMapping(value="/gadget/device/getMCUCodi")
    public ModelAndView getMCUCodi(@RequestParam("mcuId") int mcuId) {

        MCU mcu = mcuManager.getMCU(mcuId);
        MCUCodi mcuCodi = mcu.getMcuCodi();

        ModelAndView mav = new ModelAndView("jsonView");

        if(mcuCodi == null) {
            mav.addObject("isNull", "true");
            mav.addObject("codiFwVer", "");
            mav.addObject("codiHwVer", "");
            mav.addObject("codiZAIfVer", "");
            mav.addObject("codiZZIfVer", "");
            return mav;
        } else {
            mav.addObject("isNull", "false");
            mav.addObject("mcuCodi", mcuCodi);
            mav.addObject("codiFwVer", mcuCodi.getCodiFwVer() == null ? "" : mcuCodi.getCodiFwVer());
            mav.addObject("codiHwVer", mcuCodi.getCodiHwVer() == null ? "" : mcuCodi.getCodiHwVer());
            mav.addObject("codiZAIfVer", mcuCodi.getCodiZAIfVer() == null ? "" : mcuCodi.getCodiZAIfVer());
            mav.addObject("codiZZIfVer", mcuCodi.getCodiZZIfVer() == null ? "" : mcuCodi.getCodiZZIfVer());

            return mav;
        }
    }

    @RequestMapping(value="/gadget/device/updateDeviceModel")
    public ModelAndView updateDeviceModel(@RequestParam("mcuId") int mcuId,
            @RequestParam("mcuTypeId") int mcuTypeId,
            @RequestParam("sysSwRevision") String sysSwRevision,
            @RequestParam("sysSwVersion") String sysSwVersion) {

        MCU mcu = mcuManager.getMCU(mcuId);
        Code code = codeManager.getCode(mcuTypeId);

        String mcuTypeName = code.getDescr();

        mcu.setSysType(mcuTypeId);
        mcu.setMcuType(code);
        mcu.setSysSwRevision(sysSwRevision);
        mcu.setSysSwVersion(sysSwVersion);

        mcuManager.updateMCU(mcu);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", "success");
        mav.addObject("mcuTypeName", mcuTypeName);
        mav.addObject("sysSwRevision", sysSwRevision);
        mav.addObject("sysSwVersion", sysSwVersion);

        return mav;
    }


    @RequestMapping(value="/gadget/device/checkSysId")
    public ModelAndView checkSysId(

        @RequestParam("sysID") String sysID) {

        ModelAndView mav = new ModelAndView("jsonView");

        MCU mcu = mcuManager.getMCU(sysID);

        if(mcu != null) {
            mav.addObject("result", "false");
        } else {
            mav.addObject("result", "true");
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getConnectedDevice")
    @Deprecated
    public ModelAndView getConnectedDevice(@RequestParam("mcuId") Integer mcuId) {
        Set<Modem> modems = mcuManager.getConnectedDevices(mcuId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("modems", modems);

        return mav;
    }

    /**
     * method name : getConnectedDeviceList<b/>
     * method Desc :
     *
     * @param mcuId
     * @return
     */
    @RequestMapping(value = "/gadget/device/getConnectedDeviceList")
    public ModelAndView getConnectedDeviceList(@RequestParam("mcuId") Integer mcuId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        List<Map<String, Object>> result = mcuManager.getConnectedDeviceList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", mcuManager.getConnectedDeviceListTotalCount(conditionMap));

        return mav;
    }

//    private int spaceToZero(String inVal) {
//
//      if("".equals(inVal)) return 0;
//
//      return Integer.parseInt(inVal);
//    }

    private String makeBinData(String value, String type) {

        int size =-1;

        if("DAY".equals(type))
            size = 32 - value.length();
        else if("HOUR".equals(type))
            size = 24 - value.length();

        String strZero = "";
        for(int i = 0 ; i < size; i++) strZero += "0";

        String temp = strZero + value;
        String retrurnValue = "";

        for(int i = temp.length() - 1 ; i >= 0; i--) {
            retrurnValue += temp.charAt(i);
        }

        return retrurnValue;
    }

    private String makeCodiVerData(String value) {

        if(value == null) return "";

        String tempBin  = Integer.toBinaryString(Integer.parseInt(value));

        String zero = "";
        for(int i = 0, size = 16 - tempBin.length() ; i < size; i++) zero += "0";

        String binary = zero + tempBin;

        return Integer.parseInt(binary.substring(0, 8), 2) + "." + Integer.parseInt(binary.substring(8, 16), 2);

    }

    @RequestMapping(value="/gadget/device/uploadTempImg")
    public ModelAndView uploadTempImgFile(

            HttpServletRequest request,
            HttpServletResponse response) throws ServletRequestBindingException, IOException {

        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");

        String filename = multipartFile.getOriginalFilename();

        if (filename == null || "".equals(filename))
            return null;

        String saveTempPathName = contextRoot+aimirFilePath.getPhotoTempPath();

        if (!FileUploadHelper.exists(saveTempPathName)) {
            File savedir = new File(saveTempPathName);
            savedir.mkdir();
        }

        File uFile = new File(FileUploadHelper.makePath(saveTempPathName, filename));

        multipartFile.transferTo(uFile);

        ModelAndView mav = new ModelAndView("gadget/device/deviceBulkFile");

        mav.addObject("tempFileName", uFile);
        mav.addObject("titleName", "tempImg");

        return mav;
   }

    @RequestMapping(value="/gadget/device/getMcuLocation")
    public ModelAndView getMcuLocation(

            @RequestParam("mcuId") Integer mcuId) {

        MCU mcu = mcuManager.getMCU(mcuId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("sysLocation", mcu.getSysLocation());
        mav.addObject("gpioX", mcu.getGpioX());
        mav.addObject("gpioY", mcu.getGpioY());
        mav.addObject("gpioZ", mcu.getGpioZ());

        return mav;
    }

    @RequestMapping(value="/gadget/device/mcuMaxGadgetExcelMake")
    @Deprecated
    public ModelAndView mcuMaxGadgetExcelMake(@RequestParam("supplierId") String supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locationId") String locationId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("protocol") String protocol,
            @RequestParam("filter") String filter,
            @RequestParam("order") String order,
            @RequestParam("number") String number,
            @RequestParam("mcuId2") String mcuId2,
            @RequestParam("mcuName") String mcuName,
            @RequestParam("mcuMobile") String mcuMobile,
            @RequestParam("ipAddress") String ipAddress,
            @RequestParam("swVer") String swVer,
            @RequestParam("installation") String installation,
            @RequestParam("lastCommDate") String lastCommDate,
            @RequestParam("CommStatus") String CommStatus,
            @RequestParam("msg09") String msg09,
            @RequestParam("filePath") String filePath,
            @RequestParam("HH48over") String HH48over,
            @RequestParam("HH24over") String HH24over,
            @RequestParam("normal") String normal,
            @RequestParam("title") String title,
            @RequestParam("mcuTypeFmt") String mcuTypeFmt,
            @RequestParam("vendor") String vendor,
            @RequestParam("model") String model,
            @RequestParam("hwVer") String hwVer,
            @RequestParam("protocolType") String protocolType,
            @RequestParam("location") String location) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<MCU> list = new ArrayList<MCU>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        final String mcuListData = "Concentrator";

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("hwVersion", hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("protocol", protocol);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("page", "0");
        conditionMap.put("pageSize", "0");

        List<MCU> result = null;

        try {
            result = mcuManager.getGridDataExcel(conditionMap);
            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(mcuListData);
            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("number", number);
            msgMap.put("mcuId2", mcuId2);
            msgMap.put("mcuName", mcuName);
            msgMap.put("mcuMobile", mcuMobile);
            msgMap.put("ipAddress", ipAddress);
            msgMap.put("swVer", swVer);
            msgMap.put("installation", installation);
            msgMap.put("lastCommDate", lastCommDate);
            msgMap.put("CommStatus", CommStatus);
            msgMap.put("HH48over", HH48over);
            msgMap.put("HH24over", HH24over);
            msgMap.put("normal", normal);
            msgMap.put("title", title);
            msgMap.put("mcuTypeFmt", mcuTypeFmt);
            msgMap.put("vendor", vendor);
            msgMap.put("model", model);
            msgMap.put("hwVer", hwVer);
            msgMap.put("protocolType", protocolType);
            msgMap.put("location", location);

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
            McuDataMakeExcel wExcel = new McuDataMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcelTemp(result, msgMap, isLast, filePath, sbSplFileName.toString());
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcelTemp(list, msgMap, isLast, filePath, sbSplFileName.toString());
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

    @RequestMapping(value = "/gadget/device/mcuMaxGadgetGridExcelMake")
    public ModelAndView mcuMaxGadgetExcelGridMake(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuType") String mcuType,
            @RequestParam("mcuSerial") String mcuSerial,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("protocol") String protocol,
            @RequestParam("filter") String filter,
            @RequestParam("order") String order,
            @RequestParam(value="mcuStatus", required=false) String mcuStatus,
            @RequestParam("number") String number,
            @RequestParam("mcuId2") String mcuId2,
            @RequestParam("mcuName") String mcuName,
            @RequestParam("mcuMobile") String mcuMobile,
            @RequestParam("ipAddress") String ipAddress,
            @RequestParam("swVer") String swVer,
            @RequestParam("installation") String installation,
            @RequestParam("lastCommDate") String lastCommDate,
            @RequestParam("CommStatus") String CommStatus,
            @RequestParam("msg09") String msg09,
            @RequestParam("filePath") String filePath,
            @RequestParam("HH48over") String HH48over,
            @RequestParam("HH24over") String HH24over,
            @RequestParam("normal") String normal,
            @RequestParam("title") String title,
            @RequestParam("mcuTypeFmt") String mcuTypeFmt,
            @RequestParam("vendor") String vendor,
            @RequestParam("model") String model,
            @RequestParam("hwVer") String hwVer,
            @RequestParam("protocolType") String protocolType,
            @RequestParam("location") String location,
            @RequestParam("sysLocation") String sysLocation) {

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        final String mcuListData = "Concentrator";

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("hwVersion", hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("protocol", protocol);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("mcuStatus", mcuStatus);

        List<Map<String, Object>> result = null;

        try {
            result = mcuManager.getDcuGridData(conditionMap);
            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            String ipv6Addr = "IPv6 Address";
            String macAddr = "Mac Address";
            sbFileName.append(mcuListData);
            sbFileName.append(TimeUtil.getCurrentTimeMilli());
            
            // message 생성
            msgMap.put("number", number);
            msgMap.put("mcuId2", mcuId2);
            msgMap.put("mcuName", mcuName);
            msgMap.put("mcuMobile", mcuMobile);
            msgMap.put("ipAddress", ipAddress);
            msgMap.put("swVer", swVer);
            msgMap.put("installation", installation);
            msgMap.put("lastCommDate", lastCommDate);
            msgMap.put("CommStatus", CommStatus);
            msgMap.put("HH48over", HH48over);
            msgMap.put("HH24over", HH24over);
            msgMap.put("normal", normal);
            msgMap.put("title", title);
            msgMap.put("mcuTypeFmt", mcuTypeFmt);
            msgMap.put("vendor", vendor);
            msgMap.put("model", model);
            msgMap.put("hwVer", hwVer);
            msgMap.put("protocolType", protocolType);
            msgMap.put("location", location);
            msgMap.put("ipv6Addr", ipv6Addr);
            msgMap.put("macAddr", macAddr);
            msgMap.put("mcuSerial", mcuSerial);
            msgMap.put("sysLocation", sysLocation);

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
            McuDataMakeExcel wExcel = new McuDataMakeExcel();
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
    
    @RequestMapping(value = "/gadget/device/mcuShipmentExcelMake")
    public ModelAndView mcuShipmentExcelMake(
    		@RequestParam("supplierId") Integer supplierId,
    		@RequestParam("filePath") String filePath,
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
            @RequestParam("msg_productionDate") String msg_productionDate,
            @RequestParam("msg_dcuId") String msg_dcuId) {	
    	
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
    	StringBuilder sbFileName = new StringBuilder();
    	StringBuilder sbSplFileName = new StringBuilder();
    	
        final String mcuListData = "Concentrator_Shipment";
        
        boolean isLast = false;
        Long total = 0L; 		// 데이터 조회건수
    	Long maxRows = 5000L;	// excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
        
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("modelId", model);
    	conditionMap.put("purchaseOrder", purchaseOrder);
    	
    	List<Map<String, Object>> result = null;
    	
		try {
			result = mcuManager.getDcuGridData(conditionMap);
			total = new Integer(result.size()).longValue();

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}
			
			sbFileName.append(mcuListData);
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
			msgMap.put("msg_dcuId", msg_dcuId);	
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

						// 파일길이 : 22이상, 확장자 : xls | zip
						if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(mcuListData)
									&& filename.substring(3, 11).compareTo(deleteDate) < 0) {
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
			McuDataMakeExcel wExcel = new McuDataMakeExcel();
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
    
    @RequestMapping(value = "/gadget/device/mcuEventLogExcelMake")
    public ModelAndView mcuEventLogExcelMake(
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
            McuDataMakeExcel wExcel = new McuDataMakeExcel();
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

    /**
     * method name : getCommLogList<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Operation Log 리스트를 조회한다.
     *
     * @param supplierId
     * @param senderId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/getCommLogList")
    public ModelAndView getCommLogList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("senderId") String senderId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("senderId", senderId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Map<String, Object>> result = mcuManager.getCommLogList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", mcuManager.getCommLogListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getCommLogData<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Communication Log Summary Data를 조회한다.
     *
     * @param supplierId
     * @param senderId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/getCommLogData")
    public ModelAndView getCommLogData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("senderId") String senderId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("senderId", senderId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        List<Map<String, Object>> result = mcuManager.getCommLogData(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getEventAlertLogList<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Event Alert Log 리스트를 조회한다.
     *
     * @param supplierId
     * @param mcuId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/getEventAlertLogList")
    public ModelAndView getEventAlertLogList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Map<String, Object>> result = mcuManager.getEventAlertLogList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", mcuManager.getEventAlertLogListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getOperationLogList<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Operation Log 리스트를 조회한다.
     *
     * @param supplierId
     * @param mcuId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/getOperationLogList")
    public ModelAndView getOperationLogList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("targetName") String targetName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("targetName", targetName);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Map<String, Object>> result = mcuManager.getOperationLogList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", mcuManager.getOperationLogListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getMcuCommLogExportExcel<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Communication Log 엑셀 리포트를 생성한다.
     *
     * @param supplierId
     * @param senderId
     * @param startDate
     * @param endDate
     * @param headerMsg
     * @param widths
     * @param aligns
     * @param fields
     * @param filePath
     * @param title
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/device/getMcuCommLogExportExcel")
    public ModelAndView getMcuCommLogExportExcel(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("senderId") String senderId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("widths[]") String[] widths,
            @RequestParam("aligns[]") String[] aligns,
            @RequestParam("fields[]") String[] fields,
            @RequestParam("filePath") String filePath,
            @RequestParam(value="title", required=false) String title) {
        String reportPrefix = "MCUCommLogReport";     // excel file prefix

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("senderId", senderId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        List<Map<String, Object>> result = mcuManager.getCommLogList(conditionMap);

        int total = result.size();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        Map<String, Object> resultMap = makeLogExportExcel(result, headerMsg, widths, aligns, fields, filePath, title, reportPrefix);

        mav.addObject("fileName", (String)resultMap.get("fileName"));
        mav.addObject("zipFileName", (String)resultMap.get("zipFileName"));
        mav.addObject("fileNames", (List<String>)resultMap.get("fileNames"));

        return mav;
    }

    /**
     * method name : getMcuEventAlertLogExportExcel<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Event Alert Log 엑셀 리포트를 생성한다.
     *
     * @param supplierId
     * @param mcuId
     * @param startDate
     * @param endDate
     * @param headerMsg
     * @param widths
     * @param aligns
     * @param fields
     * @param filePath
     * @param title
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/device/getMcuEventAlertLogExportExcel")
    public ModelAndView getMcuEventAlertLogExportExcel(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("widths[]") String[] widths,
            @RequestParam("aligns[]") String[] aligns,
            @RequestParam("fields[]") String[] fields,
            @RequestParam("filePath") String filePath,
            @RequestParam(value="title", required=false) String title) {
        String reportPrefix = "MCUBrokenLogReport";     // excel file prefix

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        List<Map<String, Object>> result = mcuManager.getEventAlertLogList(conditionMap);

        int total = result.size();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        Map<String, Object> resultMap = makeLogExportExcel(result, headerMsg, widths, aligns, fields, filePath, title, reportPrefix);

        mav.addObject("fileName", (String)resultMap.get("fileName"));
        mav.addObject("zipFileName", (String)resultMap.get("zipFileName"));
        mav.addObject("fileNames", (List<String>)resultMap.get("fileNames"));

        return mav;
    }

    /**
     * method name : getMcuOperationLogExportExcel<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 Operation Log 엑셀 리포트를 생성한다.
     *
     * @param supplierId
     * @param targetName
     * @param startDate
     * @param endDate
     * @param headerMsg
     * @param widths
     * @param aligns
     * @param fields
     * @param filePath
     * @param title
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/device/getMcuOperationLogExportExcel")
    public ModelAndView getMcuOperationLogExportExcel(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("targetName") String targetName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("widths[]") String[] widths,
            @RequestParam("aligns[]") String[] aligns,
            @RequestParam("fields[]") String[] fields,
            @RequestParam("filePath") String filePath,
            @RequestParam(value="title", required=false) String title) {
        String reportPrefix = "MCUCommandLogReport";     // excel file prefix

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("targetName", targetName);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        List<Map<String, Object>> result = mcuManager.getOperationLogList(conditionMap);

        int total = result.size();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }

        Map<String, Object> resultMap = makeLogExportExcel(result, headerMsg, widths, aligns, fields, filePath, title, reportPrefix);

        mav.addObject("fileName", (String)resultMap.get("fileName"));
        mav.addObject("zipFileName", (String)resultMap.get("zipFileName"));
        mav.addObject("fileNames", (List<String>)resultMap.get("fileNames"));

        return mav;
    }

    /**
     * method name : makeLogExportExcel<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭 Communication Log, Event Alert Log, Operation Log 공통 엑셀리포트 파일 생성로직.
     *
     * @param dataList
     * @param headerMsg
     * @param widths
     * @param aligns
     * @param fields
     * @param filePath
     * @param title
     * @param reportPrefix
     * @return
     */
    private Map<String, Object> makeLogExportExcel(List<Map<String, Object>> dataList, String[] headerMsg,
            String[] widths, String[] aligns, String[] fields, String filePath, String title, String reportPrefix) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int maxRows = 5000;     // excel 파일 당 최대 행수

        List<String> fileNameList = new ArrayList<String>();
        List<String> headerList = Arrays.asList(headerMsg);
        List<String> alignList = Arrays.asList(aligns);
        List<String> fieldList = Arrays.asList(fields);
        List<Integer> widthList = new ArrayList<Integer>();

        for (String obj : widths) {
            if (!StringUtil.nullToBlank(obj).isEmpty()) {
                widthList.add(Integer.parseInt(obj));
            } else {
                widthList.add(null);
            }
        }

        int total = dataList.size();
        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        try {
            sbFileName.append(reportPrefix).append(TimeUtil.getCurrentTimeMilli());

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10) + "235959"; // 10일 이전 일자

                    for (File file : files) {
                        filename = file.getName();

                        if (filename.startsWith(reportPrefix)
                                && (filename.endsWith("xls") || filename.endsWith("zip"))
                                && (((filename.indexOf("(") != -1) ? filename.indexOf("(") : (filename.length() - 4)) == (reportPrefix
                                        .length() + deleteDate.length()))
                                && filename.substring(reportPrefix.length(),
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
            McuLogMakeExcel wExcel = new McuLogMakeExcel();
            wExcel.setFilePath(filePath);
            wExcel.setHeaderList(headerList);
            wExcel.setWidthList(widthList);
            wExcel.setAlignList(alignList);
            wExcel.setDataFieldList(fieldList);
            wExcel.setTitle(title);

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.setFileName(sbSplFileName.toString());
                wExcel.setDataList(dataList);
                wExcel.writeReportExcel();
                fileNameList.add(sbSplFileName.toString());
            } else {
                int filecnt = total / maxRows;
                int remind = total % maxRows;

                if (remind != 0) {
                    filecnt++;
                }

                List<Map<String, Object>> sublist = null;

                for (int i = 0; i < filecnt; i++) {
                    sbSplFileName = new StringBuilder();
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(i+1).append(").xls");

                    if (i == (filecnt-1) && remind != 0) { // last && 나머지가 있을 경우
                        sublist = dataList.subList((i * maxRows), ((i * maxRows) + remind));
                    } else {
                        sublist = dataList.subList((i * maxRows), ((i * maxRows) + maxRows));
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

            resultMap.put("fileName", sbFileName.toString());
            resultMap.put("zipFileName", sbZipFile.toString());
            resultMap.put("fileNames", fileNameList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/gadget/device/getMCUMiniChart")
    public ModelAndView getMCUMiniChart(@RequestParam(value = "mcuChart", required = false) String mcuChart,
            @RequestParam(value = "supplierId", required = false) String supplierId,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "chartType", required = false) String chartType) {

        Map<String, Object> condition = new HashMap<String, Object>();

        if (mcuChart == null)
            mcuChart = "mc";

        condition.put("mcuChart", mcuChart);
        condition.put("supplierId", supplierId);
        condition.put("chartType", chartType);
        condition.put("message", message);

        List<List<Map<String, Object>>> miniChart = mcuManager.getMCUMiniChart(condition);

        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>> gridList = (List<Map<String, Object>>) miniChart.get(0);
        List<Map<String, Object>> chartSeriesList = (List<Map<String, Object>>) miniChart.get(1);

        int totalCnt = gridList.size();
        
        // grid data
        mav.addObject("chartData", gridList);

        // totCnt
        mav.addObject("totalCnt", totalCnt);

        // 해더값 model header value
        mav.addObject("chartSeries", chartSeriesList);

        mav.addObject("mcuChartType", mcuChart);

        return mav;
    }
    
    // 저전력 NMS과제를 위해서 추가.
    @RequestMapping(value="/gadget/device/mcuMaxGadget_topology")
    public ModelAndView loadMcuMaxGadget_topology() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMaxGadget_topology");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        String loginId = user.getLoginId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("loginId", loginId);

        List<Code> mcuTypeMap = codeManager.getChildCodes(Code.MCU_TYPE);
        List<Code> hwVersions = codeManager.getChildCodes(Code.MCU_HW_VERSION);
        List<Code> swVersions = codeManager.getChildCodes(Code.MCU_SW_VERSION);
        List<Code> protocols = codeManager.getChildCodes(Code.PROTOCOL);
        List<Code> mcuStatus = codeManager.getChildCodes(Code.MCU_STATUS);
        Map<String, List<String>> locationMap = mcuManager.getLocationTreeToRows(supplierId);
        List<Map<String, String>> deviceModels = deviceModelManager.getMCUDeviceModel();
        List<Location> locations = locationManager.getLocationsBySupplierId(supplierId);

        mav.addObject("mcuTypeMap", mcuTypeMap);
        mav.addObject("hwVersions", hwVersions);
        mav.addObject("swVersions", swVersions);
        mav.addObject("protocols", protocols);
        mav.addObject("keys", locationMap.get("keys"));
        mav.addObject("locationNames", locationMap.get("locationNames"));
        mav.addObject("deviceModels", deviceModels);
        mav.addObject("locations", locations);
        mav.addObject("mcuStatus", mcuStatus);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("cmdAuth", authMap.get("command"));  // Command권한(command = true)

        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getFirmwareVersionList_dcu")
    public ModelAndView getFirmwareVersionList(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
		
		List<String> versionList = mcuManager.getFirmwareVersionList(condition);
		
		mav.addObject("result", versionList);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getDeviceList_dcu")
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = mcuManager.getDeviceList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getTargetList_dcu")
    public ModelAndView getTargetListBy_dcu(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = mcuManager.getTargetList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }

    // Group List - Group Schedule Pop-up
    @RequestMapping(value="/gadget/device/getMcuGroupList")
    public ModelAndView loadGroupSchedule_Popup(@RequestParam("operatorId") Integer operatorId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	List<Map<String, Object>> result = groupMgmtManager.getMcuGroup(operatorId);
        
        mav.addObject("result", result);
    return mav;
    }
    
    // MCU Grid List - Group Schedule Pop-up
    @RequestMapping(value="/gadget/device/updateSelectedMcuList")
    public ModelAndView updateSelectedMcuList(@RequestParam("groupId") Integer groupId, 
    		@RequestParam(value = "supplierId", required = false) Integer supplierId) {
    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
    	ModelAndView mav = new ModelAndView("jsonView");
    	int page = Integer.parseInt(request.getParameter("page"));
    	int limit = Integer.parseInt(request.getParameter("limit"));
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("groupId", groupId);
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("page", page);
    	conditionMap.put("limit", limit);
    	
    	List<Map<String, Object>> result = groupMgmtManager.getSelectedMcuList(conditionMap);
    	int total = groupMgmtManager.getSelectedMcuCount(groupId);
        
    	mav.addObject("result", result);
    	mav.addObject("total", total);
    	return mav;
    }
    
    // Schedule Template Search File - Group Schedule Pop-up
    @RequestMapping(value = "/gadget/device/getUploadTempFile")
	public ModelAndView getUploadTempFile(HttpServletRequest request, HttpServletResponse response)
			throws ServletRequestBindingException, IOException {

		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
		
		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multiReq.getFile("userfile");

		String filename = multipartFile.getOriginalFilename();
		String tempPath = contextRoot + "temp";

		if (filename == null || "".equals(filename)) {
			return null;
		}

		if (!FileUploadHelper.exists(tempPath)) {
			File savedir = new File(tempPath);
			savedir.mkdir();
		}

		File uFile = new File(FileUploadHelper.makePath(tempPath, filename));
		
		if (FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))) {
			if (FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))) {
				multipartFile.transferTo(uFile);
			}
		} else {
			multipartFile.transferTo(uFile);
		}

		String filePath = tempPath + "/" + filename;
		String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();

		ModelAndView mav = new ModelAndView("jsonView");

		mav.addObject("tempFilepath", filePath);
		mav.addObject("titleName", mcuManager.getTitleName(filePath, ext));
		
		return mav;
	}
    
    //Schedule Template Divide - Group Schedule Pop-up
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/getExcelResult")
	public ModelAndView getExcelResult(
			@RequestParam("supplierId") int supplierId,
			@RequestParam("filePath") String filePath){
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	String resultMsg ="success";
    	List<Object> resultList = null;
		List<Object> headerList = null;
		int index = 0;
    	
		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = mcuManager.readOnlyExcelXLS(filePath, supplierId);
					log.debug(result);
				} else if ("xlsx".equals(ext)) {
					result = mcuManager.readOnlyExcelXLSX(filePath, supplierId);
					log.debug(result);
				}
			}

			if (result != null) {
				// int cnt = 0;
				resultList = (List<Object>) result.get("resultList");
				headerList = (List<Object>) result.get("headerList");
				if (result.get("index") != null) {
					index = Integer.parseInt(result.get("index").toString());
				}

				// File Copy & Temp File Delete
				FileUploadHelper.removeExistingFile(filePath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			resultMsg ="fail";
			log.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg ="fail";
			log.error(e.toString(), e);
		}finally {
			mav.addObject("resultMsg", resultMsg);
		}
		// 데이터 추출

		List<Object> result = new ArrayList();
		
		for (int i = 0; i < resultList.size(); i++) {
			List<Object> tmpList = new ArrayList();
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap = (Map<String, Object>) resultList.get(i);
			for (int j = 0; j < headerList.size(); j++) {
				tmpList.add(tmpMap.get(headerList.get(j)));
			}
			result.add(tmpList);
		}
		
    	mav.addObject("resultList", resultList);
		mav.addObject("headerList", headerList);
		mav.addObject("index", index);

    	return mav;
    }
    
    @SuppressWarnings("unchecked")
	@Deprecated
	@RequestMapping(value="/gadget/device/mcuGroupGetScheduleMakeExcel")
    public ModelAndView mcuGroupGetScheduleMakeExcel(
    		@RequestParam("loginId") String loginId,
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("filePath") String filePath,
    		@RequestParam("groupId")  Integer groupId) {
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("groupId", groupId);
    	conditionMap.put("supplierId", supplierId);
    	
    	/*List<Map<String, Object>> mcuList = groupMgmtManager.getSelectedMcuListNotPage(conditionMap);
    	log.debug(mcuList);
    	int total = groupMgmtManager.getSelectedMcuCount(groupId);*/
    	String rtnStr = "";
    	ResultStatus status = ResultStatus.FAIL;
    	final String scheduleListName = "Schedule";
    	
    	StringBuilder sbSplFileName = new StringBuilder();
    	StringBuilder sbFileName = new StringBuilder();
    	int total = 0;
    	
    	ModelAndView mav = new ModelAndView("jsonView");

    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.2")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		
		try {
			result.addAll(cmdOperationUtil.cmdMCUGroupGetSchedule(groupId));
			total = result.size();
			
			// Check Download Dir
			File downDir = new File(filePath);
			
			sbFileName.append(scheduleListName);
			sbFileName.append(TimeUtil.getCurrentTimeMilli());
			
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
                            if (filename.startsWith(scheduleListName) && filename.substring(8, 16).compareTo(deleteDate) < 0) {
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
			
			// Start Create Excel
			McuGroupScheduleMakeExcel wExcel = new McuGroupScheduleMakeExcel();
			if(total>0) {
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeScheduleReportExcel(result, filePath, sbSplFileName.toString());
				mav.addObject("filePath", filePath);
				mav.addObject("fileName", sbSplFileName.toString());
				
				status = ResultStatus.SUCCESS;
				
			}else {
				status = ResultStatus.FAIL;
			}
			
		}catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
			mav.addObject("error", rtnStr);
		}
			
		mav.addObject("status", status);
		return mav;
    }
    
    /*권한체크*/
    protected boolean commandAuthCheck(String loginId, CommandType cmdType, String command) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		if(operator==null){
			return false; // wrong id
		}

		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			return false; // 고객 권한이면
		}

		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(command))
				return true; // 관리자가 아니라도 명령에 대한 권한이 있으면
		}
		return false;
	}

    

	@RequestMapping(value="/gadget/device/mcuGroupSetSchedule")
    public ModelAndView mcuGroupSetSchedule(
    		@RequestParam(value = "loginId",required = false) String loginId,
    		@RequestParam(value = "nameArr",required = false) String[] nameArr,
    		@RequestParam(value = "suspendArr",required = false) String[] suspendArr,
    		@RequestParam(value = "conditionArr",required = false) String[] conditionArr,
    		@RequestParam(value = "taskArr",required = false) String[] taskArr,
    		@RequestParam(value = "retryCondition", required = false) String retryCondition,
    		@RequestParam(value = "groupId", required = false)  Integer sGroupId,
    		@RequestParam(value = "supplierId", required = false)  Integer supplierId,
    		@RequestParam(value = "operatorId", required = false)  Integer operatorId) {
    	
    	ResultStatus status = ResultStatus.FAIL;
    	int size = 0;
    	int mcuListSize = 0;
    	/* Set Schedule Result */
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    	/* Retry Interval Result */
    	List<Map<String, Object>> resultRetry = new ArrayList<Map<String, Object>>();
    	
    	/* Fail List - Add Group*/
    	List<String> failList = new ArrayList<String>();
    	/*saveOperationLog*/
    	List<String> mcuList = new ArrayList<String>();
    	String rtnStr = "";
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.2")){
			resultList = null;
			mav.addObject("resultList", resultList);
			mav.addObject("status", "No permission");
			return mav;
		}
		
		try {
	    	ArrayList<String> schedule = new ArrayList<String>();
	    	List<ArrayList<String>> scheduleList = new ArrayList<ArrayList<String>>();
	    	size = nameArr.length;
	    	for(int i = 0; i < size; i++) {
	    		schedule = new ArrayList<String>();
    			schedule.add(nameArr[i].replaceAll("\"", "").trim());
    			if(suspendArr[i].trim().toUpperCase().equals("TRUE")) {
    				schedule.add("1");
    			}else {
    				schedule.add("0");
    			}
    			schedule.add(conditionArr[i].replaceAll("\"", "").trim());
    			schedule.add(taskArr[i].replaceAll("\"", "").trim());
    			scheduleList.add(schedule);
	    	}
	    	log.info(scheduleList);
	    	resultList = cmdOperationUtil.cmdMCUGroupSetSchedule(sGroupId, scheduleList);
	    	mcuListSize = resultList.size();
	    	status = ResultStatus.SUCCESS;
			
		}catch (Exception e) {
			status = ResultStatus.FAIL;
			resultList = null;
			rtnStr = e.getMessage();
			log.debug(e,e);
		}

		
		String mcu1 = null;
		String mcu2 = null;
		String retryCon = null;
		
		String resultFail = null;
		
		Code operationCode = codeManager.getCodeByCode("8.3.3");
		//Set MCU Retry Interval 따로 커맨드 전송
		try {
			String[] cmdKeys = new String[]{"network.retry.default"};
			String[] cmdKeyValues = new String[]{retryCondition};
			if(!retryCondition.isEmpty()) {
				resultRetry = cmdOperationUtil.cmdMcuGroupSetProperty(sGroupId, cmdKeys, cmdKeyValues);
				for(int j = 0; j < mcuListSize; j++) {
					mcu1 = resultList.get(j).get("mcuId").toString();
					mcu2 = resultRetry.get(j).get("mcuId").toString();
					resultFail = resultList.get(j).get("result").toString().substring(0,4);

					/* Add Fail List */
					if(resultFail.equals("FAIL")) {
						failList.add(mcu1);
					}
					
					mcuList.add(mcu1);
					
					/* Set Supplier, MCU,  */
					MCU mcu = mcuManager.getMCU(mcu1);
					Supplier supplier = mcu.getSupplier();
					if (supplier == null) {
						Operator operator = operatorManager.getOperatorByLoginId(loginId);
						supplier = operator.getSupplier();
					}

					/* Add OperationLog */
					if (operationCode != null) {
						operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
								status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
					}
					
					/* Add Retry Result */ 
					if(mcu1.equals(mcu2)) {
						retryCon = resultRetry.get(j).get("result").toString();
						resultList.get(j).put("retryCon", retryCon);
					}
				}
				log.debug(mcuList);
				
			}else {
				for(int j = 0; j < size; j++) {
					retryCon = "FAIL";
					resultList.get(j).put("retryCon", retryCon);
				}
			}
			
		}catch (Exception e) {
			status = ResultStatus.FAIL;
			log.debug(e,e);
		}
		
		/* Create Fail Group */
		try {
			String groupName = "ScheduleFail_" + TimeUtil.getCurrentTimeMilli();
			String groupType = "DCU";
			String allUserAccess= "Y";
			Integer groupId = null;
			String mobileNo = "";
			Integer newGroupId = 0;
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
	        conditionMap.put("groupId", groupId);
	        conditionMap.put("groupName", groupName);
	        conditionMap.put("groupType", groupType);
	        conditionMap.put("allUserAccess", allUserAccess);
	        conditionMap.put("operatorId", operatorId);
	        conditionMap.put("mobileNo", mobileNo);
	        conditionMap.put("supplierId", supplierId);
	        
	        int resultCnt = 0;
	        
			if(failList.size() > 0) {
		        groupMgmtManager.saveGroup(conditionMap);
		        List<Map<String, Object>> result = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);
		        if(result != null) {
		        	newGroupId = (Integer)result.get(0).get("groupId");
		        	resultCnt = groupMgmtManager.addGroupFailList(newGroupId, failList);
		        }

		        if (resultCnt > 0) {
		        	mav.addObject("resultGroup", "SUCCESS");
		        } else {
		            mav.addObject("resultGroup", "FAIL(resulCount is 0)");
		        }
			}else {
				mav.addObject("resultGroup", "FAIL(Fail list is not exist.)");
			}
			
		} catch (Exception e) {
			mav.addObject("resultGroup", "FAIL(Exception)");
			log.debug(e,e);
		}
		
		mav.addObject("resultList",resultList);
		mav.addObject("status", status);
		return mav;
    }
    
	@RequestMapping(value="/gadget/device/addMcuGroup")
    public ModelAndView addMcuGroup (@RequestParam("supplierId") Integer supplierId,
            @RequestParam("mcuId") String mcuId,
            @RequestParam("mcuType") String mcuType,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("swVersion") String swVersion,
            @RequestParam("swRevison") String swRevison,
            @RequestParam("hwVersion") String hwVersion,
            @RequestParam("installDateStart") String installDateStart,
            @RequestParam("installDateEnd") String installDateEnd,
            @RequestParam("filter") String filter,
            @RequestParam("order") String order,
            @RequestParam("protocol") String protocol,
            @RequestParam("mcuStatus") String mcuStatus,
            @RequestParam("mcuSerial") String mcuSerial, 
            @RequestParam("modelId") String modelId,
            @RequestParam("groupName") String groupName,
            @RequestParam("operatorId") Integer operatorId){ 

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("mcuId", mcuId);
        conditionMap.put("mcuType", mcuType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("swVersion", swVersion);
        conditionMap.put("swRevison", swRevison);
        conditionMap.put("hwVersion",hwVersion);
        conditionMap.put("installDateStart", installDateStart);
        conditionMap.put("installDateEnd", installDateEnd);
        conditionMap.put("filter", filter);
        conditionMap.put("order", order);
        conditionMap.put("protocol", protocol);
        conditionMap.put("mcuStatus", mcuStatus);
        conditionMap.put("mcuSerial", mcuSerial);
        conditionMap.put("modelId", modelId);
        conditionMap.put("page", null);
        conditionMap.put("limit", null);

        ModelAndView mav = new ModelAndView("jsonView");
        List<String> mcuList = new ArrayList<String>();
        mcuList = mcuManager.getMcuSearchedList(conditionMap);
        
        /* Create Searched Group */
		try {
			String groupType = "DCU";
			String allUserAccess= "Y";
			Integer groupId = null;
			String mobileNo = "";
			Integer newGroupId = 0;
			
			Map<String, Object> conditionMap2 = new HashMap<String, Object>();
	        conditionMap2.put("groupId", groupId);
	        conditionMap2.put("groupName", groupName);
	        conditionMap2.put("groupType", groupType);
	        conditionMap2.put("allUserAccess", allUserAccess);
	        conditionMap2.put("operatorId", operatorId);
	        conditionMap2.put("mobileNo", mobileNo);
	        conditionMap2.put("supplierId", supplierId);
	        
	        int resultCnt = 0;
	        
			if(mcuList.size() > 0) {
		        groupMgmtManager.saveGroup(conditionMap2);
		        List<Map<String, Object>> result = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap2);
		        if(result != null) {
		        	newGroupId = (Integer)result.get(0).get("groupId");
		        	resultCnt = groupMgmtManager.addGroupFailList(newGroupId, mcuList);
		        }

		        if (resultCnt > 0) {
		        	mav.addObject("result", "SUCCESS");
		        } else {
		            mav.addObject("result", "FAIL(resulCount is 0)");
		        }
			}else {
				mav.addObject("result", "FAIL(Searched list is not exist.)");
			}
			
		} catch (Exception e) {
			mav.addObject("result", "FAIL(Exception)");
			log.debug(e,e);
		}

        return mav;
    }
	
    @RequestMapping(value="/gadget/device/getFirmwareVersionList_dcu-coordinate")
    public ModelAndView getCodiFirmwareVersionList(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
		
		List<String> versionList = mcuManager.getCodiFirmwareVersionList(condition);
		
		mav.addObject("result", versionList);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getDeviceList_dcu-coordinate")
    public ModelAndView getCodiDeviceList(
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = mcuManager.getCodiDeviceList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getTargetList_dcu-coordinate")
    public ModelAndView getCodiTargetListBy(
    		@RequestParam(value="modelId") String modelId,
    		@RequestParam(value="deviceId") String deviceId,
    		@RequestParam(value="fwVersion") String fwVersion,
    		@RequestParam(value="locationId") String locationId,
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
        condition.put("installStartDate", installStartDate);
        condition.put("installEndtDate", installEndtDate);
        condition.put("lastCommStartDate", lastCommStartDate);
        condition.put("lastCommEndDate", lastCommEndDate);
        condition.put("hwVer", hwVer);
        
		List<String> deviceList = mcuManager.getCodiTargetList(condition);
		
		mav.addObject("result", deviceList);
        return mav;
    }
}