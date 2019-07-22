package com.aimir.dao.view;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.MonthTMView;

public interface MonthTMViewDao extends GenericDao<MonthTMView, Integer>{
	
	public List<MonthTMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
}
