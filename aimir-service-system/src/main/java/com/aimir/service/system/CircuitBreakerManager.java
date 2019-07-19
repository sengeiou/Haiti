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

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.model.device.CircuitBreakerLog;
import com.aimir.model.device.CircuitBreakerSetting;

@WSDLDocumentation("Meter Relay Switch Management Service")
@WebService(name="CircuitBreakerService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface CircuitBreakerManager {
	
	@WebMethod
	@WebResult(name="SupplyCapacityList")
	List<Map<String, String>> getSupplyCapacity(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	void saveSupplyCapacity(CircuitBreakerStatus status, String targetType, CircuitBreakerCondition condition, int meterId);
	
	@WebMethod
	@WebResult(name="saveCircuitBreakerSetting")
	CircuitBreakerSetting saveCircuitBreakerSetting(
			@WebParam(name="setting") CircuitBreakerSetting setting);
	
	@WebMethod
	@WebResult(name="CircuitBreakerSetting")
	CircuitBreakerSetting getCircuitBreakerSetting(
			@WebParam(name="prepayment") CircuitBreakerCondition prepayment);
	
	@WebMethod
	@WebResult(name="CircuitBreakerList")
	List<CircuitBreakerLog> getCircuitBreakerLogGridData(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	@WebResult(name="CircuitBreakerCount")
	Long getCircuitBreakerLogGridDataCount(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	@WebResult(name="ElecSupplyCapacityList")
	List<Map<String, String>> getElecSupplyCapacityGridData(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	@WebResult(name="EmergencyElecSupplyCapacityList")
	List<Map<String, String>> getEmergencyElecSupplyCapacityGridData(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod(operationName ="ElecSupplyCapacityCount")
	@WebResult(name="ElecSupplyCapacityCount")
	String getElecSupplyCapacityGridDataCount(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	@WebResult(name="CircuitBreakerLogChartList")
	List<Map<String, String>> getCircuitBreakerLogChartData(
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod(operationName ="ElecSupplyCapacityCountList")
	@WebResult(name="ElecSupplyCapacityCountList")
	List<Map<String, String>> getElecSupplyCapacityGridDataCount(
			@WebParam(name="data") List<Map<String, String>> data, 
			@WebParam(name="paramMap") Map<String, String> paramMap);
	
	@WebMethod
	@WebResult(name="EmergencyElecSupplyCapacityCountList")
	List<Map<String, String>> getEmergencyElecSupplyCapacityGridDataCount(
			@WebParam(name="data") List<Map<String, String>> data, 
			@WebParam(name="paramMap") Map<String, String> paramMap);

	@WebMethod
	@WebResult(name="ElecSupplyCapacityMiniList")
	List<Map<String, String>> getElecSupplyCapacityMiniGridData(
			@WebParam(name="paramMap") Map<String, String> paramMap);
}
