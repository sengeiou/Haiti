package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.DayWMView;
import com.aimir.util.Condition;

public interface DayWMViewDao extends GenericDao<DayWMView, Integer>{

	public List<DayWMView> getDayWMsByListCondition(Set<Condition> list);
	
	public List<DayWMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	
	public DayWMView getDayWMbySupplierId(Map<String,Object> params);
}
