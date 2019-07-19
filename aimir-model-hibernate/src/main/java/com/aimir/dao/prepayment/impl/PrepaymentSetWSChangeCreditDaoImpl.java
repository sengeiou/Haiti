/**
 * PrepaymentSetWSChangeCreditDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeCreditDao;
import com.aimir.model.prepayment.PrepaymentSetWSChangeCredit;

/**
 * PrepaymentSetWSChangeCreditDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Change Credit available 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSChangeCreditDao")
public class PrepaymentSetWSChangeCreditDaoImpl 
			extends AbstractHibernateGenericDao<PrepaymentSetWSChangeCredit, Integer> 
			implements PrepaymentSetWSChangeCreditDao {

	@Autowired
	protected PrepaymentSetWSChangeCreditDaoImpl(SessionFactory sessionFactory) {
		super(PrepaymentSetWSChangeCredit.class);
		super.setSessionFactory(sessionFactory);
	}

}
