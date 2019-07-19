package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EndDeviceLogDao;
import com.aimir.model.device.EndDeviceLog;

@Repository(value = "enddeviceLogDao")
public class EndDeviceLogDaoImpl extends AbstractHibernateGenericDao<EndDeviceLog, Long> implements EndDeviceLogDao {

	@Autowired
	protected EndDeviceLogDaoImpl(SessionFactory sessionFactory) {
		super(EndDeviceLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
    public List<EndDeviceLog> getEndDeviceLogByEndDeviceId(List<Integer> endDeviceId) {
		
		if(endDeviceId==null||endDeviceId.size()<1){
			endDeviceId = new ArrayList<Integer>();
			endDeviceId.add(-1);
		}
		
		Criteria criteria = getSession().createCriteria(EndDeviceLog.class);
		criteria.add(Restrictions.in("enddevice.id", endDeviceId));
		criteria.addOrder(Order.desc("writeDatetime")); 
        List<EndDeviceLog> result = (List<EndDeviceLog>) criteria.list();
		return result;
	}

	@Override
	public long getTotalSize(List<Integer> location) {
		Criteria criteria = getSession().createCriteria(EndDeviceLog.class);
		if(location != null) {
			criteria.add(Restrictions.in("location.id", location));
		}
		return (Long) criteria.setProjection(Projections.count("id")).uniqueResult();
	}
	
	
	// 2012. 06.05 javarouka
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<EndDeviceLog> getEndDeviceLogByLocationId(
			List<Integer> locationId, int start, int limit) {
		Criteria criteria = getSession().createCriteria(EndDeviceLog.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(start);
		criteria.setMaxResults(limit);
		
		criteria.add(Restrictions.in("location.id", locationId));
		criteria.addOrder(Order.desc("writeDatetime")); 
		
		List<EndDeviceLog> result = (List<EndDeviceLog>) criteria.list();
		return result;
	}
	
	// 2012. 06.05 javarouka
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<EndDeviceLog> getEndDeviceLogs(int start, int limit) {
		Criteria criteria = getSession().createCriteria(EndDeviceLog.class);
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.desc("writeDatetime"));
		criteria.setFirstResult(start);
		criteria.setMaxResults(limit);
		List<EndDeviceLog> result = (List<EndDeviceLog>) criteria.list();
		return result;
	}
	
}