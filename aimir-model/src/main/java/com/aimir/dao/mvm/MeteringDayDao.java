package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringDay;

public interface MeteringDayDao extends GenericDao<MeteringDay, Integer>{

	// 소비 랭킹
    public List<Object> getConsumptionRanking(Map<String,Object> condition);
    @Deprecated
    public List<Object> getConsumptionRankingList(Map<String,Object> condition);

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount Count 조회 여부
     * @return
     */
    public List<Map<String, Object>> getConsumptionRankingDataList(Map<String,Object> conditionMap, boolean isCount);

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount Count 조회 여부
     * @param isAll 전체 조회 여부
     * @return
     */
    public List<Map<String, Object>> getConsumptionRankingDataList(Map<String,Object> conditionMap, boolean isCount, boolean isAll);

    // 검침데이터 Max가젯
    public List<Object> getMeteringDayMaxGadgetWeekDataList(Map<String,Object> condition);
    // Year Data Max 가젯
    public List<Object> getMeteringDayMaxGadgetYearDataList(Map<String,Object> condition);
    public int getLoadDurationChartTotalCount(Map<String,Object> condition);
    public List<Object> getLoadDurationChartData(Map<String,Object> condition);
    public List<Object> getOverlayChartData(Map<String,Object> condition);
    public List<Object> getInOffTimeChartData(Map<String,Object> condition);
    @Deprecated
    public List<Object> getDetailDaySearchData(HashMap<String, Object> hm);
    @Deprecated
    public List<Object> getDetailDayMaxMinAvgSumData(HashMap<String, Object> condition);
    
    public List<Map<String, Object>> getOverlayChartDailyData(Map<String,Object> condition, Integer contractId);
    
    public List<Object> getCalendarDetailDaySearchData(HashMap<String, Object> hm);
    
	/**
	 * 오늘날짜의 검침데이터를 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param condition
	 * @return
	 */
	public List<Object> getUsageForEndDevicesByDay(Map<String,Object> params,Map<String,Object> params2);
	/**
	 * 특정날짜의 검침데이터를 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param condition
	 * @return
	 */
	public List<Object> getUsageForEndDevicesBySearchDate(Map<String,Object> params,Map<String,Object> params2);
	/**
	 * 특정주의 검침데이터를 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param params - 
	 * @param params2 - 
	 * @return
	 */
	public List<Object> getUsageForEndDevicesByWeek(Map<String,Object> params,Map<String,Object> params2);
	/**
	 * 특정년도의 검침데이터를 월별로 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param params - 
	 * @param params2 - 
	 * @return
	 */
	public List<Object> getUsageForEndDevicesByMonth(Map<String,Object> params,Map<String,Object> params2);
	
	/**
	 * 특정기간의 검침데이터를 일별로 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param params - 
	 * @return
	 */
	public List<Object> getUsageForEndDevicesByDayPeriod(Map<String,Object> params,Map<String,Object> params2);
	
	/**
	 * 특정기간의 검침데이터를 월별로 조회한다.
	 * 입력된 EndDeviceId,ModemId,MeterID 목록을 IN 조건으로 조회하며 각각 OR 조건으로 묶는다.
	 * @param params - 
	 * @return
	 */
	
	public List<Object> getUsageForExhibitionTotalByDay(Map<String,Object> params);
	
	public List<Object> getUsageForExhibitionTotalByMonth(Map<String,Object> params);
	
	public List<Object> getUsageForEndDevicesByMonthPeriod(Map<String,Object> params,Map<String,Object> params2);
	
	public List<Object> getUsageForEndDevicesByMonthPeriodReport(Map<String,Object> params,Map<String,Object> params2);
	
	public List<Object> getUsageForSubLocationByDay(Map<String,Object> params);
	
	public List<Object> getUsageForSubLocationByMonth(Map<String,Object> params);
	
	public List<Object> getUsageForLocationByDay(Map<String,Object> params);
	
	public List<Object> getTemperatureHumidityLocation(String meterType);
	
	public List<Object> getTemperatureHumidityForLocationByDay(Map<String,Object> params);
	
	public List<Object> getUsageForLocationByMonth(Map<String,Object> params);
	public List<Object> getTemperatureHumidityForLocationByMonth(Map<String,Object> params);
	
	public List<Object> getVEEThresholdData(HashMap<String, Object> hm);
	public List<Object> getVEEValidateCheckMiniDayData(HashMap<String, Object> hm);	
	
	
	//고객용 사용량
	public List<Object> getCustomerUsageEmDaily(String METER_TYPE, Map<String, Object> condition);
	public List<Object> getCustomerUsageEmMonthly(String METER_TYPE, Map<String, Object> condition);
	public List<Object> getCustomerUsageEmYearly(String METER_TYPE, Map<String, Object> condition);
	public List<Object> getCustomerUsageEmHourly(String METER_TYPE, Map<String, Object> condition);
	public Map<String, Object> getCustomerUsageFee(Map<String, Object> condition);
	
	public List<Object> getCustomerCO2Daily(Map<String, Object> condition);
	
	//vee
	public List<Map<String, Object>> getDayVEEList(String qry);
	public List<Map<String, Object>> getDayVEEListPage(String qry, int startRow, int pageSize);
	public List<Map<String, Object>> getDayVEEListTotal(String qry);
	public List<Map<String, Object>> getBemsFloorUsageReductRankingDay(Map<String, Object> params);
	public List<Map<String, Object>> getBemsFloorUsageReductRankingMonth(Map<String, Object> params);

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev);
    // Improvement Version 16-10-03
    public List<Map<String, Object>> getMeteringDataDailyData2(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 채널2번 누적유효사용량을 조회한다. : 대성에너지 
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyChannel2Data(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev);

    /**
     * method name : getMeteringValueMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isSub
     * @return
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isSub);

    /**
     * method name : getMeteringValueYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringValueYearlyData(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * method name : getMeteringValueYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isSub
     * @return
     */
    public List<Map<String, Object>> getMeteringValueYearlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isSub);
    
    /**
     * method name : getMeteringDataDetailDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isSum
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailDailyData(Map<String, Object> conditionMap, boolean isSum);

    /**
     * method name : getMeteringDataDetailRatelyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailRatelyData(Map<String, Object> conditionMap);
    
    /**
     * method name : getMeteringValueDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @param isSum
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDetailMonthlyData(Map<String, Object> conditionMap, boolean isSum);
    
    /**
     * method name : getDayUsage
     * method Desc : 각 DayEM, DayWM, DayGM 테이블에서 해당날짜의 사용량을 가져온다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getDayUsage(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringSuccessCountListPerLocation<b/>
     * method Desc : 
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> meterType : String - meter type (ex.EnergyMeter)
     * <li> startDate : String - 조회시작일자 (yyyyMMdd)
     * <li> endDate : String - 조회종료일자 (yyyyMMdd)
     * </ul>
     * @return List of Map {
     *                      LOC_ID : Location.id - location id
     *                      SUCCESS_CNT : Integer - metering success count
     *                     }
     */
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap);

    /**
     * method name : getSuccessCountByLocation<b/>
     * method Desc :
     *
     * @param condition
     * @return
     */
    public String getSuccessCountByLocation(Map<String, Object> condition);

    /**
     * method name : getFailureCountByCauses<b/>
     * method Desc : MeteringFail 가젯의 Cause1/Cause2 Count 를 조회한다.<b/>
     *               1. meteringdata_em 테이블에 없는 meter 테이블 데이터의 LAST_READ_DATE 를 검색.<b/>
     *               2. LAST_READ_DATE 의 값이 현재일과 하루이상 차이가 나면 Cause1(통신장애)<b/>
     *               3. LAST_READ_DATE 의 값이 현재일과 같으면 Cause2(포멧에러)
     *               4. 그 외 경우 ETC
     *
     * @param condition
     * @return
     */
    public Map<String, String> getFailureCountByCauses(Map<String, Object> condition);

    public List<Map<String, Object>> getRealTimeMeterValues(Map<String, Object> conditionMap, boolean isTotal);

}
