/**
 * SicLoadProfileController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.mvm;

import java.text.ParseException;
import java.util.HashMap;
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
import com.aimir.service.mvm.SicLoadProfileManager;
import com.aimir.service.system.CodeManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;


/**
 * SicLoadProfileController.java Description
 * <p>
 * 
 * <pre>
 * Date          Version     Author   Description
 * 2012. 4. 12.  v1.0        문동규   SIC Load Profile View Controller
 * </pre>
 */
@Controller
public class SicLoadProfileController
{

	protected static Log log = LogFactory
			.getLog(SicLoadProfileController.class);

	@Autowired
	SicLoadProfileManager sicLoadProfileManager;

	@Autowired
	CodeManager codeManager;

	/**
	 * method name : loadSicLoadProfileMini<b/> method Desc : SIC Load Profile
	 * Mini Gadget
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gadget/mvm/sicLoadProfileMiniGadget")
	public ModelAndView sicLoadProfileMiniGadget()
	{
		ModelAndView mav = new ModelAndView("/gadget/mvm/sicLoadProfileMiniGadget");

		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		return mav;
	}

	/**
	 * method name : loadSicLoadProfileMax<b/> method Desc : SIC Load Profile
	 * Max Gadget
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gadget/mvm/sicLoadProfileMaxGadget")
	public ModelAndView loadSicLoadProfileMax()
	{
		ModelAndView mav = new ModelAndView("/gadget/mvm/sicLoadProfileMaxGadget");

		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());

		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

		AimirUser user = (AimirUser) instance.getUserFromSession();

		mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

		return mav;
	}

	/**
	 * method name : getSicCustomerEnergyUsageList<b/> method Desc : SIC Load
	 * Profile 고객사 에너지 사용랑 리스트 fetch
	 * 
	 * @param supplierId
	 * @param searchStartDate
	 * @param searchEndDate
	 * @return
	 */
	@RequestMapping(value = "/gadget/mvm/getSicContEnergyUsageTreeData")
    public ModelAndView getSicContEnergyUsageTreeData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        if (StringUtil.nullToBlank(searchStartDate).isEmpty()) {
            try {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<Map<String, Object>> result = sicLoadProfileManager.getSicContEnergyUsageTreeData(conditionMap);

        mav.addObject("result", result);
//        mav.addObject("totalCount", sicLoadProfileManager.getEbsSuspectedDtsListTotalCount(conditionMap));

        return mav;
    }

//	/**
//	 * 
//	 * Profile 고객사 에너지 사용랑 리스트 fetch 수정된것.
//	 * 
//	 * @param supplierId
//	 * @param searchStartDate
//	 * @param searchEndDate
//	 * @return
//	 */
//	@RequestMapping(value = "/gadget/mvm/getSicCustomerEnergyUsageList2")
//    @Deprecated
//    public ModelAndView getSicCustomerEnergyUsageList2(@RequestParam("supplierId") Integer supplierId,
//            @RequestParam("searchStartDate") String searchStartDate,
//            @RequestParam("searchEndDate") String searchEndDate) {
//        ModelAndView mav = new ModelAndView("jsonView");
//
//        Map<String, Object> conditionMap = new HashMap<String, Object>();
//        conditionMap.put("supplierId", supplierId);
//        conditionMap.put("searchStartDate", searchStartDate);
//        conditionMap.put("searchEndDate", searchEndDate);
//
//        if (StringUtil.nullToBlank(searchStartDate).isEmpty()) {
//            try {
//                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
//                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        List<Map<String, Object>> result = sicLoadProfileManager.getSicCustomerEnergyUsageList2(conditionMap);
//
//        // TEST
//        // List<Map<String, Object>> result2 = sicLoadProfileManager.getSicCustomerEnergyUsageList(conditionMap);
//
//        mav.addObject("result", result);
//        mav.addObject("totalCount", sicLoadProfileManager.getEbsSuspectedDtsListTotalCount2(conditionMap));
//
//        return mav;
//    }

	/**
	 * method name : getSicLoadProfileChartData<b/> 
	 * method Desc : SIC Load Profile 맥스가젯의 Load Profile Chart Data 를 조회한다.
	 * 
	 * @param supplierId
	 * @param sicCode
	 * @param searchStartDate
	 * @param searchEndDate
	 * @return
	 */
    @RequestMapping(value = "/gadget/mvm/getSicLoadProfileChartData")
    public ModelAndView getSicLoadProfileChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("sicCode") String sicCode,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("supplierId", supplierId);
        conditionMap.put("sicCode", sicCode);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        if (StringUtil.nullToBlank(searchStartDate).isEmpty()) {
            try {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = sicLoadProfileManager.getSicLoadProfileChartData(conditionMap);
        mav.addAllObjects(result);

        return mav;
    }

	/**
	 * method name : getSicTotalLoadProfileChartData<b/>
	 * method Desc : SIC Load Profile 맥스가젯의 Total Load Profile Chart Data 를 조회한다.
	 * 
	 * @param supplierId
	 * @param searchStartDate
	 * @param searchEndDate
	 * @return
	 */
    @RequestMapping(value = "/gadget/mvm/getSicTotalLoadProfileChartData")
    public ModelAndView getSicTotalLoadProfileChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        if (StringUtil.nullToBlank(searchStartDate).isEmpty()) {
            try {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = sicLoadProfileManager.getSicTotalLoadProfileChartData(conditionMap);
        mav.addAllObjects(result);

        return mav;
    }
}