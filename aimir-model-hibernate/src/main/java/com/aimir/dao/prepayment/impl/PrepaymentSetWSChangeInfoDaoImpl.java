/**
 * PrepaymentSetWSChangeInfoDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeInfoDao;
import com.aimir.model.prepayment.PrepaymentSetWSChangeInfo;

/**
 * PrepaymentSetWSChangeInfoDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 8. 4.   v1.0        문동규   Change Energy Utility Information 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSChangeInfoDao")
public class PrepaymentSetWSChangeInfoDaoImpl 
			extends AbstractHibernateGenericDao<PrepaymentSetWSChangeInfo, Integer> 
			implements PrepaymentSetWSChangeInfoDao {

	@Autowired
	protected PrepaymentSetWSChangeInfoDaoImpl(SessionFactory sessionFactory) {
		super(PrepaymentSetWSChangeInfo.class);
		super.setSessionFactory(sessionFactory);
	}

}
