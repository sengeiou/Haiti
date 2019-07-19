package com.aimir.dao.integration.impl;

import com.aimir.model.integration.WSMeterConfigOBIS;
import com.aimir.model.integration.WSMeterConfigUser;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.integration.WSMeterConfigUserDao;

@Repository(value = "wsmeterconfiguserDao")
public class WSMeterConfigUserDaoImpl extends AbstractHibernateGenericDao<WSMeterConfigUser, Long> implements WSMeterConfigUserDao {

	private static Log log = LogFactory.getLog(WSMeterConfigUser.class);
	
	@Autowired
	protected WSMeterConfigUserDaoImpl(SessionFactory sessionFactory) {
		super(WSMeterConfigUser.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public WSMeterConfigUser get(String userId) {
		
        StringBuffer query = new StringBuffer();

        query.append(" SELECT   u ");
        query.append(" FROM     WSMeterConfigUser u ");
        query.append(" WHERE    u.userId = :userid ");

        Query _query = getSession().createQuery(query.toString());
        _query.setParameter("userid",  userId);
        
		if (_query.list().size() == 0) {
			return null;
		}        
        
        return (WSMeterConfigUser) _query.list().get(0);
    }

	@Override
	public String getPassword(String userId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT u.password ");
		hqlBuf.append(" FROM WSMeterConfigUser u");
		hqlBuf.append(" WHERE u.userId = :userId"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("userId", userId);
		
		List result = query.list();
		
		if (result.size() == 0) {
			return null;
		}
		else {
			if (result.get(0) != null) {
				return result.get(0).toString();
			}
			else {
				return null;
			}
		}		
	}
}