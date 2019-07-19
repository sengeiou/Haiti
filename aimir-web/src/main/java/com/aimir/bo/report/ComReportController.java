package com.aimir.bo.report;


import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.ComReportManager;
import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ComReportController {

    private Logger logger = Logger.getLogger(ComReportController.class);

    /**
     * MANAGER
     */
    @Autowired
    private ComReportManager crManager;

    /**
     * Mini Gadget
     */
    @RequestMapping(value="/gadget/report/ComReportMiniGadget.do")
    public ModelAndView loadComReportMiniGadget() {
        return new ModelAndView("gadget/report/comReportMiniGadget");
    }

    /**
     * Max Gadget
     */
    @RequestMapping(value="/gadget/report/ComReportMaxGadget.do")
    public ModelAndView loadComReportMaxGadget() {
        ModelAndView mav = new ModelAndView("gadget/report/comReportMaxGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

    /**
     * 일자별
     */
    @RequestMapping(value="/gadget/report/getValidMeteringRate.do")
    public ModelAndView getDefaultUploadHistory(
                                                @RequestParam(value="supplierId") String supplierId,
                                                @RequestParam(value="mdevId") String mdevId,
                                                @RequestParam(value="searchDate") String searchDate){
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String,Object> result = new TreeMap<>();
        Map<String,Object> condition = new HashMap<>();

        //검색조건
        condition.put("channel",1); //channel=1 고정
        condition.put("mdevId", mdevId);
        condition.put("yyyymmdd", searchDate);

        //시간순 LP및 정전 이벤트
        result = crManager.getValidMeteringRate(condition);

        Map<String,String> message = (Map<String,String>)result.get("MESSAGE");
        mav.addObject("message", message);
        if(result.containsKey("CALC"))
            mav.addObject("calc", (List<Map<String,String>>)result.get("CALC"));

        return mav;
    }

    /**
     * 미터 리스트
     */
    @RequestMapping(value="/gadget/report/getMeterNumberList.do")
    public ModelAndView getMeterSerialList(
                                            @RequestParam(value="supplierId") String supplierId,
                                            @RequestParam(value = "curPage", required = false) String curPage){

        ModelAndView mav = new ModelAndView("jsonView");
        String limit = null;
        if (curPage == null) {
            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
            curPage = request.getParameter("page");
            limit = request.getParameter("limit");
        }

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("curPage", curPage);
        condition.put("limit", limit);
        condition.put("supplierId", supplierId);
        List<Object> meterSearchGrid = crManager.getMeterNumberList(condition);

        mav.addObject("totalCnt", meterSearchGrid.get(0));
        mav.addObject("gridData", meterSearchGrid.get(1));
        mav.addObject("allGridData", meterSearchGrid.get(2));

        return mav;
    }
}
