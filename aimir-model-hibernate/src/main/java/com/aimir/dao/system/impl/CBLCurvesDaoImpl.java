package com.aimir.dao.system.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.CBLCurvesDao;
import com.aimir.model.system.CBLCurves;


@Repository(value="cblcurvesDao")
public class CBLCurvesDaoImpl extends AbstractHibernateGenericDao<CBLCurves, Integer> implements CBLCurvesDao{

	@Autowired
	protected CBLCurvesDaoImpl(SessionFactory sessionFactory) {
		super(CBLCurves.class);
		super.setSessionFactory(sessionFactory);
	}
}
