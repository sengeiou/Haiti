package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthGM;
import com.aimir.model.mvm.MonthWM;
import com.aimir.util.Condition;

public interface MonthGMDao extends GenericDao<MonthGM, Integer>{

    public List<Object> getMonthGMsMaxMinAvgSum(Set<Condition> conditions, String div);
    public List<MonthGM> getMonthGMsByListCondition(Set<Condition> set);
    public List<Object> getMonthGMsCountByListCondition(Set<Condition> set);
	public List<Object[]> getMonthBillingChartData(Map<String, String> conditionMap);
	public List<Object[]> getMonthBillingGridData(Map<String, String> conditionMap);
	public List<MonthGM> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	public Long getElecCustomerBillingGridDataCount(Map<String, Object> conditionMap);

	public List<Object> getConsumptionTmHmMonitoring(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonthMonitoringLocationId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonthMonitoringParentId(Map<String, Object> condition);
	public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal(Map<String, Object> conditionYear);

	public List<Map<String, String>> getContractBillingChartData(Map<String, String> conditionMap);
	
	public MonthGM getMonthGM(Map<String,Object> params);
	/**
	 * method name : getMonthGMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 MonthGM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public MonthGM getMonthGMbySupplierId(Map<String,Object> params);
	
	public List<Object> getMonthToYears();
	
	public List<Object> getEnergySavingReportMonthlyData(String[] years, int channel, Integer[] meterIds);
	public List<Object> getConsumptionGmCo2MonthSearchDayTypeTotal2(Map<String, Object> conditionYear);
	public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType seasonal);
	public List<Object> getConsumptionGmValueSum(int supplierId ,String startDate, String endDate,int startValue, int endValue);
	
	/**
	 * @Methodname getMonthGMsCount
	 * @Description 조건에 해당하는 MonthGM테이블의 데이터 개수를 반환한다.
	 * 
	 * @param conditions
	 * @param div
	 * @return
	 */
    public List<Object> getMonthGMsCount(Set<Condition> conditions, String div);
}
