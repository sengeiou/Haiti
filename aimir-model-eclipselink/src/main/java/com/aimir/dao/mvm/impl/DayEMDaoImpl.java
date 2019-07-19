/**
 * DayEMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;

/**
 * DayEMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 29.   v1.0       김상연         DayEM 조회 조건 (DayEM)
 * 2011. 5. 16.   v1.1       김상연         기기별 그리드 조회
 *
 */
@Repository(value = "dayemDao")
@SuppressWarnings("unchecked")
public class DayEMDaoImpl extends AbstractJpaDao<DayEM, Integer> 
        implements DayEMDao {

    private static Log logger = LogFactory.getLog(DayEMDaoImpl.class);
    
    public DayEMDaoImpl() {
        super(DayEM.class);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<DayEM> getDayEMsByListCondition(Set<Condition> set) {

        return findByConditions(set);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getDayEMsCountByListCondition(Set<Condition> set) {

        return findTotalCountByConditions(set);
    }

    // totCount를 int 형으로 retrun
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int getIntDayEMsCountByListCondition(Set<Condition> set) {
        Integer objVal = 0;
        Iterator it = findTotalCountByConditions(set).iterator();

        while (it.hasNext()) {
            objVal = (Integer) it.next();
        }
        return objVal;
    }

    @Override
    public Class<DayEM> getPersistentClass() {
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
    public List<Object> getTotalGroupByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayEMsGroupByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayEMsSumList(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayEMsMaxMinAvgSum(Set<Condition> conditions,
            String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayEmsZoneUsage(
            Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getTotalDayEmsZoneUsage(
            Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayEmsLocationUsage(
            Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getTotalDayEmsLocationUsage(
            Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getSumTotalUsageByCondition(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Map<String, Double> getSumUsageByCondition(
            Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDaySumValueByYYYYMM(DayPk daypk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getTotalCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getAbnormalContractUsageEM(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getAbnormalContractUsageEMTotal(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getAbnormalContractUsageEMList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayEM> getMeteringFailureMeteringData(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getDayBillingChartData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getDayBillingGridData(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayEM> getDayCustomerBillingGridData(
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
    public List<Object> getDemandManagement(Map<String, Object> condition,
            String type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDemandManagementList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getRootLocationId(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionMonitoring(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2DayMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2DayMonitoringParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2WeekMonitoringLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2WeekMonitoringParentId(
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
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId_bck(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2MonitoringSumMinMaxPrentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCompareFacilityDayData(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2SearchDayTypeTotal(
            Map<String, Object> conditionDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getContractIds(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2DayValuesParentId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getConsumptionEmCo2DayValuesLocationId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDayEMsByNoSended(String date, String type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateSendedResult(String table, String date, DeviceType type,
            String mdev_id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getConsumptionEmValueSum(int supplierId,
            String startDate, String endDate, int startValue, int endValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DayEM> getDayEMs(DayEM dayEM) {
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
    public List<Object> getDayEMsAvg(DayEM dayEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayEMsUsageAvg(DayEM dayEM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDayEMsUsageMonthToDate(DayEM dayEM, String startDay,
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
    public List<Map<String, Object>> getSicCustomerEnergyUsageList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicCustomerEnergyUsageList2(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicIdList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicIdList2(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicEnergyUsageList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicCustomerEnergyUsageTotalSum(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicLoadProfileChartDataByDayAvg(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicLoadProfileChartDataByDaySum(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicLoadProfileChartDataByPeakDay(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getSicTotalLoadProfileChartData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(
            Map<String, Object> conditionMap) {
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
    public List<Object> getDayEMsCount(Set<Condition> conditions, String div) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String meterId, String yyyymmdd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void oldLPDelete(String mdsId, String substring) {
        // TODO Auto-generated method stub
        
    }

}