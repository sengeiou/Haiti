/**
 * RoleManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.Gadget;
import com.aimir.model.system.Role;

/**
 * RoleManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 12.   v1.0       김상연         Role Id 검색 (이름)
 *
 */
@WSDLDocumentation("Login User Role Management")
@WebService(name="RoleService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface RoleManager {
	
	@WebMethod
	@WebResult(name="roleList")
    public List<Role> getRoles();
	
	@WebMethod
	@WebResult(name="Role")
    public Role getRole(@WebParam(name ="roleId")Integer roleId);
	
	@WebMethod
	@WebResult(name="Role")
    public Role addRole(@WebParam(name ="role")Role role);
	
	@WebMethod
	@WebResult(name="Role") 
    public Role updateRole(@WebParam(name ="role")Role role);
	
	@WebMethod
    public void deleteRole(@WebParam(name ="role")Role role);
	
	@WebMethod
	@WebResult(name="RoleBySupplierIdList")
    public List<Role> getRoleBySupplierId(
    		@WebParam(name ="supplierId")Integer supplierId);
    
	@WebMethod
	@WebResult(name="gadgetList")
	public List<Gadget> getGadgetList();	
	
	@WebMethod
	public void updateGadget(
			@WebParam(name ="roleId")Integer roleId, 
			@WebParam(name ="gadgetId")Integer gadgetId);
	
	@WebMethod
	public void delGadget(
			@WebParam(name ="gadgetId")Integer gadgetId);
	
	@WebMethod
	@WebResult(name="gadgetSearchList")
	public List<Gadget> gadgetSearch(
			@WebParam(name ="roleId")Integer roleId,
			@WebParam(name ="gadgetName")String gadgetName);
	
	@WebMethod
	@WebResult(name="gadgetSearchByTagList")
	public List<Gadget> gadgetSearchByTag(
			@WebParam(name ="roleId")Integer roleId,
			@WebParam(name ="tag")String tag);
	
	@WebMethod
	@WebResult(name="PermitedGadgetsList")
	public List<Gadget> getPermitedGadgets(
			@WebParam(name ="roleId")Integer roleId);
	
	@WebMethod
	@WebResult(name="gadgetAllSearchList")
	public List<Gadget> gadgetAllSearch(
			@WebParam(name ="roleId")Integer roleId, 
			@WebParam(name ="name")String name);
	
	@WebMethod
	@WebResult(name="gadgetAllSearchByTagList")
	public List<Gadget> gadgetAllSearchByTag(
			@WebParam(name ="roleId")Integer roleId, 
			@WebParam(name ="tag")String tag);
	
	@WebMethod
	@WebResult(name="gadgetList")
	public List<Gadget> search(
			@WebParam(name ="name")String name);
	
	@WebMethod
	@WebResult(name="nameOverlapCheck")
	public int nameOverlapCheck(
			@WebParam(name ="name")String name);
	
	/**
	 * method name : getRoleByName
	 * method Desc : Role Id 검색 (이름)
	 *
	 * @param roleId
	 * @return
	 */
	@WebMethod
	@WebResult(name="Role")
	public Role getRoleByName(
			@WebParam(name ="name")String name);

    /**
     * method name : getGadgetListByRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="GadgetListByRoleSet")
    public Set<Gadget> getGadgetListByRole(		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);
	
	
	
	public List getGadgetListByRole2( Map<String, Object> conditionMap);

    /**
     * method name : getRemainGadgetList<b/>
     * method Desc : UserManagement 맥스가젯에서 전체가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="RemainGadgetListSet")
    public Set<Gadget> getRemainGadgetList(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);

    /**
     * method name : addGadgetRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트에 선택한 가젯들을 등록한다.
     *
     * @param conditionMap
     */
	@WebMethod
    public void addGadgetRole(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);
    
    /**
     * method name : delGadgetRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트에서 선택한 가젯들을 삭제한다.
     *
     * @param conditionMap
     */
	@WebMethod
    public void delGadgetRole(
    		@WebParam(name ="conditionMap")Map<String, Object> conditionMap);
	
    /**
     * method name : getRoleBySupplierIdForCustomer<b/>
     * method Desc : 공급사 아이디에 해당하는 Customer Role 리스트를 리턴한다.
     *
     * @param sppilerId
     */
    public List<Role> getRoleBySupplierIdForCustomer(@WebParam(name ="supplierId")Integer supplierId);
}