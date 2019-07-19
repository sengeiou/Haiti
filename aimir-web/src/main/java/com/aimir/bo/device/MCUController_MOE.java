package com.aimir.bo.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUInstallImg;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Role;
import com.aimir.service.device.MCUInstallImgManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MCUManager_MOE;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;



@Controller
public class MCUController_MOE {
	
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
    RoleManager roleManager;
	
	@Autowired
    MCUManager_MOE snrManager;
		
	
	//MOE용 집중기 Mini Gadget
    @RequestMapping(value="/gadget/device/mcuMiniGadget_MOE.do")
    public ModelAndView loadMcuMiniGadget_MOE() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMiniGadget_MOE");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);

        return mav;
    }

    //MOE용 집중기 Max Gadget
    @RequestMapping(value="/gadget/device/mcuMaxGadget_MOE.do")
    public ModelAndView loadMcuMaxGadget_MOE() {
        ModelAndView mav = new ModelAndView("/gadget/device/mcuMaxGadget_MOE");
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
	
    @RequestMapping(value="/gadget/device/mcuInfo_MOE")
    public ModelAndView getMCU_MOE(

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
        if(mcu.getMacAddr() != null){
        	mcu.setSysPhoneNumber(mcu.getMacAddr());
        }

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
	
	@RequestMapping(value="/gadget/device/getConDevice")
    public ModelAndView getConDevice(@RequestParam("mcuId") String sMcuId,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("isLatest") String isLatest,
    		@RequestParam("isPoor") String isPoor,
    		@RequestParam("page") Integer page,            
			@RequestParam("pageSize") Integer pageSize,
            @RequestParam("supplierId") Integer supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Integer mcuId = Integer.parseInt(sMcuId);
    	Map<String, Object> condition = new HashMap<String, Object>();
        MCU mcu = mcuManager.getMCU(mcuId);
        if(mcu==null){
        	return mav;
        }
        condition.put("mcuId", mcuId);
        condition.put("sysId", mcu.getSysID());
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("isLatest", isLatest);
        condition.put("isPoor", isPoor);
        condition.put("page", page);
        condition.put("limit", pageSize);
        condition.put("supplierId", supplierId);

        Map<String,Map<String, Object>> snrList = snrManager.getMcuSnrList2(condition);
        if(snrList==null){
        	return mav;
        }
        mav.addObject("totalCount", snrList.get("totalCount").get("totalCount"));
        snrList.remove("totalCount");
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(snrList.values());
        
        mav.addObject("result", result);
        

        return mav;
    }
    
    @RequestMapping(value="/gadget/device/getDetailSnr")
    public ModelAndView getDetailSnr(@RequestParam("modemId") String modemId,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("isLatest") String isLatest,
            @RequestParam("supplierId") Integer supplierId) {
    	
    	Map<String, Object> condition = new HashMap<String, Object>();
        
        condition.put("modemId", modemId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("isLatest", isLatest);
        condition.put("supplierId", supplierId);
        List<Map<String,Object>> snrList = snrManager.getModemSnrList(condition);

        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("result", snrList);

        return mav;
    }
}
