package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.model.mvm.MonthWM;
import com.aimir.util.Condition;

@Repository(value = "monthwmDao")
public class MonthWMDaoImpl extends AbstractJpaDao<MonthWM, Integer> implements MonthWMDao {

    Log logger = LogFactory.getLog(MonthWMDaoImpl.class);
    
    public MonthWMDaoImpl() {
		super(MonthWM.class);
	}

    @Override
    public List<Object> getMonthWMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthWM> getMonthWMsByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthWMsCountByListCondition(Set<Condition> set) {
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
    public List<MonthWM> getMonthCustomerBillingGridData(
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
    public List<Object> getConsumptionWmCo2MonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2MonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2MonthMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2MonthMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2MonthSearchDayTypeTotal(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getContractBillingChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonthWM getMonthWM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonthWM getMonthWMbySupplierId(Map<String, Object> params) {
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
    public List<Object> getConsumptionWmCo2MonthSearchDayTypeTotal2(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmValueSum(int supplierId,
            String startDate, String endDate, int startValue, int endValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthWMsCount(Set<Condition> conditions, String div) {
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
    public Class<MonthWM> getPersistentClass() {
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