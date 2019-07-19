package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.EnergySavingGoal2;

@WebService(name="KTSeochoExhibitService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface KTSeochoExhibitManager {
	
	@WebMethod
	@WebResult(name="EnergyDataMap")
	public Map<String,Object> getEnergyData(
			@WebParam(name ="locationId")String locationId);
	
	@WebMethod
	@WebResult(name="EnergyDataByLocationMap")
	public Map<String, Object> getEnergyDataByLocation(
			@WebParam(name ="locationId")String locationId);
	
	@WebMethod
	@WebResult(name="SavingGoal2List")
	public List<EnergySavingGoal2> getSavingGoal(@WebParam(name ="searchDateType")String searchDateType ,
			@WebParam(name ="energyType")String energyType ,  
			@WebParam(name ="startDate")String startDate ,
			@WebParam(name ="supplierId")Integer supplierId);
}

