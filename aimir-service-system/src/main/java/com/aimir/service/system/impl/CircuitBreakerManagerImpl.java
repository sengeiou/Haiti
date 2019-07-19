package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.CircuitBreakerLog;
import com.aimir.model.device.CircuitBreakerSetting;
import com.aimir.model.device.EnergyMeter;
import com.aimir.service.system.CircuitBreakerManager;
import com.aimir.util.DateTimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.CircuitBreakerManager")
@Service(value = "circuitBreakerManager")
@Transactional(readOnly=false)
public class CircuitBreakerManagerImpl implements CircuitBreakerManager {

    @Autowired ContractDao contractDao;
    @Autowired EnergyMeterDao energyMeterDao;
    @Autowired CircuitBreakerSettingDao circuitBreakerSettingDao;
    @Autowired CircuitBreakerLogDao circuitBreakerLogDao;
    
	public List<Map<String, String>> getSupplyCapacity(Map<String, String> paramMap) {
		
		return contractDao.getSupplyCapacity(paramMap);
	}

//	public void saveSupplyCapacity(Map<String, String> paramMap) {
//		
//		int contractId = Integer.parseInt(paramMap.get("contractId"));
//		Contract contract = contractDao.get(contractId);
//		
//		// 차단/해제 필드 업데이트
//		EnergyMeter energyMeter = energyMeterDao.get(contract.getMeter().getId());	
//		energyMeter.setSwitchStatus(CircuitBreakerStatus.Activation.name());
//		energyMeterDao.update(energyMeter);
//		
//		// 로그 업데이트
//		CircuitBreakerLog log = new CircuitBreakerLog();
//		log.setTargetType(GroupType.Meter);
//		log.setTarget(contract.getMeter().getMdsId());
//		log.setStatus(energyMeter.getSwitchStatus());
//		log.setCondition(condition);  
//		log.setResult(result);
//	}

	public void saveSupplyCapacity(CircuitBreakerStatus status, String targetType, CircuitBreakerCondition condition, int meterId) {
		
		EnergyMeter energyMeter = energyMeterDao.get(meterId);
		
		// 차단/해제 필드 업데이트
		//FIXME [현재 switchStatus db:integer 임 추후 주석 해제]		
		if(CircuitBreakerStatus.Activation == status && !MeterStatus.Normal.name().equals(energyMeter.getMeterStatus().getName()))
			energyMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
		if(CircuitBreakerStatus.Deactivation == status && !MeterStatus.CutOff.name().equals(energyMeter.getMeterStatus().getName()))
			energyMeter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
		
		energyMeterDao.update(energyMeter);
		
		// 로그 업데이트
		CircuitBreakerLog log = new CircuitBreakerLog();
		log.setTargetType(GroupType.valueOf(targetType));
		log.setTarget(energyMeter.getMdsId());
		log.setStatus(status);
		log.setCondition(condition);
		log.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat(null));
		//FIXME 이거 점 낭중이 점 오치기 점 허자 점
		log.setResult(ResultStatus.SUCCESS);
		
		circuitBreakerLogDao.saveOrUpdate(log);
	} 
	
	public CircuitBreakerSetting saveCircuitBreakerSetting(CircuitBreakerSetting setting) {

		CircuitBreakerSetting currSetting = circuitBreakerSettingDao.findByCondition("condition", setting.getCondition());
		
		if(currSetting == null) {
			currSetting = circuitBreakerSettingDao.saveOrUpdate(setting);
		} else {
			currSetting.setCondition(setting.getCondition());
			currSetting.setBlockingThreshold(setting.getBlockingThreshold());
			currSetting.setAlarmThreshold(setting.getAlarmThreshold());
			currSetting.setAutomaticDeactivation(setting.getAutomaticDeactivation());
			currSetting.setAutomaticActivation(setting.getAutomaticActivation());
			currSetting.setRecoveryTime(setting.getRecoveryTime());
			currSetting.setRecoveryTime(setting.getRecoveryTime());
			currSetting.setTimeUnit(setting.getTimeUnit());
			currSetting.setAlarm(setting.getAlarm());
			
			currSetting = circuitBreakerSettingDao.update(currSetting);
		}
			
		return currSetting;
	}

	public CircuitBreakerSetting getCircuitBreakerSetting(CircuitBreakerCondition condition) {

		return circuitBreakerSettingDao.findByCondition("condition", condition);
	}

	public List<CircuitBreakerLog> getCircuitBreakerLogGridData(Map<String, String> paramMap) {
		
		return circuitBreakerLogDao.getCircuitBreakerLogGridData(paramMap);
	}

	public Long getCircuitBreakerLogGridDataCount(Map<String, String> paramMap) {

		return circuitBreakerLogDao.getCircuitBreakerLogGridDataCount(paramMap);
	}

	public List<Map<String, String>> getElecSupplyCapacityGridData(Map<String, String> paramMap) {

		return energyMeterDao.getElecSupplyCapacityGridData(paramMap);
	}
	
	public List<Map<String, String>> getEmergencyElecSupplyCapacityGridData(Map<String, String> paramMap){
		return energyMeterDao.getEmergencyElecSupplyCapacityGridData(paramMap);
	}

	public String getElecSupplyCapacityGridDataCount(Map<String, String> paramMap) {

		return energyMeterDao.getElecSupplyCapacityGridDataCount(paramMap);
	}
	
	public List<Map<String, String>> getEmergencyElecSupplyCapacityGridDataCount(List<Map<String, String>> data, Map<String, String> paramMap) {

		int page = Integer.parseInt(paramMap.get("page"));
		int pageSize = Integer.parseInt(paramMap.get("pageSize"));
		int totalCount = Integer.parseInt(paramMap.get("totalCount"));
		int firstIndex = page * pageSize;
		int lastIndex = firstIndex + pageSize;
		if(lastIndex > totalCount) lastIndex = totalCount;
						
		return data.subList(firstIndex, lastIndex);
	}


	public List<Map<String, String>> getCircuitBreakerLogChartData(	Map<String, String> paramMap) {

		return circuitBreakerLogDao.getCircuitBreakerLogChartData(paramMap);
	}

	public List<Map<String, String>> getElecSupplyCapacityGridDataCount(List<Map<String, String>> data, Map<String, String> paramMap) {

		int page = Integer.parseInt(paramMap.get("page"));
		int pageSize = Integer.parseInt(paramMap.get("pageSize"));
		int totalCount = Integer.parseInt(paramMap.get("totalCount"));
		int firstIndex = page * pageSize;
		int lastIndex = firstIndex + pageSize;
		if(lastIndex > totalCount) lastIndex = totalCount;
						
		return data.subList(firstIndex, lastIndex);
	}

	public List<Map<String, String>> getElecSupplyCapacityMiniGridData(Map<String, String> paramMap) {
		return energyMeterDao.getElecSupplyCapacityMiniGridData(paramMap);
	}
}
