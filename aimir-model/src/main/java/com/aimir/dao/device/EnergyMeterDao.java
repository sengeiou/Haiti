package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.EnergyMeter;

public interface EnergyMeterDao extends GenericDao<EnergyMeter, Integer> {
	
	public List<Map<String, String>> getElecSupplyCapacityGridData(	Map<String, String> paramMap);
	public List<Map<String, String>> getEmergencyElecSupplyCapacityGridData(	Map<String, String> paramMap);
	public String getElecSupplyCapacityGridDataCount(Map<String, String> paramMap);
	
	public List<Map<String, String>> getElecSupplyCapacityMiniGridData(	Map<String, String> paramMap);
}
