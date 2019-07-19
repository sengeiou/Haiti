package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MbusSlaveIOModuleDao;
import com.aimir.model.device.MbusSlaveIOModule;
import com.aimir.util.Condition;


/**
 * SP-929
 *
 */
@Repository(value = "mbusSlaveIOModuleDao")
public class MbusSlaveIOModuleDaoImpl extends AbstractJpaDao<MbusSlaveIOModule, Long> implements MbusSlaveIOModuleDao {

    Log logger = LogFactory.getLog(MbusSlaveIOModuleDaoImpl.class);
    
    protected MbusSlaveIOModuleDaoImpl() {
        super(MbusSlaveIOModule.class);
        // TODO Auto-generated constructor stub
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public MbusSlaveIOModule get(String mdsId) {
        return findByCondition("mdsId", mdsId);
    }

    @Override
    public Class<MbusSlaveIOModule> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MbusSlaveIOModule get(Integer meterId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Map<String, Object>>  getMbusSlaveIOModuleInfo(String mdsId) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<Map<String, Object>> getMbusSlaveIOModuleCountListPerLocation(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }
}
