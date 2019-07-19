package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.SolarPowerMeterDao;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.util.Condition;

@Repository(value = "SolarPowerMeterDao")
public class SolarPowerMeterDaoImpl extends AbstractHibernateGenericDao<SolarPowerMeter, Integer> implements SolarPowerMeterDao {

    Log logger = LogFactory.getLog(SolarPowerMeterDaoImpl.class);
    
	@Autowired
	protected SolarPowerMeterDaoImpl(SessionFactory sessionFactory) {
		super(SolarPowerMeter.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}	
}
