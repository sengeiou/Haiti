/**
 * GetBalanceWSGetHistoryDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.GetBalanceWSGetHistoryDao;
import com.aimir.model.prepayment.GetBalanceWSGetHistory;

/**
 * GetBalanceWSGetHistoryDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   잔액 충전 내역 조회 모델 DaoImpl
 *
 */
@Repository(value = "getBalanceWSGetHistoryDao")
public class GetBalanceWSGetHistoryDaoImpl 
			extends AbstractHibernateGenericDao<GetBalanceWSGetHistory, Integer> 
			implements GetBalanceWSGetHistoryDao {

	@Autowired
	protected GetBalanceWSGetHistoryDaoImpl(SessionFactory sessionFactory) {
		super(GetBalanceWSGetHistory.class);
		super.setSessionFactory(sessionFactory);
	}

}
