package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.VerifyPrepaymentCustomerWSDao;
import com.aimir.model.prepayment.VerifyPrepaymentCustomerWS;

@Repository(value = "verifyPrepaymentCustomerWSDao")
public class VerifyPrepaymentCustomerWSDaoImpl 
				extends AbstractHibernateGenericDao<VerifyPrepaymentCustomerWS, Integer> 
				implements VerifyPrepaymentCustomerWSDao {

		@Autowired
		protected VerifyPrepaymentCustomerWSDaoImpl(SessionFactory sessionFactory) {
			super(VerifyPrepaymentCustomerWS.class);
			super.setSessionFactory(sessionFactory);
		}

	}
