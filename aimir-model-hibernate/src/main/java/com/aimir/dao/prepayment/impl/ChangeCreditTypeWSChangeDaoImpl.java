/**
 * ChangeCreditTypeWSChangeDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.ChangeCreditTypeWSChangeDao;
import com.aimir.model.prepayment.ChangeCreditTypeWSChange;

/**
 * ChangeCreditTypeWSChangeDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 21.  v1.0        문동규   고객의 과금방식 변경 모델 DaoImpl
 *
 */
@Repository(value = "changeCreditTypeWSChangeDao")
public class ChangeCreditTypeWSChangeDaoImpl 
			extends AbstractHibernateGenericDao<ChangeCreditTypeWSChange, Integer> 
			implements ChangeCreditTypeWSChangeDao {

	@Autowired
	protected ChangeCreditTypeWSChangeDaoImpl(SessionFactory sessionFactory) {
		super(ChangeCreditTypeWSChange.class);
		super.setSessionFactory(sessionFactory);
	}

}
