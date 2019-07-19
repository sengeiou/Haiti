package com.aimir.dao.integration.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.integration.WSMeterConfigLogDao;
import com.aimir.model.integration.WSMeterConfigLog;
import com.aimir.model.integration.WSMeterConfigLogPk;
import com.aimir.util.Condition;

@Repository(value = "wsmeterconfiglogDao")
public class WSMeterConfigLogDaoImpl extends AbstractJpaDao<WSMeterConfigLog, WSMeterConfigLogPk> implements WSMeterConfigLogDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(WSMeterConfigLogDaoImpl.class);

	public WSMeterConfigLogDaoImpl() {
		super(WSMeterConfigLog.class);
	}

	@Override
	public Class<WSMeterConfigLog> getPersistentClass() {
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
	public WSMeterConfigLog getByAsyncTrId(String deviceId, String AsyncTrId,
			String command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WSMeterConfigLog> getLogListByCondition(
			Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSMeterConfigLog get(String requestDate, String deviceId,
			String trId, String command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
