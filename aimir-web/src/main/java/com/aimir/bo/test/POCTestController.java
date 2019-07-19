package com.aimir.bo.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.ModemManager;
import com.aimir.util.TimeUtil;

@Controller
public class POCTestController {

    @Autowired
    MCUManager mcuManager;
    @Autowired
    ModemManager modemManager;

	private static Log log = LogFactory.getLog(POCTestController.class);

	@RequestMapping(value="/gadget/test/pocTestMiniGadget")
    public ModelAndView pocTestMiniGadget(HttpSession session) 
	{
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        ModelAndView mav = new ModelAndView("/gadget/test/pocTestMiniGadget");
        mav.addObject("supplierId", supplierId);

        return mav;
    }
	
    @RequestMapping(value="/gadget/test/pocTestMaxGadget")
    public ModelAndView pocTestMaxGadget(HttpSession session) 
	{
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        
        ModelAndView mav = new ModelAndView("/gadget/test/pocTestMaxGadget");
        mav.addObject("supplierId", supplierId);
        mav.addObject("pocWSURL",CommandProperty.getProperty("poc.test.ws"));

        return mav;
    }

	@RequestMapping(value="/gadget/test/getTargetNodes")
    public ModelAndView getTargetNodes(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("supplierId", supplierId);
		List<MCUTypeByLocationVO> mcuList = mcuManager.getMcusByCondition(condition);

        Map<String, Object> condition2 = new HashMap<String, Object>();
        condition2.put("supplierId", supplierId);
        condition2.put("sModemType", "SubGiga");
        condition2.put("curPage", 0);
        for(MCUTypeByLocationVO mcu: mcuList) {
        	condition2.remove("sMcuName");
        	condition2.put("sMcuName", mcu.getSysID());
        	condition2.put("page", 0);
        	condition2.put("pageSize", 20);
        	condition2.put("sOrder", "5");
        	List<Object> modemList = modemManager.getModemSearchGrid(condition2);
            Map<String, Object> info = new HashMap<String, Object>();
            info.put("mcu", mcu);
            if(modemList!=null && modemList.size()>1) {
                info.put("modems", modemList.get(1));
            }

            mav.addObject(mcu.getSysID(), info);
        }

        return mav;
    }

	@RequestMapping(value="/gadget/test/executeICMP6Ping")
	@Deprecated
	public ModelAndView executeICMP6Ping() {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        // AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        // AimirUser user = (AimirUser) instance.getUserFromSession();

        ModelAndView mav = new ModelAndView("jsonView");

        Calendar cal = Calendar.getInstance();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "datetime");
		data.put("value", cal.getTime().toString());
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "hoptogw");
		data.put("value", null);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "icmp6loss");
		data.put("value", 20);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "icmp6rtt");
		data.put("value", 30);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "rsswapc");
		data.put("value", null);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "linkbudge");
		data.put("value", null);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "rfoutpower");
		data.put("value", null);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "temperature");
		data.put("value", 13);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "weather");
		data.put("value", "Rainy");
		list.add(data);

        mav.addObject("result",list);

        return mav;
	}
	
	@RequestMapping(value="/gadget/test/executeCOAPPing")
	@Deprecated
	public ModelAndView executeCOAPPing() {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        // AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        // AimirUser user = (AimirUser) instance.getUserFromSession();

        ModelAndView mav = new ModelAndView("jsonView");

        mav.addObject("result","success");
		return mav;
	}
	
	@RequestMapping(value="/gadget/test/executeOBISCodeReading")
	@Deprecated
	public ModelAndView executeOBISCodeReading() {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        // AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        // AimirUser user = (AimirUser) instance.getUserFromSession();

        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "1");
		data.put("value", "aaa");
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "2");
		data.put("value", null);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "3");
		data.put("value", "bbb");
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "4");
		data.put("value", 30);
		list.add(data);
		data = new HashMap<String, Object>();
		data.put("name", "5");
		data.put("value", null);
		list.add(data);

        mav.addObject("result",list);

        return mav;
	}

	@RequestMapping(value="/gadget/test/getResultHistory")
	public ModelAndView getResultHistory(@RequestParam("command") String command,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        // AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        // AimirUser user = (AimirUser) instance.getUserFromSession();

        ModelAndView mav = new ModelAndView("jsonView");

        if(command == null || command.equals("")) {
        	return mav;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -50);

            if(command.equals("icmp6")) {

                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("hoptogw", null);
        		data.put("icmp6loss", 20);
        		data.put("icmp6rtt", 30);
        		data.put("rsswapc", null);
        		data.put("linkbudge", null);
        		data.put("rfoutpower", null);
        		data.put("temperature", 13);
        		data.put("weather", "Rainy");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("hoptogw", null);
        		data.put("icmp6loss", 20);
        		data.put("icmp6rtt", 30);
        		data.put("rsswapc", null);
        		data.put("linkbudge", null);
        		data.put("rfoutpower", null);
        		data.put("temperature", 13);
        		data.put("weather", "Rainy");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("hoptogw", null);
        		data.put("icmp6loss", 20);
        		data.put("icmp6rtt", 30);
        		data.put("rsswapc", null);
        		data.put("linkbudge", null);
        		data.put("rfoutpower", null);
        		data.put("temperature", 13);
        		data.put("weather", "Rainy");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("hoptogw", null);
        		data.put("icmp6loss", 20);
        		data.put("icmp6rtt", 30);
        		data.put("rsswapc", null);
        		data.put("linkbudge", null);
        		data.put("rfoutpower", null);
        		data.put("temperature", 13);
        		data.put("weather", "Rainy");
        		list.add(data);

                mav.addObject("result",list);
        	} else if(command.equals("coap")) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("result", "success");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("result", "fail");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("result", "success");
        		list.add(data);

                mav.addObject("result",list);
        	} else if(command.equals("obiscode")) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("1", "df");
        		data.put("2", "dfaa");
        		data.put("3", "a332");
        		data.put("4", "a3324");
        		data.put("5", "a3324a");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("1", "df3ad");
        		data.put("2", "dfaa3ff");
        		data.put("3", "a332gaaz");
        		data.put("4", "a3324377");
        		data.put("5", "a3324a224");
        		list.add(data);

        		cal.add(Calendar.MINUTE, 5);
        		data = new HashMap<String, Object>();
                data.put("targetNode", "DCU - 1");
        		data.put("datetime", cal.getTime().toString());
        		data.put("1", "df11");
        		data.put("2", "dfaa53");
        		data.put("3", "a33233");
        		data.put("4", "a3324hhk");
        		data.put("5", "a3324a457");
        		list.add(data);

                mav.addObject("result",list);
        	}
        }
        
        return mav;
	}
}
