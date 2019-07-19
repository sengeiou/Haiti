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

import com.aimir.model.system.DeviceModel;
@WebService(name="DeviceModelService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DeviceModelManager {

	@WebMethod
	@WebResult(name="DeviceModelInstance")
	public DeviceModel getDeviceModel(
			@WebParam(name="modelId") int modelId);
	
	@WebMethod(operationName ="DeviceModelsList")
	@WebResult(name="DeviceModelsList")
	public List<DeviceModel> getDeviceModels(
			@WebParam(name="vendorId") Integer vendorId);
	
	@WebMethod(operationName ="DeviceModelsListByName")
	@WebResult(name="DeviceModelsList")
	public List<DeviceModel> getDeviceModels(
			@WebParam(name="vendorName") String vendorName);

	@WebMethod(operationName ="DeviceModelsListByDashboardId")
	@WebResult(name="DeviceModelsList")
	public List<DeviceModel> getDeviceModels(
			@WebParam(name="vendorId") int vendorId, 
			@WebParam(name="dashboardId") int deviceTypeId);
	
	@WebMethod
	@WebResult(name="DeviceModelsListByMap")
	public List<DeviceModel> getDeviceModels(
			@WebParam(name="condition") Map<String, Object> condition);
	
	@WebMethod
	@WebResult(name="addDeviceModel")
	public DeviceModel addDeviceModel(
			@WebParam(name="deviceModel") DeviceModel deviceModel);
	
	@WebMethod
	@WebResult(name="updateDeviceModel")
	public DeviceModel updateDeviceModel(
			@WebParam(name="deviceModel") DeviceModel deviceModel);

	@WebMethod
	public void deleteDeviceModel(
			@WebParam(name="deviceModel") DeviceModel deviceModel);
	
	@WebMethod
	@WebResult(name="DeviceModelByNameList")
	public List<DeviceModel> getDeviceModelByName(
			@WebParam(name="supplierId") Integer supplierId, 
			@WebParam(name="name") String name);
	
	@WebMethod
	@WebResult(name="DeviceModelByCode")
	public DeviceModel getDeviceModelByCode(
			@WebParam(name="supplierId") Integer supplierId, 
			@WebParam(name="code") Integer code, 
			@WebParam(name="deviceTypeCodeId") Integer deviceTypeCodeId);
	
	@WebMethod
	@WebResult(name="DeviceModelByTypeId")
	public List<DeviceModel> getDeviceModelByTypeId(
			@WebParam(name="supplierId") Integer supplierId, 
			@WebParam(name="typeId") Integer typeId);
	
	@WebMethod
	@WebResult(name="DeviceModelBySupplierId")
	public List<DeviceModel> getDeviceModelBySupplierId(
			@WebParam(name="supplierId") Integer supplierId);
	
	@WebMethod
	@WebResult(name="DeviceModelByTypeIdUnknownList")
	public List<DeviceModel> getDeviceModelByTypeIdUnknown(
			@WebParam(name="supplierId") Integer supplierId);

	@WebMethod
	@WebResult(name="MCUDeviceModelList")
	public List<Map<String, String>> getMCUDeviceModel();
	
	

}
