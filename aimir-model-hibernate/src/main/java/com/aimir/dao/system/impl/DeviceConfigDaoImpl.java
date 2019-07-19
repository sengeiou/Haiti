package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DeviceConfigDao;
import com.aimir.model.system.DeviceConfig;


@Repository(value = "deviceconfigDao")
public class DeviceConfigDaoImpl extends
		AbstractHibernateGenericDao<DeviceConfig, Integer> implements
		DeviceConfigDao {

	@Autowired
	protected DeviceConfigDaoImpl(SessionFactory sessionFactory) {
		super(DeviceConfig.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public DeviceConfig getDeviceConfigByModelId(Integer deviceModelId) {
		
		Criteria criteria = getSession().createCriteria(DeviceConfig.class);
		criteria.add(Restrictions.eq("deviceModel.id", deviceModelId));

		List<DeviceConfig> list =  criteria.list();
		if (list.size() > 0) {
			return (DeviceConfig)list.get(0);
		}
		return null;
	}

}
