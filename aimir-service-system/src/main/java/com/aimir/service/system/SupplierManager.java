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
import com.aimir.model.system.Supplier;

@WSDLDocumentation("Supplier(Energy supply services company), Utility Company Information Management Service")
@WebService(name="SupplierService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface SupplierManager {
	
	@WebMethod
	@WebResult(name="SuppliersList")
    public List<Supplier> getSuppliers();
	
	@WebMethod
	@WebResult(name="SupplierInstance")
    public Supplier getSupplier(
    		@WebParam(name="id") Integer id);
	
	@WebMethod
	@WebResult(name="SupplierByName")
    public Supplier getSupplierByName(
    		@WebParam(name="name") String name);
	
	@WebMethod
    public void add(
    		@WebParam(name="supplier") Supplier supplier);
	@WebMethod
    public void update(
    		@WebParam(name="supplier") Supplier supplier);
	@WebMethod
    public void delete(
    		@WebParam(name="supplierId") Integer supplierId);
	
	@WebMethod
	@WebResult(name="Count")
    public Integer getCount();
	
	@WebMethod
	@WebResult(name="CountryID")
    public Integer getCountryID(
    		@WebParam(name="supplierID") Integer supplierID);
	
	@WebMethod
	@WebResult(name="LanguageID")
    public Integer getLanguageID(
    		@WebParam(name="supplierID") Integer supplierID);
	
	@WebMethod
	@WebResult(name="TimeZoneID")
    public Integer getTimeZoneID(
    		@WebParam(name="supplierID") Integer supplierID);
}
