package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.DeviceRegLogDao;
import com.aimir.model.device.DeviceRegLog;

@Repository(value = "devicereglogDao")
public class DeviceRegLogDaoImpl extends AbstractHibernateGenericDao<DeviceRegLog, Long> implements DeviceRegLogDao {

	@Autowired
	protected DeviceRegLogDaoImpl(SessionFactory sessionFactory) {
		super(DeviceRegLog.class);
		super.setSessionFactory(sessionFactory);
	}

}
