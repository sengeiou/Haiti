package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.PowerQualityStatusDao;
import com.aimir.model.mvm.PowerQualityStatus;
import com.aimir.model.mvm.PowerQualityStatusPk;

@Repository(value = "powerqualitystatusDao")
public class PowerQualityStatusDaoImpl extends AbstractHibernateGenericDao<PowerQualityStatus, PowerQualityStatusPk> implements PowerQualityStatusDao {

	private static Log logger = LogFactory.getLog(PowerQualityStatusDaoImpl.class);
    
	@Autowired
	protected PowerQualityStatusDaoImpl(SessionFactory sessionFactory) {
		super(PowerQualityStatus.class);
		super.setSessionFactory(sessionFactory);
	}

}