package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.OperationConstraintDao;
import com.aimir.model.device.OperationConstraint;

@Repository(value = "operationconstraintDao")
public class OperationConstraintDaoImpl extends AbstractHibernateGenericDao<OperationConstraint, Integer> implements OperationConstraintDao {

	@Autowired
	protected OperationConstraintDaoImpl(SessionFactory sessionFactory) {
		super(OperationConstraint.class);
		super.setSessionFactory(sessionFactory);
	}
}
