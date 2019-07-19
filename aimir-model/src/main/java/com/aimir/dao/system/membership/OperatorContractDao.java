/**
 * OperatorContractDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.membership;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.OperatorContract;

/**
 * OperatorContractDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 5.   v1.0       김상연         Operator 정보 조회
 *
 */

public interface OperatorContractDao extends GenericDao<OperatorContract, Integer> {
	
	/**
	 * method name : getOperatorContract
	 * method Desc : OperatorContract 정보 조회 (조건)
	 *
	 * @param operatorContract
	 * @return
	 */
	public List<OperatorContract> getOperatorContract(OperatorContract operatorContract);

    /**
     * method name : getPrepaymentOperatorContract
     * method Desc : 로긴 회원의 선불계약정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<OperatorContract> getPrepaymentOperatorContract(Map<String, Object> conditionMap);
}
