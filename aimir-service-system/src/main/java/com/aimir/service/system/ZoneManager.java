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
import org.apache.cxf.annotations.WSDLDocumentationCollection;

import com.aimir.model.system.Location;
import com.aimir.model.system.Zone;

@WSDLDocumentation("Building Zone Management")
@WebService(name="ZoneService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ZoneManager {
	
    /**
     * BEMS - 빌딩(location) 의 Zone 목록을 조회한다.
     * @param location
     * @return
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="This refers to zones in the building list.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ZonesByLocation List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ZonesByLocationList")
    public List<Zone> getZonesByLocation(
    		@WebParam(name="location") Location location);
    
    /**
     * BEMS - 빌딩의 Zone에 해당하는 EndDevice를 상태별로 카운트한다.
     * @param params
     * @return
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Brings the number and status of each Zone enddevice.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="EndDeviceTypeAndStatusCountList", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="EndDeviceTypeAndStatusCountList")
    public List<Object> getEndDeviceTypeAndStatusCountByZones(Map<String,Object> params);
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Add to the building zone.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void add(
    		@WebParam(name="zone") Zone zone);
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="ID of the zone as a zone to look up information.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ZoneInstance", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ZoneInstance")
    public Zone getZone(
			@WebParam(name="id") Integer id);
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="zone information is updated.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void update(
    		@WebParam(name="zone") Zone zone);
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="zone information is updated.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void delete(
    		@WebParam(name="zoneId") Integer zoneId);

    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get a list of zones that match the name.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ZoneByNameList", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ZoneByNameList")
    public List<Zone> getZoneByName(
    		@WebParam(name="name") String name);

    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="hierarchy and order of the zone is updated. parsermeter(orderNo - new order number, oriOrderNo - old order number)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
	public void updateOrderNo(
			@WebParam(name="parentId") Integer parentId,
			@WebParam(name="orderNo") Integer orderNo,
			@WebParam(name="oriOrderNo") Integer oriOrderNo);
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get a list of zones that type is parent.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ParentZoneList", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ParentZoneList")
	public List<Zone> getParentZone();
    
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Zone should be able to add child nodes.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="addNewChildZone", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )	
    @WebMethod
	@WebResult(name="addNewChildZone")
	public Zone addNewChildZone(
			@WebParam(name="parentId") Integer parentId);

    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Change the zone name.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="updateZoneName", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="updateZoneName")
    public Boolean updateZoneName(
    		@WebParam(name="zoneId") Integer zoneId, 
    		@WebParam(name="newName") String newName);}
