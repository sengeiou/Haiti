package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PeakDemandLogDao;
import com.aimir.model.system.PeakDemandLog;
import com.aimir.util.Condition;

/**
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "peakDemandLogDao")
public class PeakDemandLogDaoImpl 
	extends AbstractHibernateGenericDao<PeakDemandLog, Integer> implements PeakDemandLogDao {
	
	@Autowired
	protected PeakDemandLogDaoImpl(SessionFactory sessionFactory) {
		super(PeakDemandLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
}