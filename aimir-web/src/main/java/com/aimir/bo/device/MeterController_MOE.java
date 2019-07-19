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
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.service.device.MeterInstallImgManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.DeviceVendorManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.AimirFilePath;
import com.aimir.util.CommonUtils;

@Controller
public class MeterController_MOE {

    private static Log log = LogFactory.getLog(MeterController_MOE.class);

    @Autowired
    MeterManager meterManager;

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
    SupplierManager supplierManager;

    @RequestMapping(value="/gadget/device/meterMiniGadget_MOE")
    public ModelAndView getMiniChart(@RequestParam(value="meterChart" ,required=false) String meterChart) {
        ModelAndView mav = new ModelAndView("gadget/device/meterMiniGadget_MOE");
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

    // MeterMaxGadget 초기 검색조건 조회
    @RequestMapping(value = "/gadget/device/meterMaxGadget_MOE")
    public ModelAndView getMeterMaxGadgetCondition() {
        ModelAndView mav = new ModelAndView("gadget/device/meterMaxGadget_MOE");
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

        return mav;
    }


}
