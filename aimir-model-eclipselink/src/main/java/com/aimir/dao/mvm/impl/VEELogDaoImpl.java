package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.VEELogDao;
import com.aimir.model.mvm.VEELog;
import com.aimir.util.Condition;

@Repository(value = "veelogDao")
public class VEELogDaoImpl extends AbstractJpaDao<VEELog, Integer> implements VEELogDao {
	
//	private static Log logger = LogFactory.getLog(VEELogDaoImpl.class);
	    
    public VEELogDaoImpl() {
		super(VEELog.class);
	}
	
	public List<VEELog> getVEELogByListCondition(Set<Condition> set) {
		return findByConditions(set);
	}
	
	public List<VEELog> getVEELogByListCondition(Set<Condition> set, int startRow, int pageSize) {
		return findByConditions(set);
	}

    @Override
    public List<Object> getVeeLogByDataList(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getVeeLogByCountList(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<VEELog> getPersistentClass() {
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
