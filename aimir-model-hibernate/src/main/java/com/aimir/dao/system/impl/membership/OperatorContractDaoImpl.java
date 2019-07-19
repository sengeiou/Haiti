/**
 * OperatorContractDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.membership;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.system.OperatorContract;
import com.aimir.util.StringUtil;

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
				extends AbstractHibernateGenericDao<OperatorContract, Integer> 
				implements OperatorContractDao {
	
    Log logger = LogFactory.getLog(OperatorContractDaoImpl.class);
    
	@Autowired
	protected OperatorContractDaoImpl(SessionFactory sessionFactory) {
		
		super(OperatorContract.class);
		super.setSessionFactory(sessionFactory);
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.system.membership.OperatorContractDao#getOperatorContract(com.aimir.model.system.OperatorContract)
	 */
	@SuppressWarnings("unchecked")
	public List<OperatorContract> getOperatorContract(OperatorContract operatorContract) {
		
		Criteria criteria = getSession().createCriteria(OperatorContract.class);
		
		if (operatorContract != null) {
			if (operatorContract.getId() != null) {
				
				criteria.add(Restrictions.eq("id", operatorContract.getId()));
			}
			
			if (operatorContract.getOperator() != null) {
				
				criteria.add(Restrictions.eq("operator.id", operatorContract.getOperator().getId()));
			}
			
			if (operatorContract.getContract() != null) {
				
				criteria.add(Restrictions.eq("contract.id", operatorContract.getContract().getId()));
			}
			
			if (operatorContract.getContractStatus() != null) {
				
				criteria.add(Restrictions.eq("contractStatus", operatorContract.getContractStatus()));
			}
		}
		
		List<OperatorContract> operatorContracts = criteria.list();
		
		return operatorContracts;
	}

    /**
     * method name : getPrepaymentOperatorContract
     * method Desc : 로긴 회원의 선불계약정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<OperatorContract> getPrepaymentOperatorContract(Map<String, Object> conditionMap) {

        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String serviceType  = StringUtil.nullToBlank(conditionMap.get("serviceType"));

        StringBuilder sb = new StringBuilder();
        
        sb.append("FROM OperatorContract o ");
        sb.append("WHERE o.operator.id = :operatorId ");
        sb.append("AND   o.contract.creditType.code IN ('2.2.1', '2.2.2') ");  // 선불,Emergency Credit
        sb.append("AND   o.contract.serviceTypeCode.code = :serviceType ");

        Query query = getSession().createQuery(sb.toString());
        
        query.setInteger("operatorId", operatorId);
        query.setString("serviceType", serviceType);
        
        return query.list();
    }

}
