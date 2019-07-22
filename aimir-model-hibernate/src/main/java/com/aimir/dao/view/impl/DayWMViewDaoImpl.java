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
import com.aimir.dao.view.DayWMViewDao;
import com.aimir.model.view.DayWMView;
import com.aimir.util.Condition;

@Repository(value="daywmDaoView")
public class DayWMViewDaoImpl extends AbstractHibernateGenericDao<DayWMView, Integer> implements DayWMViewDao {
	private static Log log = LogFactory.getLog(DayWMViewDaoImpl.class);

    @Autowired
    protected DayWMViewDaoImpl(SessionFactory sessionFactory) {
        super(DayWMView.class);
        super.setSessionFactory(sessionFactory);
    }

	@Override
	public List<DayWMView> getDayWMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayWMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public DayWMView getDayWMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
    

}
