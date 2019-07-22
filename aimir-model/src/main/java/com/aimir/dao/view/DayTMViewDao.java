package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.DayTMView;
import com.aimir.util.Condition;

public interface DayTMViewDao extends GenericDao<DayTMView, Integer>{

	public List<DayTMView> getDayTMsByListCondition(Set<Condition> list);
	
	public List<DayTMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
}
