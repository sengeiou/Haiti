package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataEMDao;
import com.aimir.model.mvm.MeteringDataEM;

@Repository(value = "meteringdataemDao")
public class MeteringDataEMDaoImpl extends AbstractHibernateGenericDao<MeteringDataEM, Integer> implements MeteringDataEMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	@Autowired
	protected MeteringDataEMDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataEM.class);
		super.setSessionFactory(sessionFactory);
	}
}
