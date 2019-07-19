package com.aimir.dao.device.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandLogPk;
import com.aimir.util.StringUtil;

@Repository(value = "asynccommandlogDao")
public class AsyncCommandLogDaoImpl extends AbstractHibernateGenericDao<AsyncCommandLog, AsyncCommandLogPk> implements AsyncCommandLogDao {

	@Autowired
	protected AsyncCommandLogDaoImpl(SessionFactory sessionFactory) {
		super(AsyncCommandLog.class);
		super.setSessionFactory(sessionFactory);
	}

	public Integer getCmdStatus(String deviceId, String cmd) {

		Criteria criteria = getSession().createCriteria(AsyncCommandLog.class);

		if (deviceId != null && !"".equals(deviceId)) {

			criteria.add(Restrictions.eq("deviceId", deviceId));
		}

		if (cmd != null && !"".equals(cmd)) {
			criteria.add(Restrictions.eq("command", cmd));
		}

		DetachedCriteria dc = DetachedCriteria.forClass(AsyncCommandLog.class);
		dc.add(Restrictions.eq("deviceId", deviceId));
		dc.add(Restrictions.eq("command", cmd));
		dc.setProjection(Projections.projectionList().add(Projections.max("id.trId")));

		criteria.add(Subqueries.propertyEq("id.trId", dc));
		criteria.setProjection(Projections.projectionList().add(Projections.property("state").as("state")));

		return (Integer) criteria.list().get(0);

	}
	
	public Integer getCmdStatusByTrId(String deviceId, long trId) throws Exception {
		Criteria criteria = getSession().createCriteria(AsyncCommandLog.class);
		
		if (deviceId != null && !"".equals(deviceId)) {
		
			criteria.add(Restrictions.eq("deviceId", deviceId));
		}
		
		criteria.add(Restrictions.eq("id.trId", trId));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("state").as("state")));

		return (Integer)criteria.list().get(0);
	}

	public List<AsyncCommandLog> getLogListByCondition(Map<String, Object> condition) throws Exception {

		String meterId = StringUtil.nullToBlank(condition.get("meterId"));
		String modemId = StringUtil.nullToBlank(condition.get("modemId"));
		Integer state = condition.get("state") == null ? null : (Integer) condition.get("state");

		Criteria criteria = getSession().createCriteria(AsyncCommandLog.class);

		Criterion meter = Restrictions.eq("deviceId", meterId);
		Criterion modem = Restrictions.eq("deviceId", modemId);

		LogicalExpression expression = Restrictions.or(meter, modem);
		criteria.add(Restrictions.eq("state", state));
		criteria.add(expression);
		criteria.addOrder(Order.desc("createTime"));

		return criteria.list();
	}

	public Long getMaxTrId(String deviceId) {
		return getMaxTrId(deviceId, null);
	}

	public Long getMaxTrId(String deviceId, String cmd) {
		Criteria criteria = getSession().createCriteria(AsyncCommandLog.class);
		if (deviceId != null && !"".equals(deviceId)) {
			criteria.add(Restrictions.eq("id.mcuId", deviceId));
			if (cmd != null)
				criteria.add(Restrictions.eq("command", cmd));
		}

		criteria.setProjection(Projections.projectionList().add(Projections.max("id.trId")));
		Long value = 0l;
		if (criteria.list().get(0) == null) {
			return 0l;
		} else if (criteria.list().get(0) instanceof Integer) {
			Integer val = (Integer) criteria.list().get(0);
			return new Long(val.intValue());
		} else if (criteria.list().get(0) instanceof Long) {
			return (Long) criteria.list().get(0);
		} else if (criteria.list().get(0) instanceof BigDecimal) {
			BigDecimal big = (BigDecimal) criteria.list().get(0);
			return big.longValue();
		} else if (criteria.list().get(0) instanceof Number) {
			Number number = (Number) criteria.list().get(0);
			return number.longValue();
		}

		return value;
	}
	
	@Override
	public List<Object> getCommandLogList(Map<String, Object> condition) throws Exception {
		String modemSerial = condition.get("deviceSerial").toString();
		String startDate = StringUtil.nullToBlank(condition.get("startDate"));
        String endDate = StringUtil.nullToBlank(condition.get("endDate")) +"235959";
        
		StringBuffer sqlBuf = new StringBuffer();
		Integer page = (Integer) condition.get("page");
		Integer limit = (Integer) condition.get("limit");
		
		sqlBuf.append("SELECT        			   				         	 \n");
		sqlBuf.append("       log.command,           				      	 \n");
		sqlBuf.append("       log.requestTime,           			      	 \n");
		sqlBuf.append("       log.state,                 			      	 \n");
		sqlBuf.append("       log.mcuId,               			          	 \n");
		sqlBuf.append("       log.trId                 			          	 \n");
		sqlBuf.append("FROM   ASYNC_COMMAND_LOG log     		          	 \n");
		sqlBuf.append("WHERE  log.mcuId = :modemSerial        			  	 \n");
		sqlBuf.append("AND   log.requestTime BETWEEN :startDate AND :endDate \n");
		sqlBuf.append("ORDER BY requestTime DESC						  	 \n");
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("modemSerial", modemSerial);
		query.setString("startDate", startDate);
        query.setString("endDate", endDate);
		
		if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
		
		return query.list();
	}
	
	public List<Map<String, Object>> getCommandLogListTotalCount(Map<String, Object> condition) throws Exception{
		List<Map<String, Object>> result;
		String modemSerial = condition.get("deviceSerial").toString();
        String startDate = StringUtil.nullToBlank(condition.get("startDate"));
        String endDate = StringUtil.nullToBlank(condition.get("endDate")) +"235959";
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("\nSELECT COUNT(*) AS cnt ");
		sqlBuf.append("FROM   ASYNC_COMMAND_LOG log             \n");
		sqlBuf.append("WHERE  log.mcuId = :modemSerial          \n");
        sqlBuf.append("AND   log.requestTime BETWEEN :startDate AND :endDate \n");
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setString("modemSerial", modemSerial);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", ((Number) query.uniqueResult()).intValue());
		result = new ArrayList<Map<String, Object>>();
		result.add(map);
		return result;
	}

}
