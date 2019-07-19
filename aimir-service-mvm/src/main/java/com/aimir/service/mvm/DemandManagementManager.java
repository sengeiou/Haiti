package com.aimir.service.mvm;

import java.util.Map;

public interface DemandManagementManager {

	public Map<String, Object> getDemandManagement(Map<String, Object> condition);
	public Map<String, Object> getDemandManagementList(Map<String, Object> condition);
	
}
