package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ModemPowerLogDao;
import com.aimir.model.device.ModemPowerLog;
import com.aimir.util.Condition;

@Repository(value = "modempowerlogDao")
public class ModemPowerLogDaoImpl extends AbstractJpaDao<ModemPowerLog, Integer> implements ModemPowerLogDao {

    Log log = LogFactory.getLog(ModemPowerLogDaoImpl.class);
    
    public ModemPowerLogDaoImpl() {
		super(ModemPowerLog.class);
	}

    @Override
    public Class<ModemPowerLog> getPersistentClass() {
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