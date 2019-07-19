package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.EnergySavingGoal2Dao;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.EnergySavingGoalPk2;

@Repository(value = "energySavingGoal2Dao")
public class EnergySavingGoal2DaoImpl extends AbstractHibernateGenericDao<EnergySavingGoal2, EnergySavingGoalPk2> implements EnergySavingGoal2Dao {

	@Autowired
	protected EnergySavingGoal2DaoImpl(SessionFactory sessionFactory) {
		super(EnergySavingGoal2.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<EnergySavingGoal2> getAll() {
		
		Criteria criteria = getSession().createCriteria( EnergySavingGoal2.class );
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
		criteria.addOrder( Order.desc( "id.startDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal2> resultList = criteria.list();

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
    public List<EnergySavingGoal2> getEnergySavingGoal2ListByStartDate( String searchDateType , String energyType , String startDate , Integer supplierId ) {
		Criteria criteria = getSession().createCriteria( EnergySavingGoal2.class );
		criteria.add( Restrictions.eq( "supplier.id", supplierId ) );		
		criteria.add( Restrictions.eq( "id.supplyType", Integer.parseInt( energyType ) ) );
		
		if( CommonConstants.DateType.DAILY.getCode().equals( searchDateType ) ){
			
			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.DAILY ) );
		}else if( CommonConstants.DateType.WEEKLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.WEEKLY ) );
		}else if( CommonConstants.DateType.MONTHLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.MONTHLY ) );
		}else if( CommonConstants.DateType.YEARLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.YEARLY ) );
		}
		
		criteria.add( Restrictions.le( "id.startDate", startDate ) );		
		criteria.addOrder( Order.desc( "id.startDate" ) );
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal2> result = criteria.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public List<EnergySavingGoal2> getEnergySavingGoal2ListByAverageUsage( String searchDateType , String energyType ,  String startDate , Integer supplierId , Integer averageUsageId ) {
		Criteria criteria = getSession().createCriteria( EnergySavingGoal2.class );
		criteria.add( Restrictions.eq( "supplier.id", supplierId ) );
		criteria.add( Restrictions.eq( "averageUsage.id", averageUsageId ) );
		criteria.add( Restrictions.eq( "id.supplyType", Integer.parseInt( energyType ) ) );
		
		if( CommonConstants.DateType.DAILY.getCode().equals( searchDateType ) ){
			
			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.DAILY ) );
		}else if( CommonConstants.DateType.WEEKLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.WEEKLY ) );
		}else if( CommonConstants.DateType.MONTHLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.MONTHLY ) );
		}else if( CommonConstants.DateType.YEARLY.getCode().equals( searchDateType ) ){

			criteria.add( Restrictions.eq( "id.dateType", CommonConstants.DateType.YEARLY ) );
		}
		
		criteria.add( Restrictions.le( "id.startDate", startDate ) );
		criteria.addOrder( Order.desc( "id.startDate" ) );
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal2> result = criteria.list();
		return result;
	}
	
	
	public List<EnergySavingGoal2> getEnergySavingGoal2ListByAvg( String supplierId, String energyType, String avgInfoId, String allView){
		
		Criteria criteria = getSession().createCriteria( EnergySavingGoal2.class );
		criteria.add( Restrictions.eq( "averageUsage.id", Integer.parseInt( avgInfoId ) ) );
		criteria.add( Restrictions.eq( "supplier.id", Integer.parseInt( supplierId ) ) );
//		criteria.add( Restrictions.eq( "id.supplyType", Integer.parseInt( energyType ) ) );		// energyType과 관계 없이 모두 가져옴 (2010-10-04. cmyang)
		criteria.addOrder( Order.desc( "id.createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<EnergySavingGoal2> result = criteria.list();
		return result;
	}
}
