/**
 * EnergySavingTargetDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * EnergySavingTargetDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 9.   v1.0       김상연         
 *
 */

@Repository(value = "energySavingTargetDao")
public class EnergySavingTargetDaoImpl 
				extends AbstractJpaDao<EnergySavingTarget, Integer> 
				implements EnergySavingTargetDao {

	public EnergySavingTargetDaoImpl() {
		super(EnergySavingTarget.class);
	}

	public List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget
			, String fromDay, String toDay) {

	    Set<Condition> conditions = new HashSet<Condition>();
	    
		if (energySavingTarget != null) {
			
			if (energySavingTarget.getId() != null) {
				conditions.add(new Condition("id", 
				        new Object[]{energySavingTarget.getId()}, null, Restriction.EQ));
			}
			
			if (energySavingTarget.getCreateDate() != null) {
				if ( 2 == energySavingTarget.getCreateDate().length() ) {
				    conditions.add(new Condition("createDate",
	                        new Object[]{"%"+energySavingTarget.getCreateDate()}, null, Restriction.LIKE));
				}
				else {
				    conditions.add(new Condition("createDate",
	                        new Object[]{energySavingTarget.getCreateDate()}, null, Restriction.LE));
				}
			}
			
			if (energySavingTarget.getOperatorContract() != null) {
				if (energySavingTarget.getOperatorContract().getId() != null) {
				    conditions.add(new Condition("operatorContract.id",
	                        new Object[]{energySavingTarget.getOperatorContract().getId()}, null, Restriction.EQ));
				}
			}
		}
		
		if (null != fromDay) {
		    conditions.add(new Condition("createDate",
                    new Object[]{fromDay}, null, Restriction.GE));
		}
		
		if (null != toDay) {
		    conditions.add(new Condition("createDate",
                    new Object[]{toDay}, null, Restriction.LE));
		}
		
		conditions.add(new Condition("createDate",
                new Object[]{energySavingTarget.getCreateDate()}, null, Restriction.ORDERBYDESC));
		
		return findByConditions(conditions);
	}
	
	public String getEnergySavingStartDate(int id) {
		StringBuilder sqlStr = new StringBuilder();
		sqlStr.append("SELECT min(createDate) ")
		.append("FROM EnergySavingTarget ")
		.append("WHERE operatorContract.id = :id ");
		
		TypedQuery<String> query = em.createQuery(sqlStr.toString(), String.class);
		//SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setParameter("id",id);
		
		return query.getSingleResult();
	}


	/* (non-Javadoc)
	 * @see com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao#deleteByOperatorContractId(int)
	 */
	public void deleteByOperatorContractId(int operatorContractId) {
		StringBuffer sql = new StringBuffer();             
        sql.append("DELETE EnergySavingTarget WHERE operatorContract.id = ? ");
        
        Query query = em.createQuery(sql.toString());
        query.executeUpdate();
	}

    @Override
    public Class<EnergySavingTarget> getPersistentClass() {
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
