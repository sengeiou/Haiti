/**
 * DayHMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayPk;
import com.aimir.util.Condition;

/**
 * DayHMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 03.   v1.0       김미선         DayHM 조회 조건 (DayHM)
 *
 */

@SuppressWarnings("unchecked")
public interface DayHMDao extends GenericDao<DayHM, Integer>{

    public List<DayHM> getDayHMsByMap(Map map);
    public List<Object> getDayHMsCountByListCondition(Set<Condition> set);
    public List<DayHM> getDayHMsByList(List<Map> list);
    public List<DayHM> getDayHMsByListCondition(Set<Condition> list);
    public List<Object> getDayHMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<Object> getDayHMsSumList(Set<Condition> conditions);
    public int getTotalGroupByListCondition(Set<Condition> conditions);
	public List<Object[]> getDayBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getDayBillingGridData(Map<String, String> conditionMap);
	public List<DayHM> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	//BEMS
	public List<Object> getRootLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2DayValuesParentId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2DayMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2DayMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2WeekMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2WeekMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionTmHmWeekMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonitoringSumMinMaxPrentId(Map<String, Object> condition);
	public List<Object> getCompareFacilityDayData(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2SearchDayTypeTotal(Map<String, Object> conditionDay);
	public List<Object> getConsumptionHmValueSum(int supplierId, String startDate, String endDate,int startValue, int endValue);
	
	public DayHM getDayHM(Map<String,Object> params);
	public List<Integer> getContractIds(Map<String, String> conditionMap);
	public List<Object> getDayHMsByNoSended(String date);
	public String getDaySumValueByYYYYMM(DayPk daypk);
	
	/**
	 * method name : getDayHMs
	 * method Desc : DayHM 조회 조건 (DayHM)
	 *
	 * @param meteringDay
	 * @return
	 */
	public List<DayHM> getDayHMs(DayHM meteringDay);
	public List<Object> getDayHMsAvg(DayHM dayHM);
	public Double getDayHMsUsageAvg(DayHM dayHM);
	public Double getDayHMsUsageMonthToDate(DayHM dayHM, String startDay, String endDay);
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
	 * @Methodname getDayHMsCount
	 * @Description 조건에 해당하는 DayHM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getDayHMsCount(Set<Condition> conditions, String div);
}