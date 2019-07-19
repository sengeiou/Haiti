package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MbusSlaveIOModule;

/**
 * SP-929
 * Mbus Slave I/O Module for Net-station Monitoring
 */
public interface MbusSlaveIOModuleDao extends GenericDao<MbusSlaveIOModule, Long> {
    public MbusSlaveIOModule get(String mdsId);
    public MbusSlaveIOModule get(Integer meterId);
    List<Map<String, Object>> getMbusSlaveIOModuleInfo(String mdsId);	
    public List<Map<String, Object>> getMbusSlaveIOModuleCountListPerLocation(Map<String, Object> conditionMap);
}
