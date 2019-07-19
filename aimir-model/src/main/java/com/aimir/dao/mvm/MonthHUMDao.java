package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthHUM;
import com.aimir.util.Condition;


public interface MonthHUMDao extends GenericDao<MonthHUM, Integer>{
	
    public List<MonthHUM> getMonthHUMsByListCondition(Set<Condition> list);
    public List<Object> getMonthHUMsCountByListCondition(Set<Condition> set);
    public List<Object> getMonthHUMsMaxMinAvg(Set<Condition> conditions, String div);
	public List<Object> getConsumptionHumMonitoring(Map<String, Object> condition);
	public List<Object> getUsageChartData(Set<Condition> conditions);
	
}
