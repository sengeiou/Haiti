package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DashboardDao;
import com.aimir.model.system.Dashboard;

@Repository(value = "dashboardDao")
public class DashboardDaoImpl extends AbstractHibernateGenericDao<Dashboard, Integer> implements DashboardDao {

    Log logger = LogFactory.getLog(DashboardDaoImpl.class);

	@Autowired
	protected DashboardDaoImpl(SessionFactory sessionFactory) {
		super(Dashboard.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardsByOperator(Integer operatorId){
		Criteria criteria = getSession().createCriteria(Dashboard.class);
		criteria.add(Restrictions.eq("operator.id", operatorId));
		criteria.addOrder(Order.asc("orderNo"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardsByRole(Integer roleId){
		Criteria criteria = getSession().createCriteria(Dashboard.class);
		criteria.add(Restrictions.eq("role.id", roleId));
		criteria.addOrder(Order.asc("orderNo"));
		return criteria.list();
	}

	public boolean checkDashboardCountByOperator(Integer operatorId) {
		Criteria criteria = getSession().createCriteria(Dashboard.class);
		criteria.add(Restrictions.eq("operator.id", operatorId));

		if (criteria.list().size() > 1) {
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.DashboardDao#getDashboardByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardByName(String name){
		Criteria criteria = getSession().createCriteria(Dashboard.class);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.isNull("role.id"));
		criteria.add(Restrictions.isNull("operator.id"));
		//criteria.addOrder(Order.asc("orderNo"));
		return criteria.list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.DashboardDao#getDashboardByNameOpeatorId(java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardByNameOpeatorId(String name, int operatorId){
		Criteria criteria = getSession().createCriteria(Dashboard.class);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("operator.id", operatorId));
		return criteria.list();
	}
}