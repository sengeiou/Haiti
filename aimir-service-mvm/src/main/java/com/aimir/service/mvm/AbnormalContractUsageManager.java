package com.aimir.service.mvm;

import java.util.Map;

public interface AbnormalContractUsageManager {

	public Map<String, Object> getAbnormalContractUsageEM(Map<String, Object> condition);
    public Map<String, Object> getAbnormalContractUsageEMList(Map<String, Object> condition);
    
}
