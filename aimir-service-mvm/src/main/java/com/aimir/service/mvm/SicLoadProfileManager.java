package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

/**
 * SicLoadProfileManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 4. 12.  v1.0        문동규   SIC Load Profile Service
 * </pre>
 */
public interface SicLoadProfileManager {

//    /**
//     * method name : getSicCustomerEnergyUsageList<b/>
//     * method Desc : SIC Load Profile 미니가젯에서 List 를 조회한다.
//     *
//     * @param conditionMap
//     * {@code}
//     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
//     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
//     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
//     * 
//     * @return List of Map {e.contract.customer.customTypeCode.id AS codeId,
//     *                      e.contract.customer.customTypeCode.name AS codeName,
//     *                      SUM(e.contract.customer.id) AS customerCount,
//     *                      SUM(e.total) AS usageSum}
//     */
//    public List<Map<String, Object>> getSicCustomerEnergyUsageList(Map<String, Object> conditionMap);
    
//    public List<Map<String, Object>> getSicCustomerEnergyUsageList2(Map<String, Object> conditionMap);

    
//    /**
//     * method name : getSicCustomerEnergyUsageList<b/>
//     * method Desc : SIC Load Profile 미니가젯에서 List Total Count 를 조회한다.
//     *
//     * @param conditionMap
//     * {@code}
//     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
//     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
//     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
//     * 
//     * @return count 
//     */
//    public Integer getEbsSuspectedDtsListTotalCount(Map<String, Object> conditionMap);
    
//    public Integer getEbsSuspectedDtsListTotalCount2(Map<String, Object> conditionMap);

    /**
     * method name : getSicContEnergyUsageTreeData<b/>
     * method Desc : SIC Load Profile 가젯에서 SIC 별 에너지사용량 tree data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @return List of Map {e.contract.customer.customTypeCode.id AS codeId,
     *                      e.contract.customer.customTypeCode.name AS codeName,
     *                      SUM(e.contract.customer.id) AS customerCount,
     *                      SUM(e.total) AS usageSum}
     */
    public List<Map<String, Object>> getSicContEnergyUsageTreeData(Map<String, Object> conditionMap);

    /**
     * method name : getSicLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Load Profile Chart Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @return List of Map
     */
    public Map<String, Object> getSicLoadProfileChartData(Map<String, Object> conditionMap);

    /**
     * method name : getSicTotalLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Total Load Profile Chart Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @return List of Map
     */
    public Map<String, Object> getSicTotalLoadProfileChartData(Map<String, Object> conditionMap);

}