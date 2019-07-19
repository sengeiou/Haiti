package com.aimir.service.system;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.MeterConfig;
@WSDLDocumentation("Meter Configuration Information Service")
@WebService(name="MeterConfigService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface MeterConfigManager {

	@WebMethod
	@WebResult(name="MeterConfig")
	public MeterConfig getMeterConfig(
			@WebParam(name ="meterConfigId")Integer meterConfigId);
	
	@WebMethod
	@WebResult(name="MeterConfig")
	public MeterConfig addMeterConfig(
			@WebParam(name ="meterConfigId")MeterConfig meterConfig);
	
	@WebMethod
	@WebResult(name="MeterConfig")
	public MeterConfig updateMeterConfig(
			@WebParam(name ="meterConfigId")MeterConfig meterConfig);
	
	@WebMethod
	public void deleteMeterConfig(
			@WebParam(name ="meterConfigId")MeterConfig meterConfig);
	
	@WebMethod
	@WebResult(name="MeterConfig")
	public MeterConfig getDeviceConfigs(
			@WebParam(name ="configId")Integer configId);
	
	@WebMethod
	public Integer getLastId();
}
