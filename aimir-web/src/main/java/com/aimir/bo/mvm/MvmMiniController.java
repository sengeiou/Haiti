package com.aimir.bo.mvm;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.service.mvm.MeteringRateManager;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeUtil;

@Controller
public class MvmMiniController {

    @Autowired
    MeteringRateManager meteringRateManager;

    @Autowired
    OperatorManager operatorManager;

    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetEM.do")
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
        mav.setViewName("/gadget/mvm/mvmMiniGadget");
        mav.addObject("mvmMiniType", "EM");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetGM.do")
    public final ModelAndView executeGM() {
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
        mav.setViewName("/gadget/mvm/mvmMiniGadget");
        mav.addObject("mvmMiniType", "GM");
        return mav;
    }
    
    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetWM.do")
    public final ModelAndView executeWM() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        // user 별 location 제한
        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(user.getOperator(new Operator()).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mav.setViewName("/gadget/mvm/mvmMiniGadget");
        mav.addObject("mvmMiniType", "WM");
        return mav;
    }
    
    @RequestMapping(value="/gadget/mvm/mvmMiniGadgetHM.do")
    public final ModelAndView executeHM() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        // user 별 location 제한
        Operator operator = null;
        if (user != null && !user.isAnonymous()) {
            try {
                operator = operatorManager.getOperator(user.getOperator(new Operator()).getId());

                if (operator.getUseLocation() != null && operator.getUseLocation()) {
                    // set a permit location
                    mav.addObject("permitLocationId", (operator.getLocationId() == null) ? 0 : operator.getLocationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mav.setViewName("/gadget/mvm/mvmMiniGadget");
        mav.addObject("mvmMiniType", "HM");
        return mav;
    }

    /**
     * method name : getMeteringSuccessRateListWithChildren<b/>
     * method Desc : MeteringData Mini 가젯에서 location 별 검침 성공율 grid 의 데이터를 조회한다.
     *
     * @param searchStartDate
     * @param searchEndDate
     * @param meterType
     * @param supplierId
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value="/gadget/mvm/getMeteringSuccessRateListWithChildren")
    public ModelAndView getMeteringSuccessRateListWithChildren(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("meterType") String meterType,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("permitLocationId") Integer permitLocationId,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("permitLocationId", permitLocationId);
        
        List<MeteringFailureData> resultList = meteringRateManager.getMeteringSuccessRateListWithChildren(conditionMap);
        int listlength = resultList.size();
        mav.addObject("result", ReflectionUtils.getDefineListToMapList(resultList.subList(0, listlength-1)));
        mav.addObject("total",ReflectionUtils.getDefineListToMapList(resultList.subList(listlength-1, listlength)));
        return mav;
    }
}