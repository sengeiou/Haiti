package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthWM;
import com.aimir.util.Condition;

public interface MonthWMDao extends GenericDao<MonthWM, Integer>{

    public List<Object> getMonthWMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<MonthWM> getMonthWMsByListCondition(Set<Condition> set);
    public List<Object> getMonthWMsCountByListCondition(Set<Condition> set);
	public List<Object[]> getMonthBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getMonthBillingGridData(Map<String, String> conditionMap);
	public List<MonthWM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	public List<Object> getConsumptionTmHmMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonthMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonthMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionWmCo2MonthSearchDayTypeTotal(Map<String, Object> conditionYear);
	public List<Object[]> getContractBillingChartData(Map<String, String> conditionMap);
	public MonthWM getMonthWM(Map<String,Object> params);
	/**
	 * method name : getMonthWMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 MonthWM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public MonthWM getMonthWMbySupplierId(Map<String,Object> params);

	public List<Object> getMonthToYears();
	
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds);
	public List<Object> getConsumptionWmCo2MonthSearchDayTypeTotal2(Map<String, Object> conditionYear);
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType seasonal);
	public List<Object> getConsumptionWmValueSum(int supplierId, String startDate, String endDate,int startValue, int endValue);
	
	/**
	 * @Methodname getMonthWMsCount
	 * @Description 조건에 해당하는 MonthWM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getMonthWMsCount(Set<Condition> conditions, String div);
}
