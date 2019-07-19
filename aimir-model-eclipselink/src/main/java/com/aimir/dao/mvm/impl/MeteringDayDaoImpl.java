package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.util.Condition;

@Repository(value = "meteringdayDao")
@SuppressWarnings("unchecked")
public class MeteringDayDaoImpl extends AbstractJpaDao<MeteringDay, Integer> implements MeteringDayDao {

//  private static Log logger = LogFactory.getLog(MeteringDayDaoImpl.class);
    
    public MeteringDayDaoImpl() {
        super(MeteringDay.class);
    }

    @Override
    public List<Object> getConsumptionRanking(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getConsumptionRankingList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getConsumptionRankingDataList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getConsumptionRankingDataList(
            Map<String, Object> conditionMap, boolean isCount, boolean isAll) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDayMaxGadgetWeekDataList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDayMaxGadgetYearDataList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLoadDurationChartTotalCount(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getLoadDurationChartData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getOverlayChartData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getInOffTimeChartData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getDetailDaySearchData(HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getDetailDayMaxMinAvgSumData(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getOverlayChartDailyData(
            Map<String, Object> condition, Integer contractId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCalendarDetailDaySearchData(
            HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByDay(Map<String, Object> params,
            Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesBySearchDate(
            Map<String, Object> params, Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByWeek(Map<String, Object> params,
            Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByMonth(
            Map<String, Object> params, Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByDayPeriod(
            Map<String, Object> params, Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForExhibitionTotalByDay(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForExhibitionTotalByMonth(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByMonthPeriod(
            Map<String, Object> params, Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForEndDevicesByMonthPeriodReport(
            Map<String, Object> params, Map<String, Object> params2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForSubLocationByDay(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForSubLocationByMonth(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForLocationByDay(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTemperatureHumidityLocation(String meterType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTemperatureHumidityForLocationByDay(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageForLocationByMonth(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTemperatureHumidityForLocationByMonth(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getVEEThresholdData(HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getVEEValidateCheckMiniDayData(
            HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCustomerUsageEmDaily(String METER_TYPE,
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCustomerUsageEmMonthly(String METER_TYPE,
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCustomerUsageEmYearly(String METER_TYPE,
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCustomerUsageEmHourly(String METER_TYPE,
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getCustomerUsageFee(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getCustomerCO2Daily(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayVEEList(String qry) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayVEEListPage(String qry,
            int startRow, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayVEEListTotal(String qry) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBemsFloorUsageReductRankingDay(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getBemsFloorUsageReductRankingMonth(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDailyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDailyData2(Map<String, Object> conditionMap, boolean isTotal) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDailyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataHourlyChannel2Data(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataWeeklyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataWeeklyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDetailDailyData(
            Map<String, Object> conditionMap, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDetailRatelyData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDayUsage(
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
    public String getSuccessCountByLocation(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getFailureCountByCauses(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeteringDay> getPersistentClass() {
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
    public List<Map<String, Object>> getMeteringValueMonthlyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringValueMonthlyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isSub) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringValueYearlyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringValueYearlyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isSub) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringValueDetailMonthlyData(
            Map<String, Object> conditionMap, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Map<String, Object>> getRealTimeMeterValues(Map<String, Object> conditionMap, boolean isTotal) {
		// TODO Auto-generated method stub
		return null;
	}
}