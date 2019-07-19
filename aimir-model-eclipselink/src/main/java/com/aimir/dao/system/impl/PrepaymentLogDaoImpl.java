package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.util.Condition;

@Repository(value = "prepaymentlogDao")
public class PrepaymentLogDaoImpl  extends AbstractJpaDao< PrepaymentLog, Long> implements PrepaymentLogDao {

	public PrepaymentLogDaoImpl() {
		super(PrepaymentLog.class);
	}

	@Override
	public List<PrepaymentLog> getPrepaymentLogByListCondition(
			Set<Condition> set) {
		return findByConditions(set);
	}
	@Override
	public List<Object> getPrepaymentLogCountByListCondition(Set<Condition> set) {
		return findTotalCountByConditions(set);
	}

    @Override
    public List<Map<String, Object>> getPrepaymentChargeHistoryList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeInfo(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistory(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistoryByMaxUnderDate(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistoryForCustomer(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistoryByLastTokenDate(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getChargeHistoryList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getMonthlyPaidAmount(Contract contract, String yyyymm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM,
            String tariffName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM,
            String tariffName, List<Integer> locationIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyReceiptLog(String yyyyMM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getRecentPrepaymentLogId(String contractNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyCredit(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getMonthlyUsageByContract(Contract contract, String yyyymm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean checkMonthlyFirstReceipt(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PrepaymentLog getMonthlyPaidData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getAddBalanceList(Integer page, Integer limit,
            String searchDate, String vendorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentLogList(Integer contractId,
            String startDate, String endDate, String vendorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyPaidDataCount(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PrepaymentLog> getMonthlyNotCalculationReceiptLog(String yyyyMM,
            String[] modelName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getNextVal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDoubleSalesList(String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<PrepaymentLog> getPersistentClass() {
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
    public List<PrepaymentLog> getMonthlyConsumptionLogByGeocode(String yyyyMM,
            String tariffName, String geocode) {
        // TODO Auto-generated method stub
        return null;
    }
	
	
}