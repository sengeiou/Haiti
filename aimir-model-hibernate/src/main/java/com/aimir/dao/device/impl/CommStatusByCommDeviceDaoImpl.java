package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.CommStatusByCommDeviceDao;
import com.aimir.model.device.CommStatusByCommDevice;
import com.aimir.model.device.CommStatusByCommDevicePk;

@Repository(value = "commStatusByCommDeviceDao")
public class CommStatusByCommDeviceDaoImpl extends AbstractHibernateGenericDao<CommStatusByCommDevice, CommStatusByCommDevicePk> 
					implements CommStatusByCommDeviceDao {
	
	@Autowired
	protected CommStatusByCommDeviceDaoImpl(SessionFactory sessionFactory) {
		super(CommStatusByCommDevice.class);
		super.setSessionFactory(sessionFactory);
	}

}
