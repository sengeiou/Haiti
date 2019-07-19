package com.aimir.dao.device.impl;


import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.HeadendDao;
import com.aimir.model.device.Headend;
import com.aimir.model.device.HeadendCtrl;

@Repository(value="headendDao")
public class HeadendDaoImpl extends AbstractHibernateGenericDao<Headend, Integer> implements HeadendDao {
	
	@Autowired
	protected HeadendDaoImpl(SessionFactory sessionFactory) {
		super(Headend.class);
		super.setSessionFactory(sessionFactory);
	}

	public List<Headend> getLastData() {
		Criteria criteria  = getSession().createCriteria(Headend.class);
		criteria.addOrder(Order.desc("writeDate"));
		criteria.setMaxResults(1);
		return criteria.list();
	}
	
}
