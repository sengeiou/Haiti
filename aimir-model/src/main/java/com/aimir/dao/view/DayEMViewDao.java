package com.aimir.dao.view;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.view.DayEMView;
import com.aimir.util.Condition;

public interface DayEMViewDao extends GenericDao<DayEMView, Integer>{

	public List<DayEMView> getDayEMs(DayEMView dayEMView);
	public List<DayEMView> getDayEMsByListCondition(Set<Condition> list);
	public List<DayEMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap);
	
	//BEMS
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition);
	
	// 검침 데이터 조회
    public List<DayEMView> getMeteringFailureMeteringData(Map<String,Object> params);
}
