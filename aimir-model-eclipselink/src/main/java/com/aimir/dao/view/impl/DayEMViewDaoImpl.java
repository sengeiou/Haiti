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
import com.aimir.dao.view.DayEMViewDao;
import com.aimir.model.view.DayEMView;
import com.aimir.util.Condition;

@Repository(value="dayemDaoView")
public class DayEMViewDaoImpl extends AbstractJpaDao<DayEMView, Integer> implements DayEMViewDao {
	private static Log log = LogFactory.getLog(DayEMViewDaoImpl.class);

    protected DayEMViewDaoImpl() {
        super(DayEMView.class);
    }
    
    @Override
	public List<DayEMView> getDayEMs(DayEMView dayEMView) {
		return null;
	}

	@Override
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<DayEMView> getDayEMsByListCondition(Set<Condition> list) {
		return findByConditions(list);
	}
	
	@Override
	public List<DayEMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayEMView> getMeteringFailureMeteringData(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<DayEMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}
}
