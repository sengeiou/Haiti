package com.aimir.bo.system.code;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.aimir.model.system.Code;
import com.aimir.service.system.CodeManager;

@Controller
@RequestMapping("/addCode.do")
@SessionAttributes(types = Code.class)
public class AddCodeForm {
	private static Log log = LogFactory.getLog(AddCodeForm.class);
	
	@Autowired
	CodeManager codeManager;

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model) {
		Code code = new Code();
		model.addAttribute(code);
		return "codeForm";
	}
	
	/** 저장 */
	@RequestMapping(method = RequestMethod.POST)
	public String addCode(@ModelAttribute Code code, BindingResult result, SessionStatus status, 
			HttpServletRequest request) {
		
	    log.debug("code=" + code.toString());
		new CodeValidator().validate(code, result);
		
		/* validator check */
		if ( result.hasErrors() ) 			
			return "addCode";

		//codeManager.add(code);				
		status.setComplete();				
		return "redirect:codes.do" ;
	}	
}
