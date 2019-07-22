package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.view.DayGMView;
import com.aimir.util.Condition;

public interface DayGMViewDao extends GenericDao<DayGMView, Integer>{

	/**
	 * method name : getDayGMbySupplierId
	 * method Desc : 해당 공급사의 고객에 대한 DayGM정보만 가져오는 조건
	 * 
	 * @param params
	 * @return
	 */
	public List<DayGMView> getDayGMsByListCondition(Set<Condition> list);
	
	public List<DayGMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	
	public DayGMView getDayGMbySupplierId(Map<String,Object> params);
}
