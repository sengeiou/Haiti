package com.aimir.dao.integration.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.integration.WSMeterConfigResultDao;
import com.aimir.model.integration.WSMeterConfigResult;
import com.aimir.model.integration.WSMeterConfigResultPk;
import com.aimir.util.Condition;

@Repository(value = "wsmeterconfigresultDao")
public class WSMeterConfigResultDaoImpl extends AbstractJpaDao<WSMeterConfigResult, WSMeterConfigResultPk> implements WSMeterConfigResultDao {

	public WSMeterConfigResultDaoImpl() {
		super(WSMeterConfigResult.class);
	}


	@Override
	public Class<WSMeterConfigResult> getPersistentClass() {
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
	public List<WSMeterConfigResult> getResultsByAsyncTrId(String deviceId,
			String asyncTrId, String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WSMeterConfigResult> getResults(String requestDate,
			String deviceId, String trId, String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addByAsyncTrId(String deviceId, String asyncTrId,
			String resultValue, String command) {
		// TODO Auto-generated method stub

	}


}
