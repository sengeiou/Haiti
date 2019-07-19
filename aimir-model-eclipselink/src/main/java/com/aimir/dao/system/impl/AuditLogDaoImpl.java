package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.AuditLogDao;
import com.aimir.model.system.AuditLog;
import com.aimir.util.Condition;

@Repository(value = "auditLogDao")
public class AuditLogDaoImpl extends AbstractJpaDao<AuditLog, Integer> implements
        AuditLogDao {

    public AuditLogDaoImpl() {
        super(AuditLog.class);
    }

    @Override
    public List<Map<String, Object>> getAuditLogRankingList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getAuditLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<AuditLog> getPersistentClass() {
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