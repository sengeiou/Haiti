package com.aimir.bo.device.firmware;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class coapOperationController {
	@RequestMapping(value="/gadget/device/coap/coapBrowserPopup")
    public ModelAndView coapBrowserPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/coap/coapBrowserPopup");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }
	
	
	@RequestMapping(value="/gadget/device/coap/coapHelpPopup")
    public ModelAndView coapHelpPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/coap/coapHelpPopup");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }
}



