package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.MonthGMView;
import com.aimir.model.view.MonthHMView;
import com.aimir.util.Condition;

public interface MonthHMViewDao extends GenericDao<MonthHMView, Integer>{
	
	public List<MonthHMView> getMonthHMsByListCondition(Set<Condition> list);
	
	public List<MonthHMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
}
