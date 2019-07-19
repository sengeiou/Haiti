package com.aimir.service.system;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;
import com.aimir.model.system.TimeZone;

@WSDLDocumentation("Timezone Information")
@WebService(name="TimeZoneService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface TimeZoneManager {
	
	@WebMethod
	@WebResult(name="TimeZone")
	public TimeZone get(Integer timezoneId);
	
	
	@WebMethod
	@WebResult(name="TimeZonesList")
	public List<TimeZone> getTimeZones();
}
