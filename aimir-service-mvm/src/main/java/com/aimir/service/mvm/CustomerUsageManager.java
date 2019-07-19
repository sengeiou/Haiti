package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

@Deprecated
public interface CustomerUsageManager {
	
	// MiniGadget
	public List<Object> getCustomerUsageMiniChart(Map<String, Object> condition);
	public List<Object> getCustomerUsageMiniChartbySearchDate(Map<String, Object> condition);
	public List<Object> getCustomerUsageFee(Map<String, Object> condition);
	public Map<String, Object> getCustomerTariff(Map<String, Object> condition);
	
	public List<Object> getCustomerCO2Daily(Map<String, Object> condition);
}
