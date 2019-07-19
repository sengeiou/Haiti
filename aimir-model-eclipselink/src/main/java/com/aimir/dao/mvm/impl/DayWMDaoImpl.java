/**
 * DayWMDaoImpl.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DayWM;
import com.aimir.util.Condition;

/**
 * DayWMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 18.   v1.0       김상연         DayWM 조회 조건 (DayWM)
 *
 */
@Repository(value = "daywmDao")
@SuppressWarnings("unchecked")
public class DayWMDaoImpl extends AbstractJpaDao<DayWM, Integer> implements DayWMDao {

	private static Log logger = LogFactory.getLog(DayWMDaoImpl.class);
    
	public DayWMDaoImpl() {
		super(DayWM.class);
	}

    @Override
    public List<DayWM> getDayWMsByMap(Map<?, ?> map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayWM> getDayWMsByList(List<Map<?, ?>> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayWM> getDayWMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayWMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayWMsSumList(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayWMsCountByListCondition(Set<Condition> set) {
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
    public List<DayWM> getDayCustomerBillingGridData(
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
    public List<Object> getConsumptionWmCo2DayValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2DayValuesLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2DayMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2DayMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2WeekMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2WeekMonitoringParentId(
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
    public List<Object> getConsumptionWmCo2MonitoringSumMinMaxLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionWmCo2MonitoringSumMinMaxPrentId(
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
    public List<Object> getConsumptionWmCo2SearchDayTypeTotal(
            Map<String, Object> conditionDay) {
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
    public DayWM getDayWM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DayWM getDayWMbySupplierId(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContractIds(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayWMsByNoSended(String date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDaySumValueByYYYYMM(DayPk daypk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayWM> getDayWMs(DayWM dayWM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayWMsAvg(DayWM dayWM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayWMsUsageAvg(DayWM dayWM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayWMsUsageMonthToDate(DayWM dayWM, String startDay,
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
    public List<Object> getDayWMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DayWM> getPersistentClass() {
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
