package com.aimir.bo.system.code;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.aimir.model.system.Code;
import com.aimir.service.system.CodeManager;

@Controller
@RequestMapping("/editCode.do")
@SessionAttributes(types = Code.class)
public class EditCodeForm {
	
	@Autowired
	CodeManager codeManager;	
	
	@RequestMapping(method = RequestMethod.GET)
	public String getCode(@RequestParam("id") int codeId, Model model) {
		Code code = codeManager.getCode(codeId);		
		model.addAttribute(code);
		return "codeForm";
	}
	
	/** 수정 */
	@RequestMapping(method = RequestMethod.POST)
	public String updateCode(@ModelAttribute Code code, BindingResult result, SessionStatus status,
			HttpServletRequest request) {
		
		new CodeValidator().validate(code, result);
		
		/* validator check */
		if ( result.hasErrors() ) 			
			return "codeForm";

		//codeManager.add(code);			
		status.setComplete();					
		return "redirect:codes.do";
	}
}
