package com.aimir.dao.device.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.DeviceDao;
import com.aimir.model.device.Device;
import com.aimir.model.device.Device.DeviceType;

@Repository(value = "deviceDao")
public class DeviceDaoImpl extends AbstractHibernateGenericDao<Device, Long> implements DeviceDao {

	@Autowired
	protected DeviceDaoImpl(SessionFactory sessionFactory) {
		super(Device.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Device> getDevicesByDeviceType(DeviceType deviceType) {
		
		Criteria criteria = getSession().createCriteria(Device.class);
		criteria.add(Restrictions.eq("deviceType", deviceType));

		return criteria.list();
	}
}
