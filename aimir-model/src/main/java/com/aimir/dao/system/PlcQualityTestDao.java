package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.PlcQualityTest;

public interface PlcQualityTestDao extends GenericDao<PlcQualityTest, Integer>{

	List<Object> getPlcQualityResult(Map<String, Object> condition);
	
	PlcQualityTest getInfoByZig(String zigId);
	
	Integer getCount(Integer zigId);
    
}
