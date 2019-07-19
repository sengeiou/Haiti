/**
 * GetBalanceWSGetInfoDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.GetBalanceWSGetInfoDao;
import com.aimir.model.prepayment.GetBalanceWSGetInfo;

/**
 * GetBalanceWSGetInfoDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   현재 잔액 정보 요청 모델 DaoImpl
 *
 */
@Repository(value = "getBalanceWSGetInfoDao")
public class GetBalanceWSGetInfoDaoImpl 
			extends AbstractHibernateGenericDao<GetBalanceWSGetInfo, Integer> 
			implements GetBalanceWSGetInfoDao {

	@Autowired
	protected GetBalanceWSGetInfoDaoImpl(SessionFactory sessionFactory) {
		super(GetBalanceWSGetInfo.class);
		super.setSessionFactory(sessionFactory);
	}

}
