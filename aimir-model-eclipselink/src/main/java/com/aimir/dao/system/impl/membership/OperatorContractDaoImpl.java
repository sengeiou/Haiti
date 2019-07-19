/**
 * OperatorContractDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.membership;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.system.OperatorContract;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

/**
 * OperatorContractDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 5.   v1.0       김상연         OperatorContract 정보 조회
 *
 */

@Repository(value = "operatorContractDao")
public class OperatorContractDaoImpl 
				extends AbstractJpaDao<OperatorContract, Integer> 
				implements OperatorContractDao {
	
    Log logger = LogFactory.getLog(OperatorContractDaoImpl.class);
    
	public OperatorContractDaoImpl() {
		super(OperatorContract.class);
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.system.membership.OperatorContractDao#getOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	public List<OperatorContract> getOperatorContract(OperatorContract operatorContract) {
		
	    Set<Condition> conditions = new HashSet<Condition>();
		
		if (operatorContract != null) {
			if (operatorContract.getId() != null) {
			    conditions.add(new Condition("id", 
			            new Object[]{operatorContract.getId()}, null, Restriction.EQ));
			}
			
			if (operatorContract.getOperator() != null) {
			    conditions.add(new Condition("operator.id",
			            new Object[]{operatorContract.getOperator().getId()}, null, Restriction.EQ));
			}
			
			if (operatorContract.getContract() != null) {
			    conditions.add(new Condition("contract.id", 
			            new Object[]{operatorContract.getContract().getId()}, null, Restriction.EQ));
			}
			
			if (operatorContract.getContractStatus() != null) {
			    conditions.add(new Condition("contractStatus", 
                        new Object[]{operatorContract.getContractStatus()}, null, Restriction.EQ));
			}
		}
		
		return findByConditions(conditions);
	}

    /**
     * method name : getPrepaymentOperatorContract
     * method Desc : 로긴 회원의 선불계약정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<OperatorContract> getPrepaymentOperatorContract(Map<String, Object> conditionMap) {

        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String serviceType  = StringUtil.nullToBlank(conditionMap.get("serviceType"));

        StringBuilder sb = new StringBuilder();
        
        sb.append("FROM OperatorContract o ");
        sb.append("WHERE o.operator.id = :operatorId ");
        sb.append("AND   o.contract.creditType.code IN ('2.2.1', '2.2.2') ");  // 선불,Emergency Credit
        sb.append("AND   o.contract.serviceTypeCode.code = :serviceType ");

        TypedQuery<OperatorContract> query = em.createQuery(sb.toString(), OperatorContract.class);
        
        query.setParameter("operatorId", operatorId);
        query.setParameter("serviceType", serviceType);
        
        return query.getResultList();
    }

    @Override
    public Class<OperatorContract> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
