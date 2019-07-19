/**
 * OperatorDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Operator;

/**
 * OperatorDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0       김상연         주민등록번호 정보 추출
 * 2011. 4. 14.  v1.0       김상연         Operator 조회 (조건 : Operator)
 *
 */
public interface OperatorDao  extends GenericDao<Operator, Integer>{
	
	/**
	 * method name : checkOperator
	 * method Desc : 사용자 로그인 아이디와 패스워드로 조회하여 해당 계정이 존재하는지 리턴
	 * 
	 * @param userId Operator.loginId
	 * @param pw Operator.password
	 * @return 
	 */
	public boolean checkOperator(int userId, String pw); 
	
	/**
	 * method name : getOperatorByLoginId
	 * method Desc : 사용자 로그인 아이디로 Operator 객체를 조회하여 리턴
	 * 
	 * @param loginId Operator.loginId
	 * @return  @see com.aimir.model.system.Operator
	 */
	public Operator getOperatorByLoginId(String loginId);
	
	/**
	 * method name : getOperatorsByRole
	 * method Desc : roleId에 해당하는 Operator 객체 목록을 조회하여 리턴
	 * 
	 * @param roleId Role.id
	 * @return  @see com.aimir.model.system.Operator
	 */
	@Deprecated
	public List<Operator> getOperatorsByRole(Integer roleId);

    /**
     * method name : getOperatorListByRole<b/>
     * method Desc : User Management 가젯에서 Operator List 를 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getOperatorListByRole(Map<String, Object> conditionMap, boolean isCount);

	/**
	 * method name : getOperatorsHaveNoSupplier
	 * method Desc : Operator 목록 중 공급사 정보 (Supplier.id)가 널인 Operator 목록을 리턴
	 * 
	 * @return List of Operator @see com.aimir.model.system.Operator
	 */
	public List<Operator> getOperatorsHaveNoSupplier();
	
	/**
	 * method name : getOperatorsHaveNoRole
	 * method Desc : Operator 목록 중 공급사 아이디에 해당하는 Operator 중 Role id가 널인 Operator 목록을 리턴
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Operator @see com.aimir.model.system.Operator
	 */
	public List<Operator> getOperatorsHaveNoRole(Integer supplierId);
	
	/**
	 * method name : checkDuplicateLoginId
	 * method Desc : 로그인 아이디가 중복되는지를 확인 이미 등록되어 있는지 체크하여 리턴
	 * 
	 * @param loginId Operator.loginId
	 * @return 중복되면 false를 리턴하고 중복되지 않으면 true를 리턴
	 */
	public boolean checkDuplicateLoginId(String loginId);
	
	/**
	 * method name : getOperators
	 * method Desc : Role ID와 일치하는 operator 목록을 조회하여 리턴 페이지번호, 최대 카운트 단위로 데이터를 리턴
	 * 
	 * @param page page number
	 * @param count max data count
	 * @param roleId Role.id
	 * @return List of Operator @see com.aimir.model.system.Operator
	 */
	public List<Operator> getOperators(int page, int count, int roleId);
	
	/**
	 * method name : count
	 * method Desc : Role ID와 일치하는 operator 목록을 조회하여 카운트를 리턴
	 * 
	 * @param roleId Role.id
	 * @return
	 */
	public Integer count(int roleId);
	
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
	public List<Object> getLoginLogGrid(Map<String, Object> condition);
	
	/**
	 * 전체 login log fetch
	 * @param condition
	 * @return
	 */
	public List<List<HashMap<String, Object>>> getLoginLogGrid2(Map<String, Object> condition);

	/**
	 * method name : getOperatorById
	 * method Desc : Operator ID와 일치하는 operator 객체를 조회하여 리턴
	 * 
	 * @param operatorId Operator.id
	 * @return @see com.aimir.model.system.Operator
	 */
	public Operator getOperatorById(Integer operatorId);

	/**
	 * method name : getGroupMember
	 * method Desc : 그룹 관리 중 멤버 리스트 조회
	 * 
	 * @param condition
	 * {@code}
	 * 		String member = StringUtil.nullToBlank(condition.get("member"));
	 * 		Integer.parseInt((String)condition.get("supplierId"))
	 * @return List of Object {Operator.id, Operator.name}
	 */
	@Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Operator 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

	/**
	 * method name : getOperatorByPucNumber
	 * method Desc : 주민등록번호 정보 추출
	 *
	 * @param pucNumber Operator.pubNumber
	 * @return @see com.aimir.model.system.Operator
	 */
	public Operator getOperatorByPucNumber(String pucNumber);
	
	/**
	 * method name : getOperatorByOperator
	 * method Desc : Operator 조회 (조건 : Operator)
	 *
	 * @param operator
	 * @return List of Operator  @see com.aimir.model.system.Operator
	 */
	public List<Operator> getOperatorByOperator(Operator operator);
	
	/**
	 * @MethodName getVendorByLoginIdAndName
	 * @Date 2013. 6. 26.
	 * @param page 조회하려는 페이지 
	 * @param limit 페이지 항목 제한
	 * @param loginId 
	 * @param name
	 * @param supplierId
 	 * @param supplierName
	 * @return List<Operator>
	 * @Modified 
	 * @Description 접속 계정 혹은 사용자 이름에 따른 Operator list
	 */
	public List<Operator> getVendorByLoginIdAndName(int page, int limit, String loginId, String name, Integer supplierId, String supplierName);
	
	
	/**
	 * @MethodName getVendorCountByLoginIdAndName
	 * @Date 2013. 7. 1.
	 * @param loginId
	 * @param name
	 * @param supplierId
	 * @param supplierName
	 * @return
	 * @Modified
	 * @Description
	 */
	public int getVendorCountByLoginIdAndName(String loginId, String name, Integer supplierId, String supplierName);
	
	/**
	 * 
	 * method name : getOperatorListByRoleType<b/>
	 * method Desc : role type에 따른 OperatorList를 조회해온다.
	 *
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getOperatorListByRoleType(Map<String, Object> params);
	
	/**
	 * @MethodName chargeDeposit
	 * @Date 2013. 6. 27.
	 * @param vendorId
	 * @param amount
	 * @return update 성공 여부
	 * @Modified
	 * @Description vendor의 deposit을 amount 만큼 추가한다
	 */
	public int chargeDeposit(String vendorId, Double amount);
	
    /**
     * method name : getOperatorByName<b/>
     *
     * @param String name
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Operator> getOperatorByName(String name);

    public List<Map<String,Object>> getLoginId();
}
