package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Contract;
import com.aimir.model.view.MonthEMView;
import com.aimir.util.Condition;

public interface MonthEMViewDao extends GenericDao<MonthEMView, Integer>{
	
	public List<MonthEMView> getMonthEMsByListCondition(Set<Condition> list);
	
	public List<MonthEMView> getMonthEMs(MonthEMView monthEMView);
	public List<MonthEMView> getMonthEMsByCondition(Map<String, Object> condition);
	public List<MonthEMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap);
	
	/**
	 * @MethodName getMonthlyUsage
	 * @Date 2013. 10. 28.
	 * @param contract
	 * @param yyyymm
	 * @param channels
	 * @return
	 * @Modified
	 * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다.
	 */	
	public List<MonthEMView> getMonthlyUsageByContract(Contract contract, String yyyymm, String channels);
	
	public List<MonthEMView> getMonthEMbySupplierId(Map<String,Object> params);
}
