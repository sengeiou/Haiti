package com.aimir.service.system;

import java.util.List;
import java.util.Map;

public interface PlcQualityTestManager {

	public List<Map<String,Object>> getReadExcelAsset(String savePath, String zigName);
	public List<Object> getPlcQualityResult(Map<String, Object> condition);
	public List<Object> getPlcQualityDetailResult(Map<String, Object> condition);
	public void testStart(String savePath, String[] zigName);
	public void testEnd(String savePath, String[] zigName);
	public List<Map<String,Object>> checkResult(String zigName);
	public int changeNullResult(Integer zigId, String testStartDate);
	public Map<String, Object> getSummaryInfo(Map<String, Object> condition);
}
