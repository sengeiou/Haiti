package com.aimir.dao.mvm.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeasurementHistoryDao;
import com.aimir.model.device.MeasurementHistory;

@Repository(value="measurementhistoryDao")
public class MeasurementHistoryDaoImpl extends AbstractHibernateGenericDao<MeasurementHistory, Long> implements MeasurementHistoryDao{

	@Autowired
	protected MeasurementHistoryDaoImpl(SessionFactory sessionFactory) {
		super(MeasurementHistory.class);
		super.setSessionFactory(sessionFactory);
	}
}
