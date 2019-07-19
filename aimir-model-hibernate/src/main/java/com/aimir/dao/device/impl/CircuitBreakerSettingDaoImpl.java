package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.model.device.CircuitBreakerSetting;


@Repository(value = "circuitbreakersettingDao")
public class CircuitBreakerSettingDaoImpl extends AbstractHibernateGenericDao<CircuitBreakerSetting, Integer> implements CircuitBreakerSettingDao {
	@Autowired
	protected CircuitBreakerSettingDaoImpl(SessionFactory sessionFactory) {
		super(CircuitBreakerSetting.class);
		super.setSessionFactory(sessionFactory);
	}
}	
