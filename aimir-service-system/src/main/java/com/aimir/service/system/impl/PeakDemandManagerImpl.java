package com.aimir.service.system.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.PeakAndDemandThreshold;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.system.PeakDemandLogDao;
import com.aimir.dao.system.PeakDemandScenarioDao;
import com.aimir.dao.system.PeakDemandSettingDao;
import com.aimir.dao.system.ContractCapacityDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.PeakDemandLog;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.model.system.Operator;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.ContractEnergyPeakDemandManager;
import com.aimir.service.system.PeakDemandManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * 피크 수요 매니저 서비스 구현체
 * 
 * @see com.aimir.service.system.PeakDemandManager
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Service(value="peakDemandManager")
public class PeakDemandManagerImpl implements PeakDemandManager {

	@Autowired PeakDemandLogDao peakDemandLogDao;
	@Autowired PeakDemandScenarioDao peakDemandScenarioDao;
	@Autowired PeakDemandSettingDao peakDemandSettingDao;
	@Autowired ContractCapacityDao contractCapacityDao;
	@Autowired OperatorDao operatorDao;
	@Autowired ContractEnergyPeakDemandManager contractEnergyPeakDemandManager;
	@Autowired ContractCapacityManager contractCapacityManager;

	private final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private Set<Condition> setPagingVariables(Map<String, Integer> pagingVars) {
		Set<Condition> conditions = new HashSet<Condition>();
		if(pagingVars != null) {
			if(pagingVars.containsKey("first")) {
				int first = (pagingVars.get("first") != null) ? pagingVars.get("first") : 0;
				conditions.add(
					new Condition("", new Object[]{first}, null, Restriction.FIRST)
				);
			}
			else {
				conditions.add(
					new Condition("", new Object[]{0}, null, Restriction.FIRST)
				);
			}
			
			if(pagingVars.containsKey("max")) {
				int first = (pagingVars.get("max") != null) ? pagingVars.get("max") : 10;
				conditions.add(
					new Condition("", new Object[]{first}, null, Restriction.MAX)
				);
			}
			else {
				conditions.add(
					new Condition("", new Object[]{10}, null, Restriction.MAX)
				);
			}
		}
		return conditions;
	}
	
	/**
	 * Map parameter로 Condition 셋을 만든다.
	 * 
	 * @param parameters 파라미터 값
	 * @param pagingVars 페이징 값
	 * @return Condition 셋 객체 
	 */
	private Set<Condition> buildConditionLog(Map<String, String> parameters, Map<String, Integer> pagingVars) {
		
		Set<Condition> conditions = new HashSet<Condition>();
		
		if(parameters.containsKey("scenario")) {
			try {
				int scenario = Integer.parseInt(parameters.get("scenario"));
				conditions.add(
					new Condition("scenario.id", new Object[]{scenario}, null, Restriction.EQ)
				);
			}
			catch(Exception ignore) {}
		}
		if(parameters.containsKey("result")) {
			try {
				ResultStatus result = ResultStatus.valueOf(parameters.get("result"));
				conditions.add(
					new Condition("result",new Object[]{result},null, Restriction.EQ)
				);
			}
			catch(IllegalArgumentException ignore) {}
		}
		
		if(parameters.containsKey("searchDate")){
			String date = parameters.get("searchDate");
			if(date.contains("@")) {
				String [] dates = date.split("@");
				if(dates.length == 2) {
					String startDate = dates[0] + "000000";
					String endDate = dates[1] + "235959";
					conditions.add(
						new Condition(
							"runTime", 
							new Object[]{startDate, endDate}, 
							null, Restriction.BETWEEN
						)
					);
				}
			}
		}
		
		conditions.add(
			new Condition("runTime", null, null, Restriction.ORDERBYDESC)
		);
		
		if(pagingVars != null) {
			conditions.addAll(setPagingVariables(pagingVars));			
		}
		return conditions;
	}
	
	@Override
	public List<PeakDemandLog> getPeakDemandLogs(
			Map<String, String> parameters, Map<String, Integer> pagingVars) {
		return peakDemandLogDao.findByConditions(buildConditionLog(parameters, pagingVars));
	}

	@Override
	public long getTotalPeakDemandLog(Map<String, String> parameters) {
		return peakDemandLogDao.totalByConditions(buildConditionLog(parameters, null));
	}
	
	/**
	 * Map parameter로 Condition 셋을 만든다.
	 * 
	 * @param parameters 파라미터 값
	 * @param pagingVars 페이징 값
	 * @return Condition 셋 객체 
	 */
	private Set<Condition> buildConditionScenario(Map<String, String> parameters, Map<String, Integer> pagingVars) {
		
		Set<Condition> conditions = new HashSet<Condition>();
		
		if(parameters.containsKey("name")) {
			conditions.add(
				new Condition(
					"name", 
					new Object[]{parameters.get("name")}, 
					null, Restriction.EQ
				)
			);
		}
		if(parameters.containsKey("contractCapacity")) {
			try {
				int contractCapacity = Integer.parseInt(parameters.get("contractCapacity"));
				conditions.add(
					new Condition(
						"contractCapacity.id", 
						new Object[]{contractCapacity}, 
						null, Restriction.EQ
					)
				);
			}
			catch(Exception ignore) {}
		}
		
		conditions.add(
			new Condition("name", null, null, Restriction.ORDERBY)
		);
		
		if(pagingVars != null) {
			conditions.addAll(setPagingVariables(pagingVars));	
		}
		return conditions;
	}

	@Override
	public List<PeakDemandScenario> getPeakDemandScenarios(
			Map<String, String> parameters, Map<String, Integer> pagingVars) {
		return peakDemandScenarioDao.findByConditions(
			buildConditionScenario(parameters, pagingVars)
		);
	}
	
	@Override
	public long getTotalPeakDemandScenario(Map<String, String> parameters) {
		return peakDemandScenarioDao.totalByConditions(
			buildConditionScenario(parameters, null)
		);
	}
	
	@Override
	@Transactional(readOnly=false)
	public PeakDemandScenario addPeakDemandScenario(
		Map<String, String> params,
		ContractCapacity contractCapacity,
		Operator operator) {
		
		PeakDemandScenario scenario = new PeakDemandScenario();
		scenario.setContractCapacity(contractCapacity);
		scenario.setOperator(operator);
		
		scenario.setDescr(params.get("description"));
		scenario.setName(params.get("scenarioName"));
		scenario.setModifyTime(yyyyMMddHHmmss.format(Calendar.getInstance().getTime()));
		scenario.setTarget(params.get("target"));
		
		return peakDemandScenarioDao.add(scenario);
	}
	
	@Override
	@Transactional(readOnly=false)
	public PeakDemandScenario addPeakDemandScenario(Map<String, String> params, Operator operator) {
		try {
			Integer contractLocation = Integer.parseInt(params.get("contractLocation"));
			ContractCapacity contractCapacity = contractCapacityDao.findByCondition("id", contractLocation);
			
			if(contractCapacity != null) {
				return addPeakDemandScenario(params, contractCapacity, operator);				
			}
			else {
				throw new IllegalArgumentException("aimir.form.required.contractLocation");
			}
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("aimir.form.required.contractLocation");
		}
	}

	@Override
	@Transactional(readOnly=false)
	public int deletePeakDemandScenario(Integer id) {
		
		PeakDemandSetting setting = peakDemandSettingDao.findByCondition("scenarioId", id);
		if(setting != null && setting.getIsAction()) {
			throw new IllegalArgumentException("aimir.bems.peakdemand.activateScenarioCannotDeleted");
		}		
		return peakDemandScenarioDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=false)
	public PeakDemandScenario modifyPeakDemandScenario(Map<String, String> params) {
		PeakDemandScenario scenario = null;
		try {
			Integer scenarioId = Integer.parseInt(params.get("scenarioId"));
			scenario = peakDemandScenarioDao.get(scenarioId);
			
			Integer contractLocation = Integer.parseInt(params.get("contractLocation"));		
			ContractCapacity contractCapacity = contractCapacityDao.get(contractLocation);
			if(contractCapacity != null) {
				scenario.setContractCapacity(contractCapacity);
			}
			
			String loginId = params.get("loginId");
			if(loginId != null && !loginId.equals(scenario.getOperatorId())) {
				Operator operator = operatorDao.getOperatorByLoginId(params.get("loginId"));	
				if(contractCapacity != null) {
					scenario.setOperator(operator);
				}
			}
			
			scenario.setDescr(params.get("description"));
			scenario.setName(params.get("name"));
			scenario.setModifyTime(yyyyMMddHHmmss.format(Calendar.getInstance().getTime()));
			scenario.setTarget(params.get("target"));
			
			scenario = peakDemandScenarioDao.update(scenario);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("aimir.form.required.DRScenarioInvalid");
		}
		return scenario;
	}

	@Override
	@Transactional(readOnly=false)
	public PeakDemandSetting applyPeakDemandConfiguration(
		Map<String, String> params, PeakDemandScenario scenario, Operator operator) {
		PeakDemandSetting peakDemandSetting = null;
		try {			
			Boolean isAction = Boolean.valueOf(params.get("isAction"));						
			if(scenario == null && isAction) {
				throw new IllegalArgumentException("aimir.form.required.DRScenarioInvalid");
			}
			
			if(!params.containsKey("level") || params.get("level") == null) {
				throw new IllegalArgumentException("aimir.form.required.peakThreshold");
			}
			PeakAndDemandThreshold threshold = PeakAndDemandThreshold.valueOf(
				params.get("level").toUpperCase()
			);			
			peakDemandSetting = peakDemandSettingDao.findByCondition("thresholdLevel", threshold.getCode());
			
			if(peakDemandSetting == null) {
				peakDemandSetting = new PeakDemandSetting();
				peakDemandSetting.setIsAction(isAction.toString().toLowerCase());
				peakDemandSetting.setModifyTime(yyyyMMddHHmmss.format(Calendar.getInstance().getTime()));
				peakDemandSetting.setOperator(operator);
				peakDemandSetting.setScenario(scenario);
				peakDemandSetting.setThresholdLevel(threshold.getCode());
				peakDemandSettingDao.add(peakDemandSetting);
			}
			else {
				// 액션을 해지할 경우 세팅을 삭제한다.
				if(!isAction) {
					peakDemandSettingDao.delete(peakDemandSetting);
				}
				// 그렇지 않으면 단순 업데이트 처리
				else {
					if(isAction && scenario != null) {
						peakDemandSetting.setScenario(scenario);
					}				
					peakDemandSetting.setIsAction(isAction.toString().toLowerCase());
					peakDemandSetting.setModifyTime(yyyyMMddHHmmss.format(Calendar.getInstance().getTime()));
					peakDemandSetting = peakDemandSettingDao.update(peakDemandSetting);
				}				
			}
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("aimir.form.required.peakThreshold");
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("aimir.form.required.peakThreshold");
		}
		return peakDemandSetting;
	}
	
	@Override
	@Transactional(readOnly=false)
	public PeakDemandSetting applyPeakDemandConfiguration(
		Map<String, String> params, Operator operator) {
		PeakDemandScenario scenario = null;
		try {
			String s = params.get("scenarioId");
			if(s != null && !s.trim().isEmpty()) {				
				Integer scenarioId = Integer.parseInt(s.trim());
				scenario = peakDemandScenarioDao.get(scenarioId);
			}	
			return applyPeakDemandConfiguration(params, scenario, operator);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("aimir.form.required.DRScenarioInvalid");
		}
	}
	
	@Override
	public Map<String, PeakDemandSetting> getAllPeakDemandSettings() {
		Map<String, PeakDemandSetting> peakSettings = new HashMap<String, PeakDemandSetting>();
		PeakAndDemandThreshold [] vals = PeakAndDemandThreshold.values();
		for (PeakAndDemandThreshold threshold : vals) {
			peakSettings.put(threshold.toString(), getPeakDemandSetting(threshold.getIntValue()));
		}
		return peakSettings;
	}
	
	@Override
	public PeakDemandSetting getPeakDemandSetting(Integer threshold) {
		return peakDemandSettingDao.findByCondition("thresholdLevel", threshold);
	}

	@Override
	public PeakDemandScenario getPeakDemandScenario(Integer scenarioId) {
		return peakDemandScenarioDao.get(scenarioId);
	}
}
