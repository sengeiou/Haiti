/**
 * EmergencyCreditWSStartDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.EmergencyCreditWSStartDao;
import com.aimir.model.prepayment.EmergencyCreditWSStart;

/**
 * EmergencyCreditWSStartDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 25.  v1.0        문동규   Emergency Credit 모델 DaoImpl
 *
 */
@Repository(value = "emergencyCreditWSStartDao")
public class EmergencyCreditWSStartDaoImpl 
			extends AbstractHibernateGenericDao<EmergencyCreditWSStart, Integer> 
			implements EmergencyCreditWSStartDao {

	@Autowired
	protected EmergencyCreditWSStartDaoImpl(SessionFactory sessionFactory) {
		super(EmergencyCreditWSStart.class);
		super.setSessionFactory(sessionFactory);
	}

}
