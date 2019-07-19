package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ModemConfig;
import com.aimir.util.Condition;

public interface ModemConfigDao extends GenericDao<ModemConfig, Integer>{
	
	/**
     * method name : getDeviceConfigs
     * method Desc : MeterConfig 아이디와 일치하는 ModemConfig 목록을 조회한다.
     * 
	 * @param configId ModemConfig.deviceconfig.id
	 * 
	 * @return List of ModemConfig @see com.aimir.model.system.ModemConfig
	 */
	public List<ModemConfig> getDeviceConfigs(Integer configId);
	
	/**
	 * @deprecated
     * method name : getDeviceConfig
     * method Desc : 
     * 
	 * @param swVersion
	 * @param swRevision
	 * @param connectedDeviceModel
	 * 
	 * @return @see com.aimir.model.system.ModemConfig
	 */
	public ModemConfig getDeviceConfig(String swVersion,String swRevision, Integer connectedDeviceModel);
	
	/**
     * method name : getDeviceConfig
     * method Desc : 조회조건과 일치하는 ModemConfig 정보를 리턴한다.
     * 
	 * @param conditions
	 * 
	 * @return @see com.aimir.model.system.ModemConfig
	 */
	public ModemConfig getDeviceConfig(Set<Condition> conditions);
	
	/**
     * method name : getParsers
     * method Desc : 장비 모델별 파서명을 리턴한다.
     * 
	 * @return Map {deviceModel'name,  parserName}
	 */
	public Map<?, ?> getParsers();
}

