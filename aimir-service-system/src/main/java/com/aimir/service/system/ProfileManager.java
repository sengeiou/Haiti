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

import com.aimir.model.system.Profile;

@WSDLDocumentation("User Profile(Event Alarm Setting by personal user)")
@WebService(name="ProfileService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ProfileManager {
	
	@WebMethod
	@WebResult(name="ProfileByUserList")
    public List<Profile> getProfileByUser(
    		@WebParam(name ="userId")Integer userId);
	
	@WebMethod
    public void addProfile(@WebParam(name ="profile")Profile profile);
}

