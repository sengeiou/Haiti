package com.aimir.bo.device.eventAlert;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.Authenticator;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommonConstantsProperty;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.service.device.EventAlertManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ProfileManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Controller
public class EventAlertLogMdisController {
	private static Log log = LogFactory.getLog(EventAlertLogMdisController.class);

	@Autowired
	EventAlertLogManager eventAlertLogManager;

	@Autowired
	EventAlertManager eventAlertManager;

	@Autowired
	CodeManager codeManager;

	@Autowired
	ProfileManager profileManager;

	@Autowired
	SupplierManager supplierManager;

	/**
	 * method name : getEventAlertLogMax<b/>
	 * method Desc : MDIS. EventAlertLog Max Gadget
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMdisMax.*")
	public ModelAndView getEventAlertLogMax(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		ESAPI.httpUtilities().setCurrentHTTP(request, response);

		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI
				.authenticator();

		AimirUser user = (AimirUser) instance.getUserFromSession();

		if (user != null && !user.isAnonymous()) {
			try {
				mav.addObject("userId", user.getOperator(new Operator())
						.getId());
				mav.addObject("supplierId", user.getRoleData().getSupplier()
						.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String interval = CommonConstantsProperty.getProperty("eventAlarm.interval");
		mav.addObject("interval", interval);
		mav.setViewName("/gadget/device/eventAlert/eventAlertLogMdisMax");

		return mav;
	}

	/**
	 * method name : getEventAlertLogMini<b/>
	 * method Desc : MDIS. EventAlertLog Mini Gadget
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMdisMini.*")
	public ModelAndView getEventAlertLogMini(HttpServletRequest request,
	        HttpServletResponse response) {
	    ModelAndView mav = new ModelAndView();

	    ESAPI.httpUtilities().setCurrentHTTP(request, response);

	    // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
	    AimirAuthenticator instance = (AimirAuthenticator) ESAPI
	            .authenticator();

	    AimirUser user = (AimirUser) instance.getUserFromSession();

	    if (user != null && !user.isAnonymous()) {
	        try {
	            mav.addObject("userId", user.getOperator(new Operator())
	                    .getId());
	            mav.addObject("supplierId", user.getRoleData().getSupplier()
	                    .getId());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

        String interval = CommonConstantsProperty.getProperty("eventAlarm.interval");
        mav.addObject("interval", interval);
	    mav.setViewName("/gadget/device/eventAlert/eventAlertLogMdisMini");

	    return mav;
	}

    /**
     * method name : getRealTimeEventAlertLogFromDB<b/>
     * method Desc : MDIS. EventAlertLog 가젯에서 RealTime 데이터를 DB에서 조회한다.
     *
     * @param supplierId
     * @param startDateTime
     * @return
     */
    @RequestMapping(value = "/gadget/device/eventAlert/getRealTimeEventAlertLogFromDB")
    public ModelAndView getRealTimeEventAlertLogFromDB(@RequestParam("supplierId") Integer supplierId, 
            @RequestParam("interval") Integer interval) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("supplierId", supplierId);

        try {
            String currentDateTime = TimeUtil.getCurrentTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(currentDateTime));

            cal.add(Calendar.MILLISECOND,(0-interval));
            conditionMap.put("searchStartDateTime", TimeUtil.getFormatTime(cal));
            conditionMap.put("searchEndDateTime", currentDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> result = eventAlertLogManager.getEventAlertLogFromDB(conditionMap);
        mav.addObject("logList", result);
        return mav;
    }
}