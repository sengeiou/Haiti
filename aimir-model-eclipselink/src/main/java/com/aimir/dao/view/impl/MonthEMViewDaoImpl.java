package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.MonthEMViewDao;
import com.aimir.model.system.Contract;
import com.aimir.model.view.MonthEMView;
import com.aimir.util.Condition;

@Repository(value="monthemDaoView")
public class MonthEMViewDaoImpl extends AbstractJpaDao<MonthEMView, Integer> implements MonthEMViewDao {
	private static Log log = LogFactory.getLog(MonthEMViewDaoImpl.class);

    protected MonthEMViewDaoImpl() {
        super(MonthEMView.class);
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthEMsByListCondition(Set<Condition> list) {
    	return findByConditions(list);
	}

	@Override
	public List<MonthEMView> getMonthEMsByCondition(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthEMView> getMonthEMs(MonthEMView monthEMView) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthEMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<MonthEMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthEMView> getMonthlyUsageByContract(Contract contract, String yyyymm, String channels) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthEMView> getMonthEMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
