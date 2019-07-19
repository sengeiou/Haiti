package com.aimir.dao.integration.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.integration.WSMeterConfigUserDao;
import com.aimir.model.integration.WSMeterConfigUser;
import com.aimir.util.Condition;

@Repository(value = "wsmeterconfiguserDao")
public class WSMeterConfigUserDaoImpl extends AbstractJpaDao<WSMeterConfigUser, Long> implements WSMeterConfigUserDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(WSMeterConfigUserDaoImpl.class);

	public WSMeterConfigUserDaoImpl() {
		super(WSMeterConfigUser.class);
	}

	@Override
	public Class<WSMeterConfigUser> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions,
			String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSMeterConfigUser get(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword(String userId) {
		// TODO Auto-generated method stub
		return null;
	}


}