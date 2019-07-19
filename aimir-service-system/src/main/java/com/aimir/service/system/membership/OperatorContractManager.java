/**
 * OperatorContractManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.membership;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;


/**
 * OperatorContractManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 5.   v1.0       김상연         ContractNo 유효성 체크
 * 2011. 4. 12.  v1.1       김상연         OperatorContract 등록
 * 2011. 4. 13.  v1.2       김상연         OperatorContract 정보 조회 (Operator 조건)
 * 2011. 4. 14.  v1.3       김상연         OperatorContract 삭제
 * 2011. 4. 14.  v1.4       김상연         OperatorContract 수정
 * 2011. 4. 14.  v1.5       김상연         OperatorContract 정보 조회 (OperatorContract 조건)
 *
 */

public interface OperatorContractManager {

	/**
	 * method name : checkOperatorContract
	 * method Desc : ContractNo 유효성 체크
	 *
	 * @param operator
	 * @return
	 */
	public boolean checkOperatorContract(Operator operator);
	
	/**
	 * method name : addOperatorContract
	 * method Desc : OperatorContract 등록
	 *
	 * @param OperatorContract
	 * @return
	 */
	public OperatorContract addOperatorContract(OperatorContract operatorContract);
	
	/**
	 * method name : getOperatorContractByOperator
	 * method Desc : OperatorContract 정보 조회 (Operator 조건)
	 *
	 * @param operator
	 * @return
	 */
	public List<OperatorContract> getOperatorContractByOperator(Operator operator);
	
	/**
	 * method name : updateOperatorContract
	 * method Desc : OperatorContract 수정
	 *
	 * @param operatorContract
	 * @return
	 */
	public OperatorContract updateOperatorContract(OperatorContract operatorContract);
	
	/**
	 * method name : deleteOperatorContract
	 * method Desc : OperatorContract 삭제
	 *
	 * @param operatorContract
	 * @return
	 */
	public void deleteOperatorContract(OperatorContract operatorContract);
	
	/**
	 * method name : getOperatorContract
	 * method Desc : OperatorContract 정보 조회 (OperatorContract 조건)
	 *
	 * @param operatorContract
	 */
	public List<OperatorContract> getOperatorContract(OperatorContract operatorContract);

	   /**
     * method name : getPrepaymentOperatorContractByOperator
     * method Desc : 로긴 회원의 선불계약정보 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<OperatorContract> getPrepaymentOperatorContractByOperator(Map<String, Object> conditionMap);
    
	/**
	 * method name : saveDashboardGadgetInfo
	 * method Desc : 회원정보 등록시 추가한 계약정보에 해당하는 디폴트 대시보드 가젯 정보를 설정한다.
	 *
	 * @param operatorID
	 */
	public void saveDashboardGadgetInfo(int operatorID);
	
	/**
	 * method name : modifyDashboardGadgetInfo
	 * method Desc : 회원정보 수정시 추가한 계약정보에 해당하는 디폴트 대시보드 가젯 정보를 설정한다.
	 *
	 * @param operatorId
	 */
	public void modifyDashboardGadgetInfo(int operatorId);
}
