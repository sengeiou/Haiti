package com.aimir.bo.device;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import nl.captcha.Captcha;

@Controller
public class CaptChaController {
    @RequestMapping(value="/gadget/report/CaptchaSubmit.do")
    public ModelAndView check(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session =request.getSession();
        ModelAndView mav = new ModelAndView("jsonView");
        
        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
        String answer = request.getParameter("answer"); //사용자가 입력한 문자열
        if ( answer != null && !"".equals(answer)){
        	if (captcha.isCorrect(answer)){ 
        		mav.addObject("capcahResult","true");
        		session.removeAttribute(Captcha.NAME);
        	}else{
        		mav.addObject("capcahResult","false");
        		session.removeAttribute(Captcha.NAME);
        	}
        }
        return mav;
    }
}
