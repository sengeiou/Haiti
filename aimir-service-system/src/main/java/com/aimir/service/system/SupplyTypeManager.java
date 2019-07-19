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

import com.aimir.model.system.SupplyType;
@WebService(name="SupplyTypeService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface SupplyTypeManager {
	
	@WebMethod
	@WebResult(name="SupplyTypeInstance")
    public SupplyType getSupplyType(
    		@WebParam(name="supplyTypeId") Integer supplyTypeId);
	
	@WebMethod
    public void add(
    		@WebParam(name="SupplyType") SupplyType SupplyType);
	
	@WebMethod
    public void update(
    		@WebParam(name="SupplyType") SupplyType SupplyType);
	
	@WebMethod
    public void delete(
    		@WebParam(name="supplyTypeId") Integer supplyTypeId);
	
	@WebMethod
	@WebResult(name="SupplyTypeList")
    public List<SupplyType> getSupplyTypeBySupplierId(
    		@WebParam(name="supplierId") Integer supplierId);
	
	@WebMethod
	@WebResult(name="checkSupplyType")
    public boolean checkSupplyType(
    		@WebParam(name="supplierId") Integer supplierId, 
    		@WebParam(name="typeId") Integer typeId);
	
	@WebMethod(operationName ="SupplyTypeListBySupplier")
	@WebResult(name="SupplyTypeList")
	public List<SupplyType> getSupplyTypeList(
			@WebParam(name="supplier") int supplier);
	
	
	@WebMethod(operationName ="SupplyTypeList")
	@WebResult(name="SupplyTypeList")
	//BEMS 에서 사용
	public List<SupplyType> getSupplyTypeList();
}
