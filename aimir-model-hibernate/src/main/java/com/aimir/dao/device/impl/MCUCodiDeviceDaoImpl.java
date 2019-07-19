package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUCodiDeviceDao;
import com.aimir.model.device.MCUCodiDevice;

@Repository(value = "mcucodideviceDao")
public class MCUCodiDeviceDaoImpl extends AbstractHibernateGenericDao<MCUCodiDevice, Long> implements MCUCodiDeviceDao {

	@Autowired
	protected MCUCodiDeviceDaoImpl(SessionFactory sessionFactory) {
		super(MCUCodiDevice.class);
		super.setSessionFactory(sessionFactory);
	}
}
