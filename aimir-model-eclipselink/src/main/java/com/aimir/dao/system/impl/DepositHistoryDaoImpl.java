package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.util.Condition;

@Repository("DepositHitoryDao")
public class DepositHistoryDaoImpl extends AbstractJpaDao<DepositHistory, Integer> implements DepositHistoryDao {

	public DepositHistoryDaoImpl() {
		super(DepositHistory.class);
	}

    @Override
    public Class<DepositHistory> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getHistoryList(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getDepositHistoryList(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getRecentDepositId(String vendorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteByPrepaymentLogId(long pId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, Object> getArrearsInfo(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getDebtInfo(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

}