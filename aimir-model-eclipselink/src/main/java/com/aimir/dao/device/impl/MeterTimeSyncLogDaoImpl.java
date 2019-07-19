package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;

import com.aimir.model.device.MeterTimeSyncLog;
import com.aimir.util.Condition;

@Repository(value = "metertimesynclogDao")
public class MeterTimeSyncLogDaoImpl extends AbstractJpaDao<MeterTimeSyncLog, Long> implements MeterTimeSyncLogDao {
	
    Log logger = LogFactory.getLog(MeterTimeSyncLogDaoImpl.class);
    
    public MeterTimeSyncLogDaoImpl() {
		super(MeterTimeSyncLog.class);
	}

    @Override
    public Class<MeterTimeSyncLog> getPersistentClass() {
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


