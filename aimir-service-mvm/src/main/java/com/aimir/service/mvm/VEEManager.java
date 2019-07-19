package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.service.mvm.bean.VEEMaxData;
import com.aimir.service.mvm.bean.VEEMiniData;
import com.aimir.service.mvm.bean.VEEParameterData;

public interface VEEManager {
	/*
	public List<String> getTableItemList();
	public List<Object> getMiniVEEHistoryManager(String[] values);
	public List<String> getVEERuleList();
	public List<Object> getMiniVEEValidationCheckManager(String[] values);
	public List<Object> getMaxVEEHistoryManager(String[] values);
	public List<Object> getMaxVEEValidationCheckManager(String[] values);
	public List<VEEParameterData> getMaxVEEParametersManager(String values);
	public List<String> getVEEParameterNameList();
	*/
	
	
	public List<String> getTableItemList();
	public List<Object> getMiniVEEHistoryManager(String[] values);
	public List<String> getVEERuleList();
	public List<VEEMiniData> getMiniVEEValidationCheckManager(String[] values);
	public Map<String, String> getMaxVEEHistoryManagerTotal(String[] values);
	public List<Object> getMaxVEEHistoryManager(String[] values, String page, String limit);
	public Map<String, String> getMaxVEEValidationCheckManagerTotal(String[] values);
	public List<VEEMaxData> getMaxVEEValidationCheckManager(String[] values, String startRow, String pageSize);
	public List<VEEParameterData> getMaxVEEParametersManager(String values);
	public List<Map<String, Object>> getVEEParameterNameList();
	public List<Map<String, Object>> getVEEEditItemList() ;
	public Map<String, Object>  getLpData(String meterType, String item, String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String supplierId);
	public String updateLpData(String meterType, String userId, String supplierId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst, String[] params);
	public String estimationData(String meterType, String userId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst);
	public Map<String, Object> getPreviewAutoEstimation(Map<String, Object> conditions);
	public Map<String, Object> updateAutoEstimation(Map<String, Object> conditions);
	public List<Map<String, String>> getVEEParamNames(String ruleType);
}
