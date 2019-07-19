package com.aimir.dao.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringMonth;

public interface MeteringMonthDao extends GenericDao<MeteringMonth, Integer>{

    // 소비 랭킹
    @Deprecated
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

    // 검침데이터 상세
    @Deprecated
    public List<Object> getDetailMonthSearchData(HashMap<String, Object> condition);
    @Deprecated
    public List<Object> getDetailMonthMaxMinAvgSumData(HashMap<String, Object> condition);

    /**
     * Overlay Chart Data (Weekly, Monthly)
     * 
     * @param condition
     * @param contractId
     * @return
     */
    public List<Map<String, Object>> getOverlayChartMonthlyData(Map<String,Object> condition, Integer contractId);

    /**
     * method name : getDetailDailySearchData<b/>
     * method Desc : 검침데이터 상세 일별 데이터
     *
     * @param condition
     * @param isSum
     * @return
     */
    public List<Map<String, Object>> getDetailDailySearchData(Map<String, Object> condition, boolean isSum);

    /**
     * method name : getDetailMonthlySearchData<b/>
     * method Desc : 검침데이터 상세 월별 데이터
     *
     * @param condition
     * @param isSum
     * @return
     */
    public List<Object> getDetailMonthlySearchData(Map<String, Object> condition, boolean isSum);

    /**
     * method name : getMeteringDataMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataMonthlyData(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * method name : getMeteringDataMonthlyChannel2Data<b/>
     * method Desc : Metering Data 맥스가젯에서 채널2번 누적유효사용량을 조회한다. : 대성에너지 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataMonthlyChannel2Data(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataMonthlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev);


    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @param isPrev
     * @return
     */
    public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap, boolean isTotal, boolean isPrev);

    /**
     * method name : getMeteringDataDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDetailMonthlyData(Map<String, Object> conditionMap, boolean isSum);
}