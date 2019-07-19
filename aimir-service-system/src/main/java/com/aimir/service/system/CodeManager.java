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

import com.aimir.model.system.Code;

@WSDLDocumentation("System code management service")
@WebService(name="CodeService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface CodeManager {
	
	/**
	 * method name : getParents
	 * method Desc : parent에 해당하는 코드 목록을 리턴
	 * 
	 * @return List of Code  @see com.aimir.model.system.Code
	 */
	@WebMethod
	@WebResult(name="ParentsList")
	public List<Code> getParents();	
	
	/**
	 * method name : getCode
	 * method Desc : Code의 아이디 로 코드 객체를 찾아서 리턴
	 * 
	 * @param codeId Code.id
	 * @return
	 */
	@WebMethod
	@WebResult(name="CodeInstance")
    public Code getCode(@WebParam(name="codeId") Integer codeId);
    
	/**
	 * method name : getChildCodes
	 * method Desc : parent의 code값으로 Code List를 리턴
	 * 
	 * @param parentCode Code.parent.code
	 * @return List of Code  @see com.aimir.model.system.Code
	 */
	@WebMethod
	@WebResult(name="ChildCodesList")
    public List<Code> getChildCodes(@WebParam(name="parentCode") String parentCode);

    /**
     * method name : getChildCodesSelective
     * method Desc : parent 의 code 값으로 Code List 를 리턴. parameter 로 넘어온 코드(들)를 제외하고 조회함.
     * 
     * @param parentCode Code.parent.code
     * @param excludeCodes String ','값으로 구분된 조회시 제외할 code 들
     * @return List of Code  @see com.aimir.model.system.Code
     */
    @WebMethod
    @WebResult(name="ChildCodesSelectiveList")
    public List<Code> getChildCodesSelective(@WebParam(name="parentCode") String parentCode,
            @WebParam(name="excludeCodes") String excludeCodes);

    /**
     * method name : getChildCodesOrder
     * method Desc : parent의 code값으로 codeorder 로 정렬된 Code List를 리턴
     * 
     * @param parentCode Code.parent.code
     * @return List of Code  @see com.aimir.model.system.Code
     */
    @WebMethod
    @WebResult(name="ChildCodesOrderList")
	public List<Code> getChildCodesOrder(@WebParam(name="parentCode") String parentCode);

	/**
	 * method name : getCodesByName
	 * method Desc : 코드명으로  코드를 찾아옴
	 * 
	 * @param name Code.name
	 * @return List of Code  @see com.aimir.model.system.Code
	 */
	@WebMethod
	@WebResult(name="CodesList")
    public List<Code> getCodesByName(@WebParam(name="name") String name);
    
    /**
	 * method name : getCodeByName
	 * method Desc : 코드의 이름으로 코드 객체를 찾아서 리턴
	 * 
     * @param name
     * @return  @see com.aimir.model.system.Code
     */
	@WebMethod
	@WebResult(name="CodeByName")
    public Code getCodeByName(@WebParam(name="name") String name);
    
    
    /**
	 * method name : getEnergyList
	 * method Desc : 에너지 사용타입에 대한 리스트 코드를 반환
	 * 
     * @param customerId Contract.customer.id
     * @return List of Code  @see com.aimir.model.system.Code
     */
	@WebMethod
	@WebResult(name="EnergyList")
    public List<Code> getEnergyList(@WebParam(name="customerId") int customerId);
    
	/**
	 * method name : getCodeIdByCode
	 * method Desc : Code객체의 아이디를 리턴
	 * 
	 * @param code Code.code
	 * @return Code.id
	 */
	@WebMethod
	@WebResult(name="CodeId")
	public int getCodeIdByCode(@WebParam(name="code") String code);
	
	/**
	 * method name : getCodeByCode
	 * method Desc : code값으로 Code객체 리턴
	 * 
	 * @param code Code.code
	 * @return @see com.aimir.model.system.Code
	 */
	@WebMethod
	@WebResult(name="CodeByCode")
	public Code getCodeByCode(@WebParam(name="code") String code);

	   /**
     * method name : getSicCodeList<b/>
     * method Desc :
     *
     * @param parentCode
     * @return
     */
    public List<Map<String, Object>> getSicCodeList(String parentCode);
    
    
    public List<Map<String, Object>> getCodeListwithChildren();
    
    public void deleteCodeTreeNode(String codeId);
    
    public String saveCode(Map<String,Object> codeMap);
    
    public String updateCode(Map<String,Object> codeMap);
    
    public Code getCodeByCondition(Map<String, Object> condition);
    @WebMethod
	@WebResult(name="CodeByCodesOrderBy")
    public List<Code> getChildCodesOrderBy(String parentCode, String orderBy);
}