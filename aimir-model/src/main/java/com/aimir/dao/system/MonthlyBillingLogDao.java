package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.model.system.MonthlyBillingLogPk;

public interface MonthlyBillingLogDao extends GenericDao<MonthlyBillingLog, MonthlyBillingLogPk> {	
	public MonthlyBillingLog getLastMonthlyBillingLog(Integer contractId, String mdevId);
}
