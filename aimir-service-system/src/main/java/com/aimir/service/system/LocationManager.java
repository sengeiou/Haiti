package com.aimir.service.system;

import java.io.UnsupportedEncodingException;
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

import com.aimir.model.system.Location;

@WSDLDocumentation("Location Information Service(Building, Zone, Floor or Some Area)")
@WebService(name="LocationService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface LocationManager {
	

	/**
     * method name : getParents
     * method Desc : parent인  목록 전체를 찾아서 리턴한다.
	 * 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Location of parent is to find a full list is returned.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="Parents List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ParentsList")
    public List<Location> getParents();
    
	/**
     * method name : getChildren
     * method Desc : parent 에 해당하는 children 목록 전체를 찾아서 리턴한다.
     * 
	 * @param parentId parent id Location.id 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="children find the whole list is returned.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="locationList")
    public List<Location> getChildren(
    		@WebParam(name="supplierId") Integer parentId);
    
    /**
     * method name : getChildren
     * method Desc : parent 에 해당하는 children 목록 전체를 찾아서 리턴한다.
     * 
     * @param locationId Location.id
     * @return @see com.aimir.model.system.Location
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Location information is viewed by Location id.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location Instance", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="locationInstance")
    public Location getLocation(
    		@WebParam(name = "locationId") Integer locationId);
    
    /**
     * method name : add
     * method Desc : Location 객체를 entity에 추가한다. Database 상의 insert를 수행한다.
     * 
     * @param location
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Add the location object.(new instance)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void add(
    		@WebParam(name="location")Location location);
    
    /**
     * method name : update
     * method Desc : Location 객체 entity를 갱신한다 . Database 상의 update를 수행한다.
     * 
     * @param location
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Location information is updated.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void update(
    		@WebParam(name="location")Location location);
    
    /**
     * method name : delete
     * method Desc : Location 객체 entity를 삭제한다 . Database 상의 delete를 수행한다. 
     * 
     * @param locationId Location.id
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Add the location object.(new instance)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void delete(
    		@WebParam(name="locationId")Integer locationId);
    
	/**
     * method name : getParentsBySupplierId
     * method Desc : 공급사 아이디에 충족하는 parent인  목록 전체를 찾아서 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Sub-regions do not have to return only a list of the top area.(by supplier ID)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ParentsBySupplierId List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ParentsBySupplierIdList")
    public List<Location> getParentsBySupplierId(
    		@WebParam(name="supplierId")Integer supplierId);
    
	/**
     * method name : getParentsBySupplierId
     * method Desc : 공급사 아이디에 충족하는,  keyWord가 location name과 일치하는  parent인  목록 전체의 카운트를 구한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param keyWord Location.name
	 * @return
     * @throws UnsupportedEncodingException
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of sub-regions by supplier ID", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="Parents BykeyWord", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="ParentsBykeyWord")
    public int getParentsBykeyWord(
    		@WebParam(name="supplierId")Integer supplierId, 
    		@WebParam(name="keyWord") String keyWord) throws UnsupportedEncodingException;
    
	/**
     * method name : getChildrenBySupplierId
     * method Desc :  공급사 아이디에 충족하는 children 목록 전체를 찾아서 리턴한다.
	 * 
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of sub-regions by supplier ID", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="locationList")
    public List<Location> getChildrenBySupplierId(
    		@WebParam(name="supplierId")  Integer supplierId);
    
    /**
     * method name : getLocationsBySupplierId
     * method Desc : 공급사 아이디에 속하는 Location 목록을 리턴한다.
     * 
     * @param supplierId Supplier.id
     * @return List of Location @see com.aimir.model.system.Location
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Returns a list of locations by supplier ID", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
	@WebResult(name="locationList")
    public List<Location> getLocationsBySupplierId(
    		@WebParam(name="supplierId")  Integer supplierId);
    
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
    public List<Location> getLocations();
    
    /**
     * method name : getLocationByName
     * method Desc : Location 명에 like 조건에 부합하는 Location목록을 리턴한다.
     * 
     * @param name Location.name (%like%)
     * @return List of Location @see com.aimir.model.system.Location
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Location that matches the name on the list is returned.(condition %like%)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="location List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="locationList")
    public List<Location> getLocationByName(
    		@WebParam(name="locationName") String name);
    
	/**
     * method name : updateOrderNo
     * method Desc : BEMS 에서 사용 OrderNo 업데이트  
	 * 
	 * @param Integer supplierId, Integer parentId,
			Integer orderNo,Integer oriOrderNo
	 * @return void
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="hierarchy and order of the zone is updated. parsermeter(orderNo - new order number, oriOrderNo - old order number)", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION)
           })
    @WebMethod
    public void updateOrderNo(
    		@WebParam(name="supplierId")Integer supplierId,
    		@WebParam(name="parentId")Integer parentId,
    		@WebParam(name="orderNo")Integer orderNo,
    		@WebParam(name="oriOrderNo")Integer oriOrderNo);

    /**
     * method name : getUserLocation<b/>
     * method Desc :
     *
     * @param locationId
     * @return
     */
    public List<Location> getUserLocation(Integer locationId);

    /**
     * method name : getUserLocationBySupplierId<b/>
     * method Desc :
     *
     * @param locationId
     * @param supplierId
     * @return
     */
    public List<Location> getUserLocationBySupplierId(Integer locationId, Integer supplierId);

    /**
     * method name : getAllLocationsForExcel<b/>
     * method Desc :
     *
     * @param supplierId
     * @return
     */
    public List<Object> getAllLocationsForExcel(Integer supplierId);
    
    /**
     * method name : getRootLocationListBySupplier<b/>
     * method Desc : SP-572
     * @param supplierId
     * @return
     */
    public List<Location> getRootLocationListBySupplier(Integer supplierId);
    
    public List<String> getLocationsName();
}