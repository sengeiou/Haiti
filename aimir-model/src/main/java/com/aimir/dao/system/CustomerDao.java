package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Customer;
import com.aimir.util.Condition;

public interface CustomerDao extends GenericDao<Customer, Integer> {

	/**
	 * method name : getCustomersByName
	 * method Desc : 이름 목록에 해당하는  Customer List 리턴
	 * 
	 * @param name  Customer.name에 해당하는 String Array
	 * @return List of Customer @see com.aimir.model.system.Customer
	 */
    public List<Customer> getCustomersByName(String[] name);    
    
    /**
	 * method name : getCustomersByCustomerNo
	 * method Desc : 고객번호 목록에 해당하는 Customer List 리턴
	 * 
     * @param customerNo Customer.customerNo에 해당하는 String Array
     * @return List of Customer @see com.aimir.model.system.Customer
     */
    public List<Customer> getCustomersByCustomerNo(String[] customerNo);
    
    /**
	 * method name : getCustomersByLoginId
	 * method Desc : Customer.loginId에 해당하는 Customer객체 리턴
	 * 
     * @param loginId
     * @return @see com.aimir.model.system.Customer
     */
    public Customer getCustomersByLoginId(String loginId);
    
    /**
	 * method name : idOverlapCheck
	 * method Desc : 고객번호 중복 체크
	 * 
     * @param customerNo Customer.customerNo
     * @return 0이면 중복되지 않은 것임
     */
	public int idOverlapCheck(String customerNo);
	
    /**
     * method name : loginIdOverlapCheck
     * method Desc : loginId 중보체크
     * 
     * @param loginId, customerNo
     * @return 0이면 중복되지 않은 것임
     */
    public int loginIdOverlapCheck(String loginId, String customerNo);

	/**
	 * method name : checkCustomerNoLoginMapping
     * method Desc : 입력받은 CustomerNumber가 다른 customer의 Login아이디와 매핑되어있는지 체크한다.
     * 
     * @param customerNo
     * @return
     */
    public List<Map<String, String>> checkCustomerNoLoginMapping(String customerNo);
	
	/**
	 * method name : customerSearchList
	 * method Desc : 여러 조회조건에 해당하는 Customer 목록 리턴
	 * 
	 * @param set 조회조건 @see com.aimir.util.Condition
	 * @return List of Customer @see com.aimir.model.system.Customer
	 */
	public List<Customer> customerSearchList(Set<Condition> set);
	
	/**
	 * method name : customerSearchListCount
	 * method Desc : 여러 조회조건에 해당하는 Customer 목록의 카운트 리턴
	 * 
	 * @param customerNo Customer.customerNo
	 * @param name Customer.name
	 * @return
	 */
	public int customerSearchListCount(String customerNo, String name);
	
	/**
	 * method name : getTotalCustomer
	 * method Desc : 조회조건에 해당하는 customer 목록의 전체 카운트를 리턴
	 * 
	 * @param conditionMap
	 * {@code}
	 * 		String customerName = conditionMap.get("customerName");
	 * 		String locationId = conditionMap.get("location");
	 * 		String mdsId = conditionMap.get("mdsId");
	 * 		String customType = conditionMap.get("customerType");
	 * 		String address = conditionMap.get("address");
	 * 		String serviceType = conditionMap.get("serviceType");
	 * 		String supplierId = conditionMap.get("supplierId");
	 *      List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
	 * @return
	 */
	public Integer getTotalCustomer(Map<String, Object> conditionMap);
	
//	/**
//	 * method name : updateCustomer
//	 * method Desc : Customer 객체의 속성을 업데이트
//	 * 
//	 * @param c
//	 */
//	public void updateCustomer(Customer c);
	
	/**
	 * method name : getDemandResponseCustomerList
	 * method Desc : DR고객 리스트 취득
	 * 
	 * @return List of Customer @see com.aimir.model.system.Customer
	 */
	public List<Customer> getDemandResponseCustomerList();
	
    /**
     * method name : getCustomerCount<b/>
     * method Desc : 고객의 개수를 조회
     *
     * @param condition
     * @return
     */
    public Integer getCustomerCount(Map<String, String> condition);
    
    /**
     * @MethodName getNextId
     * @Date 2014. 3. 3.
     * @return 다음에 생성될 customer.id를 반환한다.
     * @Modified
     * @Description
     */
    public Integer getNextId();
    
    /**
     * method name : getCustomerListByRole<b/>
     * method Desc : User Management 가젯에서 Operator(Customer) List 를 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getCustomerListByRole(Map<String, Object> conditionMap, boolean isCount);
}
