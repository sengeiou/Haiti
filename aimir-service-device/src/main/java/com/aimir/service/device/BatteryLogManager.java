package com.aimir.service.device;

import java.util.ArrayList;
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

@WebService(name="BatteryLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface BatteryLogManager {

	@WebMethod
	@WebResult(name="ModemTypeCombo")
	public List<Object> getModemTypeCombo();
	
	@WebMethod
	@WebResult(name="PowerTypeCombo")
	public List<Map<String, Object>> getPowerTypeCombo();
	
	public Map<String, Object> getBatteryLog(Map<String, Object> condition);
	
	@WebMethod
	@WebResult(name="BatteryLogMap")
	public Map<String, Object> getBatteryLogByParam(
			@WebParam(name="supplierId")   String supplierId,
			@WebParam(name="modemType") String modemType,
			@WebParam(name="modemTypeName") String modemTypeName);


    public Map<String, Object> getBatteryLogList(Map<String, Object> condition);
    public Map<String, Object> getBatteryLogListTotalCount(Map<String, Object> condition);
    public ArrayList<Map<String, Object>> getBatteryVoltageLogList(Map<String,Object> condition);
    @WebMethod
	@WebResult(name="BatteryLogMap")
	public Map<String, Object> getBatteryLogListByParam(
			@WebParam(name = "supplierId") String supplierId,
			@WebParam(name = "modemType") String modemType,
			@WebParam(name = "modemTypeName") String modemTypeName,
			@WebParam(name = "modemId") String modemId,
			@WebParam(name = "powerType") String powerType,
			@WebParam(name = "meterLocation") String meterLocation,
			@WebParam(name = "batteryStatus") String batteryStatus,
			@WebParam(name = "batteryVoltSign") String batteryVoltSign,
			@WebParam(name = "batteryVolt") String batteryVolt,
			@WebParam(name = "operatingDaySign") String operatingDaySign,
			@WebParam(name = "operatingDay") String operatingDay);
	

    public Map<String, Object> getBatteryLogDetailList(Map<String, Object> condition);
    public Map<String, Object> getBatteryLogDetailListTotalCount(Map<String, Object> condition);
    
	@WebMethod
	@WebResult(name = "BatteryLogDetailMap")
	public Map<String, Object> getBatteryLogDetailListByParam(
			@WebParam(name = "supplierId") String supplierId,
			@WebParam(name = "modemType") String modemType,
			@WebParam(name = "modemId") String modemId,
			@WebParam(name = "dateType") String dateType,
			@WebParam(name = "fromDate") String fromDate,
			@WebParam(name = "toDate") String toDate);
}
