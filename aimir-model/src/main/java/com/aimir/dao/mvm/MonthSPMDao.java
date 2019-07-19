package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MonthSPM;
import com.aimir.util.Condition;

public interface MonthSPMDao extends GenericDao<MonthSPM, Integer> {
	
	public long totalByConditions(final Set<Condition> conditions);
	public List<MonthSPM> getMonthSPMsByListCondition(final Set<Condition> conditions);
	public List<Object> getMonthSPMsCountByListCondition(final Set<Condition> conditions);
	public List<Object> getConsumptionEmCo2ManualMonitoring(
			Map<String, Object> condition, DateType monthly);
}
