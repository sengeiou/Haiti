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

import com.aimir.model.system.ContractCapacity;

@WSDLDocumentation("Energy Contract Information Service")
@WebService(name="ContractCapacityService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ContractCapacityManager {
	
	@WebMethod
	public void add(
			@WebParam(name="contractCapacity") ContractCapacity contractCapacity);
	
	@WebMethod
	public void delete(
			@WebParam(name="contractCapacityId") int contractCapacityId);
	
	@WebMethod
	public void update(
			@WebParam(name="contractCapacity") ContractCapacity contractCapacity);
	
	@WebMethod
	@WebResult(name="ContractCapacityInstance")
	public ContractCapacity getContractCapacity(
			@WebParam(name="contractCapacityId") int contractCapacityId);
	
	@WebMethod(operationName ="ContractCapacityList")
	@WebResult(name="ContractCapacityList")
	public List<ContractCapacity> getContractCapacityList();
	
	@WebMethod(operationName ="ContractCapacityListByPageCount")
	@WebResult(name="ContractCapacityList")
	public List<ContractCapacity> getContractCapacityList(
			@WebParam(name="page") int page, 
			@WebParam(name="count") int count);
	
	@WebMethod
	@WebResult(name="LocationSupplierContractMap")
	public Map<String,Object> getLocationSupplierContract(
			@WebParam(name="params") Map<String, Object> params);
	
	@WebMethod
	@WebResult(name="contractEnergyExistCheck")
	public boolean contractEnergyExistCheck(
			@WebParam(name="serviceTypeId") Integer serviceTypeId, 
			@WebParam(name="locationId") Integer locationId);
}

