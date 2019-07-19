/**
 * ReportParameterDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ReportParameterDao;
import com.aimir.model.system.ReportParameter;

/**
 * ReportParameterDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportParameterDao")
public class ReportParameterDaoImpl extends AbstractHibernateGenericDao<ReportParameter, Integer> implements ReportParameterDao {

	@Autowired
	protected ReportParameterDaoImpl(SessionFactory sessionFactory) {
		super(ReportParameter.class);
		super.setSessionFactory(sessionFactory);
	}

}
