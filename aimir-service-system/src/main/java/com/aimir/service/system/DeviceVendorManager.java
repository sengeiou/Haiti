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

import com.aimir.model.system.DeviceVendor;

@WSDLDocumentation("The manufacturer of the equipment in the system management information is managed.")
@WebService(name="DeviceVendorService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface DeviceVendorManager {
	
	/**
	 * method name : getDeviceVendor
	 * method Desc : DeviceVendor id와 일치하는 DeviceVendor 정보를 리턴한다.
	 * 
	 * @param vendorId DeviceVendor.id
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get the device vendor information by DeviceVendor.id", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="deviceVendorInstance", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
	@WebMethod
	@WebResult(name="deviceVendorInstance")
	public DeviceVendor getDeviceVendor(
			@WebParam(name="deviceVendorId") int vendorId);

	/**
	 * 
	 * method name : getDeviceVendorsBySupplierId
	 * method Desc : 공급사 아이디 정보로 해당하는 장비 벤더 정보 목록을 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get a a list of manufacturers that managed by the supplier. parameter (Supplier.id)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="deviceVendor List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="deviceVendorList")
	public List<DeviceVendor> getDeviceVendorsBySupplierId(
			@WebParam(name="supplierId") int supplierId);
	
	/**
	 * method name : getDeviceVendorsForTree
	 * method Desc : 공급사 아이디 정보로 해당하는 장비 벤더 및 하위 트리 정보(DeviceModel) 정보까지 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return DeviceType.name
	 *         DeviceType.id
	 *         DeviceVendor.name
	 *         DeviceVendor.id
	 *         DeviceModel.name
	 *         DeviceModel.id
	 *         Supplier.id
	 */
    @WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Brings the list of vendors in a tree structure.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters(supplierId : Supplier.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return List of Object{",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "name : device type name (DeviceType.name)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "id : device type id (DeviceType.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "name : Device Vendor Name",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "id : DeviceVendor ID (DeviceVendor.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "name : DeviceModel Name (DeviceModel.name)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "id : DeviceModel ID (DeviceModel.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT),
		        @WSDLDocumentation(value = "}",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)
	@WebMethod
	@WebResult(name="deviceVendorTree")
	public List<Object[]> getDeviceVendorsForTree(
			@WebParam(name="supplierId") int supplierId);
	
	/**
	 * method name : addDeviceVendor
	 * method Desc : DeviceVendor 객체 정보를 Entity에 추가한다. Database상의 insert를 수행한다.
	 * 
	 * @param deviceVendor
	 * @return
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Adding a device vendor information", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="DeviceVendor object", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="addDeviceVendor")
	public DeviceVendor addDeviceVendor(
			@WebParam(name="deviceVendor") DeviceVendor deviceVendor);
	
	/**
	 * method name : updateDeviceVendor
	 * method Desc : DeviceVendor 객체  Entity를 갱신한다. Database상의 update를 수행한다.
	 * 
	 * @param deviceVendor
	 * @return
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Updating of device vendor information", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="DeviceVendor object", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="updateDeviceVendor")
    public DeviceVendor updateDeviceVendor(
			@WebParam(name="deviceVendor") DeviceVendor deviceVendor);
	
	/**
	 * method name : deleteDeviceVendor
	 * method Desc : DeviceVendor 객체  Entity를 삭제한다. Database상의 delete를 수행한다.
	 * 
	 * @param deviceVendorId DeviceVendor.id
	 * @return
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Deleting of device vendor information , parameter(DeviceVendor.id)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="DeviceVendor object", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod(operationName ="deleteDeviceVendorByVendorId")
	@WebResult(name="deleteDeviceVendor")
	public int deleteDeviceVendor(
			@WebParam(name="deviceVendorId") int deviceVendorId);

	/**
	 * method name : deleteDeviceVendor
	 * method Desc : DeviceVendor 객체  Entity를 삭제한다. Database상의 delete를 수행한다.
	 * 
	 * @param deviceVendor
	 * @return
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Deleting of device vendor information , parameter(DeviceVendor instance)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod(operationName ="deleteDeviceVendor")
	public void deleteDeviceVendor(
			@WebParam(name="deviceVendor") DeviceVendor deviceVendor);

	/**
	 * 
	 * method name : getDeviceVendorByName
	 * method Desc : 공급사 아이디 정보와, 제조사 이름으로 해당하는 장비 벤더 리스트 정보를 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param name DeviceVendor.name
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
    @WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Brings the list of vendors.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters(supplierId : Supplier.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Parameters(deviceVendorName : DeviceVendor.name (%like%)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT)
		    }
	)
	@WebMethod
	@WebResult(name="deviceVendorList")
	public List<DeviceVendor> getDeviceVendorByName(
			@WebParam(name="supplierId") Integer supplierId, 
			@WebParam(name="deviceVendorName") String name);	
	/**
	 * 
	 * method name : getDeviceVendorByCode
	 * method Desc : 공급사 아이디 정보와, 제조사 고유코드로 해당하는 장비 벤더 리스트 정보를 추출한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param code DeviceVendor.code
	 * @return @see com.aimir.model.system.DeviceVendor
	 */
    @WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "Brings the list of vendors.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters(supplierId : Supplier.id)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Parameters(deviceVendorCode : DeviceVendor.code",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT)
		    }
	)
	@WebMethod
	@WebResult(name="deviceVendorList")
	public List<DeviceVendor> getDeviceVendorByCode(
			@WebParam(name="supplierId") Integer supplierId, 
			@WebParam(name="deviceVendorCode") Integer code);}
