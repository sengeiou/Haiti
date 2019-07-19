/**
 * OperatorManager.java Copyright NuriTelecom Limited 2011
 */

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

import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.User;

/**
 * OperatorManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0       김상연         주민등록번호 가입 여부 확인
 * 2011. 4. 12.  v1.1       김상연         Operator 등록 시 Operator 반환
 * 2011. 4. 14.  v1.2       김상연         Operator 조회 (조건 : Operator)
 *
 */
@WSDLDocumentation("Login User management service")
@WebService(name="OperatorService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface OperatorManager {
	
	/**
	 * 
	 * @return
	 */
	@WebMethod(operationName ="operatorList")
	@WebResult(name="operatorList")
    public List<Operator> getOperators();
    
    /**
     * 
     * @param userId
     * @return
     */
	@WebMethod
	@WebResult(name="Operator")
    public Operator getOperator(
    		@WebParam(name ="userId")Integer userId);
    
    /**
     * 
     * @param operator
     */
	@WebMethod
    public void addOperator(
    		@WebParam(name ="operator")Operator operator);
    
    /**
     * 
     * @param operator
     */
	@WebMethod
    public void updateOperator(
    		@WebParam(name ="operator")Operator operator);
    
    /**
     * 
     * @param operator
     */
	@WebMethod
    public void deleteOperator(
    		@WebParam(name ="operator")Operator operator);
    
	/**
	 * method name : checkOperator
	 * method Desc : 사용자 로그인 아이디와 패스워드로 조회하여 해당 계정이 존재하는지 리턴
	 * 
	 * @param userId Operator.loginId
	 * @param pw Operator.password
	 * @return 
	 */
	@WebMethod
	@WebResult(name="checkOperator")
	public boolean checkOperator(
			@WebParam(name ="userId")String userId, 
			@WebParam(name ="pw")String pw);
	
	/**
	 * method name : getOperatorByLoginId
	 * method Desc : 사용자 로그인 아이디로 Operator 객체를 조회하여 리턴
	 * 
	 * @param loginId Operator.loginId
	 * @return  @see com.aimir.model.system.Operator
	 */
	@WebMethod
	@WebResult(name="OperatorByLoginId")
	public Operator getOperatorByLoginId(
			@WebParam(name ="loginId")String loginId);
	
	/**
	 * method name : getOperatorsByRole
	 * method Desc : roleId에 해당하는 Operator 객체 목록을 조회하여 리턴
	 * 
	 * @param roleId Role.id
	 * @return  List<Object>
	 */
	@WebMethod
	@WebResult(name="OperatorsByRoleList")
	@Deprecated
	public List<Object> getOperatorsByRole(
			@WebParam(name ="roleId")Integer roleId);

    /**
     * method name : getOperatorListByRole<b/>
     * method Desc : User Management 가젯에서 Operator List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getOperatorListByRole(Map<String, Object> conditionMap);

    /**
     * method name : getOperatorListByRoleTotalCount<b/>
     * method Desc : User Management 가젯에서 Operator List Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getOperatorListByRoleTotalCount(Map<String, Object> conditionMap);

	/**
	 * method name : getOperatorsHaveNoSupplier
	 * method Desc : Operator 목록 중 공급사 정보 (Supplier.id)가 널인 Operator 목록을 리턴
	 * 
	 * @return List of Operator @see com.aimir.model.system.Operator
	 */
	@WebMethod
	@WebResult(name="OperatorsHaveNoSupplierList")
	public List<Operator> getOperatorsHaveNoSupplier();
	
	/**
	 * method name : getOperatorsHaveNoRole
	 * method Desc : Operator 목록 중 공급사 아이디에 해당하는 Operator 중 Role id가 널인 Operator 목록을 리턴
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Operator @see com.aimir.model.system.Operator
	 */
	@WebMethod
	@WebResult(name="OperatorsHaveNoRoleList")
	public List<Operator> getOperatorsHaveNoRole(@WebParam(name ="supplierId")Integer supplierId);
	
	/**
	 * method name : checkDuplicateLoginId
	 * method Desc : 로그인 아이디가 중복되는지를 확인 이미 등록되어 있는지 체크하여 리턴
	 * 
	 * @param loginId Operator.loginId
	 * @return 중복되면 false를 리턴하고 중복되지 않으면 true를 리턴
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Login ID Validation - If it is not registered in the system returns true.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="checkDuplicateLoginId", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="checkDuplicateLoginId")
	public boolean checkDuplicateLoginId(
			@WebParam(name ="loginId")String loginId);	
	/**
	 * 
	 * @param loginId Operator.loginId
	 * @param password Operator.password
	 * @return
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Login Password Validation - If it is not match in the system returns false.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="checkPassword", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="checkPassword")
	public boolean checkPassword(
			@WebParam(name ="loginId")String loginId, 
			@WebParam(name ="password")String password);		
	/**
	 * method name : getOperators
	 * method Desc : Role ID와 일치하는 operator 목록을 조회하여 리턴 페이지번호, 최대 카운트 단위로 데이터를 리턴
	 * 
	 * @param page page number
	 * @param count max data count
	 * @param roleId Role.id
	 * @return List<Object>
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get a list of operators", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="operator List", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod(operationName ="operatorListByPageCountRoleId")
	@WebResult(name="operatorList")
    public List<Object> getOperators(
    		@WebParam(name ="page")int page, 
    		@WebParam(name ="count")int count, 
    		@WebParam(name ="roleId")int roleId);    
	/**
	 * method name : getCount
	 * method Desc : Role ID와 일치하는 operator 목록을 조회하여 카운트를 리턴
	 * 
	 * @param roleId Role.id
	 * @return {total,count}
	 */
    @WebMethod
	@WebResult(name="countMap")
	public Map<String,String> getCount(
			@WebParam(name ="roleId")int roleId);
	/**
	 * 
	 * @param condition
	 * @param operator
	 * @return
	 */
    @WebMethod
	@WebResult(name="loginLog")
	public Long addLoginLog(
			@WebParam(name ="condition")Map<String, Object> condition, 
			@WebParam(name ="operator")Operator operator);	
    
    @WebMethod
	@WebResult(name="loginLogCustomer")
	public Long loginLogCustomer(
			@WebParam(name ="condition")Map<String, Object> condition, 
			@WebParam(name ="customer")Customer customer);	
    
	/**
	 * 
	 * @param condition
	 */
    @WebMethod
	public void addLogoutLog(
			@WebParam(name ="condition")Map<String, Object> condition);
	/**
	 * method name : getLoginLogGrid
	 * method Desc : Role ID와 일치하는 operator 목록을 조회하여 카운트를 리턴
	 * 
	 * @param condition
	 * {@code}
	 * 		String roleId            = StringUtil.nullToBlank(condition.get("roleId"));
	 * 		String loginId           = StringUtil.nullToBlank(condition.get("loginId"));   
	 * 		String ipAddr            = StringUtil.nullToBlank(condition.get("ipAddr"));
	 * 		boolean login            = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("login")));		                                           
	 * 		boolean logOut           = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("logOut")));
	 * 		boolean loginFail        = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("loginFail")));
	 * 		String searchStartDate   = StringUtil.nullToBlank(condition.get("searchStartDate"));   
	 * 		String searchEndDate     = StringUtil.nullToBlank(condition.get("searchEndDate"));
	 * 		String curPage   		= StringUtil.nullToBlank(condition.get("curPage"));
	 * 
	 * @return List of Object {	LoginLog.LOGIN_ID     as userId
	 * 							Operator.NAME         as userName
	 * 							Role.Name             as userGroup
	 * 							LoginLog.IP_ADDR      as ipAddr	
	 * 							LoginLog.LOGIN_DATE   as loginTime
	 * 							LoginLog.LOGOUT_DATE  as logouTime
	 * 							LoginLog.STATUS       as status }
	 */
    @WSDLDocumentationCollection(
		    {
		        @WSDLDocumentation(value = "This refers to the login history period.",	
									placement = WSDLDocumentation.Placement.BINDING_OPERATION),
		        @WSDLDocumentation(value = "Parameters(roldId, loginId,ipAddr, login,logout,loginFail,searchStartDate,searchEndDate,curPage)",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
		        @WSDLDocumentation(value = "Return List of Object{userId,userName,userGroup,ipAddr,loginTime,logoutTime,status}",
		                           placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
		    }
	)	
	@WebMethod
	@WebResult(name="loginLogList") 
	public List<Object> getLoginLogGrid(@WebParam(name = "condition") Map<String, Object> condition);
    
    
    /**
     * @desc 로긴 로그 리스트 all data fetch
     * @param condition
     * @return
     */
    public List<Object> getLoginLogGrid2( Map<String, Object> condition);
	
	/**
	 * method name : checkPucNumber
	 * method Desc : 주민등록번호 가입 여부 확인
	 *
	 * @param pucNumber
	 * @return 중복되는것이 없으면 true를 리턴
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="PUC Number Validation - If it is not registered in the system returns true.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="checkPucNumber", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="checkPucNumber")
	public boolean checkPucNumber(
			@WebParam(name ="pucNumber")String pucNumber);
	
	/**
	 * method name : addOperatorReturnOperator
	 * method Desc : Operator 등록 시 Operator 반환
	 *
	 * @param operator
	 * @return @see com.aimir.model.system.Operator
	 */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="PUC Number Validation - If it is not registered in the system returns true.", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="Operator", 
            		placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
	@WebResult(name="Operator")
	public Operator addOperatorReturnOperator(
			@WebParam(name ="operator")Operator operator);
	
	/**
	 * method name : getOperatorByOperator
	 * method Desc : Operator 조회 (조건 : Operator)
	 *
	 * @param operator
	 * @return List of Operator  @see com.aimir.model.system.Operator
	 */
    @WebMethod
	@WebResult(name="OperatorList")
	public List<Operator> getOperatorByOperator(
			@WebParam(name ="operator")Operator operator);
    
    /**
     * 
     * @param operator
     */
	@WebMethod
    public void updateOperatorInfo(Operator operator);
	
	public User getUser(Integer userId, String loginId);
	
    public List<Map<String,Object>> getLoginId();

	
}
