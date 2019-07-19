package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataSPMDao;
import com.aimir.model.mvm.MeteringDataSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 검침데이터 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "meteringDataSPMDao")
public class MeteringDataSPMDaoImpl 
	extends AbstractHibernateGenericDao<MeteringDataSPM, Integer>
	implements MeteringDataSPMDao {

	@Autowired
	protected MeteringDataSPMDaoImpl(SessionFactory sessionFactory) {
		super(MeteringDataSPM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}
}
