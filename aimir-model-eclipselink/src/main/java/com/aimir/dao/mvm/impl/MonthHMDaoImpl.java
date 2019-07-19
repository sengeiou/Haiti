package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.model.mvm.MonthHM;
import com.aimir.util.Condition;

@Repository(value = "monthhmDao")
public class MonthHMDaoImpl extends AbstractJpaDao<MonthHM, Integer> implements MonthHMDao {

	private static Log logger = LogFactory.getLog(MonthHMDaoImpl.class);
    
	public MonthHMDaoImpl() {
		super(MonthHM.class);
	}

    @Override
    public List<Object> getMonthHMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthHM> getMonthHMsByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthHMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getMonthBillingChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getMonthBillingGridData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthHM> getMonthCustomerBillingGridData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getElecCustomerBillingGridDataCount(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionTmHmMonitoring(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonthMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonthMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonthSearchDayTypeTotal(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getContractBillingChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonthHM getMonthHM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthToYears() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getEnergySavingReportMonthlyData(String[] years,
            int channel, Integer[] meterIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonthSearchDayTypeTotal2(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(
            Map<String, Object> condition, DateType seasonal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmValueSum(int supplierId,
            String startDate, String endDate, int startValue, int endValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthHMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MonthHM> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
