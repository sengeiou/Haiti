package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.model.system.MonthlyBillingLogPk;

public interface MonthlyBillingLogDao extends GenericDao<MonthlyBillingLog, MonthlyBillingLogPk> {
	
	/*
	 * 월정산시 해당 미터의 이전 마지막 정산 내용을 가져오기 위한 용도
	 */
	public MonthlyBillingLog getLastMonthlyBillingLog(Integer contractId, String mdevId, String yyyymm);
	
	/*
	 * 특정 미터의 월사용량 및 월사용금액을 업데이트 하기 위한 용도
	 */
	public int updateMonthlyUsageInfo(int contractId, String mdevId, String yyyymmdd, double monthlyConsumption, double monthlyUsageBill, double activeEnergyImport, double activeEnergyExport);
}
