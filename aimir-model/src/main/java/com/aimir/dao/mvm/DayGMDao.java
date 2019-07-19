/**
 * DayGMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DayWM;
import com.aimir.util.Condition;

/**
 * DayGMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 18.   v1.0       김상연         DayGM 조회 조건 (DayGM)
 *
 */

@SuppressWarnings("unchecked")
public interface DayGMDao extends GenericDao<DayGM, Integer>{

    public List<DayGM> getDayGMsByMap(Map map);
    public List<Object> getDayGMsCountByListCondition(Set<Condition> set);
    public List<DayGM> getDayGMsByList(List<Map> list);
    public List<DayGM> getDayGMsByListCondition(Set<Condition> list);
    public List<Object> getDayGMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<Object> getDayGMsSumList(Set<Condition> conditions);
    public int getTotalGroupByListCondition(Set<Condition> conditions);
	public List<Object[]> getDayBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap);
	public List<DayGM> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	//BEMS
	public List<Object> getRootLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2DayValuesParentId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2DayMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2DayMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2WeekMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2WeekMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionTmHmWeekMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonitoringSumMinMaxPrentId(Map<String, Object> condition);
	public List<Object> getCompareFacilityDayData(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2SearchDayTypeTotal(Map<String, Object> conditionDay);
	public List<Object> getConsumptionGmValueSum(int supplierId, String startDate, String endDate,int startValue, int endValue);
	
	public DayGM getDayGM(Map<String,Object> params);
	/**
	 * method name : getDayGMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 DayGM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public DayGM getDayGMbySupplierId(Map<String,Object> params);
	public List<Integer> getContractIds(Map<String, String> conditionMap);
	public List<Object> getDayGMsByNoSended(String date);
	public String getDaySumValueByYYYYMM(DayPk daypk);
	
	/**
	 * method name : getDayGMs
	 * method Desc : DayGM 조회 조건 (DayGM)
	 *
	 * @param meteringDay
	 * @return
	 */
	public List<DayGM> getDayGMs(DayGM meteringDay);
	public List<Object> getDayGMsAvg(DayGM dayGM);
	public Double getDayGMsUsageAvg(DayGM dayGM);
	public Double getDayGMsUsageMonthToDate(DayGM dayGM, String startDay, String endDay);
	public Map<String, Object> getLast(Integer id);
	public List<Object> getConsumptionGmCo2DayValuesLocationId(Map<String, Object> condition);
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
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType weekly);
	
	/**
	 * @Methodname getDayGMsCount
	 * @Description 조건에 해당하는 DayGM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getDayGMsCount(Set<Condition> conditions, String div);
}