package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataHMDao;
import com.aimir.model.mvm.MeteringDataHM;

@Repository(value = "meteringdatahmDao")
public class MeteringDataHMDaoImpl extends AbstractHibernateGenericDao<MeteringDataHM, Integer> implements MeteringDataHMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	@Autowired
	protected MeteringDataHMDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataHM.class);
		super.setSessionFactory(sessionFactory);
	}
}
