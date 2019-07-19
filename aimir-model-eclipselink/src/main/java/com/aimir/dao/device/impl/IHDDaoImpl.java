package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.IHDDao;
import com.aimir.model.device.IHD;
import com.aimir.util.Condition;

@Repository(value = "ihdDao")
public class IHDDaoImpl extends AbstractJpaDao<IHD, Integer> implements IHDDao {

    Log log = LogFactory.getLog(IHDDaoImpl.class);
    
	public IHDDaoImpl() {
		super(IHD.class);
	}

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<IHD> getPersistentClass() {
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