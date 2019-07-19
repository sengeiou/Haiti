package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PeakDemandSettingDao;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.util.Condition;

@Repository(value = "peakDemandSettingDao")
public class PeakDemandSettingDaoImpl
	extends AbstractHibernateGenericDao<PeakDemandSetting, Integer> implements PeakDemandSettingDao {

	@Autowired
	protected PeakDemandSettingDaoImpl(SessionFactory sessionFactory) {
		super(PeakDemandSetting.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
}
