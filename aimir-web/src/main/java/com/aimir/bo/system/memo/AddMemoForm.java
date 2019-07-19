package com.aimir.bo.system.memo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;

import com.aimir.model.system.Memo;
import com.aimir.service.system.MemoManager;

@Controller
@RequestMapping("/gadget/system/memo/addMemo.do")
public class AddMemoForm {
	
	@SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddMemoForm.class);
	
	@Autowired
    MemoManager memoManager;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addMemo(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Memo memo, BindingResult result) 
	{ 
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
		AimirUser user = (AimirUser)instance.getUserFromSession();
		
		ModelAndView mav = new ModelAndView("jsonView");

		memo.setCoord("white");
		memo.setCont("Click here to edit your text");
		memo.setIn_date(formatter.format(Calendar.getInstance().getTime()));
		memo.setUserId(user.getAccountId());

		
		memoManager.add(memo);
		
		mav.addObject("result","success");
		
		return mav;
	}

}
