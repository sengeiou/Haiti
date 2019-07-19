package com.aimir.dao.device.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EventAlertAttrDao;
import com.aimir.model.device.EventAlertAttr;

@Repository(value = "eventalertattrDao")
public class EventAlertAttrDaoImpl extends AbstractHibernateGenericDao<EventAlertAttr, Integer> implements EventAlertAttrDao {

	@Autowired
	protected EventAlertAttrDaoImpl(SessionFactory sessionFactory) {
		super(EventAlertAttr.class);
		super.setSessionFactory(sessionFactory);
	}

}
