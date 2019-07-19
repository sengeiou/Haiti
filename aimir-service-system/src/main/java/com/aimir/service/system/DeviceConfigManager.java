package com.aimir.service.system;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.DeviceConfig;
@WebService(name="DeviceConfigService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DeviceConfigManager {

	@WebMethod
	@WebResult(name="DeviceConfigInstance")
	public DeviceConfig getDeviceConfig(
			@WebParam(name="deviceConfigId") Integer deviceConfigId);
	
	@WebMethod
	@WebResult(name="DeviceConfigByModelId")
	public DeviceConfig getDeviceConfigByModelId(
			@WebParam(name="deviceModelId") Integer deviceModelId);
	
	@WebMethod
	@WebResult(name="addDeviceConfig")
	public DeviceConfig addDeviceConfig(
			@WebParam(name="deviceConfig") DeviceConfig deviceConfig);
	
	@WebMethod
	@WebResult(name="updateDeviceConfig")
	public DeviceConfig updateDeviceConfig(
			@WebParam(name="deviceConfig") DeviceConfig deviceConfig);
	
	@WebMethod
	public void deleteDeviceConfig(
			@WebParam(name="deviceConfig") DeviceConfig deviceConfig);
}
