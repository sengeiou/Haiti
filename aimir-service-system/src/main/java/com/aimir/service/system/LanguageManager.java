package com.aimir.service.system;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.Language;

@WSDLDocumentation("Language Information Service for system Locale setting")
@WebService(name="LanguageService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface LanguageManager {
	
	@WebMethod
	@WebResult(name="Language")
	public Language get(
			@WebParam(name ="launuageId")Integer languageId);
	
	@WebMethod
	@WebResult(name="LanguageList")
    public List<Language> getLanguaes();
}
