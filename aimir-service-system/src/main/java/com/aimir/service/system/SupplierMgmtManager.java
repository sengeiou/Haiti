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

import com.aimir.model.system.TariffType;

@WSDLDocumentation("Supplier(Energy supply services company), Utility Company Information Management Service")
@WebService(name="SupplierMgmtService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface SupplierMgmtManager {
	
	@WebMethod
	@WebResult(name="YyyymmddList")
	public List<Object> getYyyymmddList(
			@WebParam(name="supplierType") String supplierType,
			@WebParam(name="supplierId") Integer supplierId);
	
	@WebMethod
	@WebResult(name="ChargeMgmtMap")
    public Map<String, Object> getChargeMgmtList(
    		@WebParam(name="condition") Map<String, Object> condition);
	
	@WebMethod
	@WebResult(name="insertEMData")
	public int insertEMData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
	
	@WebMethod
	@WebResult(name="insertGMData")
	public int insertGMData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
	
	@WebMethod
	@WebResult(name="insertWMData")
	public int insertWMData(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
	
	public String getAppliedTariffDate(String supplierType, String yyyymmdd, Integer supplierId);
	
	public String updateTariffEMTable(String date, String jsonString);
	
	/**
     * tariff_wm 테이블 업데이트
     * @param date : 적용일자
     * @param jsonString : 값
     */
    public String updateTariffWMTable(String date, String jsonString);
    
	public TariffType addTariffType(Map<String, Object> condition);
}
