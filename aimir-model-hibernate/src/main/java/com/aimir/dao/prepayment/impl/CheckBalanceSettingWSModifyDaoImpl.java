/**
 * CheckBalanceSettingWSModifyDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.CheckBalanceSettingWSModifyDao;
import com.aimir.model.prepayment.CheckBalanceSettingWSModify;

/**
 * CheckBalanceSettingWSModifyDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   잔액 체크 주기 설정 모델 DaoImpl
 *
 */
@Repository(value = "checkBalanceSettingWSModifyDao")
public class CheckBalanceSettingWSModifyDaoImpl 
			extends AbstractHibernateGenericDao<CheckBalanceSettingWSModify, Integer> 
			implements CheckBalanceSettingWSModifyDao {

	@Autowired
	protected CheckBalanceSettingWSModifyDaoImpl(SessionFactory sessionFactory) {
		super(CheckBalanceSettingWSModify.class);
		super.setSessionFactory(sessionFactory);
	}

}
