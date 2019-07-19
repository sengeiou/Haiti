package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringSLADao;
import com.aimir.model.mvm.MeteringSLA;
import com.aimir.util.Condition;

@Repository(value = "meteringslaDao")
public class MeteringSLADaoImpl extends AbstractJpaDao<MeteringSLA, Integer> implements MeteringSLADao {
	
	private static Log logger = LogFactory.getLog(MeteringSLADaoImpl.class);
	    
	public MeteringSLADaoImpl() {
		super(MeteringSLA.class);
	}

    @Override
    public List<Object> getMeteringSLASummaryGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringSLAList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringSLAMissingData(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringSLAMissingDetailChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringSLAMissingDetailGrid(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeteringSLA> getPersistentClass() {
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
