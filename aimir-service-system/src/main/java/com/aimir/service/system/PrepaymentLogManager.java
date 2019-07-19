package com.aimir.service.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.PrepaymentLog;
import com.aimir.util.Condition;

@WSDLDocumentation("AuditLog to obtain information related to the class of service")
@WebService(name="PrepaymentLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface PrepaymentLogManager {
    
	@WebMethod
	@WebResult(name="PrepaymentLog")
	public PrepaymentLog getPrepaymentLog(
			@WebParam(name ="id")Long id);
	
	@WebMethod
    public void addPrepaymentLog(
    		@WebParam(name ="prepaymentLog")PrepaymentLog prepaymentLog);
	
	@WebMethod
	public void updatePrepaymentLog(
			@WebParam(name ="prepaymentLog")PrepaymentLog prepaymentLog);
	
	@WebMethod
	@WebResult(name="PrepaymentLogCountByListConditionList")
    public List<Object> getPrepaymentLogCountByListCondition(
    		@WebParam(name ="set")Set<Condition> set);
	
	@WebMethod(operationName ="PrepaymentLogByListConditionList")
	@WebResult(name="PrepaymentLogByListConditionList")
	public List<PrepaymentLog> getPrepaymentLogByListCondition(
			@WebParam(name ="set")Set<Condition> set);
	
	@WebMethod(operationName ="PrepaymentLogByListConditionListBySupplierId")
	@WebResult(name="PrepaymentLogByListConditionList")
	public List<Map<String,Object>> getPrepaymentLogByListCondition(
			@WebParam(name = "set") Set<Condition> set, 
			@WebParam(name = "supplierId") String supplierId);
	
	@WebMethod
    public Double getMonthlyCredit(
            @WebParam(name = "condition") Map<String, Object> condition);
	
}

