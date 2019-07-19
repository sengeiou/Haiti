package com.aimir.service.device;

import java.util.Map;

import javax.jws.WebService;

import com.aimir.model.device.MbusSlaveIOModule;

/**
 * SP-929
 * for Net-station Monitoring
 */
@WebService(name="MbusSlaveIOModuleService", targetNamespace="http://aimir.com/services")
public interface MbusSlaveIOModuleManager {

    public MbusSlaveIOModule getMbusSlaveIOModule(Integer meterId);
    public MbusSlaveIOModule getMbusSlaveIOModule(String mdsId);
    Map<String, Object> getMbusSlaveIOModuleInfo(Integer meterId, Integer supplierId);
    Map<String, Object> getMbusSlaveIOModuleInfo(String mdsId, Integer supplierId);
}
