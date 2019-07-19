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

import com.aimir.model.system.Gadget;
@WebService(name="GadgetService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface GadgetManager {
	
	/**
     * method name : searchGadgetList
     * method Desc : 가젯명과 롤로 가젯 목록을 리턴한다.
	 * 
	 * @param gadgetName Gadget.name
	 * @param roleId GadgetRole.id
	 * 
	 * @return List of Gadget @see com.aimir.model.system.Gadget
	 */
	@WebMethod
	@WebResult(name="searchGadgetList")
	public List<Gadget> searchGadgetList(
			@WebParam(name ="gadgaeName")String gadgetName,
			@WebParam(name ="roleId")Integer roleId);
	
	/**
     * method name : getGadgets
     * method Desc : 가젯 전체 목록을 리턴한다.
	 * 
	 * @return  List of Gadget @see com.aimir.model.system.Gadget
	 */
	@WebMethod
	@WebResult(name="gadgetsList")
    public List<Gadget> getGadgets();
	
	/**
	 * 전체 가젯 리스트 가져오기 order by gadget.name 
	 * @return
	 */
	public List<Gadget> getGadgets2(Map<String, Object> conditionMap);
    
    /**
     * method name : getGadget
     * method Desc : 가젯 아이디로 가젯 정보를 리턴한다.
     * 
     * @param gadgetId Gadget.id
     * @return @see com.aimir.model.system.Gadget
     */ 
	@WebMethod
	@WebResult(name="gadget")
    public Gadget getGadget(
    		@WebParam(name ="gadgetId")Integer gadgetId);
    
    /**
     * method name : add
     * method Desc : Gadget 정보를 신규 추가한다.
     * 
     * @param gadget
     */
	@WebMethod
    public void add(
    		@WebParam(name ="gadget")Gadget gadget);  
    
    /**
     * method name : update
     * method Desc : Gadget 정보를 업데이트 한다.
     *  
     * @param gadget
     */
	@WebMethod
    public void update(
    		@WebParam(name ="gadget")Gadget gadget);
    
    /**
     * method name : delete
     * method Desc : Gadget 정보를 삭제한다.
     * 
     * @param gadget
     */
	@WebMethod
    public void delete(
    		@WebParam(name ="gadget")Gadget gadget);
}