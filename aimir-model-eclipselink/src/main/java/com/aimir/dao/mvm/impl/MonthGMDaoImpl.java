package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.model.mvm.MonthGM;
import com.aimir.util.Condition;

@Repository(value = "monthgmDao")
public class MonthGMDaoImpl extends AbstractJpaDao<MonthGM, Integer> implements MonthGMDao {

	private static Log logger = LogFactory.getLog(MonthGMDaoImpl.class);
    
	public MonthGMDaoImpl() {
		super(MonthGM.class);
	}

    @Override
    public List<Object> getMonthGMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthGM> getMonthGMsByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthGMsCountByListCondition(Set<Condition> set) {
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
    public List<MonthGM> getMonthCustomerBillingGridData(
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
    public List<Object> getConsumptionGmCo2MonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2MonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2MonthMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2MonthMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal(
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
    public MonthGM getMonthGM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonthGM getMonthGMbySupplierId(Map<String, Object> params) {
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
    public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal2(
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
    public List<Object> getConsumptionGmValueSum(int supplierId,
            String startDate, String endDate, int startValue, int endValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthGMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MonthGM> getPersistentClass() {
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
