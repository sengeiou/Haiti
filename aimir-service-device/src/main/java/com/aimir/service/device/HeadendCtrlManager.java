package com.aimir.service.device;

import java.util.Map;

import javax.jws.WebService;

@WebService(name="headendCtrlService", targetNamespace="http://aimir.com/services")
public interface HeadendCtrlManager {
	
	public void insertHeadendCtrl(Map<String, Object> conditionMap);
	
	public Map<String, Object> getHeadendCtrlCommandResultData(Map<String, Object> conditionMap);
}
