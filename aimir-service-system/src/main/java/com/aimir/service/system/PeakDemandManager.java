package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PeakDemandLog;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.model.system.PeakDemandSetting;

/**
 *
 * 피크 수요 매니저 인터페이스
 * @see com.aimir.model.system.PeakDemandLogDao
 * @see com.aimir.model.system.PeakDemandScenarioDao
 * @see com.aimir.model.system.PeakDemandSetting
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
public interface PeakDemandManager {
	
	public PeakDemandScenario getPeakDemandScenario(Integer scenarioId);
	
	public PeakDemandScenario addPeakDemandScenario(
		Map<String, String> params,ContractCapacity contractCapacity,Operator operator);
	public PeakDemandScenario addPeakDemandScenario(Map<String, String> params,Operator operator);
	
	public int deletePeakDemandScenario(Integer id);
	public PeakDemandScenario modifyPeakDemandScenario(Map<String, String> params);	
	
	public PeakDemandSetting applyPeakDemandConfiguration(
		Map<String, String> params,PeakDemandScenario scenario,Operator operator);
	public PeakDemandSetting applyPeakDemandConfiguration(Map<String, String> params,Operator operator);
	
	public Map<String, PeakDemandSetting> getAllPeakDemandSettings();
	public PeakDemandSetting getPeakDemandSetting(Integer threshold);
	
	public List<PeakDemandLog> getPeakDemandLogs(
		Map<String, String> parameters,
		Map<String, Integer> pagingVars
	);
	
	public long getTotalPeakDemandLog(Map<String, String> parameters);
	
	public List<PeakDemandScenario> getPeakDemandScenarios(
		Map<String, String> parameters,
		Map<String, Integer> pagingVars
	);
	
	public long getTotalPeakDemandScenario(Map<String, String> parameters);
}
