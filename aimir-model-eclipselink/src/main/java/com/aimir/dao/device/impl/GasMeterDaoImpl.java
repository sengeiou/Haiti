package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.GasMeterDao;
import com.aimir.model.device.GasMeter;
import com.aimir.util.Condition;

@Repository(value = "gasmeterDao")
public class GasMeterDaoImpl extends AbstractJpaDao<GasMeter, Integer> implements GasMeterDao {

    Log logger = LogFactory.getLog(MeterDaoImpl.class);
    
    public GasMeterDaoImpl() {
		super(GasMeter.class);
	}

    @Override
    public Class<GasMeter> getPersistentClass() {
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