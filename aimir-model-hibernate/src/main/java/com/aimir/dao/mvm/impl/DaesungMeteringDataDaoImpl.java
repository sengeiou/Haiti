package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.DaesungMeteringDataDao;
import com.aimir.model.mvm.DaesungMeteringData;

@Repository(value = "daesungMeteringdataDao")
public class DaesungMeteringDataDaoImpl extends
		AbstractHibernateGenericDao<DaesungMeteringData, Integer> implements
		DaesungMeteringDataDao {

	@SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(DaesungMeteringDataDaoImpl.class);

	@Autowired
	protected DaesungMeteringDataDaoImpl(SessionFactory sessionFactory) {
		super(DaesungMeteringData.class);
		super.setSessionFactory(sessionFactory);
	}
}