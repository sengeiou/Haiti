package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.MeterConfigDao;
import com.aimir.model.system.MeterConfig;

@Repository(value="meterconfigDao")
public class MeterConfigDaoImpl extends AbstractHibernateGenericDao<MeterConfig, Integer> implements MeterConfigDao{

	@Autowired
	protected MeterConfigDaoImpl(SessionFactory sessionFactory) {
		super(MeterConfig.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public MeterConfig getDeviceConfig(Integer configId) {
		Query query = getSession().createQuery("from MeterConfig c where c.deviceconfig.id = :configId");
		query.setInteger("configId", configId);
		
		List<MeterConfig> list = query.list();
				
		MeterConfig meterconfig = new MeterConfig();
		if (list.size() > 0) {
			meterconfig = list.get(0);
		}
		return meterconfig;
		
		/*List<MeterConfig> list = (List<MeterConfig>)getHibernateTemplate().find("from MeterConfig c where c.deviceconfig.id = ?", configId);
		MeterConfig meterconfig = new MeterConfig();
		if (list.size() > 0) {
			meterconfig = list.get(0);
		}
		return meterconfig;*/
	}
	
	@SuppressWarnings("unchecked")
	public Integer getMeterConfigId(){
		
		Criteria criteria =  getSession().createCriteria(MeterConfig.class);
		criteria.setProjection(Projections.max("id"));
		Integer configId = (Integer) criteria.list().get(0);
		
		return configId;
	}

}
