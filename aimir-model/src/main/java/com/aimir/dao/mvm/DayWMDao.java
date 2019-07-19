/**
 * DayGMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DayWM;
import com.aimir.util.Condition;

/**
 * DayWMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 18.   v1.0       김상연         DayWM 조회 조건 (DayWM)
 *
 */

public interface DayWMDao extends GenericDao<DayWM, Integer>{
	
    public List<DayWM> getDayWMsByMap(Map<?, ?> map);
    public List<DayWM> getDayWMsByList(List<Map<?,?>> list);
    public List<DayWM> getDayWMsByListCondition(Set<Condition> list);
    public List<Object> getDayWMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<Object> getDayWMsSumList(Set<Condition> conditions);
    public List<Object> getDayWMsCountByListCondition(Set<Condition> set);
    public int getTotalGroupByListCondition(Set<Condition> conditions);
	public List<Object[]> getDayBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap);
	public List<DayWM> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	//BEMS
	public List<Object> getRootLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2DayValuesParentId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2DayValuesLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2DayMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2DayMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2WeekMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2WeekMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionTmHmWeekMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonitoringSumMinMaxPrentId(Map<String, Object> condition);
	public List<Object> getCompareFacilityDayData(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2SearchDayTypeTotal(Map<String, Object> conditionDay);
	public List<Object> getConsumptionWmValueSum(int supplierId, String startDate, String endDate,int startValue, int endValue);

	public DayWM getDayWM(Map<String,Object> params);
	/**
	 * method name : getDayWMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 DayWM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public DayWM getDayWMbySupplierId(Map<String,Object> params);
	public List<Integer> getContractIds(Map<String, String> conditionMap);
	public List<Object> getDayWMsByNoSended(String date);
	public String getDaySumValueByYYYYMM(DayPk daypk);
	
	/**
	 * method name : getDayWMs
	 * method Desc : DayWM 조회 조건 (DayWM)
	 *
	 * @param meteringDay
	 * @return
	 */
	public List<DayWM> getDayWMs(DayWM dayWM);
	
	public List<Object> getDayWMsAvg(DayWM dayWM);
	public Double getDayWMsUsageAvg(DayWM dayWM);
	public Double getDayWMsUsageMonthToDate(DayWM dayWM, String startDay, String endDay);
	public Map<String, Object> getLast(Integer id);

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
	 * @Methodname getDayWMsCount
	 * @Description 조건에 해당하는 DayWM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getDayWMsCount(Set<Condition> conditions, String div);
}