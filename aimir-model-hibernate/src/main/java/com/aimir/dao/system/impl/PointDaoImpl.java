package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PointDao;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.Point;

@Repository(value = "pointDao")
public class PointDaoImpl extends AbstractHibernateGenericDao<Point, Integer> implements PointDao {

	@Autowired
	protected PointDaoImpl(SessionFactory sessionFactory) {
		super(Point.class);
		super.setSessionFactory(sessionFactory);
	}
	
	
}
