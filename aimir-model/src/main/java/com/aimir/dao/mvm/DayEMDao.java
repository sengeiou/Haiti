/**
 * DayEMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;

/**
 * DayEMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 29.   v1.0       김상연         DayEM 조회 조건 (DayEM)
 * 2011. 5. 16.   v1.1       김상연         기기별 그리드 조회
 *
 */

public interface DayEMDao extends GenericDao<DayEM, Integer>{
    
    public List<DayEM> getDayEMsByListCondition(Set<Condition> list);
    public List<Object> getDayEMsCountByListCondition(Set<Condition> set);
    public int getIntDayEMsCountByListCondition(Set<Condition> set);
    public List<Object> getTotalGroupByListCondition(Set<Condition> set);
    public List<Object> getDayEMsGroupByListCondition(Set<Condition> set);
    public List<Object> getDayEMsSumList(Set<Condition> conditions);
    public List<Object> getDayEMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<Map<String, Object>> getDayEmsZoneUsage(Map<String, Object> conditions);
    public List<Map<String, Object>> getTotalDayEmsZoneUsage(Map<String, Object> conditions);
    public List<Map<String, Object>> getDayEmsLocationUsage(Map<String, Object> conditions);
    public List<Map<String, Object>> getTotalDayEmsLocationUsage(Map<String, Object> conditions);
   
    /**
	 * 발전량의 총 합을 구한다.
	 * 
	 * @param conditions 조건
	 * @return 발전량의 총 합
	 */
	public double getSumTotalUsageByCondition(Set<Condition> conditions);
	
	/**
	 * 시간별 발전량의 총 합을 구한다.
	 * 
	 * @param conditions 조건
	 * @return 시간별 발전량의 총 합의 리스트
	 */
	public Map<String, Double> getSumUsageByCondition(Set<Condition> conditions);
    
    
    //day의 yyyymm의 value_xx 값의 합을 리턴 : month_em에 넣을 값.
    public String getDaySumValueByYYYYMM(DayPk daypk);

	// 계약용량 이상 고객
	public long getTotalCount(Map<String, Object> condition);
	public List<Object> getAbnormalContractUsageEM(Map<String, Object> condition);
	public long getAbnormalContractUsageEMTotal(Map<String, Object> condition);
	public List<Object> getAbnormalContractUsageEMList(Map<String, Object> condition);
    
	// 검침 데이터 조회
    public List<DayEM> getMeteringFailureMeteringData(Map<String,Object> params);
    
    // 빌링
	public List<Object[]> getDayBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap);
	public List<DayEM> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);	
	
    // 수요관리
	public List<Object> getDemandManagement(Map<String, Object> condition, String type);
	public List<Object> getDemandManagementList(Map<String, Object> condition);
	
	//BEMS
	public List<Object> getRootLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2DayMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2DayMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2WeekMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2WeekMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionTmHmWeekMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId_bck(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxPrentId(Map<String, Object> condition);
	public List<Object> getCompareFacilityDayData(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2SearchDayTypeTotal(Map<String, Object> conditionDay);
	public List<Integer> getContractIds(Map<String, String> conditionMap);
	public List<Object> getConsumptionEmCo2DayValuesParentId(Map<String, Object> condition);
	public List<Object> getConsumptionEmCo2DayValuesLocationId(Map<String, Object> condition);
	public List<Object> getDayEMsByNoSended(String date,String type);
	public void updateSendedResult(String table,String date,DeviceType type,String mdev_id);
    public List<Object> getConsumptionEmValueSum(int supplierId,String startDate, String endDate,int startValue, int endValue);
	
	/**
	 * method name : getDayEMs
	 * method Desc : DayEM 조회 조건 (DayEM)
	 *
	 * @param dayEM
	 * @return
	 */
	public List<DayEM> getDayEMs(DayEM dayEM);
	
	/**
	 * method name : getDeviceSpecificGrid
	 * method Desc : 기기별 그리드 조회
	 *
	 * @param basicDay
	 * @param contractId
	 * @return
	 */
	public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId);
	
	public List<Object> getDayEMsAvg(DayEM dayEM);
	public Double getDayEMsUsageAvg(DayEM dayEM);
	public Double getDayEMsUsageMonthToDate(DayEM dayEM, String startDay, String endDay);
	public Map<String, Object> getLast(Integer id);

    /**
     * method name : getSicCustomerEnergyUsageList<b/>
     * method Desc : SIC Load Profile 미니가젯의 List 를 조회한다. 
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @param isTotal total count 여부
     * 
     * 
     * @return if parameter isCount is true then return count (total) only
     *         else List of Map {contract.sic.id AS sicId,
     *                           contract.sic.code AS sicCode,
     *                           contract.sic.name AS sicName,
     *                           COUNT(DISTINCT contract.id) AS customerCount,
     *                           SUM(dayem.total) AS usageSum}
     */
    public List<Map<String, Object>> getSicCustomerEnergyUsageList(Map<String, Object> conditionMap, boolean isTotal);
    
    
    public List<Map<String, Object>> getSicCustomerEnergyUsageList2(Map<String, Object> conditionMap, boolean isTotal);
    
    //public List<Map<String, Object>> getSicIdList(Map<String, Object> conditionMap);
    public List<Map<String, Object>> getSicIdList();
    
    public List<Map<String, Object>> getSicIdList2(Map<String, Object> conditionMap);

   

    /**
     * method name : getSicEnergyUsageList<b/>
     * method Desc : SIC Load Profile 가젯에서 에너지 사용량 데이터를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getSicEnergyUsageList(Map<String, Object> conditionMap);

    /**
     * method name : getSicCustomerEnergyUsageTotalSum<b/>
     * method Desc : SIC Load Profile 미니가젯의 List 의 Total 데이터를 조회한다. 
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @return List of Map {0 AS sicId,
     *                      '0' AS sicCode,
     *                      'Total' AS sicName,
     *                      SUM(e.contract.customer.id) AS customerCount,
     *                      SUM(e.total) AS usageSum}
     */
    public List<Map<String, Object>> getSicCustomerEnergyUsageTotalSum(Map<String, Object> conditionMap);

    /**
     * method name : getSicLoadProfileChartDataByDayAvg<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Load Profile Chart 의 WorkingDay/Saturday/Sunday/Holiday Avg Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         // 0:working day, 1:saturday, 2:sunday, 3:holiday
     *         Integer dayType = (Integer)conditionMap.get("dayType");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     *         String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));
     * 
     * @return List of Map {AVG(de.value_00) AS value_00,
     *                      AVG(de.value_01) AS value_01,
     *                      AVG(de.value_02) AS value_02,
     *                      AVG(de.value_03) AS value_03,
     *                      AVG(de.value_04) AS value_04,
     *                      AVG(de.value_05) AS value_05,
     *                      AVG(de.value_06) AS value_06,
     *                      AVG(de.value_07) AS value_07,
     *                      AVG(de.value_08) AS value_08,
     *                      AVG(de.value_09) AS value_09,
     *                      AVG(de.value_10) AS value_10,
     *                      AVG(de.value_11) AS value_11,
     *                      AVG(de.value_12) AS value_12,
     *                      AVG(de.value_13) AS value_13,
     *                      AVG(de.value_14) AS value_14,
     *                      AVG(de.value_15) AS value_15,
     *                      AVG(de.value_16) AS value_16,
     *                      AVG(de.value_17) AS value_17,
     *                      AVG(de.value_18) AS value_18,
     *                      AVG(de.value_19) AS value_19,
     *                      AVG(de.value_20) AS value_20,
     *                      AVG(de.value_21) AS value_21,
     *                      AVG(de.value_22) AS value_22,
     *                      AVG(de.value_23) AS value_23}
     */
    public List<Map<String, Object>> getSicLoadProfileChartDataByDayAvg(Map<String, Object> conditionMap);

    /**
     * method name : getSicLoadProfileChartDataByDaySum<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Load Profile Chart 의 WorkingDay/Saturday/Sunday/Holiday Sum Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         // 0:working day, 1:saturday, 2:sunday, 3:holiday
     *         Integer dayType = (Integer)conditionMap.get("dayType");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     *         String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));
     * 
     * @return List of Map {SUM(de.value_00) AS value_00,
     *                      SUM(de.value_01) AS value_01,
     *                      SUM(de.value_02) AS value_02,
     *                      SUM(de.value_03) AS value_03,
     *                      SUM(de.value_04) AS value_04,
     *                      SUM(de.value_05) AS value_05,
     *                      SUM(de.value_06) AS value_06,
     *                      SUM(de.value_07) AS value_07,
     *                      SUM(de.value_08) AS value_08,
     *                      SUM(de.value_09) AS value_09,
     *                      SUM(de.value_10) AS value_10,
     *                      SUM(de.value_11) AS value_11,
     *                      SUM(de.value_12) AS value_12,
     *                      SUM(de.value_13) AS value_13,
     *                      SUM(de.value_14) AS value_14,
     *                      SUM(de.value_15) AS value_15,
     *                      SUM(de.value_16) AS value_16,
     *                      SUM(de.value_17) AS value_17,
     *                      SUM(de.value_18) AS value_18,
     *                      SUM(de.value_19) AS value_19,
     *                      SUM(de.value_20) AS value_20,
     *                      SUM(de.value_21) AS value_21,
     *                      SUM(de.value_22) AS value_22,
     *                      SUM(de.value_23) AS value_23}
     */
    public List<Map<String, Object>> getSicLoadProfileChartDataByDaySum(Map<String, Object> conditionMap);

    /**
     * method name : getSicLoadProfileChartDataByDayMax<b/>
     * method Desc : SIC Load Profile 맥스가젯의 LoadProfileChart 의 PeakDay Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     *         String sicCode = StringUtil.nullToBlank(conditionMap.get("sicCode"));
     * 
     * @return List of Map {MAX(de.value_00) AS value_00,
     *                      MAX(de.value_01) AS value_01,
     *                      MAX(de.value_02) AS value_02,
     *                      MAX(de.value_03) AS value_03,
     *                      MAX(de.value_04) AS value_04,
     *                      MAX(de.value_05) AS value_05,
     *                      MAX(de.value_06) AS value_06,
     *                      MAX(de.value_07) AS value_07,
     *                      MAX(de.value_08) AS value_08,
     *                      MAX(de.value_09) AS value_09,
     *                      MAX(de.value_10) AS value_10,
     *                      MAX(de.value_11) AS value_11,
     *                      MAX(de.value_12) AS value_12,
     *                      MAX(de.value_13) AS value_13,
     *                      MAX(de.value_14) AS value_14,
     *                      MAX(de.value_15) AS value_15,
     *                      MAX(de.value_16) AS value_16,
     *                      MAX(de.value_17) AS value_17,
     *                      MAX(de.value_18) AS value_18,
     *                      MAX(de.value_19) AS value_19,
     *                      MAX(de.value_20) AS value_20,
     *                      MAX(de.value_21) AS value_21,
     *                      MAX(de.value_22) AS value_22,
     *                      MAX(de.value_23) AS value_23}
     */
    public List<Map<String, Object>> getSicLoadProfileChartDataByPeakDay(Map<String, Object> conditionMap);

    /**
     * method name : getSicTotalLoadProfileChartData<b/>
     * method Desc : SIC Load Profile 맥스가젯의 Total Load Profile Chart 의 Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     * 
     * @return List of Map {de.sic AS sicCode,
     *                      cd.name AS sicName,
     *                      SUM(de.value_00)/1000 AS value_00,
     *                      SUM(de.value_01)/1000 AS value_01,
     *                      SUM(de.value_02)/1000 AS value_02,
     *                      SUM(de.value_03)/1000 AS value_03,
     *                      SUM(de.value_04)/1000 AS value_04,
     *                      SUM(de.value_05)/1000 AS value_05,
     *                      SUM(de.value_06)/1000 AS value_06,
     *                      SUM(de.value_07)/1000 AS value_07,
     *                      SUM(de.value_08)/1000 AS value_08,
     *                      SUM(de.value_09)/1000 AS value_09,
     *                      SUM(de.value_10)/1000 AS value_10,
     *                      SUM(de.value_11)/1000 AS value_11,
     *                      SUM(de.value_12)/1000 AS value_12,
     *                      SUM(de.value_13)/1000 AS value_13,
     *                      SUM(de.value_14)/1000 AS value_14,
     *                      SUM(de.value_15)/1000 AS value_15,
     *                      SUM(de.value_16)/1000 AS value_16,
     *                      SUM(de.value_17)/1000 AS value_17,
     *                      SUM(de.value_18)/1000 AS value_18,
     *                      SUM(de.value_19)/1000 AS value_19,
     *                      SUM(de.value_20)/1000 AS value_20,
     *                      SUM(de.value_21)/1000 AS value_21,
     *                      SUM(de.value_22)/1000 AS value_22,
     *                      SUM(de.value_23)/1000 AS value_23}
     */
    public List<Map<String, Object>> getSicTotalLoadProfileChartData(Map<String, Object> conditionMap);

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
    @Deprecated
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap);
    
    /**
     * 수검침 사용량 및 탄소배출량 목록을 반환한다.
     * 
     * @param condition 데이터 검색 조건
     * @param dateType 가져올 통계 주기
     * @return 결과 리스트 셋
     */
    public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType);

	/**
	 * @Methodname getDayEMsCount
	 * @Description 조건에 해당하는 DayEM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getDayEMsCount(Set<Condition> conditions, String div);
    
    /**
     * 일별 검침데이타를 삭제한다.
     * @param meterId
     * @param yyyymmdd
     */
    public void delete(String meterId, String yyyymmdd);
    
    /**
     * 해당 미터의 오래된(bDate보다 오래된)LP 삭제
     * @param mdsId
     * @param bDate
     */
	public void oldLPDelete(String mdsId, String substring);
}