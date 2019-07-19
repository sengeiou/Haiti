package com.aimir.service.system.prepayment;

import java.util.List;
import java.util.Map;

public interface PrepaymentMgmtCustomerManager {

    /**
     * method name : getChargeInfo
     * method Desc : 고객 선불관리 화면의 충전 정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getChargeInfo(Map<String, Object> conditionMap);

    /**
     * method name : getBalanceNotifySetting
     * method Desc : 고객 선불관리 화면의 통보설정 정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getBalanceNotifySetting(Map<String, Object> conditionMap);

    /**
     * method name : getLocaleFormatAllHours
     * method Desc : Locale formatting 된 00시 ~ 23시 시간형식 리스트를 조회한다.
     *
     * @param supplierId
     * @return
     */
    public List<String> getLocaleFormatAllHours(Integer supplierId);

    /**
     * method name : updateBalanceNotifySetting
     * method Desc : 고객 선불관리 통보설정 정보를 저장한다.
     *
     * @param conditionMap
     */
    public void updateBalanceNotifySetting(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistory(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistoryTotalCount
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getChargeHistoryTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistoryDetailChartData
     * method Desc : 고객 선불관리 화면의 충전 이력의 상세 차트 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryDetailChartData(Map<String, Object> conditionMap);

    /**
     * method name : changeEmergencyCreditMode
     * method Desc : 고객 선불관리에서 Credit Type 을 Emergency Credit Mode 로 전환한다.
     *
     * @param conditionMap
     */
    public void changeEmergencyCreditMode(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentTariff
     * method Desc : 고객 선불관리 화면에서 요금단가를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getPrepaymentTariff(Map<String, Object> conditionMap);
    
    /**
     * method name : getChargeHistoryForCustomer
     * method Desc : 고객 관리 가젯에서의 선불 충전 이력을 위한 정보 취득
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryForCustomer(Map<String, Object> conditionMap);
    
}