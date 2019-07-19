package com.aimir.dao.integration.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.integration.WSMeterConfigLogDao;
import com.aimir.model.integration.WSMeterConfigLog;
import com.aimir.model.integration.WSMeterConfigLogPk;

@Repository(value = "wsmeterconfiglogDao")
public class WSMeterConfigLogDaoImpl extends AbstractHibernateGenericDao<WSMeterConfigLog, WSMeterConfigLogPk> implements WSMeterConfigLogDao {

	@Autowired
	protected WSMeterConfigLogDaoImpl(SessionFactory sessionFactory) {
		super(WSMeterConfigLog.class);
		super.setSessionFactory(sessionFactory);
	}


	public Integer getStatusByTrId(String deviceId, String trId)
			throws Exception {
		Criteria criteria = getSession().createCriteria(WSMeterConfigLog.class);
		
		if (deviceId != null && !"".equals(deviceId)) {
		
			criteria.add(Restrictions.eq("deviceId", deviceId));
		}
		
		criteria.add(Restrictions.eq("trId", trId));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("state").as("state")));

		return (Integer)criteria.list().get(0);
	}
	
	public WSMeterConfigLog getByAsyncTrId(String deviceId, String asyncTrId, String command) throws Exception {
		String requestDate = asyncTrId.substring(0, 14);
		String trId = asyncTrId.substring(15);
		return get(requestDate, deviceId,  trId,  command);
	}
	
	public WSMeterConfigLog  get(String requestDate, String deviceId, String trId, String command)
	{
		Criteria criteria = getSession().createCriteria(WSMeterConfigLog.class);
		
		if (deviceId != null && !"".equals(deviceId)) {
		
			criteria.add(Restrictions.eq("deviceId", deviceId));
		}
		
		if (requestDate != null && !"".equals(requestDate)) {
			
			criteria.add(Restrictions.eq("id.requestDate", requestDate));
		}
		
		criteria.add(Restrictions.eq("id.trId", trId));
		
		if (command != null && !"".equals(command)) {
			
			criteria.add(Restrictions.eq("command", command));
		}
		
		
		List<Object> result =  criteria.list();
		if ( result.size() > 0 )
			return (WSMeterConfigLog)result.get(0);
		else
			return null;
	}


	@Override
	public List<WSMeterConfigLog> getLogListByCondition(
			Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
