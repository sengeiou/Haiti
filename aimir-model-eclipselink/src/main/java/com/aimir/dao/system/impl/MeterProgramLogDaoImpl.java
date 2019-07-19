package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.MeterProgramLogDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.MeterProgramLog;
import com.aimir.util.Condition;

@Repository(value = "meterProgramLogDao")
public class MeterProgramLogDaoImpl extends AbstractJpaDao<MeterProgramLog, Integer> implements
MeterProgramLogDao {

    public MeterProgramLogDaoImpl() {
        super(MeterProgramLog.class);
    }

    @Override
    public List<MeterProgramLog> findbyMeter(Meter meter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MeterProgramLog> findbyMeterId(Integer meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Map<String, Object>> getMeterProgramLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterProgramLogListRenew(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeterProgramLog> getPersistentClass() {
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