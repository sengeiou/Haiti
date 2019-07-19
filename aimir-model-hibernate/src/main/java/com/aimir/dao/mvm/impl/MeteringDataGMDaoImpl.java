package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataGMDao;
import com.aimir.model.mvm.MeteringDataGM;

@Repository(value = "meteringdatagmDao")
public class MeteringDataGMDaoImpl extends AbstractHibernateGenericDao<MeteringDataGM, Integer> implements MeteringDataGMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	@Autowired
	protected MeteringDataGMDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataGM.class);
		super.setSessionFactory(sessionFactory);
	}
}
