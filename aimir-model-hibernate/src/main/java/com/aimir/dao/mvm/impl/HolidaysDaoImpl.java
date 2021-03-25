package com.aimir.dao.mvm.impl;


import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.HolidaysDao;
import com.aimir.model.mvm.Holidays;

@Repository(value = "holidaysDao")
public class HolidaysDaoImpl extends AbstractHibernateGenericDao<Holidays, Integer> implements HolidaysDao {

	@Autowired
	protected HolidaysDaoImpl(SessionFactory sessionFactory) {
		super(Holidays.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public Holidays getHoliday(int mm, int dd) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append("SELECT * FROM holidays h WHERE h.MONTH = :mm")
			.append(" AND h.DAY = :dd");
		
		Query query = getSession().createNativeQuery(sbQuery.toString()).addEntity(Holidays.class);
		query.setParameter("mm", mm);
		query.setParameter("dd", dd);
		
		return (Holidays) query.getSingleResult();
	}
}
