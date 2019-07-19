package com.aimir.bo.device;

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
import com.aimir.model.device.Headend;
import com.aimir.model.system.Role;
import com.aimir.service.device.HeadendCtrlManager;
import com.aimir.service.device.HeadendManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Controller
public class HeadendController {

    @Autowired
    HeadendManager headendManager;

    @Autowired
    HeadendCtrlManager headendCtrlManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/device/headendMaxGadget")
    public ModelAndView headendMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/headendMaxGadget");
        return mav;
    }

    @RequestMapping(value="/gadget/device/headendMiniGadget")
    public ModelAndView headendMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/headendMiniGadget");

        List<Headend> result = headendManager.getLastData();
        Headend headend = new Headend();
        if (result != null && !result.isEmpty()) {
            headend = result.get(0);
        }

        mav.addObject("headend", headend);
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("cmdAuth", authMap.get("command"));  // Command 권한(command = true)
        return mav;
    }

    @RequestMapping(value = "/gadget/device/saveHeadendCtrlCommand")
    public ModelAndView saveHeadendCtrlCommand(@RequestParam("ctrlId") String ctrlId,
            @RequestParam(value = "timeout", required = false) String timeout,
            @RequestParam(value = "retry", required = false) String retry) {

        ModelAndView mav = new ModelAndView("jsonView");

        // Registed date
        // Calendar cal = Calendar.getInstance();
        // SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String writeDate = null;

        try {
            writeDate = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (ctrlId.isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", " Error : Control ID is null!");
            mav.addObject("result", resultMap);
            return mav;
        }
        if (StringUtil.nullToBlank(writeDate).isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : writeDate is null!");
            mav.addObject("result", resultMap);
            return mav;
        }
        if (timeout.isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : Timeout is null!");
            mav.addObject("result", resultMap);
            return mav;
        }
        if (retry.isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : RetryCount is null!");
            mav.addObject("result", resultMap);
            return mav;
        }

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);
        conditionMap.put("writeDate", writeDate);
        conditionMap.put("timeout", timeout);
        conditionMap.put("retry", retry);

        headendCtrlManager.insertHeadendCtrl(conditionMap);

        resultMap.put("status", "success");
        mav.addObject("headendCtrlwriteDate", writeDate);
        mav.addObject("result", resultMap);

        return mav;
    }

    /**
     * method name : getHeadendCtrlCommandResultData
     * method Desc : MDIS 커맨드 결과 정보를 조회한다.
     *
     * @param ctrlId
     * @param writeDate
     * @return
     */
    @RequestMapping(value="/gadget/device/getHeadendCtrlCommandResultData")
    public ModelAndView getHeadendCtrlCommandResultData(@RequestParam("ctrlId") String ctrlId,
                                    @RequestParam("writeDate") String writeDate) {

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);
        conditionMap.put("writeDate", writeDate);

        Map<String, Object> result = headendCtrlManager.getHeadendCtrlCommandResultData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }


}
