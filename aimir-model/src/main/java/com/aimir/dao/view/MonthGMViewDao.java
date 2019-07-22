package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.MonthGMView;
import com.aimir.util.Condition;

public interface MonthGMViewDao extends GenericDao<MonthGMView, Integer>{
	
	public List<MonthGMView> getMonthGMsByListCondition(Set<Condition> list);
	
	public List<MonthGMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	
	public MonthGMView getMonthGMbySupplierId(Map<String,Object> params);
}
