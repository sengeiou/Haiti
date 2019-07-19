/**
 * SupplyControlWSRearmDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.SupplyControlWSRearmDao;
import com.aimir.model.prepayment.SupplyControlWSRearm;

/**
 * SupplyControlWSRearmDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 20.  v1.0        문동규   공급 재개 모델 DaoImpl
 *
 */
@Repository(value = "supplyControlWSRearmDao")
public class SupplyControlWSRearmDaoImpl 
			extends AbstractHibernateGenericDao<SupplyControlWSRearm, Integer> 
			implements SupplyControlWSRearmDao {

	@Autowired
	protected SupplyControlWSRearmDaoImpl(SessionFactory sessionFactory) {
		super(SupplyControlWSRearm.class);
		super.setSessionFactory(sessionFactory);
	}

}
