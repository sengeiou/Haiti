/**
 * DayHMDaoImpl.java Copyright NuriTelecom Limited 2012
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;

/**
 * DayHMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012.7. 03.   v1.0       김미선         DayHM 조회 조건 (DayHM)
 *
 */
@Repository(value = "dayhmDao")
@SuppressWarnings("unchecked")
public class DayHMDaoImpl extends AbstractJpaDao<DayHM, Integer> implements DayHMDao {

	private static Log logger = LogFactory.getLog(DayHMDaoImpl.class);
    
	public DayHMDaoImpl() {
		super(DayHM.class);
	}

    @Override
    public List<DayHM> getDayHMsByMap(Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHM> getDayHMsByList(List<Map> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHM> getDayHMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsSumList(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTotalGroupByListCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object[]> getDayBillingChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHM> getDayCustomerBillingGridData(
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
    public List<Object> getRootLocationId(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionMonitoring(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2DayValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2DayMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2DayMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2WeekMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2WeekMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionTmHmWeekMonitoring(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonitoringSumMinMaxLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2MonitoringSumMinMaxPrentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCompareFacilityDayData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionHmCo2SearchDayTypeTotal(
            Map<String, Object> conditionDay) {
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
    public DayHM getDayHM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContractIds(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsByNoSended(String date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDaySumValueByYYYYMM(DayPk daypk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayHM> getDayHMs(DayHM meteringDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsAvg(DayHM dayHM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayHMsUsageAvg(DayHM dayHM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayHMsUsageMonthToDate(DayHM dayHM, String startDay,
            String endDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getLast(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(
            Map<String, Object> condition, DateType weekly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayHMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DayHM> getPersistentClass() {
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
