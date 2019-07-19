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

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

import com.aimir.constants.CommonConstants.MeteringDataType;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Location;

@WSDLDocumentation("Mobile App Service Web interface")
@WebService(name="AppService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface AppServiceManager {
	
   
	/**
     * method name : getLocations
     * method Desc : 전체 지역 목록을 리턴한다.
     * 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of the all area.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="locationList")
    public List<Location> getLocationList();
    
    
	/**
     * method name : getModemList
     * method Desc : 전체 지역 목록을 리턴한다.
     * 
	 * @return List of Modem @see com.aimir.model.device.Modem
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of the modem.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="supplierId(Integer), locationId(Integer)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
            @WSDLDocumentation(value="modem List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="modemList")
    public List<Modem> getModemList(@WebParam Integer supplierId, @WebParam Integer locationId);
    
    
	/**
     * method name : getMeterList
     * method Desc : 전체 지역 목록을 리턴한다.
     * 
	 * @return List of Meter @see com.aimir.model.device.Meter
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of the meter.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="supplierId(Integer), locationId(Integer)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
            @WSDLDocumentation(value="meter List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="meterList")
    public List<Meter> getMeterList(@WebParam Integer supplierId, @WebParam Integer locationId);
    
	/**
     * method name : getMeterListByModem
     * method Desc : 전체 지역 목록을 리턴한다.
     * 
	 * @return List of Meter @see com.aimir.model.device.Meter
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of the meter.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="modem Serial", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
            @WSDLDocumentation(value="meter List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="meterListByModem")
    public List<Meter> getMeterListByModem(@WebParam String modemSerial);
    
	/**
     * method name : saveMeterData
     * method Desc : 수검침 데이터를 저장한다.
     * 
	 * @return List of Returns results of save meter data
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns results of save meter data.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="meter Serial(String), meteringDate(yyyyMMdd, yyyyMMddHHmm, yyyyMM), meteringDataType, meteringValues", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
            @WSDLDocumentation(value="saveMeterDataResults", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="saveMeterDataResults")
    public Boolean[] saveMeterData(@WebParam String meterSerial, 
    								@WebParam String meteringDate, 
    								@WebParam MeteringDataType mDataType, 
    								@WebParam Double[] meteringValues) throws Exception;

}