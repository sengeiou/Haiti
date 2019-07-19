/**
 * EnergySavingTargetDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl.energySavingGoal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao;
import com.aimir.model.system.EnergySavingTarget;

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
				extends AbstractHibernateGenericDao<EnergySavingTarget, Integer> 
				implements EnergySavingTargetDao {

	@Autowired
	protected EnergySavingTargetDaoImpl(SessionFactory sessionFactory) {
		
		super(EnergySavingTarget.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget
			, String fromDay, String toDay) {

		Criteria criteria = getSession().createCriteria(EnergySavingTarget.class);
		
		if (energySavingTarget != null) {
			
			if (energySavingTarget.getId() != null) {
				
				criteria.add(Restrictions.eq("id", energySavingTarget.getId()));
			}
			
			if (energySavingTarget.getCreateDate() != null) {
				
				criteria.add(Restrictions.le("createDate", energySavingTarget.getCreateDate()));
				
				if ( 8 == energySavingTarget.getCreateDate().length() ) {
					
					criteria.add(Restrictions.le("createDate", energySavingTarget.getCreateDate()));
				} else if ( 2 == energySavingTarget.getCreateDate().length() ) {
					
					criteria.add(Restrictions.like("createDate", "%" + energySavingTarget.getCreateDate()));
				}
			}
			
			if (energySavingTarget.getOperatorContract() != null) {
				
				if (energySavingTarget.getOperatorContract().getId() != null) {
					
					criteria.add(Restrictions.eq("operatorContract.id", energySavingTarget.getOperatorContract().getId()));
				}
			}
		}
		
		if (null != fromDay) {
			
			criteria.add(Restrictions.ge("createDate", fromDay));
		}
		
		if (null != toDay) {
			
			criteria.add(Restrictions.le("createDate", toDay));
		}
		
		criteria.addOrder(Order.desc("createDate"));

		List<EnergySavingTarget> energySavingTargets = criteria.list();
		
		return energySavingTargets;
	}
	
	public String getEnergySavingStartDate(int id) {
		StringBuilder sqlStr = new StringBuilder();
		sqlStr.append("SELECT min(create_Date) ")
		.append("FROM ENERGY_SAVING_TARGET ")
		.append("WHERE operatorContract_id = :id ");
		
		SQLQuery query = getSession().createSQLQuery(sqlStr.toString());
		//SQLQuery query = getSession().createSQLQuery(sb.toString());
		query.setInteger("id",id);
		
		return (query.uniqueResult() == null ? "" : query.uniqueResult().toString());
	}


	/* (non-Javadoc)
	 * @see com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao#deleteByOperatorContractId(int)
	 */
	public void deleteByOperatorContractId(int operatorContractId) {
		StringBuffer query = new StringBuffer();             
        query.append("DELETE EnergySavingTarget WHERE operatorContract_id = ? ");	    
        // bulkUpdate 때문에 주석처리
        /*this.getHibernateTemplate().bulkUpdate(query.toString(), operatorContractId );*/
	}
	
}
