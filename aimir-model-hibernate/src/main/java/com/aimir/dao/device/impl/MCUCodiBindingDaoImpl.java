package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUCodiBindingDao;
import com.aimir.model.device.MCUCodiBinding;

@Repository(value = "mcucodibindingDao")
public class MCUCodiBindingDaoImpl extends AbstractHibernateGenericDao<MCUCodiBinding, Long> implements MCUCodiBindingDao {

	@Autowired
	protected MCUCodiBindingDaoImpl(SessionFactory sessionFactory) {
		super(MCUCodiBinding.class);
		super.setSessionFactory(sessionFactory);
	}
}
