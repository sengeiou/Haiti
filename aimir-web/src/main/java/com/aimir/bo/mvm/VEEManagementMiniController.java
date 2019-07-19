package com.aimir.bo.mvm;

import java.util.List;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.VEEManager;
import com.aimir.service.mvm.bean.VEEMiniData;


@Controller
public class VEEManagementMiniController {
	
	@Autowired
    VEEManager VEEManager;
	
	@RequestMapping(value="/gadget/mvm/VEEManagementEmMiniGadget.do")
    public final ModelAndView executeEM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("mvmMiniType", CommonConstants.MeterType.EnergyMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementGmMiniGadget.do")
    public final ModelAndView executeGM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("mvmMiniType", CommonConstants.MeterType.GasMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementWmMiniGadget.do")
    public final ModelAndView executeWM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("mvmMiniType", CommonConstants.MeterType.WaterMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/VEEManagementHmMiniGadget.do")
    public final ModelAndView executeHM() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/gadget/mvm/VEEManagementEmMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("mvmMiniType", CommonConstants.MeterType.HeatMeter);
        mav.addObject("veeTalbeItemList", VEEManager.getTableItemList());
        return mav;
    }	

    // MiniVEEValidationCheckManager데이터 사용
    @RequestMapping(value = "/gadget/device/getMiniVEEValidationCheckManager.do")
    public ModelAndView getMiniVEEValidationCheckManager(@RequestParam(value = "meterType", required = false) String meterType,
            @RequestParam(value = "tabType", required = false) String tabType,
            @RequestParam(value = "dateType", required = false) String dateType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "selectData", required = false) String selectData,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "pageSize", required = false) String pageSize) {

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Integer supplierId = user.getRoleData().getSupplier().getId();
        
        String[] values = new String[9];

        values[0] = meterType;
        values[1] = tabType;
        values[2] = dateType;
        values[3] = startDate;
        values[4] = endDate;
        values[5] = selectData;
        values[6] = page;
        values[7] = pageSize;
        values[8] = supplierId.toString();

        List<VEEMiniData> resultList = VEEManager.getMiniVEEValidationCheckManager(values);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("gridData", resultList);
        mav.addObject("totalCnt", resultList.size());

        return mav;
    }

    // MiniVEEHistoryManager데이터 사용
    @RequestMapping(value = "/gadget/device/getMiniHistoryManager.do")
    public ModelAndView getMiniHistoryManager(@RequestParam(value = "meterType", required = false) String meterType,
            @RequestParam(value = "tabType", required = false) String tabType,
            @RequestParam(value = "dateType", required = false) String dateType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "selectData", required = false) String selectData,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "pageSize", required = false) String pageSize) {

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Integer supplierId = user.getRoleData().getSupplier().getId();
        
        String[] values = new String[9];

        values[0] = meterType;
        values[1] = tabType;
        values[2] = dateType;
        values[3] = startDate;
        values[4] = endDate;
        values[5] = selectData;
        values[6] = page;
        values[7] = pageSize;
        values[8] = supplierId.toString();

        List<Object> resultList = VEEManager.getMiniVEEHistoryManager(values);

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("gridData", resultList);
        mav.addObject("totalCnt", resultList.size());

        return mav;
    }
}
