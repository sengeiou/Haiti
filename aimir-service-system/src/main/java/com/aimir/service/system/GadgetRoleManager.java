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

import com.aimir.model.system.GadgetRole;
@WebService(name="GadgetRoleService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface GadgetRoleManager {
	
	
	/**
	 * method name : getGadgetRoles
	 * method Desc : GadgetRole 전체 목록을 리턴합니다.
	 * 
	 * @return List of GadgetRole  @see com.aimir.model.system.GadgetRole
	 */
	@WebMethod
	@WebResult(name="gadgetRolesList")
	public List<GadgetRole> getGadgetRoles();
	
    /**
     * method name : getGadgetRolesList<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer roleId = (Integer)conditionMap.get("roleId");
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String gadgetName = StringUtil.nullToBlank(conditionMap.get("gadgetName"));
     *         String tagName = StringUtil.nullToBlank(conditionMap.get("tagName"));
     * @return
     */
	@WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "UserManagement information is viewed in the allowed list of gadgets.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters(roldId, supplierId, gadgetName,tagName)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Binding documentation",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="gadgetRoleList")
	public List<Map<String, Object>> getGadgetRolesList(
			@WebParam(name ="params")Map<String, Object> params);
	
	/**
	 * method name : add
	 * method Desc : GadgetRole 정보를 entity에 추가한다.
	 * 
	 * @param gadgetRole
	 */
	@WebMethod
    public void add(
    		@WebParam(name ="gadgetRole")GadgetRole gadgetRole);

    /**
     * method name : addGadgetRoles<b/>
     * method Desc : User Management 에서 그룹 등록 시 허용된 가젯을 저장한다.
     *
     * @param gadgetRoles
     */
	@WebMethod
    public void addGadgetRoles(
    		@WebParam(name ="gadgetRoles")List<GadgetRole> gadgetRoles);
}