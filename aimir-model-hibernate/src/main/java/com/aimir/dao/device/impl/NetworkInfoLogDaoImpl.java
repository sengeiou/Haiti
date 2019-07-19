package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.NetworkInfoLogDao;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.NetworkInfoLogPk;

@Repository(value = "nlogDao_HN")
public class NetworkInfoLogDaoImpl extends AbstractHibernateGenericDao<NetworkInfoLog, NetworkInfoLogPk> implements NetworkInfoLogDao {

	Log log = LogFactory.getLog(NetworkInfoLogDaoImpl.class);
	
	@Autowired
	public NetworkInfoLogDaoImpl(SessionFactory sessionFactory) {
		super(NetworkInfoLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public NetworkInfoLog[] list(String command, String startDate, String endDate, int pageNo, int rowCnt)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
