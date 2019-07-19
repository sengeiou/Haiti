package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DemandResponseEventLogDao;
import com.aimir.model.system.DemandResponseEventLog;

@Repository(value = "demandResponseEventLogDao")
public class DemandResponseEventLogDaoImpl extends AbstractHibernateGenericDao<DemandResponseEventLog, Integer> implements DemandResponseEventLogDao {
	
	@Autowired
	protected DemandResponseEventLogDaoImpl(SessionFactory sessionFactory) {
		super(DemandResponseEventLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDemandResponseHistory(String userId, String contractNumber, int page, int limit, String fromDate, String toDate){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT o.id AS id ")
		.append(", 1 AS div ")
		.append(", o.error_reason AS errorReason  ")
		.append(", o.target_name AS targetName ")
		.append(", o.yyyymmddhhmmss AS runDate ")
		.append(", o.status AS status ")
		.append(", o.description AS description ")	
		.append(", o.contractnumber AS contractnumber ")
		.append(", o.operation_Command_Code AS operationCommandCode ")	
		.append(", '' AS operationName ")
		.append(", '' AS programName ")
		.append(", '' AS notificationTime ")
		.append(", '' AS endTime ")
		.append(", '' AS drname ")
		.append(" FROM operation_log o  ")
		.append(" WHERE o.user_id = :userId ")
		.append(" AND o.contractnumber = :contractNumber ")
		.append(" AND o.yyyymmdd >= :fromDate ")					
		.append(" AND o.yyyymmdd <= :toDate ")
		.append(" UNION ALL  ")
		.append(" SELECT d.id AS id ")
		.append(", 2 AS div ")
		.append(", '' AS errorReason  ")
		.append(", '' AS targetName ")
		.append(", d.start_time AS runDate ")
		.append(", d.optout_status AS status ")
		.append(", '' AS description ")	
		.append(", '' AS contractnumber ")
		.append(", 0 AS operationCommandCode ")
		.append(", event_identifier AS operationName ")
		.append(", d.program_name AS programName ")
		.append(", d.notification_time AS notificationTime ")
		.append(", d.end_time AS endTime ")
		.append(", d.operation_mode_value AS drname ")
		.append(" FROM demand_response_event_log d  ")
		.append(" WHERE d.dras_client_id = 'nuritelecom.hems_001' ")
//		.append(" AND o.contractnumber = :contractNumber ")
		.append(" AND d.yyyymmdd >= :fromDate ")					
		.append(" AND d.yyyymmdd <= :toDate ")
		.append(" ORDER BY runDate DESC ");
		
		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setString("userId", userId);
		query.setString("contractNumber", contractNumber);
		query.setString("fromDate", fromDate);
		query.setString("toDate", toDate);
		query.setFirstResult((page - 1) * limit);
		query.setMaxResults(limit);
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	public String getDemandResponseHistoryTotalCount(String userId, String contractNumber, String fromDate, String toDate){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT count(o.id) ")
		.append(" FROM operation_log o  ")
		.append(" WHERE o.user_id = :userId ")
		.append(" AND o.contractnumber = :contractNumber ")
		.append(" AND o.yyyymmdd >= :fromDate ")					
		.append(" AND o.yyyymmdd <= :toDate ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setString("userId", userId);
		query.setString("contractNumber", contractNumber);
		query.setString("fromDate", fromDate);
		query.setString("toDate", toDate);

		int cnt1 = Integer.parseInt(query.uniqueResult().toString());

		sbSql.setLength(0);
		
		sbSql.append(" SELECT count(d.id) ")
		.append(" FROM demand_response_event_log d  ")
		.append(" WHERE d.dras_client_id = 'nuritelecom.hems_001' ")
		.append(" AND d.yyyymmdd >= :fromDate ")					
		.append(" AND d.yyyymmdd <= :toDate ");

		SQLQuery _query = getSession().createSQLQuery(sbSql.toString());
		_query.setString("fromDate", fromDate);
		_query.setString("toDate", toDate);
		
		int cnt2 = Integer.parseInt(_query.uniqueResult().toString());
		return Integer.toString(cnt1+cnt2);
	}
	
	@SuppressWarnings("unchecked")
	public List<DemandResponseEventLog> getDemandResponseEventLogs (DemandResponseEventLog drEventLog) {
		Criteria criteria = getSession().createCriteria(DemandResponseEventLog.class);
		
		if(drEventLog != null){
		
			if (drEventLog.getId() != null) {	
				criteria.add(Restrictions.eq("id", drEventLog.getId()));
			}
			
			if(drEventLog.getDrasClientId() != null) {
				criteria.add(Restrictions.eq("drasClientId", drEventLog.getDrasClientId()));
			}

			if(drEventLog.getOptOutStatus() != null) {
				criteria.add(Restrictions.eq("optOutStatus", drEventLog.getOptOutStatus()));
			}
		}

		criteria.addOrder(Order.asc("startTime"));

		return criteria.list();
	}

	/**
	 * @deprecated
	 */
	public void getDrCustomerList(){
//	public List<Map<String, Object>> getDrCustomerList(){		

//		StringBuffer sbSql = new StringBuffer();
//		sbSql.append(" SELECT cr. ")
//		.append(" FROM CUSTOMER cr, CONTRACT ct  ")
//		.append(" WHERE cr.demandResponse = 1 ")
//		.append(" AND cr.id = ct.customerId ")
//		.append(" AND o.yyyymmdd >= :fromDate ")					
//		.append(" AND o.yyyymmdd <= :toDate ");
//
//		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
//		query.setString("userId", userId);
//		query.setString("contractNumber", contractNumber);
//		query.setString("fromDate", fromDate);
//		query.setString("toDate", toDate);
//
//		return return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
}
