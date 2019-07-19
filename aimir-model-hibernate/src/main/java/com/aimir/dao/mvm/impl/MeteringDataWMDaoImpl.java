package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataWMDao;
import com.aimir.model.mvm.MeteringDataWM;

@Repository(value = "meteringdatawmDao")
public class MeteringDataWMDaoImpl extends AbstractHibernateGenericDao<MeteringDataWM, Integer> implements MeteringDataWMDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	@Autowired
	protected MeteringDataWMDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataWM.class);
		super.setSessionFactory(sessionFactory);
	}
}
