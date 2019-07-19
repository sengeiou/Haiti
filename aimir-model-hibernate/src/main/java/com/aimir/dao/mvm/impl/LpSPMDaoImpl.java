package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.model.mvm.LpSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 LP 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "lpSPMDao")
public class LpSPMDaoImpl extends AbstractHibernateGenericDao<LpSPM, Integer> 
	implements LpSPMDao {

	@Autowired
	protected LpSPMDaoImpl(SessionFactory sessionFactory) {
		super(LpSPM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
	
	@Override
	public List<LpSPM> getLpSPMsByListCondition(Set<Condition> set) {
        return findByConditions(set);
    }

	@Override
	public List<Object> getLpSPMsCountByListCondition(Set<Condition> conditions) {
		return findTotalCountByConditions(conditions);
	}
}
