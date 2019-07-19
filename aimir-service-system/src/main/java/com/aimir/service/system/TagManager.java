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

import com.aimir.model.system.Gadget;
import com.aimir.model.system.Tag;
@WebService(name="TagService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface TagManager {
	
	/**
     * method name : getTags
     * method Desc : 공급타입의 중복을 체크한다
     * 
	 * @param roleId GadgetRole.role.id
	 * @return List of Tag @see com.aimir.model.system.Tag
	 */
	@WebMethod
	@WebResult(name="TagsList")
	public List<Tag> getTags(
			@WebParam(name="roleId") int roleId);
	
	/**
     * method name : searchGadgetByTag
     * method Desc : 공급타입의 중복을 체크한다
     * 
	 * @param tag Tag.tag
	 * @param roleId GadgetRole.role.id
	 * @return List of Gadget @see com.aimir.model.system.Gadget
	 */
	@WebMethod
	@WebResult(name="searchGadgetList")
	public List<Gadget> searchGadgetByTag(
			@WebParam(name="tag") String tag, 
			@WebParam(name="roleId") int roleId);
}
