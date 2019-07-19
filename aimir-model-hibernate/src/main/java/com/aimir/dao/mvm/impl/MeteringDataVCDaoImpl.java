package com.aimir.dao.mvm.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataVCDao;
import com.aimir.model.mvm.MeteringDataVC;

@Repository(value = "meteringdatavcDao")
public class MeteringDataVCDaoImpl extends AbstractHibernateGenericDao<MeteringDataVC, Integer> implements MeteringDataVCDao {

	private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);
    
	@Autowired
	protected MeteringDataVCDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataVC.class);
		super.setSessionFactory(sessionFactory);
	}
}
