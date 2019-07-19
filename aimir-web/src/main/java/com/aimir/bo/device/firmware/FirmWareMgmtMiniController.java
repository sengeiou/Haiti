/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWareMainController
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 * firmWareMainGadget.jsp
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.bo.device.firmware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.service.device.FirmWareManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.support.AimirFilePath;


@Controller
public class FirmWareMgmtMiniController {

	
    @Autowired
    FirmWareManager firmWareManager;
    
	@Autowired
	CodeManager codeManager;    

	@Autowired
	DeviceModelManager deviceModelManager;

    @Autowired
	AimirFilePath aimirFilePath;

    /**
     * @param       : jsp Call param
     * @exception   : 
     * @Date        : 2010/12/10
     * @Description : firmware Gaget Mini Main 페이지.
     * jsp : firmwareMgmtMax.jsp
     */
	@RequestMapping(value="/gadget/device/firmware/firmwareMgmtMiniGadget")
    public ModelAndView firmWareMainGadget() {
		ModelAndView mav = new ModelAndView("jsonView");  
        mav.setViewName("/gadget/device/firmware/firmwareMgmtMini");
        return mav;
    }
	
	
}