package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthTM;
import com.aimir.util.Condition;


public interface MonthTMDao extends GenericDao<MonthTM, Integer>{
	
    public List<MonthTM> getMonthTMsByListCondition(Set<Condition> list);
    public List<Object> getMonthTMsCountByListCondition(Set<Condition> set);
    public List<Object> getMonthTMsMaxMinAvg(Set<Condition> conditions, String div);
	public List<Object> getConsumptionTmMonitoring(Map<String, Object> condition);
	public List<Object> getUsageChartData(Set<Condition> conditions);
    
	
}
