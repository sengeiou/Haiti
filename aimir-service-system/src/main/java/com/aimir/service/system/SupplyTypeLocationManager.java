package com.aimir.service.system;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.SupplyTypeLocation;
@WebService(name="SupplyTypeLocationService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface SupplyTypeLocationManager {
	
	@WebMethod
	@WebResult(name="get")
    public SupplyTypeLocation get(
    		@WebParam(name="supplyTypeLocationId") Integer supplyTypeLocationId);
	
	@WebMethod
    public void add(
    		@WebParam(name="supplyTypeLocation") SupplyTypeLocation supplyTypeLocation);
	
	@WebMethod
    public void update(
    		@WebParam(name="supplyTypeLocation") SupplyTypeLocation supplyTypeLocation);
	
	@WebMethod
    public void delete(
    		@WebParam(name="supplyTypeLocationId") Integer supplyTypeLocationId);
	
	@WebMethod
	@WebResult(name="checkSupplyType")
    public boolean checkSupplyType(
    		@WebParam(name="typeId") Integer typeId);
}
