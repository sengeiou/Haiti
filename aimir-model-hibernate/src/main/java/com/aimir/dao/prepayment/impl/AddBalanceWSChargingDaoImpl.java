/**
 * AddBalanceWSChargingDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.model.prepayment.AddBalanceWSCharging;

/**
 * AddBalanceWSChargingDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 28   v1.0        김상연   과금 충전 모델 DaoImpl
 * 2011. 8. 12   v1.0        문동규   클래스명 수정
 *
 */
@Repository(value = "addBalanceWSChargingDao")
public class AddBalanceWSChargingDaoImpl 
			extends AbstractHibernateGenericDao<AddBalanceWSCharging, Integer> 
			implements AddBalanceWSChargingDao {

	@Autowired
	protected AddBalanceWSChargingDaoImpl(SessionFactory sessionFactory) {
		super(AddBalanceWSCharging.class);
		super.setSessionFactory(sessionFactory);
	}

}
