/**
 * MonthEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * MonthEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 11.   v1.0       김상연         MonthEM 조회 - 조건(MonthEM)
 * 2011. 5. 11.   v1.1       김상연         MonthEM 합계 조회 - 조건(MonthEM)
 * 2011. 5. 25.   v1.2       김상연         기기별 그리드 조회
 *
 */
@Repository(value = "monthemDao")
@Transactional
public class MonthEMDaoImpl extends
		AbstractJpaDao<MonthEM, Integer> implements MonthEMDao {

	private static Log log = LogFactory.getLog(MonthEMDaoImpl.class);

	public MonthEMDaoImpl() {
		super(MonthEM.class);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEM> getMonthEMsByListCondition(Set<Condition> set) {

		return findByConditions(set);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMonthEMsCountByListCondition(Set<Condition> set) {

		return findTotalCountByConditions(set);
	}

    @Override
    public List<MonthEM> getMonthEMsByCondition(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthEMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
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
    public List<MonthEM> getMonthCustomerBillingGridData(
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
    public List<Object> getConsumptionEmCo2MonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonthMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonthMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonthSearchDayTypeTotal2(
            Map<String, Object> conditionYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCompareFacilityMonthData(
            Map<String, Object> condition) {
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
    public List<Object> getSearchChartData(Set<Condition> conditions,
            int locationId, int endDeviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmValueSum(int supplierId,
            String startDate, String endDate, int startValue, int endValue) {
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
    public List<Object> getEnergySavingReportYearlyData(String[] years,
            int channel, Integer[] meterIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthEM> getMonthEMbySupplierId(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthEM> getMonthEMs(MonthEM monthEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSumMonthEMs(MonthEM monthEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay,
            int contractId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(
            Map<String, Object> condition, DateType dateType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getYearlyUsageTotal(List<String> yyyymm,
            int locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMonthByMinDate(String mdevId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthEM> getMonthlyUsageByContract(Contract contract,
            String yyyymm, String channels) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MonthEM> getMonthEMByMeter(Meter meter, String yyyymm,
            Integer... channels) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMonthlyAccumulatedUsageByMeter(Meter meter,
            String yyyymm, Integer... channels) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMonthEMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void oldLPDelete(String mdsId, String bDate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Class<MonthEM> getPersistentClass() {
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
