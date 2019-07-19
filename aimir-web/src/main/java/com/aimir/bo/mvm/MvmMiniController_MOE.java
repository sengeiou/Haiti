package com.aimir.bo.mvm;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.service.mvm.MeteringRateManager;
import com.aimir.service.system.OperatorManager;

@Controller
public class MvmMiniController_MOE {

    @Autowired
    MeteringRateManager meteringRateManager;

    @Autowired
    OperatorManager operatorManager;

    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetEM_MOE.do")
    public final ModelAndView executeEM() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        // user 별 location 제한
        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(((Operator)user.getOperator(new Operator())).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mav.setViewName("/gadget/mvm/mvmMiniGadget_MOE");
        mav.addObject("mvmMiniType", "EM");
        return mav;
    }
}