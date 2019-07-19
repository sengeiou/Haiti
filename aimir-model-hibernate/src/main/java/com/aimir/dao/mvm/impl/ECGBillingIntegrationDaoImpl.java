package com.aimir.dao.mvm.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.ECGBillingIntegrationDao;
import com.aimir.model.mvm.ECGBillingIntegration;

@Repository(value = "ecgBillingIntegrationDao")
public class ECGBillingIntegrationDaoImpl extends AbstractHibernateGenericDao<ECGBillingIntegration, Integer>
implements ECGBillingIntegrationDao {

	@Autowired
	protected ECGBillingIntegrationDaoImpl(SessionFactory sessionFactory) {
		super(ECGBillingIntegration.class);
		super.setSessionFactory(sessionFactory);
	}
}
