package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.PlcQualityTestDetail;

public interface PlcQualityTestDetailDao extends GenericDao<PlcQualityTestDetail, Integer>{

	public List<Object> getPlcQualityDetailResult(Map<String, Object> condition);
	public List<Map<String,Object>> checkResult(String zigName);
    public int changeNullResult(Integer zigId, String testStartDate);
    public List<Map<String,Object>> getSummaryInfo(Map<String, Object> condition);
}
