package com.aimir.service.system.prepayment;

import java.util.List;
import java.util.Map;

public interface PrepaymentMgmtOperatorManager {

    /**
     * method name : getEmergencyCreditContractList
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEmergencyCreditContractList(Map<String, Object> conditionMap);

    /**
     * method name : getEmergencyCreditContractListTotalCount
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEmergencyCreditContractListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentContractStatusChartData
     * method Desc : 관리자 선불관리 미니가젯의 선불고객 Pie Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getPrepaymentContractStatusChartData(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentContractList
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getPrepaymentContractList(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentContractListTotalCount
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getPrepaymentContractListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : updateEmergencyCreditInfo
     * method Desc : 관리자 선불관리 Emergency Credit 정보를 저장한다.
     *
     * @param conditionMap
     */
    public void updateEmergencyCreditInfo(Map<String, Object> conditionMap);
}