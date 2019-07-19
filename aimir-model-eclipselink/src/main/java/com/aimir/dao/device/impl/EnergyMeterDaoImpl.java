package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.util.Condition;

@Repository(value = "energymeterDao")
public class EnergyMeterDaoImpl extends AbstractJpaDao<EnergyMeter, Integer> implements EnergyMeterDao {

    Log logger = LogFactory.getLog(EnergyMeterDaoImpl.class);
    
    @Autowired CodeDao codeDao;
    @Autowired CircuitBreakerSettingDao circuitBreakerSettingDao;
    @Autowired CircuitBreakerLogDao circuitBreakerLogDao;
    @Autowired TOURateDao tOURateDao;
    @Autowired SeasonDao seasonDao;
    
	public EnergyMeterDaoImpl() {
		super(EnergyMeter.class);
	}

    @Override
    public List<Map<String, String>> getElecSupplyCapacityGridData(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getEmergencyElecSupplyCapacityGridData(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getElecSupplyCapacityGridDataCount(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getElecSupplyCapacityMiniGridData(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EnergyMeter> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}