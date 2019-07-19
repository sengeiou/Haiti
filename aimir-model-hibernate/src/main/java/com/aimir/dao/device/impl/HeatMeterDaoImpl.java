package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.HeatMeterDao;
import com.aimir.model.device.HeatMeter;

@Repository(value = "heatmeterDao")
public class HeatMeterDaoImpl extends AbstractHibernateGenericDao<HeatMeter, Integer> implements HeatMeterDao {

    Log logger = LogFactory.getLog(HeatMeterDaoImpl.class);
    
	@Autowired
	protected HeatMeterDaoImpl(SessionFactory sessionFactory) {
		super(HeatMeter.class);
		super.setSessionFactory(sessionFactory);
	}
}