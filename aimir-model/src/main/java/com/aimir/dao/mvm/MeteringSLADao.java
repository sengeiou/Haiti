package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringSLA;

public interface MeteringSLADao extends GenericDao<MeteringSLA, Integer>{
	
	public List<Object> getMeteringSLASummaryGrid(Map<String, Object> condition);
	public List<Object> getMeteringSLAList(Map<String, Object> condition);
	
	// 검침율(Missing) 
	public List<Object> getMeteringSLAMissingData(Map<String, Object> condition);
	public List<Object> getMeteringSLAMissingDetailChart(Map<String, Object> condition);
	public List<Object> getMeteringSLAMissingDetailGrid(Map<String, Object> condition);
	
	
	
	
	

	
}
