package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ScheduleResultLogDao;
import com.aimir.model.system.ScheduleResultLog;
import com.aimir.util.Condition;

@Repository(value = "scheduleResultLogDao")
public class ScheduleResultLogDaoImpl extends
        AbstractJpaDao<ScheduleResultLog, Long> implements
        ScheduleResultLogDao {

    public ScheduleResultLogDaoImpl() {
        super(ScheduleResultLog.class);
    }

    @Override
    @Deprecated
    public List<Object> getLatestScheduleResultLogByTrigger(String triggerName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLatestScheduleResultLogByJobTrigger(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getScheduleResultLogByJobName(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ScheduleResultLog> getPersistentClass() {
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