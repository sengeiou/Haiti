package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.Condition;

@Repository(value = "contractchangelogDao")
public class ContractChangeLogDaoImpl  extends AbstractJpaDao<ContractChangeLog, Long> implements ContractChangeLogDao {

    public ContractChangeLogDaoImpl() {
        super(ContractChangeLog.class);
    }

    public List<ContractChangeLog> getContractChangeLogByListCondition(
            Set<Condition> set) {
        return findByConditions(set);
    }

    public List<Object> getContractChangeLogCountByListCondition(
            Set<Condition> set) {
        return findTotalCountByConditions(set);
    }

    @Override
    public void contractLogDelete(int contractId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contractLogAllDelete(int customerId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getContractChangeLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ContractChangeLog> getPersistentClass() {
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