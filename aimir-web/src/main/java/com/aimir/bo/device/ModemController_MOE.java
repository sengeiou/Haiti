package com.aimir.bo.device;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Role;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;

@Controller
public class ModemController_MOE {
	
	private static Log log = LogFactory.getLog(ModemController_MOE.class);

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

    @RequestMapping(value = "/gadget/device/modemMiniGadget_MOE")
    public ModelAndView getMiniChart(@RequestParam(value = "modemChart", required = false) String modemChart) {
        ModelAndView mav = new ModelAndView("gadget/device/modemMiniGadget_MOE");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());        

        return mav;
    }

    @RequestMapping(value = "/gadget/device/modemMaxGadget_MOE")
    public ModelAndView loadModemMaxGadgetForm() {
        ModelAndView mav = new ModelAndView("/gadget/device/modemMaxGadget_MOE");

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


}
