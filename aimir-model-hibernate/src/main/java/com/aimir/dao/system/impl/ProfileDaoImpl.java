package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ProfileDao;
import com.aimir.model.system.Profile;

@Repository(value="profileDao")
public class ProfileDaoImpl extends AbstractHibernateGenericDao<Profile, Integer> implements ProfileDao {
    
	@Autowired
	protected ProfileDaoImpl(SessionFactory sessionFactory) {
		super(Profile.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Profile> getProfileByUser(Integer userId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT p");
		hqlBuf.append(" FROM Profile p");
		hqlBuf.append(" WHERE p.operator.id = ?");
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter(0, userId);

		return query.list();
	}

	public void deleteByUser(Integer userId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" DELETE");
		hqlBuf.append(" FROM Profile p");
		hqlBuf.append(" WHERE p.operator.id = ?"); 
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter(0, userId);
		query.executeUpdate();
	}

	public boolean checkProfileByUser(Integer userId) {
		if (getProfileByUser(userId).size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	public List<String> getMeterEventProfileByUser(Integer userId) {
	    StringBuffer hqlBuf = new StringBuffer();
	    hqlBuf.append("SELECT p.meterEvent.id ");
	    hqlBuf.append("FROM Profile p ");
	    hqlBuf.append("WHERE p.operator.id = :userId ");
	    hqlBuf.append("AND   p.meterEvent.id IS NOT NULL ");
	    Query query = getSession().createQuery(hqlBuf.toString());
	    query.setParameter("userId", userId);

	    return query.list();
	}

	public int deleteMeterEventProfileByUser(Integer userId) {
	    StringBuilder hqlBuf = new StringBuilder();
	    hqlBuf.append("DELETE ");
	    hqlBuf.append("FROM Profile p ");
	    hqlBuf.append("WHERE p.operator.id = :userId ");
	    hqlBuf.append("AND   p.meterEvent.id IS NOT NULL ");
	    
	    Query query = getSession().createQuery(hqlBuf.toString());
	    query.setInteger("userId", userId);
	    return query.executeUpdate();
	}
}
