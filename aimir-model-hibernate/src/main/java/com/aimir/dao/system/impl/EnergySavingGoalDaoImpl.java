package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.EnergySavingGoalPk;

@Repository(value = "energySavingGoalDao")
public class EnergySavingGoalDaoImpl extends AbstractHibernateGenericDao<EnergySavingGoal, EnergySavingGoalPk> implements EnergySavingGoalDao {

	@Autowired
	protected EnergySavingGoalDaoImpl(SessionFactory sessionFactory) {
		super(EnergySavingGoal.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<EnergySavingGoal> getAll() {
		
		Criteria criteria = getSession().createCriteria( EnergySavingGoal.class );
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
		criteria.addOrder( Order.desc( "id.startDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal> resultList = criteria.list();

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
    public List<EnergySavingGoal> getEnergySavingGoalListBystartDate( String startDate , Integer supplierId ) {
		Criteria criteria = getSession().createCriteria( EnergySavingGoal.class );
		criteria.add( Restrictions.eq( "supplier.id", supplierId ) );
		criteria.add( Restrictions.le( "id.startDate", startDate ) );
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal> result = criteria.list();
		return result;
	}
}
