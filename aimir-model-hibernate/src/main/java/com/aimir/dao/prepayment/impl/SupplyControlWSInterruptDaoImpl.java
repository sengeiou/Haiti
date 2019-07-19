/**
 * SupplyControlWSInterruptDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.SupplyControlWSInterruptDao;
import com.aimir.model.prepayment.SupplyControlWSInterrupt;

/**
 * SupplyControlWSInterruptDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 20.  v1.0        문동규   공급 차단 모델 DaoImpl
 *
 */
@Repository(value = "supplyControlWSInterruptDao")
public class SupplyControlWSInterruptDaoImpl 
			extends AbstractHibernateGenericDao<SupplyControlWSInterrupt, Integer> 
			implements SupplyControlWSInterruptDao {

	@Autowired
	protected SupplyControlWSInterruptDaoImpl(SessionFactory sessionFactory) {
		super(SupplyControlWSInterrupt.class);
		super.setSessionFactory(sessionFactory);
	}

}
