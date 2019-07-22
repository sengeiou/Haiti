package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.DayTMViewDao;
import com.aimir.model.view.DayHMView;
import com.aimir.model.view.DayTMView;
import com.aimir.util.Condition;

@Repository(value="daytmDaoView")
public class DayTMViewDaoImpl extends AbstractJpaDao<DayTMView, Integer> implements DayTMViewDao {
	private static Log log = LogFactory.getLog(DayTMViewDaoImpl.class);
	
    protected DayTMViewDaoImpl() {
        super(DayTMView.class);
    }
    
	@Override
	public List<DayTMView> getDayTMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayTMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<DayTMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
