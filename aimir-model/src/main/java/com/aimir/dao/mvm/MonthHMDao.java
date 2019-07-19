package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthHM;
import com.aimir.util.Condition;
/**
 * MonthHMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 03.   v1.0       김미선         MonthHM 조회 조건 (MonthHM)
 *
 */
public interface MonthHMDao extends GenericDao<MonthHM, Integer>{

    public List<Object> getMonthHMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<MonthHM> getMonthHMsByListCondition(Set<Condition> set);
    public List<Object> getMonthHMsCountByListCondition(Set<Condition> set);
	public List<Object[]> getMonthBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getMonthBillingGridData(Map<String, String> conditionMap);
	public List<MonthHM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	public List<Object> getConsumptionTmHmMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonthMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonthMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionHmCo2MonthSearchDayTypeTotal(Map<String, Object> conditionYear);

	public List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap);
	
	public MonthHM getMonthHM(Map<String,Object> params);
	
	public List<Object> getMonthToYears();
	
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds);
	public List<Object> getConsumptionHmCo2MonthSearchDayTypeTotal2(Map<String, Object> conditionYear);
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType seasonal);
	public List<Object> getConsumptionHmValueSum(int supplierId ,String startDate, String endDate,int startValue, int endValue);
	
	/**
	 * @Methodname getMonthHMsCount
	 * @Description 조건에 해당하는 MonthHM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getMonthHMsCount(Set<Condition> conditions, String div);
}
