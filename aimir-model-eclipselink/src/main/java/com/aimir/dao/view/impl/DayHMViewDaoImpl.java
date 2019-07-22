package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.view.DayHMViewDao;
import com.aimir.model.view.DayEMView;
import com.aimir.model.view.DayHMView;
import com.aimir.util.Condition;

@Repository(value="dayhmDaoView")
public class DayHMViewDaoImpl extends AbstractJpaDao<DayHMView, Integer> implements DayHMViewDao {
	private static Log log = LogFactory.getLog(DayHMViewDaoImpl.class);

    protected DayHMViewDaoImpl() {
        super(DayHMView.class);
    }
    
	@Override
	public Class<DayHMView> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayHMView> getDayHMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayHMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
