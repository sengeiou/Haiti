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

import com.aimir.model.system.ModemConfig;

@WSDLDocumentation("Modem Configuration Information Management Service")
@WebService(name="ModemConfigService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ModemConfigManager {
	
	@WebMethod
	@WebResult(name="ModemConfig")
	public ModemConfig getModemConfig(
			@WebParam(name ="modemConfig")Integer modemConfigId);
	
	@WebMethod
	@WebResult(name="ModemConfig")
	public ModemConfig addModemConfig(
			@WebParam(name ="modemConfig")ModemConfig modemConfig);
	
	@WebMethod
	@WebResult(name="ModemConfig")
	public ModemConfig updateModemConfig(
			@WebParam(name ="modemConfig")ModemConfig modemConfig);
	
	@WebMethod
	public void deleteModemConfig(
			@WebParam(name ="modemConfig")ModemConfig modemConfig);

}
