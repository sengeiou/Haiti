package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.model.system.AverageUsage;

@Repository(value = "averageUsageDao")
public class AverageUsageDaoImpl extends AbstractHibernateGenericDao<AverageUsage, Integer> implements AverageUsageDao {

	@Autowired
	protected AverageUsageDaoImpl(SessionFactory sessionFactory) {
		super(AverageUsage.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public AverageUsage getAverageUsageByUsed() {
		
		Criteria criteria = getSession().createCriteria( AverageUsage.class );
		criteria.add( Restrictions.eq( "used"	, true ) );
//		criteria.addOrder( Order.desc( "createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의

		AverageUsage result = null;
		List<AverageUsage> resultList = criteria.list();

		if( resultList != null && resultList.size() > 0 ){
			
			result = resultList.get(0);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public int usageInitSql( AverageUsage au ) {
		
		int result = 0;
		if( au.getUsed() != null && au.getUsed() ){

			SQLQuery queryPre = null;
			StringBuffer sqlBufPre = new StringBuffer();
			
			sqlBufPre.append("UPDATE AVERAGE_USAGE SET USED = ? ");	
			
			queryPre = getSession().createSQLQuery( sqlBufPre.toString());
			queryPre.setBoolean( 0 , false );
			
			result = queryPre.executeUpdate();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public int updateSql( AverageUsage au ) {
		
		int result = 0;
	
//	    try {
//	    	getSession().update(au);
//	    	result = 1;
//	    }
//	    catch(Exception e){
//	      System.out.println(e.getMessage());
//	    }
//		return result;
	    
	    
	    
//		StringBuffer hqlBuf = new StringBuffer();
//		hqlBuf.append("UPDATE AverageUsage au ");
////		hqlBuf.append("SET au.descr = ? , au.used = ? ");		
//		hqlBuf.append("SET ");		
//		hqlBuf.append("		au.avgUsageYear = ? , au.avgUsageMonth = ? , au.avgUsageWeek = ? , au.avgUsageDay = ? ");		
//		hqlBuf.append("		, au.avgCo2Year = ?   , au.avgCo2Month = ?   , au.avgCo2Week = ?   , au.avgCo2Day = ? ");		
//		hqlBuf.append("		, au.bases = ? ");		
//		hqlBuf.append("WHERE au.id = ? ");		
//		
//	
//		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.		
//		result = this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(), new Object[] {  au.getAvgUsageYear() , au.getAvgUsageMonth() , au.getAvgUsageWeek() , au.getAvgUsageDay() 
//																				, au.getAvgCo2Year()   , au.getAvgCo2Month()   , au.getAvgCo2Week()   , au.getAvgCo2Day()
//																				, aub 
//																				, au.getId() 
//		} );
//		return result;
		
		
		// usage 정보 초기화
		usageInitSql( au );
		
		
		SQLQuery query = null;
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("UPDATE AVERAGE_USAGE ");
		sqlBuf.append(" SET ");		
		if( au.getDescr() != null && au.getUsed() != null ) {
			
//			sqlBuf.append("au.CREATE_DATE = ? , au.DESCR = ? , au.USED = ? ");	
			sqlBuf.append(" DESCR = ? , USED = ? ");	
			sqlBuf.append(" WHERE ID = ? ");		

			query = getSession().createSQLQuery( sqlBuf.toString());
//			query.setString( 0 , au.getCreateDate() );
			query.setString( 0 , au.getDescr() );
			query.setBoolean( 1 , au.getUsed() );
			query.setInteger( 2 , au.getId() );
		}else {
			
			sqlBuf.append(" AVG_USAGE_YEAR = ? , AVG_USAGE_MONTH = ? , AVG_USAGE_WEEK = ? , AVG_USAGE_DAY = ? ");		
			sqlBuf.append(" , AVG_CO2_YEAR = ?   , AVG_CO2_MONTH = ?   , AVG_CO2_WEEK = ?   , AVG_CO2_DAY = ? ");		
			sqlBuf.append(" WHERE ID = ? ");		
			

			query = getSession().createSQLQuery( sqlBuf.toString());
			query.setDouble( 0 , au.getAvgUsageYear() );
			query.setDouble( 1 , au.getAvgUsageMonth() );
			query.setDouble( 2 , au.getAvgUsageWeek() );
			query.setDouble( 3 , au.getAvgUsageDay() );
			query.setDouble( 4 , au.getAvgCo2Year() );
			query.setDouble( 5 , au.getAvgCo2Month() );
			query.setDouble( 6 , au.getAvgCo2Week() );
			query.setDouble( 7 , au.getAvgCo2Day() );
			query.setInteger( 8 , au.getId() );
		}
		
		
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<AverageUsage> getAll() {
		
		Criteria criteria = getSession().createCriteria( AverageUsage.class );
		criteria.addOrder( Order.desc( "createDate" ) ); 
//		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의
		
		List<AverageUsage> resultList = criteria.list();
		
		return resultList;
	}

//	@SuppressWarnings("unchecked")
//	public List<?> getList(){
//		
//		StringBuffer hqlBuf = new StringBuffer();
//		hqlBuf.append(" SELECT id, createDate , descr , avgUsageYear , used ");
//		hqlBuf.append("   FROM AverageUsage ");
//		hqlBuf.append("  ORDER BY createDate ");
//		
//		Query query = getSession().createQuery(hqlBuf.toString());
// 		
//		return query.list();
//	}
}
