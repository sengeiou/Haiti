package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.model.device.CircuitBreakerLog;

@Repository(value = "circuitbreakerlogDao")
public class CircuitBreakerLogDaoImpl extends AbstractHibernateGenericDao<CircuitBreakerLog, Long> implements CircuitBreakerLogDao {
	@Autowired
	protected CircuitBreakerLogDaoImpl(SessionFactory sessionFactory) {
		super(CircuitBreakerLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<CircuitBreakerLog> getCircuitBreakerLogGridData(Map<String, String> paramMap) {

		String startDate = paramMap.get("startDate") + "000000";
		String endDate = paramMap.get("endDate") + "235959";		
		String switchStatus = paramMap.get("switchStatus");
		String groupType= paramMap.get("groupType");
		String target= paramMap.get("target");
		
		int page = Integer.parseInt(paramMap.get("page"));
		int pageSize = Integer.parseInt(paramMap.get("pageSize"));
		int firstIndex = page * pageSize;
		
		Criteria criteria = getSession().createCriteria(CircuitBreakerLog.class);
		criteria.add(Restrictions.between("writeTime", startDate, endDate));
		
		if(!"".equals(target))
			criteria.add(Restrictions.like("target", target, MatchMode.ANYWHERE));

		if(!"".equals(groupType)) 
			criteria.add(Restrictions.eq("targetType", GroupType.valueOf(groupType)));
		
		if("Activation".equals(switchStatus)) {
			criteria.add(Restrictions.eq("status", CircuitBreakerStatus.Activation));
		} else {
			criteria.add(Restrictions.eq("status", CircuitBreakerStatus.Deactivation));
		}
		
		criteria.setFirstResult(firstIndex);
		criteria.setMaxResults(pageSize);				
		
		return criteria.list();
	}

	public Long getCircuitBreakerLogGridDataCount(Map<String, String> paramMap) {

		String startDate = paramMap.get("startDate") + "000000";
		String endDate = paramMap.get("endDate") + "235959";		
		String switchStatus = paramMap.get("switchStatus");
		String groupType= paramMap.get("groupType");
		String target= paramMap.get("target");
		
		Criteria criteria = getSession().createCriteria(CircuitBreakerLog.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.between("writeTime", startDate, endDate));

		if(!"".equals(target))
			criteria.add(Restrictions.like("target", target, MatchMode.ANYWHERE));

		if(!"".equals(groupType)) 
			criteria.add(Restrictions.eq("targetType", GroupType.valueOf(groupType)));
		
		if("Activation".equals(switchStatus)) {
			criteria.add(Restrictions.eq("status", CircuitBreakerStatus.Activation));
		} else {
			criteria.add(Restrictions.eq("status", CircuitBreakerStatus.Deactivation));
		}
		
		return ((Number)criteria.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getCircuitBreakerLogChartData(Map<String, String> paramMap) {
		
		String startDate = paramMap.get("startDate") + "000000";
		String endDate = paramMap.get("endDate") + "235959";		
		String switchStatus = paramMap.get("switchStatus");
		String groupType = paramMap.get("groupType");
		String target= paramMap.get("target");
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT log.condition as condition, count(log) as cnt  ")
		.append("   FROM CircuitBreakerLog log       ")
		.append("  WHERE log.writeTime >= :startDate ")
		.append("    AND log.writeTime <= :endDate   ")
		.append("    AND log.status = :status        ");
		
		if(!"".equals(target))
			sb.append(" AND log.target like :target ");

		if(!"".equals(groupType)) 
			sb.append(" AND log.targetType = :targetType ");
		
		sb.append(" GROUP BY log.condition ");
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		query.setString("status", switchStatus);
		
		if(!"".equals(target))
			query.setString("target", "%" + target + "%");

		if(!"".equals(groupType)) 
			query.setString("targetType", groupType);	
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
}	
