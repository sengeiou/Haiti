package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.DayHMView;
import com.aimir.util.Condition;

public interface DayHMViewDao extends GenericDao<DayHMView, Integer>{

	public List<DayHMView> getDayHMsByListCondition(Set<Condition> list);
	
	public List<DayHMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
}
