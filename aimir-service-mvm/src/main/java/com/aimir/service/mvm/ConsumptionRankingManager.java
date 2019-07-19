package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface ConsumptionRankingManager {

    @Deprecated
	public Map<String, Object> getConsumptionRanking(Map<String, Object> condition);
	
	@Deprecated
    public Map<String, Object> getConsumptionRankingList(Map<String, Object> condition);

    /**
     * method name : getConsumptionRankingData<b/>
     * method Desc : Consumption Ranking 미니가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getConsumptionRankingData(Map<String, Object> conditionMap);

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getConsumptionRankingDataList(Map<String, Object> conditionMap);

    /**
     * method name : getConsumptionRankingDataList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isAll 전체 조회여부
     * @return
     */
    public Map<String, Object> getConsumptionRankingDataList(Map<String, Object> conditionMap, boolean isAll);
}