/**
 * DayGMDaoImpl.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;

/**
 * DayGMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 18.   v1.0       김상연         DayGM 조회 조건 (DayGM)
 *
 */
@Repository(value = "daygmDao")
@SuppressWarnings("unchecked")
public class DayGMDaoImpl extends AbstractJpaDao<DayGM, Integer> implements DayGMDao {

	private static Log logger = LogFactory.getLog(DayGMDaoImpl.class);
    
	public DayGMDaoImpl() {
		super(DayGM.class);
	}

    @Override
    public List<DayGM> getDayGMsByMap(Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayGMsCountByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayGM> getDayGMsByList(List<Map> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayGM> getDayGMsByListCondition(Set<Condition> list) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayGMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayGMsSumList(Set<Condition> conditions) {
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
    public List<DayGM> getDayCustomerBillingGridData(
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
    public List<Object> getConsumptionGmCo2DayValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2DayMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2DayMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2WeekMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2WeekMonitoringParentId(
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
    public List<Object> getConsumptionGmCo2MonitoringSumMinMaxLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionGmCo2MonitoringSumMinMaxPrentId(
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
    public List<Object> getConsumptionGmCo2SearchDayTypeTotal(
            Map<String, Object> conditionDay) {
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
    public DayGM getDayGM(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DayGM getDayGMbySupplierId(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContractIds(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayGMsByNoSended(String date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDaySumValueByYYYYMM(DayPk daypk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayGM> getDayGMs(DayGM meteringDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayGMsAvg(DayGM dayGM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayGMsUsageAvg(DayGM dayGM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayGMsUsageMonthToDate(DayGM dayGM, String startDay,
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
    public List<Object> getConsumptionGmCo2DayValuesLocationId(
            Map<String, Object> condition) {
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
    public List<Object> getDayGMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<DayGM> getPersistentClass() {
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
