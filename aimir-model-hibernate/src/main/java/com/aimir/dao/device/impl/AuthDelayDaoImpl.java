// INSERT SP-121
package com.aimir.dao.device.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.AuthDelayDao;
import com.aimir.model.device.AuthDelay;

@Repository(value = "authdelayDao")
public class AuthDelayDaoImpl extends AbstractHibernateGenericDao<AuthDelay, Long> implements AuthDelayDao {

	private static Log log = LogFactory.getLog(AuthDelay.class);
	
	@Autowired
	protected AuthDelayDaoImpl(SessionFactory sessionFactory) {
		super(AuthDelay.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public AuthDelay getAuthDelay(String  ipaddress) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT a.id, a.ipaddress, a.errorcnt, a.lastdate ");
		hqlBuf.append("FROM AuthDelay a ");
		hqlBuf.append("WHERE a.ipaddress = :ipaddress");
		//hqlBuf.append("WHERE a.ipaddress = ? ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("ipaddress", ipaddress);
		//query.setParameter(1, ipaddress );

		List result = query.list();	
		
		if (result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			AuthDelay authdelay = new AuthDelay();
			authdelay.setId(Long.parseLong(resultData[0].toString()));
			if (resultData[1] != null)authdelay.setIpAddress(resultData[1].toString());
			if (resultData[2] != null)authdelay.setErrorCnt(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)authdelay.setLastDate(resultData[3].toString());
			return authdelay;
		}	
	}		
	
	public AuthDelay getAuthDelay(String  ipaddress, Integer limitcnt) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT a.id, a.ipaddress, a.errorcnt, a.lastdate ");
		hqlBuf.append("FROM AuthDelay a ");
		hqlBuf.append("WHERE a.ipaddress = :ipaddress");
		hqlBuf.append("AND a.errorcnt >= :limitcnt");
		//hqlBuf.append("WHERE a.ipaddress = ? ");
		//hqlBuf.append("AND a.errorcnt >= ? ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("ipaddress", ipaddress);
		query.setParameter("limitcnt", limitcnt);
		//query.setParameter(1, ipaddress);
		//query.setParameter(2, limitcnt);

		List result = query.list();	
		
		if (result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			AuthDelay authdelay = new AuthDelay();
			authdelay.setId(Long.parseLong(resultData[0].toString()));
			if (resultData[1] != null)authdelay.setIpAddress(resultData[1].toString());
			if (resultData[2] != null)authdelay.setErrorCnt(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)authdelay.setLastDate(resultData[3].toString());
			return authdelay;
		}
	}
}
