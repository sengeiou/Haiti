package com.aimir.service.system;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService(name="ExhibitionUsageService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ExhibitionUsageManager {
	
	@WebMethod
	@WebResult(name="LocationExbitionUsageMap")
	Map<String, Object> getLocationExbitionUsage(
			@WebParam(name ="goal")int goal,
			@WebParam(name ="channel0")int channel0,
			@WebParam(name ="channel1")int channel1,
			@WebParam(name ="channel2")int channel2);
	
	@WebMethod
	@WebResult(name="LocationExbitionUsageJejuMap")
	Map<String, Object> getLocationExbitionUsageJeju(
			@WebParam(name ="forwardGoal")int forwardGoal,
			@WebParam(name ="reverseGoal")int reverseGoal,
			@WebParam(name ="wmGoal")int wmGoal,
			@WebParam(name ="co2Goal")int co2Goal,
			@WebParam(name ="meteringGoal")int meteringGoal);
}
