package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayHUM;
import com.aimir.util.Condition;

@SuppressWarnings("unchecked")
public interface DayHUMDao extends GenericDao<DayHUM, Integer>{

    public List<DayHUM> getDayHUMsByMap(Map map);
    public List<Object> getDayHUMsCountByListCondition(Set<Condition> set);
    public List<DayHUM> getDayHUMsByList(List<Map> list);
    public List<DayHUM> getDayHUMsByListCondition(Set<Condition> list);
    public List<Object> getDayHUMsMaxMinAvg(Set<Condition> conditions, String div);
    public List<Object> getDayHUMsAvgList(Set<Condition> conditions);
    public int getAvgGroupByListCondition(Set<Condition> conditions);
	public List<Object> getConsumptionHumMonitoring(Map<String, Object> conditionDay);
	
}
