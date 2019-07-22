package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.DayTMViewDao;
import com.aimir.model.view.DayTMView;
import com.aimir.util.Condition;

@Repository(value="daytmDaoView")
public class DayTMViewDaoImpl extends AbstractHibernateGenericDao<DayTMView, Integer> implements DayTMViewDao {
	private static Log log = LogFactory.getLog(DayTMViewDaoImpl.class);

    @Autowired
    protected DayTMViewDaoImpl(SessionFactory sessionFactory) {
        super(DayTMView.class);
        super.setSessionFactory(sessionFactory);
    }

	@Override
	public List<DayTMView> getDayTMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayTMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}


}
