package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.MonthlyBillingLogDao;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.model.system.MonthlyBillingLogPk;
import com.aimir.util.Condition;

@Repository(value = "monthlyBillingLogDao")
public class MonthlyBillingLogDaoImpl extends AbstractJpaDao<MonthlyBillingLog, MonthlyBillingLogPk> implements MonthlyBillingLogDao {

	public MonthlyBillingLogDaoImpl() {
		super(MonthlyBillingLog.class);
	}

	@Override
	public Class<MonthlyBillingLog> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonthlyBillingLog getLastMonthlyBillingLog(Integer contractId, String mdevId, String yyyymm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateMonthlyUsageInfo(String mdevId, String yyyymmdd, double monthlyConsumption, double monthlyUsageBill, double activeEnergyImport, double activeEnergyExport) {
		// TODO Auto-generated method stub
		return -1;
	}
	
}
