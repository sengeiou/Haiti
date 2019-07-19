package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface BillingManager {

	List<Map<String, String>> getElecBillingChartData(Map<String, String> conditionMap);
	List<Map<String, Object>> getElecLocationBillingGridData(Map<String, String> conditionMap);	
	List<Map<String, String>> getCustomerBillingGridData(Map<String, Object> conditionMap);
	Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);
	List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap);
    
	/**
	 * @param conditionMap
	 * @return
	 */
	Long getMeteringDataCount(Map<String, Object> conditionMap);
	
	/**
	 * @param conditionMap
	 * @return
	 */
	List<Map<String, Object>> getMeteringData(Map<String, Object> conditionMap);
	

	/**
	 * @DESC Location 의 하위노드를 fetch 
	 * @param locationIds
	 * @param supplierId
	 * @param firstFlag : 최초값인지 여부
	 * @param strbuf :  전체 노드 id(구분자 , ) 스트링버퍼
	 * @return 현재노드 와 현재노드의 하위노드들.
	 */
	public String getChildNodesInLocation(String locationIds, String supplierId, boolean firstFlag, StringBuffer strbuf );
	
	/**
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getMeteringDataReport(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDetailData<b/>
     * method Desc : TOU Report 의 상세정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDetailData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDetailUsageData<b/>
     * method Desc : TOU Report 의 상세사용량정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDetailUsageData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDetailLastData<b/>
     * method Desc : TOU Report 의 지난일자 상세 사용량정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDetailLastUsageData(Map<String, Object> conditionMap);

}