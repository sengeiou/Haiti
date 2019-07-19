package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PeakDemandScenarioDao;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.util.Condition;

/**
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "peakDemandScenarioDao")
public class PeakDemandScenarioDaoImpl 
	extends AbstractHibernateGenericDao<PeakDemandScenario, Integer> implements PeakDemandScenarioDao {

	@Autowired
	protected PeakDemandScenarioDaoImpl(SessionFactory sessionFactory) {
		super(PeakDemandScenario.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
}
