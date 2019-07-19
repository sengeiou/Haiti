/**
 * PrepaymentChangeTariffWSChangeTariffDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.PrepaymentChangeTariffWSChangeTariffDao;
import com.aimir.model.prepayment.PrepaymentChangeTariffWSChangeTariff;

/**
 * PrepaymentChangeTariffWSChangeTariffDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 28.  v1.0        문동규   선불 요금 변경 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentChangeTariffWSChangeTariffDao")
public class PrepaymentChangeTariffWSChangeTariffDaoImpl 
			extends AbstractHibernateGenericDao<PrepaymentChangeTariffWSChangeTariff, Integer> 
			implements PrepaymentChangeTariffWSChangeTariffDao {

	@Autowired
	protected PrepaymentChangeTariffWSChangeTariffDaoImpl(SessionFactory sessionFactory) {
		super(PrepaymentChangeTariffWSChangeTariff.class);
		super.setSessionFactory(sessionFactory);
	}

}
