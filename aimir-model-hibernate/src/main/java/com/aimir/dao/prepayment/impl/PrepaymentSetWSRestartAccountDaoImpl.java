/**
 * PrepaymentSetWSRestartAccountDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.PrepaymentSetWSRestartAccountDao;
import com.aimir.model.prepayment.PrepaymentSetWSRestartAccount;

/**
 * PrepaymentSetWSRestartAccountDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Restart Accounting 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSRestartAccountDao")
public class PrepaymentSetWSRestartAccountDaoImpl 
			extends AbstractHibernateGenericDao<PrepaymentSetWSRestartAccount, Integer> 
			implements PrepaymentSetWSRestartAccountDao {

	@Autowired
	protected PrepaymentSetWSRestartAccountDaoImpl(SessionFactory sessionFactory) {
		super(PrepaymentSetWSRestartAccount.class);
		super.setSessionFactory(sessionFactory);
	}

}
