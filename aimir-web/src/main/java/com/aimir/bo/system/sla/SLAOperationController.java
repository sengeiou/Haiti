package com.aimir.bo.system.sla;


import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.sla.SLAOperationManager;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SLAOperationController {
    @Autowired
    GroupMgmtManager groupMgmtManager;

    @Autowired
    SLAOperationManager slaOperationManager;

    /**
     * Mini Gadget
     */
    @RequestMapping(value="/gadget/system/sla/slaMiniGadget")
    public ModelAndView loadSlaMiniGadget() {
        return new ModelAndView("/gadget/system/sla/slaMiniGadget");
    }

    /**
     * Max Gadget
     */
    @RequestMapping(value="/gadget/system/sla/slaMaxGadget")
    public ModelAndView loadSlaMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/system/sla/slaMaxGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        int operatorId = new Long(user.getAccountId()).intValue();

        mav.addObject("supplierId", supplierId);
        mav.addObject("operatorId", operatorId);
        return mav;
    }

    /**
     * Monitoring Tab : SLA Grid
     */
    @RequestMapping(value="/gadget/system/sla/getSLAList")
    public ModelAndView getSLAList(
            @RequestParam(value = "supplierId") String supplierId,
            @RequestParam(value = "dso") String dso,
            @RequestParam(value = "msa") String msa,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
        List<Object> result	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();

        condition.put("supplierId", supplierId);
        condition.put("dso", dso);
        condition.put("msa", msa);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);

        HashMap returnResult1 = new HashMap();
        HashMap returnResult2 = new HashMap();
        HashMap returnResult3 = new HashMap();

        returnResult1.put("dso", "DSO1");
        returnResult1.put("msa", "MSA1");
        returnResult1.put("installedMeter", "1000");
        returnResult1.put("collectedRate", "99.8");
        returnResult1.put("slaMeter", "100");
        returnResult1.put("slaRate", "10");
        result.add(returnResult1);

        returnResult2.put("dso", "DSO2");
        returnResult2.put("msa", "MSA2");
        returnResult2.put("installedMeter", "1000");
        returnResult2.put("collectedRate", "99.8");
        returnResult2.put("slaMeter", "100");
        returnResult2.put("slaRate", "10");
        result.add(returnResult2);

        returnResult3.put("dso", "TEST");
        returnResult3.put("msa", "TEST");
        returnResult3.put("installedMeter", "1000");
        returnResult3.put("collectedRate", "99.8");
        returnResult3.put("slaMeter", "100");
        returnResult3.put("slaRate", "10");
        result.add(returnResult3);

        //total 고려해야 함

        mav.addObject("rtnStr", result);
        return mav;
    }

    /**
     * Schedule Tab : Group Name Grid
     * Desc : Name & Number of Meter
     */
    @RequestMapping(value="/gadget/system/sla/getGroupNameList")
    public ModelAndView getGroupNameList(
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("operatorId") Integer operatorId    ){
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("groupType", "Meter");

        List<Map<String, Object>> result = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);

        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * Schedule Tab : Group Name Grid 2
     * Desc : Retrieve the basic infomation for DCU, Meter Group
     */
    @RequestMapping(value="/gadget/system/sla/getGroupNameList2")
    public ModelAndView getGroupNameList2(
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("operatorId") Integer operatorId    ){
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("supplierId", supplierId);
        
        conditionMap.put("groupType", "Meter");
        List<Map<String, Object>> result = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);
        
        conditionMap.put("groupType", "DCU");
        List<Map<String, Object>> result2 = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);
        
        conditionMap.put("groupType", "Modem");
        List<Map<String, Object>> result3 = groupMgmtManager.getGroupListNotHomeGroupIHD(conditionMap);
        
        result.addAll(result2);
        result.addAll(result3);

        mav.addObject("result", result);
        return mav;
    }

    /**
     * Schedule Tab : Group Member Grid
     * method Desc : 선택한 그룹에 해당하는 미터의 리스트를 조회
     */
    @RequestMapping(value = "/gadget/system/sla/getGroupMeterList")
    public ModelAndView getGroupMeterList(
            @RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("groupId", groupId);

        List<Object> result = groupMgmtManager.getMemberSelectedData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }

    /**
     * Schedule Tab : Strategy Grid (Group Schedule List)
     * Desc : List of Schedule History of the each group
     */
    @RequestMapping(value = "/gadget/system/sla/getStrategyList")
    public ModelAndView getStrategyList(
            @RequestParam("supplierId") Integer supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");
        List<Object> result	= new ArrayList<Object>();


        //total 고려해야 함
        Map<String,Object> rt = new HashMap<String,Object>();
        rt= slaOperationManager.getGroupStragetyList(supplierId);

        mav.addObject("result", rt.get("list"));
        return mav;
    }


}
