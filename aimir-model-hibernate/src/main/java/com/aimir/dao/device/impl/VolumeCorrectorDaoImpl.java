package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.VolumeCorrectorDao;
import com.aimir.model.device.VolumeCorrector;

@Repository(value = "volumecorrectorDao")
public class VolumeCorrectorDaoImpl extends AbstractHibernateGenericDao<VolumeCorrector, Integer> implements VolumeCorrectorDao {

    Log log = LogFactory.getLog(VolumeCorrectorDaoImpl.class);
    
	@Autowired
	protected VolumeCorrectorDaoImpl(SessionFactory sessionFactory) {
		super(VolumeCorrector.class);
		super.setSessionFactory(sessionFactory);
	}
}