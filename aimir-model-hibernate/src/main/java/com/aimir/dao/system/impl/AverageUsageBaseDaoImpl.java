package com.aimir.dao.system.impl;

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
import com.aimir.dao.system.AverageUsageBaseDao;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.AverageUsageBasePk;

@Repository(value = "averageUsageBaseDao")
public class AverageUsageBaseDaoImpl extends AbstractHibernateGenericDao<AverageUsageBase, AverageUsageBasePk> implements AverageUsageBaseDao {

	@Autowired
	protected AverageUsageBaseDaoImpl(SessionFactory sessionFactory) {
		super(AverageUsageBase.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
  	public List<AverageUsageBase> getAverageUsageBaseListBystartDate( Integer avgUsageId , Integer supplyType, String UsageYear) {
		Criteria criteria = getSession().createCriteria( AverageUsageBase.class );
		criteria.add( Restrictions.le( "id.avgUsageId"	, avgUsageId ) );
		criteria.add( Restrictions.eq( "id.supplyType"	, supplyType ) );
		criteria.add( Restrictions.eq( "id.usageYear"	, UsageYear ) );
		criteria.addOrder( Order.desc( "id.usageYear" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		List<AverageUsageBase> result = criteria.list();
		return result;
	}
	
	public int deleteAvgUsageId( AverageUsageBase averageUsageBase) {
		
		int result = 0;
		
		SQLQuery queryPre = null;
		StringBuffer sqlBufPre = new StringBuffer();
		
		sqlBufPre.append("DELETE FROM AVERAGE_USAGE_BASE WHERE AVG_USAGE_ID = ? ");	
		

		queryPre = getSession().createSQLQuery( sqlBufPre.toString());
		queryPre.setInteger( 0 , averageUsageBase.getId().getAvgUsageId() );
		
		result = queryPre.executeUpdate();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<AverageUsageBase> getSetYearsbyId(Integer avgUsageId) {
		Query query =getSession().createQuery("SELECT DISTINCT a.id.usageYear FROM AverageUsageBase a WHERE a.id.avgUsageId = :avgUsageId " );
		query.setInteger("avgUsageId", avgUsageId);
		return query.list();
	}
}
