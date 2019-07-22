package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.MonthHMView;
import com.aimir.model.view.MonthWMView;
import com.aimir.util.Condition;

public interface MonthWMViewDao extends GenericDao<MonthWMView, Integer>{
	
	public List<MonthWMView> getMonthWMsByListCondition(Set<Condition> list);
	
	public List<MonthWMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	
	public MonthWMView getMonthWMbySupplierId(Map<String,Object> params);
}
