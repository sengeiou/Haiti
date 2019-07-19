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

import com.aimir.model.system.SupplyCapacityLog;

@WebService(name="SupplyCapacityLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface SupplyCapacityLogManager {
	
	@WebMethod
	public void add(
			@WebParam(name="supplyCapacityLog") SupplyCapacityLog supplyCapacityLog);
	
	@WebMethod
	public void delete(
			@WebParam(name="supplyCapacityLogId") int supplyCapacityLogId);
	
	@WebMethod(operationName ="SupplyCapacityLogsList")
	@WebResult(name="SupplyCapacityLogsList")
	public List<SupplyCapacityLog> getSupplyCapacityLogs();
	
	@WebMethod(operationName ="SupplyCapacityLogsListByPageCount")
	@WebResult(name="SupplyCapacityLogsList")
	public List<SupplyCapacityLog> getSupplyCapacityLogs(
			@WebParam(name="page") int page, 
			@WebParam(name="count") int count);
	
	@WebMethod
	public void supplyCapacityLogDelete(
			@WebParam(name="supplyTypeId") int supplyTypeId);
}
