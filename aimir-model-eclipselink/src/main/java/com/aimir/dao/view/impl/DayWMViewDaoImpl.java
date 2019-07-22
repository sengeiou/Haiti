package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.DayWMViewDao;
import com.aimir.model.view.DayEMView;
import com.aimir.model.view.DayWMView;
import com.aimir.util.Condition;

@Repository(value="daywmDaoView")
public class DayWMViewDaoImpl extends AbstractJpaDao<DayWMView, Integer> implements DayWMViewDao {
	private static Log log = LogFactory.getLog(DayWMViewDaoImpl.class);

    protected DayWMViewDaoImpl() {
        super(DayWMView.class);
    }
    
	@Override
	public Class<DayWMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayWMView> getDayWMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayWMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DayWMView getDayWMbySupplierId(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
