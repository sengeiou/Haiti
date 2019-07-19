package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayTM;
import com.aimir.util.Condition;

@SuppressWarnings("unchecked")
public interface DayTMDao extends GenericDao<DayTM, Integer>{

    public List<DayTM> getDayTMsByMap(Map map);
    public List<Object> getDayTMsCountByListCondition(Set<Condition> set);
    public List<DayTM> getDayTMsByList(List<Map> list);
    public List<DayTM> getDayTMsByListCondition(Set<Condition> list);
    public List<Object> getDayTMsMaxMinAvg(Set<Condition> conditions, String div);
    public List<Object> getDayTMsAvgList(Set<Condition> conditions);
    public int getAvgGroupByListCondition(Set<Condition> conditions);
	public List<Object> getConsumptionTmMonitoring(Map<String, Object> condition);
	    
}
